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
import org.drools.ruleunits.dsl.constraints.AlphaConstraint;
import org.drools.ruleunits.dsl.util.RuleDefinition;

public class Pattern1<A> extends PatternDefinition<A> {

    public Pattern1(RuleDefinition rule, Variable<A> variable) {
        super(rule, variable);
    }

    public void execute(Block1<A> block) {
        rule.setConsequence( DSL.on(variable).execute(block) );
    }

    public <G> void execute(G globalObject, Block2<G, A> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(globalObject), variable).execute(block) );
    }

    public <B> Pattern2<A, B> join(DataSource<B> dataSource) {
        return join(rule.from(dataSource));
    }

    public <B> Pattern2<A, B> join(Pattern1<B> other) {
        return new Pattern2<>(rule, this, other);
    }

    public Pattern1<A> filter(Predicate1<A> predicate) {
        constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), predicate));
        return this;
    }

    public Pattern1<A> filter(String fieldName, Predicate1<A> predicate) {
        constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), predicate, PatternDSL.reactOn(fieldName)));
        return this;
    }

    public Pattern1<A> filter(Index.ConstraintType constraintType, A rightValue) {
        return filter("this", a -> a, constraintType, rightValue);
    }

    public <V> Pattern1<A> filter(Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue) {
        return filter(null, extractor, constraintType, rightValue);
    }

    public <V> Pattern1<A> filter(String fieldName, Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue) {
        constraints.add(new AlphaConstraint<>(variable, fieldName, extractor, constraintType, rightValue));
        return this;
    }
}