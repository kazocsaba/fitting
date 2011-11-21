package hu.kazocsaba.math.geometry.fitting;

import java.util.Map;
import java.util.HashMap;
import hu.kazocsaba.math.matrix.Vector3;
import hu.kazocsaba.math.geometry.Line3;
import hu.kazocsaba.math.geometry.DegenerateCaseException;
import hu.kazocsaba.math.matrix.Vector2;
import java.util.List;
import hu.kazocsaba.math.geometry.Line2;
import hu.kazocsaba.math.matrix.MatrixFactory;
import hu.kazocsaba.math.matrix.immutable.ImmutableMatrixFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kazó Csaba
 */
public class LineFitterTest {
	private static final double EPS=1e-8;

	@Test
	public void testFit() {
		Line2 refLine=Line2.createFromDir(ImmutableMatrixFactory.createVector(3, 4), ImmutableMatrixFactory.createVector(-5, 2));
		
		Random rnd=new Random(726);
		
		List<Vector2> points=new ArrayList<Vector2>(50);
		for (int i=0; i<50; i++) points.add(refLine.getPointAt(rnd.nextDouble()));
		
		Line2 fitLine=LineFitter.fit2(points);
		
		assertEquals(1, Math.abs(refLine.getUnitDir().dot(fitLine.getUnitDir())), EPS);
		assertEquals(0, refLine.distance(fitLine.getPoint()), EPS);
	}
	
	public void testDegenerate() {
		Map<String, List<Vector2>> inputs=new HashMap<String, List<Vector2>>();
		// single point duplicated
		inputs.put("duplicated point (2, -4)", Collections.nCopies(10, MatrixFactory.createVector(2, -4)));
		// equilateral triangle
		inputs.put("equilateral triangle (0, 0) - (1, 0) - (1/2, sqrt(3)/2)", Arrays.asList(MatrixFactory.createVector(0, 0), MatrixFactory.createVector(1, 0), MatrixFactory.createVector(.5, Math.sqrt(3)/2)));
		// square
		inputs.put("unit square (0, 0)-(1, 1)", Arrays.asList(MatrixFactory.createVector(0, 0), MatrixFactory.createVector(1, 0), MatrixFactory.createVector(1, 1), MatrixFactory.createVector(0, 1)));
		
		for (Map.Entry<String, List<Vector2>> input: inputs.entrySet()) {
			try {
				Line2 line=LineFitter.fit2(input.getValue());
				fail(String.format("Degenerate case: %s; got line %s - %s",input.getKey(), line.getPoint(), line.getDir()));
			} catch (DegenerateCaseException e) {}
		}
	}
	
	@Test
	public void testFit3() {
		Line3 refLine=Line3.createFromDir(ImmutableMatrixFactory.createVector(3, 4, 1), ImmutableMatrixFactory.createVector(-5, 2, 3));
		
		Random rnd=new Random(726);
		
		List<Vector3> points=new ArrayList<Vector3>(50);
		for (int i=0; i<50; i++) points.add(refLine.getPointAt(rnd.nextDouble()));
		
		Line3 fitLine=LineFitter.fit3(points);
		
		assertEquals(1, Math.abs(refLine.getUnitDir().dot(fitLine.getUnitDir())), EPS);
		assertEquals(0, refLine.distance(fitLine.getPoint()), EPS);
	}
	
	public void testDegenerate3() {
		Map<String, List<Vector3>> inputs=new HashMap<String, List<Vector3>>();
		// single point duplicated
		inputs.put("duplicated point (2, -4, 1)", Collections.nCopies(10, MatrixFactory.createVector(2, -4, 1)));
		// equilateral triangle
		inputs.put("equilateral triangle (0, 0, 0) - (1, 0, 0), (1/2, sqrt(3)/2, 0)", Arrays.asList(MatrixFactory.createVector(0, 0, 0), MatrixFactory.createVector(1, 0, 0), MatrixFactory.createVector(.5, Math.sqrt(3)/2, 0)));
		// square
		inputs.put("unit square (0, 0, 0) - (1, 1, 0)", Arrays.asList(MatrixFactory.createVector(0, 0, 0), MatrixFactory.createVector(1, 0, 0), MatrixFactory.createVector(1, 1, 0), MatrixFactory.createVector(0, 1, 0)));
		// cube
		inputs.put("unit cube (0, 0, 0) - (1, 1, 1)", Arrays.asList(MatrixFactory.createVector(0, 0, 0), MatrixFactory.createVector(1, 0, 0), MatrixFactory.createVector(1, 1, 0), MatrixFactory.createVector(0, 1, 0), MatrixFactory.createVector(0, 0, 1), MatrixFactory.createVector(1, 0, 1), MatrixFactory.createVector(1, 1, 1), MatrixFactory.createVector(0, 1, 1)));
	
		for (Map.Entry<String, List<Vector3>> input: inputs.entrySet()) {
			try {
				Line3 line=LineFitter.fit3(input.getValue());
				fail(String.format("Degenerate case: %s; got line %s - %s",input.getKey(), line.getPoint(), line.getDir()));
			} catch (DegenerateCaseException e) {}
		}
	}
}
