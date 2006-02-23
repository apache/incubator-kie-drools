package org.drools.examples.waltz;

public class Line {
	private int p1;

	private int p2;

	public Line(int p1, int p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	protected int getP1() {
		return this.p1;
	}

	protected void setP1(int p1) {
		this.p1 = p1;
	}

	protected int getP2() {
		return this.p2;
	}

	protected void setP2(int p2) {
		this.p2 = p2;
	}

	public String toString() {
		return "{Line p1=" + this.p1 + ", p2=" + this.p2 + "}";
	}
}
