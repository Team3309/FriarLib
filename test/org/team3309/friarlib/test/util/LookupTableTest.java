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

package org.team3309.friarlib.test.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.team3309.friarlib.util.LookupTable;

public class LookupTableTest {

    LookupTable table = null;

    @Before
    public void setup() {
        table = new LookupTable(LookupTable.Interpolator.kLinear);
    }

    @Test
    public void linear1() {
        table.put(0, 0);
        table.put(10, 10);
        assertEquals(5, table.get(5), 0);
    }

    @Test
    public void linear2() {
        table.put(5, 10);
        assertEquals(10, table.get(5), 0);
    }

    @Test
    public void linear3() {
        table.put(5, 0);
        table.put(10, 2);
        table.put(15, 4);
        assertEquals(1, table.get(7.5), 0);
        assertEquals(3, table.get(12.5), 0);
    }

    @Test
    public void none1() {
        table.setInterpolator(LookupTable.Interpolator.kNone);
        table.put(0, 0);
        table.put(10, 100);
        assertEquals(0, table.get(0), 0);
        assertEquals(0, table.get(3), 0);
        assertEquals(0, table.get(5), 0);
        assertEquals(100, table.get(7), 0);
        assertEquals(100, table.get(10), 0);
    }
}
