/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.test.impl.score.stream;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.stream.common.AbstractConstraint;
import org.optaplanner.core.impl.score.stream.common.ScoreImpactType;
import org.optaplanner.test.api.score.stream.SingleConstraintAssertion;

public final class DefaultSingleConstraintAssertion<Solution_>
        implements SingleConstraintAssertion {

    private final ConstraintStreamScoreDirectorFactory<Solution_> scoreDirectorFactory;
    private final Score score;
    private final Collection<ConstraintMatchTotal> constraintMatchTotalCollection;
    private final Collection<Indictment> indictmentCollection;

    protected DefaultSingleConstraintAssertion(ConstraintStreamScoreDirectorFactory<Solution_> scoreDirectorFactory,
            Score score, Map<String, ConstraintMatchTotal> constraintMatchTotalMap,
            Map<Object, Indictment> indictmentMap) {
        this.scoreDirectorFactory = requireNonNull(scoreDirectorFactory);
        this.score = requireNonNull(score);
        this.constraintMatchTotalCollection = requireNonNull(constraintMatchTotalMap).values();
        this.indictmentCollection = requireNonNull(indictmentMap).values();
    }

    @Override
    public void penalizesBy(int matchWeightTotal, String message) {
        validateMatchWeighTotal(matchWeightTotal);
        assertImpact(ScoreImpactType.PENALTY, matchWeightTotal, message);
    }

    @Override
    public void penalizesBy(long matchWeightTotal, String message) {
        validateMatchWeighTotal(matchWeightTotal);
        assertImpact(ScoreImpactType.PENALTY, matchWeightTotal, message);
    }

    @Override
    public void penalizesBy(BigDecimal matchWeightTotal, String message) {
        validateMatchWeighTotal(matchWeightTotal);
        assertImpact(ScoreImpactType.PENALTY, matchWeightTotal, message);
    }

    @Override
    public void rewardsWith(int matchWeightTotal, String message) {
        validateMatchWeighTotal(matchWeightTotal);
        assertImpact(ScoreImpactType.REWARD, matchWeightTotal, message);
    }

    @Override
    public void rewardsWith(long matchWeightTotal, String message) {
        validateMatchWeighTotal(matchWeightTotal);
        assertImpact(ScoreImpactType.REWARD, matchWeightTotal, message);
    }

    @Override
    public void rewardsWith(BigDecimal matchWeightTotal, String message) {
        validateMatchWeighTotal(matchWeightTotal);
        assertImpact(ScoreImpactType.REWARD, matchWeightTotal, message);
    }

    private void validateMatchWeighTotal(Number matchWeightTotal) {
        if (matchWeightTotal.doubleValue() < 0) {
            throw new IllegalArgumentException("The matchWeightTotal (" + matchWeightTotal + ") must be positive.");
        }
    }

    private void assertImpact(ScoreImpactType scoreImpactType, Number matchWeightTotal, String message) {
        Number impact = deduceImpact();
        AbstractConstraint<Solution_, ?> constraint = (AbstractConstraint<Solution_, ?>) scoreDirectorFactory
                .getConstraints()[0];
        boolean sameMatchWeighTotal = matchWeightTotal.equals(impact)
                // Auto-cast an int matchWeightTotal to a long.
                || (matchWeightTotal instanceof Integer && Long.valueOf(matchWeightTotal.longValue()).equals(impact));
        if (scoreImpactType == constraint.getScoreImpactType() && sameMatchWeighTotal) {
            return;
        }
        String constraintId = constraint.getConstraintId();
        String assertionMessage = buildAssertionErrorMessage(scoreImpactType, matchWeightTotal,
                constraint.getScoreImpactType(), impact, constraintId, message);
        throw new AssertionError(assertionMessage);
    }

    private Number deduceImpact() {
        ScoreDefinition scoreDefinition = scoreDirectorFactory.getScoreDefinition();
        Score zeroScore = scoreDefinition.getZeroScore();
        Number zero = zeroScore.toLevelNumbers()[0]; // Zero in the exact numeric type expected by the caller.
        if (constraintMatchTotalCollection.isEmpty()) {
            return zero;
        }
        // We do not know the matchWeight, so we need to deduce it.
        // Constraint matches give us a score, whose levels are in the form of (matchWeight * constraintWeight).
        // Here, we strip the constraintWeight.
        Score totalMatchWeightedScore = constraintMatchTotalCollection.stream()
                .map(matchScore -> scoreDefinition.divideBySanitizedDivisor(matchScore.getScore(),
                        matchScore.getConstraintWeight()))
                .reduce(zeroScore, Score::add);
        // Each level of the resulting score now has to be the same number, the matchWeight.
        // Except for where the number is zero.
        List<Number> matchWeightsFound = Arrays.stream(totalMatchWeightedScore.toLevelNumbers())
                .distinct()
                .filter(matchWeight -> !Objects.equals(matchWeight, zero))
                .collect(Collectors.toList());
        if (matchWeightsFound.isEmpty()) {
            return zero;
        } else if (matchWeightsFound.size() != 1) {
            throw new IllegalStateException("Impossible state: expecting at most one match weight," +
                    " but got matchWeightsFound (" + matchWeightsFound + ") instead.");
        }
        return matchWeightsFound.get(0);
    }

    private String buildAssertionErrorMessage(ScoreImpactType expectedImpactType, Number expectedImpact,
            ScoreImpactType actualImpactType, Number actualImpact, String constraintId, String message) {
        String expectation = message != null ? message : "Broken expectation.";
        String preformattedMessage = "%s%n" +
                "%18s: %s%n" +
                "%18s: %s (%s)%n" +
                "%18s: %s (%s)%n%n" +
                "  %s";
        String expectedImpactLabel = "Expected " + getImpactTypeLabel(expectedImpactType);
        String actualImpactLabel = "Actual " + getImpactTypeLabel(actualImpactType);
        return String.format(preformattedMessage,
                expectation,
                "Constraint", constraintId,
                expectedImpactLabel, expectedImpact, expectedImpact.getClass(),
                actualImpactLabel, actualImpact, actualImpact.getClass(),
                AbstractScoreDirector.explainScore(score, constraintMatchTotalCollection, indictmentCollection));
    }

    private String getImpactTypeLabel(ScoreImpactType scoreImpactType) {
        if (scoreImpactType == ScoreImpactType.PENALTY) {
            return "penalty";
        } else if (scoreImpactType == ScoreImpactType.REWARD) {
            return "reward";
        } else { // Needs to work with null.
            return "impact";
        }
    }

}
