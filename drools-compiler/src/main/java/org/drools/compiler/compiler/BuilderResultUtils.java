/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.compiler;

import org.kie.internal.jci.CompilationProblem;

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
                       .append(aProblem.getMessage())
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
