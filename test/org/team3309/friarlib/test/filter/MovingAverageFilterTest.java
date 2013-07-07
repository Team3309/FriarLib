package org.team3309.friarlib.test.filter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.team3309.friarlib.filter.MovingAverageFilter;

public class MovingAverageFilterTest {

	private MovingAverageFilter filter = null;

	@Before
	public void setUp() throws Exception {
		filter = new MovingAverageFilter(3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMovingAverageFilterException() {
		assertNotNull(new MovingAverageFilter(1));
		new MovingAverageFilter(-1);
	}
	
	@Test
	public void testMovingAverageFilter(){
		MovingAverageFilter f = new MovingAverageFilter(3, 2);
		assertNotNull(f);
		assertEquals(2, f.update(2), 0);
	}

	@Test
	public void testUpdate() {
		double avg;
		filter.update(0);
		filter.update(0);
		
		avg = filter.update(0);
		assertEquals(0, avg, 0);
		
		avg = filter.update(1);
		assertEquals((double) 1 / 3, avg, 0);
		
		avg = filter.update(1);
		assertEquals((double) 2 / 3, avg, 0);
		
		avg = filter.update(1);
		assertEquals((double) 1, avg, 0);
		
		avg = filter.update(3);
		assertEquals((double) (1+1+3)/3, avg, 0);
		
		avg = filter.update(5);
		assertEquals((double) (1+3+5)/3, avg, 0);
	}
	
	@Test
	public void testGet(){
		double avg = filter.update(42);
		assertEquals(avg, filter.get(), 0);
	}

}
