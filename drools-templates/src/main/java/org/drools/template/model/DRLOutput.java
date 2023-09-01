package org.drools.template.model;

import java.io.StringWriter;

/**
 * This contains the DRL output that each piece of the parser spreadsheet will contribute to
 */
public class DRLOutput {

    private StringWriter writer;

    public void writeLine(final String line) {
        final StringBuffer buf = this.writer.getBuffer();
        buf.append(line);
        buf.append('\n');
    }

    public DRLOutput() {
        this.writer = new StringWriter();
    }

    /**
     * Return the rendered DRL so far
     */
    public String getDRL() {
        return this.writer.toString();
    }

    public String toString() {
        return getDRL();
    }

}
