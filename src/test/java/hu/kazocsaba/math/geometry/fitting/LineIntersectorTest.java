package hu.kazocsaba.math.geometry.fitting;

import java.util.List;
import java.util.Random;
import hu.kazocsaba.math.matrix.MatrixFactory;
import hu.kazocsaba.math.matrix.Vector3;
import hu.kazocsaba.math.geometry.Line3;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kazó Csaba
 */
public class LineIntersectorTest {
	
	@Test
	public void test() {
		Random rnd=new Random(3054637L);
		Vector3 center=MatrixFactory.createVector(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble());
		int n=10;
		List<Line3> lines=new ArrayList<Line3>();
		for (int i=0; i<n; i++)
			lines.add(Line3.createFromTwoPoints(center, MatrixFactory.createVector(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble())));
		for (int i=0; i<n; i++)
			lines.add(Line3.createFromTwoPoints(MatrixFactory.createVector(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble()), center));
		
		assertEquals(0, LineIntersector.intersect3(lines).error(center), 1e-5);
	}
}
