package org.team3309.friarlib.test.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.team3309.friarlib.util.Latch;

public class LatchTest {
	
	private Latch latch;

	@Before
	public void setUp() throws Exception {
		latch = new Latch();
	}

	@Test
	public void testUpdate() {
		assertFalse(latch.update(false));
		assertTrue(latch.update(true));
		assertFalse(latch.update(true));
		assertTrue(latch.update(false));
		assertFalse(latch.update(false));
	}

}
