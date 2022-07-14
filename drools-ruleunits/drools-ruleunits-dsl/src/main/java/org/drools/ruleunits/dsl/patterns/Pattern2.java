package org.drools.ruleunits.dsl.patterns;

import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.functions.Block2;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.constraints.BetaConstraint;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.ruleunits.dsl.accumulate.AccumulatePattern2.createAccumulatePattern2;

public class Pattern2<A, B> extends PatternDefinition<B> {

    protected final Pattern1<A> patternA;
    protected final Pattern1<B> patternB;

    public Pattern2(RuleDefinition rule, Pattern1<A> patternA, Pattern1<B> patternB) {
        super(rule, patternB.variable);
        this.patternA = patternA;
        this.patternB = patternB;
    }

    public <V> Pattern2<A, B> filter(Index.ConstraintType constraintType, Function1<A, B> rightExtractor) {
        return filter("this", a -> a, constraintType, rightExtractor);
    }

    public <V> Pattern2<A, B> filter(Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor) {
        return filter(null, leftExtractor, constraintType, rightExtractor);
    }

    public <V> Pattern2<A, B> filter(String fieldName, Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor) {
        patternB.constraints.add(new BetaConstraint<>(variable, fieldName, leftExtractor, constraintType, patternA.variable, rightExtractor));
        return this;
    }

    public <C> Pattern2<A, C> accumulate(Accumulator1<B, C> acc) {
        rule.removePattern(patternB);
        Pattern2<A, C> accPattern = createAccumulatePattern2(rule, patternA, patternB, acc);
        rule.addPattern(accPattern);
        return accPattern;
    }

    public void execute(Block2<A, B> block) {
        rule.setConsequence( DSL.on(patternA.variable, variable).execute(block) );
    }

    public <G> void execute(G globalObject, Block3<G, A, B> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(globalObject), patternA.variable, variable).execute(block) );
    }
}