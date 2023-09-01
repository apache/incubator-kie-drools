package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.parser.DroolsError;

public class DescrBuildError extends DroolsError {
    private BaseDescr parentDescr;
    private BaseDescr descr;
    private Object    object;
    private String    message;
    private int[]     errorLines = new int[1];

    public DescrBuildError(final BaseDescr parentDescr,
                           final BaseDescr descr,
                           final Object object,
                           final String message) {
        super( descr.getResource() != null ? descr.getResource() : ( parentDescr != null ? parentDescr.getResource() : null ) );
        this.parentDescr = parentDescr;
        this.descr = descr;
        this.object = object;
        this.message = message;
        this.errorLines[0] = getLine();
    }

    @Override
    public String getNamespace() {
        return parentDescr != null ? parentDescr.getNamespace() : descr.getNamespace();
    }

    public BaseDescr getParentDescr() {
        return this.parentDescr;
    }

    public BaseDescr getDescr() {
        return this.descr;
    }

    public Object getObject() {
        return this.object;
    }

    public int[] getLines() {
        return this.errorLines;
    }

    /**
     * This will return the line number of the error, if possible
     * Otherwise it will be -1
     */
    public int getLine() {
        return this.descr != null ? this.descr.getLine() : -1;
    }

    public int getColumn() {
        return this.descr != null ? this.descr.getColumn() : -1;
    }

    public String getMessage() {
        return BuilderResultUtils.getProblemMessage( this.object, this.message, "\n" );
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder()
                .append( this.message )
                .append( " : " )
                .append( this.parentDescr )
                .append( "\n" );
        return BuilderResultUtils.appendProblems( this.object, builder ).toString();
    }
}
