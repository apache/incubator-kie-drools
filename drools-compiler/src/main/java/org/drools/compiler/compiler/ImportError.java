package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.parser.DroolsError;

public class ImportError extends DroolsError {
    private final ImportDescr importDescr;
    private int[]  line;

    public ImportError(final ImportDescr importDescr, final int line) {
        super(importDescr.getResource());
        this.importDescr = importDescr;
        this.line = new int[] { line };
    }

    @Override
    public String getNamespace() {
        return importDescr.getNamespace();
    }

    public String getGlobal() {
        return importDescr.getTarget();
    }

    public int[] getLines() {
        return this.line;
    }

    public String getMessage() {
        return "Error importing : '" + getGlobal() + "'";
    }
    
    public String toString() {
        return getMessage();
    }

}
