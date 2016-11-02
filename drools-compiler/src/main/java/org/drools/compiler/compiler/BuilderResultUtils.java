package org.drools.compiler.compiler;

import org.drools.compiler.commons.jci.problems.CompilationProblem;

/**
 * Utility class for compilation results
 */
public final class BuilderResultUtils {

    private static final String DEFAULT_SEPARATOR = " ";

    private BuilderResultUtils() {
    }

    /**
     * Appends compilation problems to summary message if object is an array of {@link CompilationProblem}
     * separated with backspaces
     *
     * @param object  object with compilation results
     * @param summary summary message
     * @return summary message with changes
     */
    public static String getProblemMessage(Object object, String summary) {
        return getProblemMessage( object, summary, DEFAULT_SEPARATOR );
    }

    /**
     * Appends compilation problems to summary message if object is an array of {@link CompilationProblem}
     * with custom separator
     *
     * @param object    object with compilation results
     * @param summary   summary message
     * @param separator custom messages separator
     * @return summary message with changes
     */
    public static String getProblemMessage(Object object, String summary, String separator) {
        if (object instanceof CompilationProblem[]) {
            return fillSummary( (CompilationProblem[]) object, summary, separator );
        }
        return summary;
    }

    /**
     * Appends compilation problems to builder if object is an array of {@link CompilationProblem}
     * or object itself if not
     *
     * @param object  object with compilation results
     * @param builder message builder
     * @return builder
     */
    public static StringBuilder appendProblems(Object object, StringBuilder builder) {
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

    private static String fillSummary(CompilationProblem[] problem, String summary, String separator) {
        StringBuilder builder = new StringBuilder(summary)
                .append(DEFAULT_SEPARATOR)
                .append(problem[0].getMessage());
        for (int i = 1; i < problem.length; i++) {
            builder.append(separator)
                   .append(problem[i].getMessage());
        }
        return builder.toString();
    }
}
