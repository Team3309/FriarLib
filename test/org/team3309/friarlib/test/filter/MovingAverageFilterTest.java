/*
 * Copyright (c) 2013, FRC Team 3309 All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
