package hu.kazocsaba.math.geometry.fitting;

import hu.kazocsaba.math.geometry.DegenerateCaseException;
import java.util.List;
import hu.kazocsaba.math.geometry.Plane3;
import hu.kazocsaba.math.matrix.MatrixFactory;
import hu.kazocsaba.math.matrix.Vector3;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kazó Csaba
 */
public class PlaneFitterTest {
	
	@Test
	public void test() {
		Vector3[] pts=new Vector3[1000];
		
		for (int i=0; i<pts.length; i++) {
			double angle=i*2*Math.PI/pts.length;
			pts[i]=MatrixFactory.createVector(Math.cos(angle), Math.sin(angle), 0);
		}
		
		Plane3 plane=PlaneFitter.fit3(Arrays.asList(pts));
		
		assertEquals("Expected normal: (0, 0, 1); actual: "+plane.getNormal(), 1, Math.abs(plane.getNormal().getZ()), 1e-6);
	}
	
	@Test
	public void test2() {
		Vector3[] pts=new Vector3[1000];
		
		Vector3 refPoint=MatrixFactory.createVector(1, 0, 1);
		Vector3 refNormal=MatrixFactory.createVector(-3, 2, 4.6).normalized();
		
		Vector3 dir1=refNormal.cross(MatrixFactory.createVector(1, 0, 0));
		Vector3 dir2=refNormal.cross(dir1);
		
		Random rnd=new Random(23479625L);
		for (int i=0; i<pts.length; i++) {
			pts[i]=refPoint.plus(dir1.times(rnd.nextDouble()*20)).plus(dir2.times(rnd.nextDouble()*20)).plus(refNormal.times(rnd.nextDouble()-.5));
		}
		
		Plane3 plane=PlaneFitter.fit3(Arrays.asList(pts));
		
		assertEquals("Expected normal: "+refNormal+"; actual: "+plane.getNormal().normalized(), 1, Math.abs(refNormal.dot(plane.getNormal().normalized())), 1e-6);
	}
	
	@Test
	public void testDegenerate() {
		try {
			Plane3 plane=PlaneFitter.fit3(Collections.<Vector3>emptyList());
			fail(String.format("Plane(%s, %s) returned for empty point set", plane.getPoint(), plane.getNormal()));
		} catch (DegenerateCaseException e) {}
		try {
			List<Vector3> points=Arrays.asList(MatrixFactory.createVector(0,0,0), MatrixFactory.createVector(1,0,0));
			Plane3 plane=PlaneFitter.fit3(points);
			fail(String.format("Plane(%s, %s) returned for two points %s", plane.getPoint(), plane.getNormal(), points));
		} catch (DegenerateCaseException e) {
		}
	}
}
