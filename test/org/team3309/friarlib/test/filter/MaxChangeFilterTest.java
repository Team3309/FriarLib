package org.team3309.friarlib.test.filter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.team3309.friarlib.filter.MaxChangeFilter;

public class MaxChangeFilterTest {
	
	private MaxChangeFilter filter = null;

	@Before
	public void setUp() throws Exception {
		filter = new MaxChangeFilter(10);
	}

	@Test
	public void testMaxChangeFilter() {
		assertNotNull(new MaxChangeFilter(10));
		assertNotNull(new MaxChangeFilter(-5));
	}

	@Test
	public void testSetMaxChange() {
		filter.setMaxChange(5);
		assertEquals(5, filter.getMaxChange(), 0);
		filter.setMaxChange(-1);
		assertEquals(1, filter.getMaxChange(), 0);
		filter.setMaxChange(10);
		assertEquals(10, filter.getMaxChange(), 0);
	}

	@Test
	public void testGetMaxChange() {
		assertEquals(filter.getMaxChange(), 10, 0);
	}

	@Test
	public void testUpdate() {
		filter.update(0);
		assertEquals(10, filter.update(200), 0);
		
		filter.update(0);
		assertEquals(-10, filter.update(-100), 0);
	}

}
