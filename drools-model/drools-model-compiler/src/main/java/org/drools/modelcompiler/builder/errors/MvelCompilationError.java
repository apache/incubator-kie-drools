package org.drools.modelcompiler.builder.errors;

import org.drools.mvelcompiler.MvelCompilerException;
import org.kie.internal.jci.CompilationProblem;

public class MvelCompilationError implements CompilationProblem {

    MvelCompilerException exception;

    public MvelCompilationError(MvelCompilerException exception) {
        this.exception = exception;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public int getStartLine() {
        return 0;
    }

    @Override
    public int getStartColumn() {
        return 0;
    }

    @Override
    public int getEndLine() {
        return 0;
    }

    @Override
    public int getEndColumn() {
        return 0;
    }

    @Override
    public String getMessage() {
        return exception.toString();
    }
}
