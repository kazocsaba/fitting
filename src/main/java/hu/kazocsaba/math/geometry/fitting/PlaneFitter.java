package hu.kazocsaba.math.geometry.fitting;

import hu.kazocsaba.math.geometry.DegenerateCaseException;
import hu.kazocsaba.math.geometry.Plane3;
import hu.kazocsaba.math.matrix.EigenDecomposition;
import hu.kazocsaba.math.matrix.Matrix3;
import hu.kazocsaba.math.matrix.MatrixFactory;
import hu.kazocsaba.math.matrix.Vector3;
import java.util.List;

/**
 * Algorithm for fitting a plane to a set of points.
 * @author Kazó Csaba
 */
public class PlaneFitter {
	private static final double EPS=1e-8;
	
	/** Private constructor to prevent instantiation. */
	private PlaneFitter() {}
	
	public static Plane3 fit3(List<? extends Vector3> points) {
		/*
		 * This algorithm first computes the centroid of the point set,
		 * and then transforms all points so that the centroid of the
		 * transformed point set be the origin. Then the normal of the
		 * plane is the singular vector belonging to the smallest singular
		 * value for this matrix:
		 * 
		 *   ( x1  y1  z1 )
		 *   ( x2  y2  z2 )          T
		 * A=( x3  y3  z3 )         A A=( sum(xi*xi)  sum(xi*yi)  sum(xi*zi) )
		 *   (    ...     )             ( sum(xi*yi)  sum(yi*yi)  sum(yi*zi) )
		 *   ( xn  yn  zn )             ( sum(xi*zi)  sum(yi*zi)  sum(zi*zi) )
		 * 
		 * For performance reasons, we use the fact that the required
		 * singular vector is the same as the eigenvector corresponding
		 * to the smallest eigenvalue for A'*A (prime stands for transposition)
		 * so we calculate that.
		 */
		if (points.isEmpty()) throw new DegenerateCaseException("No points");
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
		if (Math.abs(eig.getEigenvalue(1))<EPS)
			throw new DegenerateCaseException("Points are collinear, cannot fit plane");
		Vector3 normal=(Vector3)eig.getEigenvector(2);
		return Plane3.create(center, normal);
	}
}
