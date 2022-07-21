package org.drools.ruleunits.dsl.patterns;

import java.util.UUID;

import org.drools.model.Condition;
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

import static org.drools.model.functions.Function1.identity;
import static org.drools.ruleunits.dsl.accumulate.AccumulatePattern2.createAccumulatePattern2;

public class Pattern1DefImpl<A> extends SinglePatternDef<A> implements Pattern1Def<A> {

    public Pattern1DefImpl(RuleDefinition rule, Variable<A> variable) {
        super(rule, variable);
    }

    @Override
    public void execute(Block1<A> block) {
        rule.setConsequence( DSL.on(variable).execute(block) );
    }

    @Override
    public <G> void execute(G globalObject, Block2<G, A> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(globalObject), variable).execute(block) );
    }

    @Override
    public <B> Pattern2DefImpl<A, B> from(DataSource<B> dataSource) {
        return join(rule.from(dataSource));
    }

    @Override
    public <B> Pattern2DefImpl<A, B> join(Function1<RuleFactory, Pattern1Def<B>> patternBuilder) {
        return join((Pattern1DefImpl) patternBuilder.apply(rule));
    }

    private <B> Pattern2DefImpl<A, B> join(Pattern1DefImpl<B> other) {
        return new Pattern2DefImpl<>(rule, this, other);
    }

    @Override
    public Pattern1DefImpl<A> filter(Predicate1<A> predicate) {
        constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), predicate));
        return this;
    }

    @Override
    public Pattern1DefImpl<A> filter(String fieldName, Predicate1<A> predicate) {
        constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), predicate, PatternDSL.reactOn(fieldName)));
        return this;
    }

    @Override
    public Pattern1DefImpl<A> filter(Index.ConstraintType constraintType, A rightValue) {
        return filter("this", identity(), constraintType, rightValue);
    }

    @Override
    public <V> Pattern1DefImpl<A> filter(Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue) {
        return filter(null, extractor, constraintType, rightValue);
    }

    @Override
    public <V> Pattern1DefImpl<A> filter(String fieldName, Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue) {
        constraints.add(new AlphaConstraint<>(variable, fieldName, extractor, constraintType, rightValue));
        return this;
    }

    @Override
    public <B, C> Pattern2Def<A, C> accumulate(Function1<Pattern1Def<A>, PatternDef> patternBuilder, Accumulator1<B, C> acc) {
        Pattern1DefImpl patternB = (Pattern1DefImpl) rule.internalCreatePattern(this, patternBuilder);
        Pattern2DefImpl<A, C> accPattern = createAccumulatePattern2(rule, this, patternB, acc);
        rule.addPattern(accPattern);
        return accPattern;
    }

    @Override
    public Pattern1DefImpl<A> exists(Function1<Pattern1Def<A>, PatternDef> patternBuilder) {
        rule.addPattern( new ExistentialPatternDef( Condition.Type.EXISTS, rule.internalCreatePattern(this, patternBuilder) ) );
        return this;
    }

    @Override
    public Pattern1DefImpl<A> not(Function1<Pattern1Def<A>, PatternDef> patternBuilder) {
        rule.addPattern( new ExistentialPatternDef( Condition.Type.NOT, rule.internalCreatePattern(this, patternBuilder) ) );
        return this;
    }
}