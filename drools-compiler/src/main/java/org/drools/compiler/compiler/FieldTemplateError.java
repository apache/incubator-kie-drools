package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.parser.DroolsError;

public class FieldTemplateError extends DroolsError {
    private Package   pkg;
    private BaseDescr descr;
    private Object    object;
    private String    message;
    private int[]     line;
    private String    namespace;

    public FieldTemplateError(final Package pkg,
                              final BaseDescr descr,
                              final Object object,
                              final String message) {
        super(descr.getResource());
        this.namespace = pkg.getName();
        this.pkg = pkg;
        this.descr = descr;
        this.object = object;
        this.message = message;
        this.line = new int[] {this.descr.getLine()};
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public Package getPackage() {
        return this.pkg;
    }

    public BaseDescr getDescr() {
        return this.descr;
    }

    public Object getObject() {
        return this.object;
    }
    
    public int[] getLines() {
        return this.line;
    }

    /** 
     * This will return the line number of the error, if possible
     * Otherwise it will be -1
     */
    public int getLine() {
        return this.line[0];
    }

    public String getMessage() {
        return BuilderResultUtils.getProblemMessage( this.object, this.message );
    }

}
