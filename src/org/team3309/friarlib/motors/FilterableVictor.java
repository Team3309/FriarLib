package org.team3309.friarlib.motors;

import org.team3309.friarlib.filter.Filter;

import edu.wpi.first.wpilibj.Victor;

public class FilterableVictor extends Victor {
	
	private Filter mFilter = null;

	public FilterableVictor(int channel) {
		super(channel);
	}
	
	public FilterableVictor(int module, int channel){
		super(module, channel);
	}
	
	public void setFilter(Filter f){
		this.mFilter = f;
	}
	
	public void set(double x){
		super.set(mFilter.update(x));
	}
	
	public void set(double x, byte syncGroup){
		super.set(mFilter.update(x), syncGroup);
	}
	
}
