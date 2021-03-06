package hu.kazocsaba.math.geometry.fitting;

import hu.kazocsaba.math.geometry.DegenerateCaseException;
import hu.kazocsaba.math.geometry.Line2;
import hu.kazocsaba.math.geometry.Line3;
import hu.kazocsaba.math.matrix.EigenDecomposition;
import hu.kazocsaba.math.matrix.Matrix3;
import hu.kazocsaba.math.matrix.MatrixFactory;
import hu.kazocsaba.math.matrix.Vector2;
import hu.kazocsaba.math.matrix.Vector3;
import java.util.List;

/**
 * 2D line fitting algorithm using principal component analysis.
 * @author Kazó Csaba
 */
public class LineFitter {
	/** Threshold value used for floating-point equality tests. */
	private static final double EPS=1e-8;
	
	/** Private constructor to prevent instantiation. */
	private LineFitter() {}
	
	/**
	 * Finds the 2D line that best fits a point set.
	 * @param points the input point set
	 * @return the line
	 * @throws DegenerateCaseException if the fitting could not be performed,
	 * either because there are less than 2 points or they are in a degenerate
	 * composition
	 */
	public static Line2 fit2(List<? extends Vector2> points) throws DegenerateCaseException {
		/*
		 * This algorithm first computes the centroid of the point set,
		 * and then transforms all points so that the centroid of the
		 * transformed point set be the origin. Then the direction of the
		 * line is the singular vector belonging to the greatest singular
		 * value for this matrix:
		 * 
		 *   ( x1  y1 )
		 *   ( x2  y2 )          T
		 * A=( x3  y3 )         A A=( sum(xi*xi)  sum(xi*yi) )
		 *   (  ...   )             ( sum(xi*yi)  sum(yi*yi) )
		 *   ( xn  yn )
		 * 
		 * For performance reasons, we use the fact that the required
		 * singular vector is the same as the eigenvector corresponding
		 * to the greatest eigenvalue for A'*A (prime stands for transposition)
		 * so we calculate that. A'*A is 2x2, so we solve the eigenvalue
		 * problem with a closed formula.
		 * 
		 * Denote the eigenvalues by c, sum(xi*xi) by xx, sum(yi*yi) by yy, and
		 * sum(xi*yi) by xy. Then the characteristic polynomial is the following:
		 * 
		 * 0 = (xx-c)*(yy-c)-xy*xy = c^2 - (xx+yy)*c + xx*yy-xy*xy
		 * 
		 * Solving the quadratic equation for the greater of the two eigenvalues,
		 * we get:
		 * 
		 * c = 1/2 * (xx+yy + sqrt((xx+yy)^2 - 4(xx*yy-xy*xy)))
		 *   = 1/2 * (xx+yy + sqrt((xx-yy)^2 + 4*xy*xy))
		 * 
		 * The eigenvector (u,v) is obtained by solving A'*A*(u,v)=c*(u,v):
		 * 
		 * xx*u + xy*v = c*u                  (xx-c)*u +   xy  *v = 0
		 * xy*u + yy*v = c*v        or          xy  *u + (yy-c)*v = 0
		 * 
		 * If xy!=0, then v = -(xx-c)*u/xy. Now u cannot be 0, otherwise v would
		 * also be 0. So we can say that u=1 and
		 * 
		 * v = (c-xx)/xy = (yy-xx + sqrt(...))/(2*xy)
		 * 
		 * If xy==0, then we have
		 * 
		 * c = 1/2 * (xx+yy + sqrt((xx-yy)^2)) = 1/2 * (xx+yy+abs(xx-yy))
		 *   = max(xx,yy)
		 * 
		 * (xx-c)*u=0
		 * (yy-c)*v=0
		 * 
		 * This means that if yy>xx, then c=yy, and we only have (xx-yy)*u=0, so
		 * we take the solution u=0 and v=1. If yy<xx, then similarly we have
		 * u=1 and v=0. If xx==yy, then every vector is an eigenvector, and we
		 * cannot provide any sensible solution.
		 */
		if (points.size()<2)
			throw new DegenerateCaseException("Not enough points, at least 2 are needed for line fitting");
		
		Vector2 center=MatrixFactory.createVector2();
		for (Vector2 p: points) center.add(p);
		center.scale(1.0/points.size());
		
		double xx=0, xy=0, yy=0;
		for (Vector2 p: points) {
			double x=p.getX()-center.getX();
			double y=p.getY()-center.getY();
			xx+=x*x;
			xy+=x*y;
			yy+=y*y;
		}
		
		Vector2 result;
		if (Math.abs(xy)>EPS) {
			double root=Math.sqrt((xx-yy)*(xx-yy)+4*xy*xy);
			double u=1;
			double v=(yy-xx+root)/(2*xy);
			result=MatrixFactory.createVector(u, v);
		} else if (yy>xx+EPS) {
			result=MatrixFactory.createVector(0, 1);
		} else if (yy<xx-EPS) {
			result=MatrixFactory.createVector(1, 0);
		} else { // xx==yy
			throw new DegenerateCaseException("No principal direction in line set");
		}
		return Line2.createFromDir(center, result);
	}
	/**
	 * Finds the 3D line that best fits a point set.
	 * @param points the input point set
	 * @return the line
	 * @throws DegenerateCaseException if the fitting could not be performed,
	 * either because there are less than 2 points or they are in a degenerate
	 * composition
	 */
	public static Line3 fit3(List<? extends Vector3> points) throws DegenerateCaseException {
		/*
		 * The approach is the same as in the 2D case, only now we don't have a simple solution to the eigen problem.
		 * This way closely resembles the plane fitting task.
		 */
		if (points.size()<2)
			throw new DegenerateCaseException("Not enough points, at least 2 are needed for line fitting");
		
		Vector3 center=MatrixFactory.createVector3();
		for (Vector3 p: points) center.add(p);
		center.scale(1.0/points.size());
		
		Matrix3 ATA=MatrixFactory.createMatrix3();
		
		for (Vector3 p: points) {
			double x=p.getX()-center.getX();
			double y=p.getY()-center.getY();
			double z=p.getZ()-center.getZ();
			ATA.setQuick(0, 0, ATA.getQuick(0, 0)+x*x);
			ATA.setQuick(0, 1, ATA.getQuick(0, 1)+x*y);
			ATA.setQuick(0, 2, ATA.getQuick(0, 2)+x*z);
			ATA.setQuick(1, 1, ATA.getQuick(1, 1)+y*y);
			ATA.setQuick(1, 2, ATA.getQuick(1, 2)+y*z);
			ATA.setQuick(2, 2, ATA.getQuick(2, 2)+z*z);
		}
		ATA.setQuick(1, 0, ATA.getQuick(0, 1));
		ATA.setQuick(2, 0, ATA.getQuick(0, 2));
		ATA.setQuick(2, 1, ATA.getQuick(1, 2));
		EigenDecomposition eig = ATA.eig();
		if (eig.getEigenvalue(0)<EPS)
			throw new DegenerateCaseException("Points are at the same position, cannot fit line");
		if (eig.getEigenvalue(1)>eig.getEigenvalue(0)-EPS)
			throw new DegenerateCaseException("The two main directions are equally strong, line is ambiguous");
		return Line3.createFromDir(center, (Vector3) eig.getEigenvector(0));
	}
}
