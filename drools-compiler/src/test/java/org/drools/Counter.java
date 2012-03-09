package org.drools;

public class Counter {
	int counterType = 0;

	public int getCounterType() {
		return counterType;
	}
	
	public Counter(final int counterType) {
		this.counterType = counterType;
	}
}
