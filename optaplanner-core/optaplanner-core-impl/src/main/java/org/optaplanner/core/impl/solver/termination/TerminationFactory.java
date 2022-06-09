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

public class TerminationFactory<Solution_> {

    public static <Solution_> TerminationFactory<Solution_> create(TerminationConfig terminationConfig) {
        return new TerminationFactory<>(terminationConfig);
    }

    private final TerminationConfig terminationConfig;

    private TerminationFactory(TerminationConfig terminationConfig) {
        this.terminationConfig = terminationConfig;
    }

    public Termination<Solution_> buildTermination(HeuristicConfigPolicy<Solution_> configPolicy,
            Termination<Solution_> chainedTermination) {
        Termination<Solution_> termination = buildTermination(configPolicy);
        if (termination == null) {
            return chainedTermination;
        }
        return new OrCompositeTermination<>(chainedTermination, termination);
    }

    /**
     * @param configPolicy never null
     * @return sometimes null
     */
    public <Score_ extends Score<Score_>> Termination<Solution_> buildTermination(
            HeuristicConfigPolicy<Solution_> configPolicy) {
        List<Termination<Solution_>> terminationList = new ArrayList<>();
        if (terminationConfig.getTerminationClass() != null) {
            Termination<Solution_> termination =
                    ConfigUtils.newInstance(terminationConfig, "terminationClass", terminationConfig.getTerminationClass());
            terminationList.add(termination);
        }

        terminationList.addAll(buildTimeBasedTermination(configPolicy));

        if (terminationConfig.getBestScoreLimit() != null) {
            ScoreDefinition<Score_> scoreDefinition = configPolicy.getScoreDefinition();
            Score_ bestScoreLimit_ = scoreDefinition.parseScore(terminationConfig.getBestScoreLimit());
            double[] timeGradientWeightNumbers = new double[scoreDefinition.getLevelsSize() - 1];
            Arrays.fill(timeGradientWeightNumbers, 0.50); // Number pulled out of thin air
            terminationList.add(new BestScoreTermination<>(scoreDefinition, bestScoreLimit_, timeGradientWeightNumbers));
        }
        if (terminationConfig.getBestScoreFeasible() != null) {
            ScoreDefinition<Score_> scoreDefinition = configPolicy.getScoreDefinition();
            if (!terminationConfig.getBestScoreFeasible()) {
                throw new IllegalArgumentException("The termination bestScoreFeasible ("
                        + terminationConfig.getBestScoreFeasible() + ") cannot be false.");
            }
            double[] timeGradientWeightFeasibleNumbers = new double[scoreDefinition.getFeasibleLevelsSize() - 1];
            Arrays.fill(timeGradientWeightFeasibleNumbers, 0.50); // Number pulled out of thin air
            terminationList.add(new BestScoreFeasibleTermination<>(scoreDefinition, timeGradientWeightFeasibleNumbers));
        }
        if (terminationConfig.getStepCountLimit() != null) {
            terminationList.add(new StepCountTermination<>(terminationConfig.getStepCountLimit()));
        }
        if (terminationConfig.getScoreCalculationCountLimit() != null) {
            terminationList.add(new ScoreCalculationCountTermination<>(terminationConfig.getScoreCalculationCountLimit()));
        }
        if (terminationConfig.getUnimprovedStepCountLimit() != null) {
            terminationList.add(new UnimprovedStepCountTermination<>(terminationConfig.getUnimprovedStepCountLimit()));
        }

        terminationList.addAll(buildInnerTermination(configPolicy));
        return buildTerminationFromList(terminationList);
    }

    protected <Score_ extends Score<Score_>> List<Termination<Solution_>>
            buildTimeBasedTermination(HeuristicConfigPolicy<Solution_> configPolicy) {
        List<Termination<Solution_>> terminationList = new ArrayList<>();
        Long timeMillisSpentLimit = terminationConfig.calculateTimeMillisSpentLimit();
        if (timeMillisSpentLimit != null) {
            terminationList.add(new TimeMillisSpentTermination<>(timeMillisSpentLimit));
        }
        Long unimprovedTimeMillisSpentLimit = terminationConfig.calculateUnimprovedTimeMillisSpentLimit();
        if (unimprovedTimeMillisSpentLimit != null) {
            if (terminationConfig.getUnimprovedScoreDifferenceThreshold() == null) {
                terminationList.add(new UnimprovedTimeMillisSpentTermination<>(unimprovedTimeMillisSpentLimit));
            } else {
                ScoreDefinition<Score_> scoreDefinition = configPolicy.getScoreDefinition();
                Score_ unimprovedScoreDifferenceThreshold_ =
                        scoreDefinition.parseScore(terminationConfig.getUnimprovedScoreDifferenceThreshold());
                if (scoreDefinition.isNegativeOrZero(unimprovedScoreDifferenceThreshold_)) {
                    throw new IllegalStateException("The unimprovedScoreDifferenceThreshold ("
                            + terminationConfig.getUnimprovedScoreDifferenceThreshold() + ") must be positive.");

                }
                terminationList.add(new UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination<>(
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

    protected List<Termination<Solution_>> buildInnerTermination(HeuristicConfigPolicy<Solution_> configPolicy) {
        if (ConfigUtils.isEmptyCollection(terminationConfig.getTerminationConfigList())) {
            return Collections.emptyList();
        }

        return terminationConfig.getTerminationConfigList().stream()
                .map(config -> TerminationFactory.<Solution_> create(config)
                        .buildTermination(configPolicy))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected Termination<Solution_> buildTerminationFromList(List<Termination<Solution_>> terminationList) {
        if (terminationList.isEmpty()) {
            return null;
        } else if (terminationList.size() == 1) {
            return terminationList.get(0);
        } else {
            AbstractCompositeTermination<Solution_> compositeTermination;
            if (terminationConfig.getTerminationCompositionStyle() == null
                    || terminationConfig.getTerminationCompositionStyle() == TerminationCompositionStyle.OR) {
                compositeTermination = new OrCompositeTermination<>(terminationList);
            } else if (terminationConfig.getTerminationCompositionStyle() == TerminationCompositionStyle.AND) {
                compositeTermination = new AndCompositeTermination<>(terminationList);
            } else {
                throw new IllegalStateException("The terminationCompositionStyle ("
                        + terminationConfig.getTerminationCompositionStyle() + ") is not implemented.");
            }
            return compositeTermination;
        }
    }
}
