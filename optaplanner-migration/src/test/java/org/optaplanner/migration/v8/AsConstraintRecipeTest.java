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

class AsConstraintRecipeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new AsConstraintRecipe())
                .parser(AsConstraintRecipe.buildJavaParser());
    }

    // ************************************************************************
    // Uni
    // ************************************************************************

    @Test
    void uniPenalizeName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalize(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniPenalizeId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalize(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniPenalizeConfigurableName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurable(\"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurable()\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniPenalizeConfigurableId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurable(\"My package\", \"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurable()\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniPenalizeNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalize(\"My constraint\", HardSoftScore.ONE_HARD, (a) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD, (a) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniPenalizeIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalize(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD, (a) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniPenalizeConfigurableNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurable(\"My constraint\", (a) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurable((a) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniPenalizeConfigurableIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurable(\"My package\", \"My constraint\", (a) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurable((a) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniPenalizeNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeLong(HardSoftLongScore.ONE_HARD, (a) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniPenalizeIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeLong(HardSoftLongScore.ONE_HARD, (a) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniPenalizeConfigurableNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurableLong(\"My constraint\", (a) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurableLong((a) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniPenalizeConfigurableIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurableLong(\"My package\", \"My constraint\", (a) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurableLong((a) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniPenalizeNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniPenalizeIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniPenalizeConfigurableNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal(\"My constraint\", (a) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal((a) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniPenalizeConfigurableIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal(\"My package\", \"My constraint\", (a) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal((a) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniRewardName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .reward(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniRewardId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .reward(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniRewardConfigurableName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurable(\"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurable()\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniRewardConfigurableId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurable(\"My package\", \"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurable()\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniRewardNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .reward(\"My constraint\", HardSoftScore.ONE_HARD, (a) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD, (a) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniRewardIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .reward(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD, (a) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniRewardConfigurableNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurable(\"My constraint\", (a) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurable((a) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniRewardConfigurableIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurable(\"My package\", \"My constraint\", (a) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurable((a) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniRewardNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardLong(HardSoftLongScore.ONE_HARD, (a) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniRewardIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardLong(HardSoftLongScore.ONE_HARD, (a) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniRewardConfigurableNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurableLong(\"My constraint\", (a) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurableLong((a) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniRewardConfigurableIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurableLong(\"My package\", \"My constraint\", (a) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurableLong((a) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniRewardNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniRewardIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniRewardConfigurableNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurableBigDecimal(\"My constraint\", (a) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurableBigDecimal((a) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniRewardConfigurableIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurableBigDecimal(\"My package\", \"My constraint\", (a) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .rewardConfigurableBigDecimal((a) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniImpactName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .impact(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniImpactId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .impact(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniImpactNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .impact(\"My constraint\", HardSoftScore.ONE_HARD, (a) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD, (a) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniImpactIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .impact(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD, (a) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniImpactNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .impactLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .impactLong(HardSoftLongScore.ONE_HARD, (a) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniImpactIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .impactLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .impactLong(HardSoftLongScore.ONE_HARD, (a) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void uniImpactNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .impactBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .impactBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void uniImpactIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .impactBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .impactBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    // ************************************************************************
    // Bi
    // ************************************************************************

    @Test
    void biPenalizeName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biPenalizeId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biPenalizeConfigurableName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable()\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biPenalizeConfigurableId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My package\", \"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable()\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biPenalizeNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My constraint\", HardSoftScore.ONE_HARD, (a, b) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD, (a, b) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biPenalizeIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a, b) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD, (a, b) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biPenalizeConfigurableNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My constraint\", (a, b) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable((a, b) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biPenalizeConfigurableIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My package\", \"My constraint\", (a, b) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable((a, b) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biPenalizeNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a, b) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(HardSoftLongScore.ONE_HARD, (a, b) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biPenalizeIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a, b) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(HardSoftLongScore.ONE_HARD, (a, b) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biPenalizeConfigurableNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong(\"My constraint\", (a, b) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong((a, b) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biPenalizeConfigurableIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong(\"My package\", \"My constraint\", (a, b) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong((a, b) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biPenalizeNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biPenalizeIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biPenalizeConfigurableNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal(\"My constraint\", (a, b) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal((a, b) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biPenalizeConfigurableIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal(\"My package\", \"My constraint\", (a, b) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal((a, b) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biRewardName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biRewardId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biRewardConfigurableName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable()\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biRewardConfigurableId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My package\", \"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable()\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biRewardNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My constraint\", HardSoftScore.ONE_HARD, (a, b) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD, (a, b) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biRewardIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a, b) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD, (a, b) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biRewardConfigurableNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My constraint\", (a, b) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable((a, b) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biRewardConfigurableIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My package\", \"My constraint\", (a, b) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable((a, b) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biRewardNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a, b) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(HardSoftLongScore.ONE_HARD, (a, b) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biRewardIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a, b) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(HardSoftLongScore.ONE_HARD, (a, b) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biRewardConfigurableNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong(\"My constraint\", (a, b) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong((a, b) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biRewardConfigurableIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong(\"My package\", \"My constraint\", (a, b) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong((a, b) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biRewardNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biRewardIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biRewardConfigurableNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal(\"My constraint\", (a, b) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal((a, b) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biRewardConfigurableIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal(\"My package\", \"My constraint\", (a, b) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal((a, b) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biImpactName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biImpactId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biImpactNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My constraint\", HardSoftScore.ONE_HARD, (a, b) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD, (a, b) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biImpactIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a, b) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD, (a, b) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biImpactNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a, b) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(HardSoftLongScore.ONE_HARD, (a, b) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biImpactIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a, b) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(HardSoftLongScore.ONE_HARD, (a, b) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void biImpactNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void biImpactIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    // ************************************************************************
    // Tri
    // ************************************************************************

    @Test
    void triPenalizeName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triPenalizeId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triPenalizeConfigurableName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable()\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triPenalizeConfigurableId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My package\", \"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable()\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triPenalizeNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My constraint\", HardSoftScore.ONE_HARD, (a, b, c) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD, (a, b, c) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triPenalizeIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a, b, c) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD, (a, b, c) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triPenalizeConfigurableNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My constraint\", (a, b, c) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable((a, b, c) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triPenalizeConfigurableIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My package\", \"My constraint\", (a, b, c) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable((a, b, c) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triPenalizeNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triPenalizeIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triPenalizeConfigurableNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong(\"My constraint\", (a, b, c) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong((a, b, c) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triPenalizeConfigurableIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong(\"My package\", \"My constraint\", (a, b, c) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong((a, b, c) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triPenalizeNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triPenalizeIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triPenalizeConfigurableNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal(\"My constraint\", (a, b, c) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal((a, b, c) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triPenalizeConfigurableIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal(\"My package\", \"My constraint\", (a, b, c) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal((a, b, c) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triRewardName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triRewardId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triRewardConfigurableName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable()\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triRewardConfigurableId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My package\", \"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable()\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triRewardNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My constraint\", HardSoftScore.ONE_HARD, (a, b, c) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD, (a, b, c) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triRewardIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a, b, c) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD, (a, b, c) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triRewardConfigurableNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My constraint\", (a, b, c) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable((a, b, c) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triRewardConfigurableIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My package\", \"My constraint\", (a, b, c) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable((a, b, c) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triRewardNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triRewardIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triRewardConfigurableNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong(\"My constraint\", (a, b, c) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong((a, b, c) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triRewardConfigurableIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong(\"My package\", \"My constraint\", (a, b, c) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong((a, b, c) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triRewardNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triRewardIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triRewardConfigurableNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal(\"My constraint\", (a, b, c) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal((a, b, c) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triRewardConfigurableIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal(\"My package\", \"My constraint\", (a, b, c) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal((a, b, c) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triImpactName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triImpactId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triImpactNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My constraint\", HardSoftScore.ONE_HARD, (a, b, c) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD, (a, b, c) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triImpactIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a, b, c) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD, (a, b, c) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triImpactNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triImpactIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(HardSoftLongScore.ONE_HARD, (a, b, c) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void triImpactNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void triImpactIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    // ************************************************************************
    // Quad
    // ************************************************************************

    @Test
    void quadPenalizeName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadPenalizeId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadPenalizeConfigurableName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable()\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadPenalizeConfigurableId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My package\", \"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable()\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadPenalizeNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My constraint\", HardSoftScore.ONE_HARD, (a, b, c, d) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD, (a, b, c, d) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadPenalizeIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a, b, c, d) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalize(HardSoftScore.ONE_HARD, (a, b, c, d) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadPenalizeConfigurableNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My constraint\", (a, b, c, d) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable((a, b, c, d) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadPenalizeConfigurableIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable(\"My package\", \"My constraint\", (a, b, c, d) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurable((a, b, c, d) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadPenalizeNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadPenalizeIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeLong(HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadPenalizeConfigurableNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong(\"My constraint\", (a, b, c, d) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong((a, b, c, d) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadPenalizeConfigurableIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong(\"My package\", \"My constraint\", (a, b, c, d) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableLong((a, b, c, d) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadPenalizeNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN)\n"
                        +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadPenalizeIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN)\n"
                        +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadPenalizeConfigurableNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal(\"My constraint\", (a, b, c, d) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal((a, b, c, d) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadPenalizeConfigurableIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal(\"My package\", \"My constraint\", (a, b, c, d) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .penalizeConfigurableBigDecimal((a, b, c, d) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadRewardName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadRewardId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadRewardConfigurableName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable()\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadRewardConfigurableId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My package\", \"My constraint\");"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable()\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadRewardNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My constraint\", HardSoftScore.ONE_HARD, (a, b, c, d) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD, (a, b, c, d) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadRewardIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a, b, c, d) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .reward(HardSoftScore.ONE_HARD, (a, b, c, d) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadRewardConfigurableNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My constraint\", (a, b, c, d) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable((a, b, c, d) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadRewardConfigurableIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable(\"My package\", \"My constraint\", (a, b, c, d) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurable((a, b, c, d) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadRewardNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadRewardIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardLong(HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadRewardConfigurableNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong(\"My constraint\", (a, b, c, d) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong((a, b, c, d) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadRewardConfigurableIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong(\"My package\", \"My constraint\", (a, b, c, d) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableLong((a, b, c, d) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadRewardNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN)\n"
                        +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadRewardIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN)\n"
                        +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadRewardConfigurableNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal(\"My constraint\", (a, b, c, d) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal((a, b, c, d) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadRewardConfigurableIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal(\"My package\", \"My constraint\", (a, b, c, d) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .rewardConfigurableBigDecimal((a, b, c, d) -> BigDecimal.TEN)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadImpactName() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadImpactId() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadImpactNameMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My constraint\", HardSoftScore.ONE_HARD, (a, b, c, d) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD, (a, b, c, d) -> 7)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadImpactIdMatchWeigherInt() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(\"My package\", \"My constraint\", HardSoftScore.ONE_HARD, (a, b, c, d) -> 7);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impact(HardSoftScore.ONE_HARD, (a, b, c, d) -> 7)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadImpactNameMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(\"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L)\n" +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadImpactIdMatchWeigherLong() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(\"My package\", \"My constraint\", HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactLong(HardSoftLongScore.ONE_HARD, (a, b, c, d) -> 7L)\n" +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    @Test
    void quadImpactNameMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(\"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN)\n"
                        +
                        "                .asConstraint(\"My constraint\");")));
    }

    @Test
    void quadImpactIdMatchWeigherBigDecimal() {
        rewriteRun(java(
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(\"My package\", \"My constraint\", HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN);"),
                wrap("        return f.forEach(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .join(String.class)\n" +
                        "                .impactBigDecimal(HardSoftBigDecimalScore.ONE_HARD, (a, b, c, d) -> BigDecimal.TEN)\n"
                        +
                        "                .asConstraint(\"My package\", \"My constraint\");")));
    }

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    private static String wrap(String content) {
        return "import java.math.BigDecimal;\n" +
                "import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;\n" +
                "import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;\n" +
                "import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;\n" +
                "import org.optaplanner.core.api.score.stream.ConstraintFactory;\n" +
                "import org.optaplanner.core.api.score.stream.Constraint;\n" +
                "\n" +
                "class Test {\n" +
                "    Constraint myConstraint(ConstraintFactory f) {\n" +
                content + "\n" +
                "    }" +
                "}\n";
    }

}
