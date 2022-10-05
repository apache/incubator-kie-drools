package org.optaplanner.constraint.streams.drools.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.optaplanner.constraint.streams.common.AbstractConstraintStream;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.drools.DroolsConstraint;
import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;

public abstract class DroolsAbstractConstraintStream<Solution_> extends AbstractConstraintStream<Solution_> {

    // TODO make private
    protected final DroolsConstraintFactory<Solution_> constraintFactory;
    private final List<DroolsAbstractConstraintStream<Solution_>> childStreamList = new ArrayList<>(2);

    public DroolsAbstractConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(retrievalSemantics);
        this.constraintFactory = Objects.requireNonNull(constraintFactory);
    }

    protected Constraint buildConstraint(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ScoreImpactType impactType, Object justificationMapping, Object indictedObjectsMapping,
            RuleBuilder<Solution_> ruleBuilder) {
        var resolvedConstraintPackage =
                Objects.requireNonNullElseGet(constraintPackage, this.constraintFactory::getDefaultConstraintPackage);
        var resolvedJustificationMapping =
                Objects.requireNonNullElseGet(justificationMapping, this::getDefaultJustificationMapping);
        var resolvedIndictedObjectsMapping =
                Objects.requireNonNullElseGet(indictedObjectsMapping, this::getDefaultIndictedObjectsMapping);
        var isConstraintWeightConfigurable = constraintWeight == null;
        var constraintWeightExtractor = isConstraintWeightConfigurable
                ? buildConstraintWeightExtractor(resolvedConstraintPackage, constraintName)
                : buildConstraintWeightExtractor(resolvedConstraintPackage, constraintName, constraintWeight);
        return new DroolsConstraint<>(constraintFactory, resolvedConstraintPackage, constraintName, constraintWeightExtractor,
                impactType, isConstraintWeightConfigurable, ruleBuilder, resolvedJustificationMapping,
                resolvedIndictedObjectsMapping);
    }

    public void addChildStream(DroolsAbstractConstraintStream<Solution_> childStream) {
        childStreamList.add(childStream);
    }

    public Collection<DroolsAbstractConstraintStream<Solution_>> getChildStreams() {
        return Collections.unmodifiableList(childStreamList);
    }

    @Override
    public DroolsConstraintFactory<Solution_> getConstraintFactory() {
        return constraintFactory;
    }
}
