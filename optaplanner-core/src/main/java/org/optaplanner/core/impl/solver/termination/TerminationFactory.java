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

package org.optaplanner.core.impl.solver.termination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.solver.termination.TerminationCompositionStyle;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public class TerminationFactory {

    public static TerminationFactory create(TerminationConfig terminationConfig) {
        return new TerminationFactory(terminationConfig);
    }

    private final TerminationConfig terminationConfig;

    private TerminationFactory(TerminationConfig terminationConfig) {
        this.terminationConfig = terminationConfig;
    }

    public Termination buildTermination(HeuristicConfigPolicy configPolicy, Termination chainedTermination) {
        Termination termination = buildTermination(configPolicy);
        if (termination == null) {
            return chainedTermination;
        }
        return new OrCompositeTermination(chainedTermination, termination);
    }

    /**
     * @param configPolicy never null
     * @return sometimes null
     */
    public Termination buildTermination(HeuristicConfigPolicy configPolicy) {
        List<Termination> terminationList = new ArrayList<>();
        if (terminationConfig.getTerminationClass() != null) {
            Termination termination =
                    ConfigUtils.newInstance(terminationConfig, "terminationClass", terminationConfig.getTerminationClass());
            terminationList.add(termination);
        }

        terminationList.addAll(buildTimeBasedTermination(configPolicy));

        if (terminationConfig.getBestScoreLimit() != null) {
            ScoreDefinition scoreDefinition = configPolicy.getScoreDefinition();
            Score bestScoreLimit_ = scoreDefinition.parseScore(terminationConfig.getBestScoreLimit());
            double[] timeGradientWeightNumbers = new double[scoreDefinition.getLevelsSize() - 1];
            Arrays.fill(timeGradientWeightNumbers, 0.50); // Number pulled out of thin air
            terminationList.add(new BestScoreTermination(scoreDefinition, bestScoreLimit_, timeGradientWeightNumbers));
        }
        if (terminationConfig.getBestScoreFeasible() != null) {
            ScoreDefinition scoreDefinition = configPolicy.getScoreDefinition();
            if (!terminationConfig.getBestScoreFeasible()) {
                throw new IllegalArgumentException("The termination bestScoreFeasible ("
                        + terminationConfig.getBestScoreFeasible() + ") cannot be false.");
            }
            double[] timeGradientWeightFeasibleNumbers = new double[scoreDefinition.getFeasibleLevelsSize() - 1];
            Arrays.fill(timeGradientWeightFeasibleNumbers, 0.50); // Number pulled out of thin air
            terminationList.add(new BestScoreFeasibleTermination(scoreDefinition, timeGradientWeightFeasibleNumbers));
        }
        if (terminationConfig.getStepCountLimit() != null) {
            terminationList.add(new StepCountTermination(terminationConfig.getStepCountLimit()));
        }
        if (terminationConfig.getScoreCalculationCountLimit() != null) {
            terminationList.add(new ScoreCalculationCountTermination(terminationConfig.getScoreCalculationCountLimit()));
        }
        if (terminationConfig.getUnimprovedStepCountLimit() != null) {
            terminationList.add(new UnimprovedStepCountTermination(terminationConfig.getUnimprovedStepCountLimit()));
        }

        terminationList.addAll(buildInnerTermination(configPolicy));
        return buildTerminationFromList(terminationList);
    }

    protected List<Termination> buildTimeBasedTermination(HeuristicConfigPolicy configPolicy) {
        List<Termination> terminationList = new ArrayList<>();
        Long timeMillisSpentLimit = terminationConfig.calculateTimeMillisSpentLimit();
        if (timeMillisSpentLimit != null) {
            terminationList.add(new TimeMillisSpentTermination(timeMillisSpentLimit));
        }
        Long unimprovedTimeMillisSpentLimit = terminationConfig.calculateUnimprovedTimeMillisSpentLimit();
        if (unimprovedTimeMillisSpentLimit != null) {
            if (terminationConfig.getUnimprovedScoreDifferenceThreshold() == null) {
                terminationList.add(new UnimprovedTimeMillisSpentTermination(unimprovedTimeMillisSpentLimit));
            } else {
                ScoreDefinition scoreDefinition = configPolicy.getScoreDefinition();
                Score unimprovedScoreDifferenceThreshold_ =
                        scoreDefinition.parseScore(terminationConfig.getUnimprovedScoreDifferenceThreshold());
                if (unimprovedScoreDifferenceThreshold_.compareTo(scoreDefinition.getZeroScore()) <= 0) {
                    throw new IllegalStateException("The unimprovedScoreDifferenceThreshold ("
                            + terminationConfig.getUnimprovedScoreDifferenceThreshold() + ") must be positive.");

                }
                terminationList.add(new UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination(
                        unimprovedTimeMillisSpentLimit, unimprovedScoreDifferenceThreshold_));
            }
        } else if (terminationConfig.getUnimprovedScoreDifferenceThreshold() != null) {
            throw new IllegalStateException("The unimprovedScoreDifferenceThreshold ("
                    + terminationConfig.getUnimprovedScoreDifferenceThreshold()
                    + ") can only be used if an unimproved*SpentLimit ("
                    + unimprovedTimeMillisSpentLimit + ") is used too.");
        }

        return terminationList;
    }

    protected List<Termination> buildInnerTermination(HeuristicConfigPolicy configPolicy) {
        if (ConfigUtils.isEmptyCollection(terminationConfig.getTerminationConfigList())) {
            return Collections.emptyList();
        }

        return terminationConfig.getTerminationConfigList().stream()
                .map(config -> TerminationFactory.create(config).buildTermination(configPolicy))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected Termination buildTerminationFromList(List<Termination> terminationList) {
        if (terminationList.isEmpty()) {
            return null;
        } else if (terminationList.size() == 1) {
            return terminationList.get(0);
        } else {
            AbstractCompositeTermination compositeTermination;
            if (terminationConfig.getTerminationCompositionStyle() == null
                    || terminationConfig.getTerminationCompositionStyle() == TerminationCompositionStyle.OR) {
                compositeTermination = new OrCompositeTermination(terminationList);
            } else if (terminationConfig.getTerminationCompositionStyle() == TerminationCompositionStyle.AND) {
                compositeTermination = new AndCompositeTermination(terminationList);
            } else {
                throw new IllegalStateException("The terminationCompositionStyle ("
                        + terminationConfig.getTerminationCompositionStyle() + ") is not implemented.");
            }
            return compositeTermination;
        }
    }
}
