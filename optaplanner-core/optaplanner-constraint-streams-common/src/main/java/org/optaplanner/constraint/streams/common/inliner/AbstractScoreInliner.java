package org.optaplanner.constraint.streams.common.inliner;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.score.buildin.BendableBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.BendableLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.BendableScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardMediumSoftBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardMediumSoftLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardMediumSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardSoftBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardSoftLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.SimpleBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.SimpleLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.constraint.DefaultConstraintMatchTotal;
import org.optaplanner.core.impl.score.constraint.DefaultIndictment;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public abstract class AbstractScoreInliner<Score_ extends Score<Score_>> {

    @Deprecated(forRemoval = true)
    private static final String CUSTOM_SCORE_INLINER_CLASS_PROPERTY_NAME =
            "org.optaplanner.score.stream.inliner";

    public static <Score_ extends Score<Score_>, ScoreInliner_ extends AbstractScoreInliner<Score_>> ScoreInliner_
            buildScoreInliner(ScoreDefinition<Score_> scoreDefinition, boolean constraintMatchEnabled) {
        if (scoreDefinition instanceof SimpleScoreDefinition) {
            return (ScoreInliner_) new SimpleScoreInliner(constraintMatchEnabled);
        } else if (scoreDefinition instanceof SimpleLongScoreDefinition) {
            return (ScoreInliner_) new SimpleLongScoreInliner(constraintMatchEnabled);
        } else if (scoreDefinition instanceof SimpleBigDecimalScoreDefinition) {
            return (ScoreInliner_) new SimpleBigDecimalScoreInliner(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardSoftScoreDefinition) {
            return (ScoreInliner_) new HardSoftScoreInliner(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardSoftLongScoreDefinition) {
            return (ScoreInliner_) new HardSoftLongScoreInliner(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardSoftBigDecimalScoreDefinition) {
            return (ScoreInliner_) new HardSoftBigDecimalScoreInliner(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardMediumSoftScoreDefinition) {
            return (ScoreInliner_) new HardMediumSoftScoreInliner(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardMediumSoftLongScoreDefinition) {
            return (ScoreInliner_) new HardMediumSoftLongScoreInliner(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardMediumSoftBigDecimalScoreDefinition) {
            return (ScoreInliner_) new HardMediumSoftBigDecimalScoreInliner(constraintMatchEnabled);
        } else if (scoreDefinition instanceof BendableScoreDefinition) {
            BendableScoreDefinition bendableScoreDefinition = (BendableScoreDefinition) scoreDefinition;
            return (ScoreInliner_) new BendableScoreInliner(constraintMatchEnabled, bendableScoreDefinition.getHardLevelsSize(),
                    bendableScoreDefinition.getSoftLevelsSize());
        } else if (scoreDefinition instanceof BendableLongScoreDefinition) {
            BendableLongScoreDefinition bendableScoreDefinition = (BendableLongScoreDefinition) scoreDefinition;
            return (ScoreInliner_) new BendableLongScoreInliner(constraintMatchEnabled,
                    bendableScoreDefinition.getHardLevelsSize(),
                    bendableScoreDefinition.getSoftLevelsSize());
        } else if (scoreDefinition instanceof BendableBigDecimalScoreDefinition) {
            BendableBigDecimalScoreDefinition bendableScoreDefinition = (BendableBigDecimalScoreDefinition) scoreDefinition;
            return (ScoreInliner_) new BendableBigDecimalScoreInliner(constraintMatchEnabled,
                    bendableScoreDefinition.getHardLevelsSize(), bendableScoreDefinition.getSoftLevelsSize());
        } else {
            String customScoreInlinerClassName = System.getProperty(CUSTOM_SCORE_INLINER_CLASS_PROPERTY_NAME);
            if (customScoreInlinerClassName == null) {
                throw new UnsupportedOperationException("Unknown score definition class (" +
                        scoreDefinition.getClass().getCanonicalName() + ").\n" +
                        "If you're attempting to use a custom score, " +
                        "provide your " + AbstractScoreInliner.class.getSimpleName() + " implementation using the '" +
                        CUSTOM_SCORE_INLINER_CLASS_PROPERTY_NAME + "' system property.\n" +
                        "Note: support for custom scores will be removed in OptaPlanner 9.0.");
            }
            try {
                Class<?> customScoreInlinerClass = Class.forName(customScoreInlinerClassName);
                if (!AbstractScoreInliner.class.isAssignableFrom(customScoreInlinerClass)) {
                    throw new IllegalStateException("Custom score inliner class (" + customScoreInlinerClassName +
                            ") does not extend " + AbstractScoreInliner.class.getCanonicalName() + ".\n" +
                            "Note: support for custom scores will be removed in OptaPlanner 9.0.");
                }
                return ((Class<ScoreInliner_>) customScoreInlinerClass).getConstructor()
                        .newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException cause) {
                throw new IllegalStateException("Custom score inliner class (" + customScoreInlinerClassName +
                        ") can not be instantiated.\n" +
                        "Maybe add a no-arg public constructor?\n" +
                        "Note: support for custom scores will be removed in OptaPlanner 9.0.", cause);
            }
        }
    }

    protected final boolean constraintMatchEnabled;
    private final Map<String, DefaultConstraintMatchTotal<Score_>> constraintMatchTotalMap;
    private final Map<Object, DefaultIndictment<Score_>> indictmentMap;

    protected AbstractScoreInliner(boolean constraintMatchEnabled) {
        this.constraintMatchEnabled = constraintMatchEnabled;
        this.constraintMatchTotalMap = constraintMatchEnabled ? new LinkedHashMap<>() : null;
        this.indictmentMap = constraintMatchEnabled ? new LinkedHashMap<>() : null;
    }

    public abstract Score_ extractScore(int initScore);

    /**
     * Create a new instance of {@link WeightedScoreImpacter} for a particular constraint.
     * 
     * @param constraint never null
     * @param constraintWeight never null
     * @return never null
     */
    public abstract WeightedScoreImpacter buildWeightedScoreImpacter(Constraint constraint, Score_ constraintWeight);

    protected final Runnable addConstraintMatch(Constraint constraint, Score_ constraintWeight, Score_ score,
            List<Object> justificationList) {
        String constraintPackage = constraint.getConstraintPackage();
        String constraintName = constraint.getConstraintName();
        DefaultConstraintMatchTotal<Score_> constraintMatchTotal = constraintMatchTotalMap.computeIfAbsent(
                constraint.getConstraintId(),
                key -> new DefaultConstraintMatchTotal<>(constraintPackage, constraintName, constraintWeight));
        ConstraintMatch<Score_> constraintMatch = constraintMatchTotal.addConstraintMatch(justificationList, score);
        DefaultIndictment<Score_>[] indictments = justificationList.stream()
                .distinct() // One match might have the same justification twice
                .map(justification -> {
                    DefaultIndictment<Score_> indictment = indictmentMap.computeIfAbsent(justification,
                            key -> new DefaultIndictment<>(justification, constraintMatch.getScore().zero()));
                    indictment.addConstraintMatch(constraintMatch);
                    return indictment;
                }).toArray(DefaultIndictment[]::new);
        return () -> {
            constraintMatchTotal.removeConstraintMatch(constraintMatch);
            if (constraintMatchTotal.getConstraintMatchSet().isEmpty()) {
                constraintMatchTotalMap.remove(constraint.getConstraintId());
            }
            for (DefaultIndictment<Score_> indictment : indictments) {
                indictment.removeConstraintMatch(constraintMatch);
                if (indictment.getConstraintMatchSet().isEmpty()) {
                    indictmentMap.remove(indictment.getJustification());
                }
            }
        };
    }

    public final Map<String, ConstraintMatchTotal<Score_>> getConstraintMatchTotalMap() {
        // Unchecked assignment necessary as CMT and DefaultCMT incompatible in the Map generics.
        return (Map) constraintMatchTotalMap;
    }

    public final Map<Object, Indictment<Score_>> getIndictmentMap() {
        // Unchecked assignment necessary as Indictment and DefaultIndictment incompatible in the Map generics.
        return (Map) indictmentMap;
    }

    protected final void validateConstraintWeight(Constraint constraint, Score_ constraintWeight) {
        if (constraintWeight == null || constraintWeight.isZero()) {
            throw new IllegalArgumentException("Impossible state: The constraintWeight (" +
                    constraintWeight + ") cannot be zero, constraint (" + constraint +
                    ") should have been culled during session creation.");
        }
    }

}
