package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.base.rule.TypeDeclaration;
import org.drools.drl.parser.DroolsError;

public class TypeDeclarationError extends DroolsError {
    private String errorMessage;
    private int[]  line;
    private String namespace;

    public TypeDeclarationError(BaseDescr typeDescr, String errorMessage) {
        super(typeDescr.getResource());
        this.errorMessage = errorMessage;
        this.line = new int[] { typeDescr.getLine() };
        this.namespace = typeDescr.getNamespace();
    }

    public TypeDeclarationError(TypeDeclaration typeDeclaration, String errorMessage) {
        super(typeDeclaration.getResource());
        this.errorMessage = errorMessage;
        this.line = new int[0];
        this.namespace = typeDeclaration.getNamespace();
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public int[] getLines() {
        return this.line;
    }

    public String getMessage() {
        return this.errorMessage;
    }
    
    public String toString() {
        return this.getMessage();
    }

}
