package org.optaplanner.constraint.streams.drools;

import org.optaplanner.constraint.streams.common.InnerConstraintFactory;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.drools.uni.DroolsFromUniConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintConfigurationDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public final class DroolsConstraintFactory<Solution_>
        extends InnerConstraintFactory<Solution_, DroolsConstraint<Solution_>> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final String defaultConstraintPackage;
    private final DroolsVariableFactory variableFactory = new DroolsVariableFactory();

    public DroolsConstraintFactory(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
        ConstraintConfigurationDescriptor<Solution_> configurationDescriptor = solutionDescriptor
                .getConstraintConfigurationDescriptor();
        if (configurationDescriptor == null) {
            Package pack = solutionDescriptor.getSolutionClass().getPackage();
            defaultConstraintPackage = (pack == null) ? "" : pack.getName();
        } else {
            defaultConstraintPackage = configurationDescriptor.getConstraintPackage();
        }
    }

    @Override
    public <A> UniConstraintStream<A> forEachIncludingNullVars(Class<A> sourceClass) {
        assertValidFromType(sourceClass);
        return new DroolsFromUniConstraintStream<>(this, sourceClass, RetrievalSemantics.STANDARD);
    }

    @Override
    public <A> UniConstraintStream<A> fromUnfiltered(Class<A> fromClass) {
        assertValidFromType(fromClass);
        return new DroolsFromUniConstraintStream<>(this, fromClass, RetrievalSemantics.LEGACY);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public DroolsVariableFactory getVariableFactory() {
        return variableFactory;
    }

    @Override
    public String getDefaultConstraintPackage() {
        return defaultConstraintPackage;
    }

}
