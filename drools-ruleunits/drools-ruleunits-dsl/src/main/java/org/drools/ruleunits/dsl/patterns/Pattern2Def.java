package org.drools.ruleunits.dsl.patterns;

import org.drools.model.Index;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Block2;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate2;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;

public interface Pattern2Def<A, B> extends PatternDef {

    Pattern2Def<A, B> filter(Predicate2<A, B> predicate);

    Pattern2Def<A, B> filter(Index.ConstraintType constraintType, Function1<A, B> rightExtractor);

    <V> Pattern2Def<A, B> filter(Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor);

    <V> Pattern2Def<A, B> filter(String fieldName, Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor);

    <C> Pattern3Def<A, B, C> on(DataSource<C> dataSource);

    <C> Pattern3Def<A, B, C> join(Function1<RuleFactory, Pattern1Def<C>> patternBuilder);

    Pattern2DefImpl<A, B> exists(Function1<Pattern2Def<A, B>, PatternDef> patternBuilder);

    Pattern2DefImpl<A, B> not(Function1<Pattern2Def<A, B>, PatternDef> patternBuilder);

    void execute(Block2<A, B> block);

    <G> void execute(G globalObject, Block3<G, A, B> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block3<ConsequenceDataStore<T>, A, B> block);
}
