package org.optaplanner.constraint.streams.bavet.tri;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraint;
import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetScoringConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.constraint.streams.common.inliner.WeightedScoreImpacter;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetScoringTriConstraintStream<Solution_, A, B, C>
        extends BavetAbstractTriConstraintStream<Solution_, A, B, C>
        implements BavetScoringConstraintStream<Solution_> {

    private final BavetAbstractTriConstraintStream<Solution_, A, B, C> parent;
    private final boolean noMatchWeigher;
    private final ToIntTriFunction<A, B, C> intMatchWeigher;
    private final ToLongTriFunction<A, B, C> longMatchWeigher;
    private final TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher;
    private BavetConstraint<Solution_> constraint;

    public BavetScoringTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent) {
        this(constraintFactory, parent, true, null, null, null);
    }

    public BavetScoringTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent,
            ToIntTriFunction<A, B, C> intMatchWeigher) {
        this(constraintFactory, parent, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent,
            ToLongTriFunction<A, B, C> longMatchWeigher) {
        this(constraintFactory, parent, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent,
            TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private BavetScoringTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent,
            boolean noMatchWeigher,
            ToIntTriFunction<A, B, C> intMatchWeigher, ToLongTriFunction<A, B, C> longMatchWeigher,
            TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.noMatchWeigher = noMatchWeigher;
        this.intMatchWeigher = intMatchWeigher;
        this.longMatchWeigher = longMatchWeigher;
        this.bigDecimalMatchWeigher = bigDecimalMatchWeigher;
    }

    @Override
    public void setConstraint(BavetConstraint<Solution_> constraint) {
        this.constraint = constraint;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parent.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parent.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parent.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        Score_ constraintWeight = buildHelper.getConstraintWeight(constraint);
        AbstractScoreInliner<Score_> scoreInliner = buildHelper.getScoreInliner();
        WeightedScoreImpacter weightedScoreImpacter = scoreInliner.buildWeightedScoreImpacter(constraint, constraintWeight);
        TriFunction<A, B, C, UndoScoreImpacter> scoreImpacter;
        if (intMatchWeigher != null) {
            scoreImpacter = (a, b, c) -> {
                int matchWeight = intMatchWeigher.applyAsInt(a, b, c);
                constraint.assertCorrectImpact(matchWeight);
                return weightedScoreImpacter.impactScore(matchWeight, () -> asList(a, b, c));
            };
        } else if (longMatchWeigher != null) {
            scoreImpacter = (a, b, c) -> {
                long matchWeight = longMatchWeigher.applyAsLong(a, b, c);
                constraint.assertCorrectImpact(matchWeight);
                return weightedScoreImpacter.impactScore(matchWeight, () -> asList(a, b, c));
            };
        } else if (bigDecimalMatchWeigher != null) {
            scoreImpacter = (a, b, c) -> {
                BigDecimal matchWeight = bigDecimalMatchWeigher.apply(a, b, c);
                constraint.assertCorrectImpact(matchWeight);
                return weightedScoreImpacter.impactScore(matchWeight, () -> asList(a, b, c));
            };
        } else if (noMatchWeigher) {
            scoreImpacter = (a, b, c) -> weightedScoreImpacter.impactScore(1, () -> asList(a, b, c));
        } else {
            throw new IllegalStateException("Impossible state: neither of the supported match weighers provided.");
        }
        TriScorer<A, B, C> scorer = new TriScorer<>(constraint.getConstraintPackage(), constraint.getConstraintName(),
                constraintWeight, scoreImpacter, buildHelper.reserveTupleStoreIndex(parent.getTupleSource()));
        buildHelper.putInsertUpdateRetract(this, scorer);
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    // No node sharing

    @Override
    public String toString() {
        return "Scoring(" + constraint.getConstraintName() + ")";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
