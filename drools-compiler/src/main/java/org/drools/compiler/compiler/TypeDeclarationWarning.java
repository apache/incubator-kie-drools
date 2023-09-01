package org.drools.compiler.compiler;

import org.drools.drl.parser.BaseKnowledgeBuilderResultImpl;
import org.kie.internal.builder.ResultSeverity;

public class TypeDeclarationWarning extends BaseKnowledgeBuilderResultImpl {
    private String message;
    private int[]  line;

    public TypeDeclarationWarning(final String message, final int line) {
        super(null);
        this.message = message;
        this.line = new int[] { line };
    }

    public int[] getLines() {
        return this.line;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return this.getMessage();
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.WARNING;
    }

}
