package org.drools.lang.descr;

/**
 * This is the parent class function/method calls.
 */
public class DeclarativeInvokerDescr {

    private int               line;
    private int               column;
    private int               endLine;
    private int               endColumn;
    
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public int getEndColumn() {
		return endColumn;
	}
	public void setEndColumn(int endColumn) {
		this.endColumn = endColumn;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
    
    
	
}
