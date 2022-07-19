package org.drools.ruleunits.dsl.patterns;

import java.util.UUID;

import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Block2;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.constraints.AlphaConstraint;
import org.drools.ruleunits.dsl.util.RuleDefinition;

public class Pattern1Def<A> extends SinglePatternDef<A> {

    public Pattern1Def(RuleDefinition rule, Variable<A> variable) {
        super(rule, variable);
    }

    public void execute(Block1<A> block) {
        rule.setConsequence( DSL.on(variable).execute(block) );
    }

    public <G> void execute(G globalObject, Block2<G, A> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(globalObject), variable).execute(block) );
    }

    public <B> Pattern2Def<A, B> from(DataSource<B> dataSource) {
        return join(rule.from(dataSource));
    }

    public <B> Pattern2Def<A, B> join(Function1<RuleFactory, Pattern1Def<B>> patternBuilder) {
        return join(patternBuilder.apply(rule));
    }

    private <B> Pattern2Def<A, B> join(Pattern1Def<B> other) {
        return new Pattern2Def<>(rule, this, other);
    }

    public Pattern1Def<A> filter(Predicate1<A> predicate) {
        constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), predicate));
        return this;
    }

    public Pattern1Def<A> filter(String fieldName, Predicate1<A> predicate) {
        constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), predicate, PatternDSL.reactOn(fieldName)));
        return this;
    }

    public Pattern1Def<A> filter(Index.ConstraintType constraintType, A rightValue) {
        return filter("this", a -> a, constraintType, rightValue);
    }

    public <V> Pattern1Def<A> filter(Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue) {
        return filter(null, extractor, constraintType, rightValue);
    }

    public <V> Pattern1Def<A> filter(String fieldName, Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue) {
        constraints.add(new AlphaConstraint<>(variable, fieldName, extractor, constraintType, rightValue));
        return this;
    }

    public <B> Pattern1Def<B> accumulate(Accumulator1<A, B> acc) {
        return rule.accumulate(this, acc);
    }
}