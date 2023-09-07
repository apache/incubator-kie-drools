/*
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

package org.optaplanner.migration.v8;

import static org.openrewrite.java.Assertions.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

class ScoreManagerMethodsRecipeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new ScoreManagerMethodsRecipe())
                .parser(ScoreManagerMethodsRecipe.buildJavaParser());
    }

    @Test
    void summary() {
        runTest("String summary = scoreManager.getSummary(solution);",
                "String summary = scoreManager.explain(solution, SolutionUpdatePolicy.UPDATE_SCORE_ONLY).getSummary();");
    }

    @Test
    void explain() {
        runTest("ScoreExplanation explanation = scoreManager.explainScore(solution);",
                "ScoreExplanation explanation = scoreManager.explain(solution, SolutionUpdatePolicy.UPDATE_SCORE_ONLY);");
    }

    @Test
    void update() {
        runTest("Object score = scoreManager.updateScore(solution);",
                "Object score = scoreManager.update(solution, SolutionUpdatePolicy.UPDATE_SCORE_ONLY);");
    }

    private void runTest(String before, String after) {
        rewriteRun(java(
                wrap(before, false),
                wrap(after, true)));
    }

    private static String wrap(String content, boolean addImport) {
        return "import org.optaplanner.core.api.score.ScoreManager;\n" +
                (addImport ? "import org.optaplanner.core.api.solver.SolutionUpdatePolicy;\n" : "") +
                "import org.optaplanner.core.api.score.ScoreExplanation;\n" +
                "import org.optaplanner.core.api.solver.SolverFactory;\n" +
                "\n" +
                "class Test {\n" +
                "    public static void main(String[] args) {\n" +
                "       SolverFactory solverFactory = SolverFactory.create(null);\n" +
                "       ScoreManager scoreManager = ScoreManager.create(solverFactory);\n" +
                "       Object solution = null;\n" +
                "       " + content.trim() + "\n" +
                "    }" +
                "}\n";
    }

}
