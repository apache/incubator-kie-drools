package org.drools.compiler.compiler;

import org.kie.internal.builder.KnowledgeBuilderResult;

public class DroolsWarningWrapper extends DroolsWarning {

    KnowledgeBuilderResult backingProblem;

    public DroolsWarningWrapper (KnowledgeBuilderResult problem) {
        super(problem.getResource());
        this.backingProblem = problem;
    }

    @Override
    public String getMessage() {
        return backingProblem.getMessage();
    }

    @Override
    public int[] getLines() {
        return backingProblem.getLines();
    }
}
