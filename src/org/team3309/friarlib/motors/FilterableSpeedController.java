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
