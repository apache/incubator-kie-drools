package org.drools.examples.waltz;

import java.io.Serializable;

public class Stage implements Serializable {
	final public static int START = 0;

	final public static int DUPLICATE = 1;

	final public static int DETECT_JUNCTIONS = 2;
	
	final public static int FIND_INITIAL_BOUNDARY = 3;

	final public static int DONE = 10;

	private int value;

	public Stage(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String toString() {
		return "{Stage value=" + this.value + "}";
	}
}