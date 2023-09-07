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

class ScoreGettersRecipeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new ScoreGettersRecipe())
                .parser(ScoreGettersRecipe.buildJavaParser());
    }

    @Test
    void bendableScore() {
        runTest("org.optaplanner.core.api.score.buildin.bendable.BendableScore",
                "BendableScore score = BendableScore.of(new int[] {1, 2}, new int[] {3, 4});",
                "int scoreLevelsSize = score.getLevelsSize();\n" +
                        "int hardScoreLevelsSize = score.getHardLevelsSize();\n" +
                        "int[] hardScores = score.getHardScores();\n" +
                        "int hardScore0 = score.getHardScore(0);\n" +
                        "int softScoreLevelsSize = score.getSoftLevelsSize();\n" +
                        "int[] softScores = score.getSoftScores();\n" +
                        "int softScore1 = score.getSoftScore(1);\n" +
                        "int initScore = score.getInitScore();\n",
                "int scoreLevelsSize = score.levelsSize();\n" +
                        "int hardScoreLevelsSize = score.hardLevelsSize();\n" +
                        "int[] hardScores = score.hardScores();\n" +
                        "int hardScore0 = score.hardScore(0);\n" +
                        "int softScoreLevelsSize = score.softLevelsSize();\n" +
                        "int[] softScores = score.softScores();\n" +
                        "int softScore1 = score.softScore(1);\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void bendableBigDecimalScore() {
        runTest("org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore",
                "BendableBigDecimalScore score = BendableBigDecimalScore.of(\n" +
                        "   new BigDecimal[] {BigDecimal.ONE},\n" +
                        "   new BigDecimal[] {BigDecimal.ONE, BigDecimal.TEN});",
                "int scoreLevelsSize = score.getLevelsSize();\n" +
                        "int hardScoreLevelsSize = score.getHardLevelsSize();\n" +
                        "BigDecimal[] hardScores = score.getHardScores();\n" +
                        "BigDecimal hardScore0 = score.getHardScore(0);\n" +
                        "int softScoreLevelsSize = score.getSoftLevelsSize();\n" +
                        "BigDecimal[] softScores = score.getSoftScores();\n" +
                        "BigDecimal softScore1 = score.getSoftScore(1);\n" +
                        "int initScore = score.getInitScore();\n",
                "int scoreLevelsSize = score.levelsSize();\n" +
                        "int hardScoreLevelsSize = score.hardLevelsSize();\n" +
                        "BigDecimal[] hardScores = score.hardScores();\n" +
                        "BigDecimal hardScore0 = score.hardScore(0);\n" +
                        "int softScoreLevelsSize = score.softLevelsSize();\n" +
                        "BigDecimal[] softScores = score.softScores();\n" +
                        "BigDecimal softScore1 = score.softScore(1);\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void bendableLongScore() {
        runTest("org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore",
                "BendableLongScore score = BendableLongScore.of(" +
                        "   new long[] {1L}, " +
                        "   new long[] {1L, 10L});",
                "int scoreLevelsSize = score.getLevelsSize();\n" +
                        "int hardScoreLevelsSize = score.getHardLevelsSize();\n" +
                        "long[] hardScores = score.getHardScores();\n" +
                        "long hardScore0 = score.getHardScore(0);\n" +
                        "int softScoreLevelsSize = score.getSoftLevelsSize();\n" +
                        "long[] softScores = score.getSoftScores();\n" +
                        "long softScore1 = score.getSoftScore(1);\n" +
                        "int initScore = score.getInitScore();\n",
                "int scoreLevelsSize = score.levelsSize();\n" +
                        "int hardScoreLevelsSize = score.hardLevelsSize();\n" +
                        "long[] hardScores = score.hardScores();\n" +
                        "long hardScore0 = score.hardScore(0);\n" +
                        "int softScoreLevelsSize = score.softLevelsSize();\n" +
                        "long[] softScores = score.softScores();\n" +
                        "long softScore1 = score.softScore(1);\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void hardMediumSoftScore() {
        runTest("org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore",
                "HardMediumSoftScore score = HardMediumSoftScore.of(1, 2, 3);",
                "int hardScore = score.getHardScore();\n" +
                        "int mediumScore = score.getMediumScore();\n" +
                        "int softScore = score.getSoftScore();\n" +
                        "int initScore = score.getInitScore();\n",
                "int hardScore = score.hardScore();\n" +
                        "int mediumScore = score.mediumScore();\n" +
                        "int softScore = score.softScore();\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void hardMediumSoftBigDecimalScore() {
        runTest("org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore",
                "HardMediumSoftBigDecimalScore score = HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN);",
                "BigDecimal hardScore = score.getHardScore();\n" +
                        "BigDecimal mediumScore = score.getMediumScore();\n" +
                        "BigDecimal softScore = score.getSoftScore();\n" +
                        "int initScore = score.getInitScore();\n",
                "BigDecimal hardScore = score.hardScore();\n" +
                        "BigDecimal mediumScore = score.mediumScore();\n" +
                        "BigDecimal softScore = score.softScore();\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void hardMediumSoftLongScore() {
        runTest("org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore",
                "HardMediumSoftLongScore score = HardMediumSoftLongScore.of(1L, 2L, 3L);",
                "long hardScore = score.getHardScore();\n" +
                        "long mediumScore = score.getMediumScore();\n" +
                        "long softScore = score.getSoftScore();\n" +
                        "int initScore = score.getInitScore();\n",
                "long hardScore = score.hardScore();\n" +
                        "long mediumScore = score.mediumScore();\n" +
                        "long softScore = score.softScore();\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void hardSoftScore() {
        runTest("org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore",
                "HardSoftScore score = HardSoftScore.of(1, 2);",
                "int hardScore = score.getHardScore();\n" +
                        "int softScore = score.getSoftScore();\n" +
                        "int initScore = score.getInitScore();\n",
                "int hardScore = score.hardScore();\n" +
                        "int softScore = score.softScore();\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void hardSoftBigDecimalScore() {
        runTest("org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore",
                "HardSoftBigDecimalScore score = HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ONE);",
                "BigDecimal hardScore = score.getHardScore();\n" +
                        "BigDecimal softScore = score.getSoftScore();\n" +
                        "int initScore = score.getInitScore();\n",
                "BigDecimal hardScore = score.hardScore();\n" +
                        "BigDecimal softScore = score.softScore();\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void hardSoftLongScore() {
        runTest("org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore",
                "HardSoftLongScore score = HardSoftLongScore.of(1L, 2L);",
                "long hardScore = score.getHardScore();\n" +
                        "long softScore = score.getSoftScore();\n" +
                        "int initScore = score.getInitScore();\n",
                "long hardScore = score.hardScore();\n" +
                        "long softScore = score.softScore();\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void simpleScore() {
        runTest("org.optaplanner.core.api.score.buildin.simple.SimpleScore",
                "SimpleScore score = SimpleScore.of(1);",
                "int value = score.getScore();\n" +
                        "int initScore = score.getInitScore();\n",
                "int value = score.score();\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void simpleBigDecimalScore() {
        runTest("org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore",
                "SimpleBigDecimalScore score = SimpleBigDecimalScore.of(BigDecimal.ONE);",
                "BigDecimal value = score.getScore();\n" +
                        "int initScore = score.getInitScore();\n",
                "BigDecimal value = score.score();\n" +
                        "int initScore = score.initScore();\n");
    }

    @Test
    void simpleLongScore() {
        runTest("org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore",
                "SimpleLongScore score = SimpleLongScore.of(1L);",
                "long value = score.getScore();\n" +
                        "int initScore = score.getInitScore();\n",
                "long value = score.score();\n" +
                        "int initScore = score.initScore();\n");
    }

    private void runTest(String scoreImplClassFqn, String scoreDeclaration, String before, String after) {
        rewriteRun(java(wrap(scoreImplClassFqn, scoreDeclaration, before), wrap(scoreImplClassFqn, scoreDeclaration, after)));
    }

    private static String wrap(String scoreImplClassFqn, String scoreDeclaration, String content) {
        return "import java.math.BigDecimal;\n" +
                "import " + scoreImplClassFqn + ";\n" +
                "\n" +
                "class Test {\n" +
                "    public static void main(String[] args) {\n" +
                scoreDeclaration.trim() + "\n" +
                content.trim() + "\n" +
                "    }" +
                "}\n";
    }

}
