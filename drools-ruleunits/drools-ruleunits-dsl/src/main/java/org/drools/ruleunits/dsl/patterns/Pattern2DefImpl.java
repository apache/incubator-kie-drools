package org.drools.ruleunits.dsl.patterns;

import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.functions.Block2;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.dsl.constraints.BetaConstraint;
import org.drools.ruleunits.dsl.util.RuleDefinition;

public class Pattern2DefImpl<A, B> extends SinglePatternDef<B> implements Pattern2Def<A, B> {

    protected final Pattern1DefImpl<A> patternA;
    protected final Pattern1DefImpl<B> patternB;

    public Pattern2DefImpl(RuleDefinition rule, Pattern1DefImpl<A> patternA, Pattern1DefImpl<B> patternB) {
        super(rule, patternB.variable);
        this.patternA = patternA;
        this.patternB = patternB;
    }

    @Override
    public <V> Pattern2DefImpl<A, B> filter(Index.ConstraintType constraintType, Function1<A, B> rightExtractor) {
        return filter("this", a -> a, constraintType, rightExtractor);
    }

    @Override
    public <V> Pattern2DefImpl<A, B> filter(Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor) {
        return filter(null, leftExtractor, constraintType, rightExtractor);
    }

    @Override
    public <V> Pattern2DefImpl<A, B> filter(String fieldName, Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor) {
        patternB.constraints.add(new BetaConstraint<>(variable, fieldName, leftExtractor, constraintType, patternA.variable, rightExtractor));
        return this;
    }

    @Override
    public void execute(Block2<A, B> block) {
        rule.setConsequence( DSL.on(patternA.variable, variable).execute(block) );
    }

    @Override
    public <G> void execute(G globalObject, Block3<G, A, B> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(globalObject), patternA.variable, variable).execute(block) );
    }

    public Pattern1DefImpl<A> getPatternA() {
        return patternA;
    }

    public Pattern1DefImpl<B> getPatternB() {
        return patternB;
    }

    @Override
    public InternalPatternDef subPatternFrom(InternalPatternDef from) {
        if (from != patternA) {
            throw new IllegalArgumentException();
        }
        return patternB;
    }
}