package org.drools.ruleunits.dsl.patterns;

import org.drools.model.Index;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Block2;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;

public interface Pattern1Def<A> extends PatternDef {
    void execute(Block1<A> block);

    <G> void execute(G globalObject, Block1<G> block);

    <G> void execute(G globalObject, Block2<G, A> block);

    <B> Pattern2Def<A, B> from(DataSource<B> dataSource);

    <B> Pattern2Def<A, B> join(Function1<RuleFactory, Pattern1Def<B>> patternBuilder);

    Pattern1Def<A> filter(Predicate1<A> predicate);

    Pattern1Def<A> filter(String fieldName, Predicate1<A> predicate);

    Pattern1Def<A> filter(Index.ConstraintType constraintType, A rightValue);

    <V> Pattern1Def<A> filter(Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue);

    <V> Pattern1Def<A> filter(String fieldName, Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue);

    <B, C> Pattern2Def<A, C> accumulate(Function1<Pattern1Def<A>, PatternDef> patternBuilder, Accumulator1<B, C> acc);

    Pattern1Def<A> exists(Function1<Pattern1Def<A>, PatternDef> patternBuilder);

    Pattern1Def<A> not(Function1<Pattern1Def<A>, PatternDef> patternBuilder);
}
