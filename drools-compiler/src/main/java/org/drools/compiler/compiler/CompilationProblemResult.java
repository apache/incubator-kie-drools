package org.drools.compiler.compiler;

import org.drools.compiler.commons.jci.problems.CompilationProblem;

/**
 * An interface with the results containing {@link org.drools.compiler.commons.jci.problems.CompilationProblem}
 */
public interface CompilationProblemResult {

    /**
     * Returns summary message
     *
     * @return
     */
    String getSummary();

    /**
     * Returns build result object
     *
     * @return
     */
    Object getObject();

    /**
     * Appends compilation problems to summary message if object is an array of {@link CompilationProblem}
     *
     * @return summary message
     */
    default String getProblemMessage() {
        String summary = getSummary();
        Object object = getObject();
        if (object instanceof CompilationProblem[]) {
            final CompilationProblem[] problem = (CompilationProblem[]) object;
            for (int i = 0; i < problem.length; i++) {
                if (i != 0) {
                    summary = summary + "\n" + problem[i].getMessage();
                } else {
                    summary = summary + " " + problem[i].getMessage();
                }
            }
        }
        return summary;
    }

    /**
     * Appends compilation problems to builder if object is an array of {@link CompilationProblem}
     * or object itself if not
     *
     * @param builder message builder
     * @return builder
     */
    default StringBuilder appendProblems(StringBuilder builder) {
        Object object = getObject();
        if (object instanceof CompilationProblem[]) {
            final CompilationProblem[] problem = (CompilationProblem[]) object;
            for (CompilationProblem aProblem : problem) {
                builder.append("\t")
                        .append(aProblem)
                        .append("\n");
            }
        } else if (object != null) {
            builder.append(object);
        }
        return builder;
    }
}
