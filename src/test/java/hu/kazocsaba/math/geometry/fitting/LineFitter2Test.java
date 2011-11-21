package hu.kazocsaba.math.geometry.fitting;

import hu.kazocsaba.math.matrix.Vector2;
import java.util.List;
import hu.kazocsaba.math.geometry.Line2;
import hu.kazocsaba.math.matrix.immutable.ImmutableMatrixFactory;
import java.util.ArrayList;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kazó Csaba
 */
public class LineFitter2Test {
	private static final double EPS=1e-8;

	@Test
	public void testFit() {
		Line2 refLine=Line2.createFromDir(ImmutableMatrixFactory.createVector(3, 4), ImmutableMatrixFactory.createVector(-5, 2));
		
		Random rnd=new Random(726);
		
		List<Vector2> points=new ArrayList<Vector2>(50);
		for (int i=0; i<50; i++) points.add(refLine.getPointAt(rnd.nextDouble()));
		
		Line2 fitLine=LineFitter2.fit(points);
		
		assertEquals(1, Math.abs(refLine.getUnitDir().dot(fitLine.getUnitDir())), EPS);
		assertEquals(0, refLine.distance(fitLine.getPoint()), EPS);
	}
}
