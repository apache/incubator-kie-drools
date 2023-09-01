package org.drools.compiler.kie.builder.impl;

/**
 * This class is intended to adapt the CompilationProblems produced by the in memory java compiler
 * to the one defined in kie-internal API
 */
public class CompilationProblemAdapter implements org.kie.internal.jci.CompilationProblem {

    private final org.kie.memorycompiler.CompilationProblem delegate;

    public CompilationProblemAdapter( org.kie.memorycompiler.CompilationProblem delegate ) {
        this.delegate = delegate;
    }

    @Override
    public boolean isError() {
        return delegate.isError();
    }

    @Override
    public String getFileName() {
        return delegate.getFileName();
    }

    @Override
    public int getStartLine() {
        return delegate.getStartLine();
    }

    @Override
    public int getStartColumn() {
        return delegate.getStartColumn();
    }

    @Override
    public int getEndLine() {
        return delegate.getEndLine();
    }

    @Override
    public int getEndColumn() {
        return delegate.getEndColumn();
    }

    @Override
    public String getMessage() {
        return delegate.getMessage();
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
