package com.g2dev.output;

import java.util.List;

public abstract class Output {
	protected boolean forceInput;

	public abstract void process(List<String[]> batch);

	public void setForceInput(boolean forceInput) {
		this.forceInput = forceInput;
		
	}
}
