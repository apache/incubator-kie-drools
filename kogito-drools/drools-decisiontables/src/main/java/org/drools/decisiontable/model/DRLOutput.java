package org.drools.decisiontable.model;

import java.io.StringWriter;


/**
 * This contains the DRL output that each piece of the parser spreadsheet will contribute to
 * 
 * @author Michael Neale
 *
 */
public class DRLOutput {
	
	private StringWriter writer;
	
	public void writeLine(String line) {
        StringBuffer buf = writer.getBuffer();
		buf.append( line);
		buf.append('\n');
	}
	
	public DRLOutput() {
		this.writer = new StringWriter();
	}
	
	/** Return the rendered DRL so far */
	public String getDRL() {
		return writer.toString();
	}

	public String toString() {
		return getDRL();
	}
	
}
