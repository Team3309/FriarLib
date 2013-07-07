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

package org.team3309.friarlib.motors;

import org.team3309.friarlib.filter.Filter;

import edu.wpi.first.wpilibj.SpeedController;

public class FilterableSpeedController implements SpeedController {
	
	private Filter mFilter = null;
	private SpeedController controller = null;

	public FilterableSpeedController(SpeedController motor, Filter filter) {
		this.controller = motor;
		this.mFilter = filter;
	}
	
	public void setFilter(Filter f){
		this.mFilter = f;
	}
	
	public void set(double x){
		controller.set(mFilter.update(x));
	}
	
	public void set(double x, byte syncGroup){
		controller.set(mFilter.update(x), syncGroup);
	}

	@Override
	public void pidWrite(double val) {
		set(val);
	}

	@Override
	public void disable() {
		controller.disable();
	}

	@Override
	public double get() {
		return controller.get();
	}
	
}
