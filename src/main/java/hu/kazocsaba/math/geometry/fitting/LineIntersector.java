package hu.kazocsaba.math.geometry.fitting;

import hu.kazocsaba.math.geometry.DegenerateCaseException;
import hu.kazocsaba.math.geometry.Line;
import hu.kazocsaba.math.geometry.Line2;
import hu.kazocsaba.math.geometry.Line3;
import hu.kazocsaba.math.matrix.Matrix;
import hu.kazocsaba.math.matrix.MatrixFactory;
import hu.kazocsaba.math.matrix.SingularityException;
import hu.kazocsaba.math.matrix.Vector;
import hu.kazocsaba.math.matrix.Vector2;
import hu.kazocsaba.math.matrix.Vector3;
import hu.kazocsaba.math.matrix.immutable.ImmutableMatrixFactory;
import java.util.List;

/**
 * Algorithm for finding the intersection point of a set of lines.
 * @author Kazó Csaba
 */
public class LineIntersector {
	
	/** Private constructor to prevent instantiation. */
	private LineIntersector() {}
	
	/**
	 * Finds the point that can be best described as the intersection of a set of lines.
	 * @param lines the collection of input lines
	 * @param dim the dimensionality of the task (e.g. 3 for 3D lines)
	 * @return the intersection point
	 * @throws IllegalArgumentException if the {@code dim} is invalid or doesn't match at least
	 * one of the lines
	 * @throws DegenerateCaseException if no intersection point could be computed
	 */
	public static Vector intersect(List<? extends Line> lines, int dim) {
		/*
		 * The algorithm we use is from {http://en.wikipedia.org/wiki/Line-line_intersection#n-line_intersection}.
		 * It writes the sum of squared distances of a variable point x, and sets its derivative to zero.
		 */
		Matrix a=MatrixFactory.createMatrix(dim, dim);
		Vector b=MatrixFactory.createVector(dim);
		Matrix i=ImmutableMatrixFactory.identity(dim);
		for (Line l: lines) {
			Vector p=l.getUnitDir();
			Matrix ivvt=i.minus(p.mul(p.transpose()));
			a.add(ivvt);
			b.add(ivvt.mul(l.getPoint()));
		}
		try {
			return (Vector)a.inverse().mul(b);
		} catch (SingularityException ex) {
			// I believe that this can only happen if there are no lines at all
			if (lines.isEmpty())
				throw new DegenerateCaseException("No lines");
			else
				throw new DegenerateCaseException("Intersection cannot be computed (coefficient matrix singular)");
		}
	}
	
	/**
	 * Finds the point that can be best described as the intersection of a set of 2D lines.
	 * @param lines the collection of input lines
	 * @return the intersection point
	 * @throws DegenerateCaseException if no intersection point could be computed
	 */
	public static Vector2 intersect2(List<Line2> lines) {
		return (Vector2) intersect(lines, 2);
	}
	
	/**
	 * Finds the point that can be best described as the intersection of a set of 3D lines.
	 * @param lines the collection of input lines
	 * @return the intersection point
	 * @throws DegenerateCaseException if no intersection point could be computed
	 */
	public static Vector3 intersect3(List<Line3> lines) {
		return (Vector3) intersect(lines, 3);
	}
}
