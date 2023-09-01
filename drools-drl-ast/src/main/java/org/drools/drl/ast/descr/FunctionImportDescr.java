package org.drools.drl.ast.descr;

/**
 * A descriptor for imported static functions
 */
public class FunctionImportDescr extends ImportDescr {

    private static final long serialVersionUID = 510l;

    public FunctionImportDescr() {
    }

    public String toString() {
        return "import function " + this.getTarget();
    }

}
