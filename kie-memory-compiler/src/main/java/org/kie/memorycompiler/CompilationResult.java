package org.kie.memorycompiler;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A CompilationResult represents the result of a compilation.
 * It includes errors (which failed the compilation) or warnings
 * (that can be ignored and do not affect the creation of the
 * class files)
 */
public final class CompilationResult {
    
    private final CompilationProblem[] errors;
    private final CompilationProblem[] warnings;
        
    public CompilationResult( final CompilationProblem[] pProblems ) {
        final Collection errorsColl = new ArrayList();
        final Collection warningsColl = new ArrayList();

        for (int i = 0; i < pProblems.length; i++) {
            final CompilationProblem problem = pProblems[i];
            if (problem.isError()) {
                errorsColl.add(problem);
            } else {
                warningsColl.add(problem);
            }
        }
        
        errors = new CompilationProblem[errorsColl.size()];
        errorsColl.toArray(errors);

        warnings = new CompilationProblem[warningsColl.size()];
        warningsColl.toArray(warnings);
    }
    
    public CompilationProblem[] getErrors() {
        final CompilationProblem[] res = new CompilationProblem[errors.length];
        System.arraycopy(errors, 0, res, 0, res.length);
        return res;
    }

    public CompilationProblem[] getWarnings() {
        final CompilationProblem[] res = new CompilationProblem[warnings.length];
        System.arraycopy(warnings, 0, res, 0, res.length);
        return res;
    }
}
