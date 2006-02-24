package org.drools.examples.waltz;

public class Edge {
	private int p1;

	private int p2;

	private boolean joined;

	private String label;

	private String plotted;
	
	final public static String NIL = "empty";
	
	final public static String TRUE = "true";

	final public static String FALSE = "false";

	final public static String B = "B";
	
	final public static String PLUS = "+";
	
	final public static String MINUS = "-";
	
	public Edge(int p1, int p2, boolean joined, String label, String plotted) {
		this.p1 = p1;
		this.p2 = p2;
		this.joined = joined;
		this.label = label;
		this.plotted = plotted;
	}

	public int getP1() {
		return this.p1;
	}

	public void setP1(int p1) {
		this.p1 = p1;
	}

	public int getP2() {
		return this.p2;
	}

	public void setP2(int p2) {
		this.p2 = p2;
	}

	public String toString() {
		return "{Edge p1=" + this.p1 + ", p2=" + this.p2 + ", joined="
				+ this.joined + ", label=" + this.label + ", plotted"
				+ this.plotted + "}";
	}

	public boolean isJoined() {
		return this.joined;
	}

	public void setJoined(boolean joined) {
		this.joined = joined;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPlotted() {
		return this.plotted;
	}

	public void setPlotted(String plotted) {
		this.plotted = plotted;
	}
}
