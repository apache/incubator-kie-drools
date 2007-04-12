package org.apache.commons.jci.compilers;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.jci.problems.CompilationProblem;

public final class CompilationResult {

    private final CompilationProblem[] errors;
    private final CompilationProblem[] warnings;

    public CompilationResult(final CompilationProblem[] pProblems) {
        final Collection errorsColl = new ArrayList();
        final Collection warningsColl = new ArrayList();

        for ( int i = 0; i < pProblems.length; i++ ) {
            final CompilationProblem problem = pProblems[i];
            if ( problem.isError() ) {
                errorsColl.add( problem );
            } else {
                warningsColl.add( problem );
            }
        }

        this.errors = new CompilationProblem[errorsColl.size()];
        errorsColl.toArray( this.errors );

        this.warnings = new CompilationProblem[warningsColl.size()];
        warningsColl.toArray( this.warnings );
    }

    public CompilationProblem[] getErrors() {
        return this.errors;
    }

    public CompilationProblem[] getWarnings() {
        return this.warnings;
    }
}
