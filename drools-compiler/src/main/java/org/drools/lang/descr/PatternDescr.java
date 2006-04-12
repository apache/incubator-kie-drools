package org.drools.lang.descr;

import java.io.Serializable;

/**
 * This is the super type for all pattern AST nodes.
 */
public class PatternDescr implements Serializable {
	
    private static final long serialVersionUID = 8250082341310416977L;
    
    private int line;
	private int column;

	public void setLocation(int line, int column) {
		this.line   = line;
		this.column = column;
	}
	
	public int getLine() {
		return this.line;
	}
	
	public int getColumn() {
		return this.column;
	}
}
