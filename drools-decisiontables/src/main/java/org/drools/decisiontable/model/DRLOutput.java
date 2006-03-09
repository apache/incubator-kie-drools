package org.drools.decisiontable.model;

import java.io.StringWriter;
import java.io.Writer;

/**
 * This contains the DRL output that each piece of the parser spreadsheet will contribute to
 * 
 * @author Michael Neale
 *
 */
public class DRLOutput {
	
	private StringWriter writer;
	
	public void writeLine(String line) {
		writer.append(line);
		writer.append('\n');
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
