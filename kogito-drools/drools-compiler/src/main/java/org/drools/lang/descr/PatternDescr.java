package org.drools.lang.descr;

public class PatternDescr {
	
	private int line;
	private int column;

	public void setLocation(int line, int column) {
		this.line   = line;
		this.column = column;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}
}
