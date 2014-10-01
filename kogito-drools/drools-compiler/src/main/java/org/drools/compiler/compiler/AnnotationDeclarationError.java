package org.drools.compiler.compiler;

import org.drools.compiler.lang.descr.AnnotationDescr;

public class AnnotationDeclarationError extends DroolsError {

    private String errorMessage;
    private int[]  line;
    private String namespace;

    public AnnotationDeclarationError(AnnotationDescr annotationDescr, String errorMessage) {
        super(annotationDescr.getResource());
        this.errorMessage = errorMessage;
        this.line = new int[0];
        this.namespace = annotationDescr.getNamespace();
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public int[] getLines() {
        return this.line;
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

}
