package org.drools.compiler.compiler;

import org.drools.drl.parser.DroolsError;
import org.kie.internal.builder.KnowledgeBuilderResult;


/**
 *
 */
public class DroolsErrorWrapper extends DroolsError {
    
    KnowledgeBuilderResult backingProblem;
    private String namespace = "";
    
    public DroolsErrorWrapper (KnowledgeBuilderResult problem) {
        super(problem.getResource());
        this.backingProblem = problem;
        if (problem instanceof DroolsError) {
            namespace = ((DroolsError)problem).getNamespace();
        }
    }
    
    @Override
    public String getMessage() {
        return backingProblem.getMessage();
    }

    @Override
    public int[] getLines() {
        return backingProblem.getLines();
    }

    @Override
    public String getNamespace() {
        return namespace;
    }
}
