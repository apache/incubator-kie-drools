package org.drools.ruleunits.dsl;

import org.drools.model.functions.Block1;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.patterns.Pattern1Def;
import org.drools.ruleunits.dsl.patterns.Pattern2Def;
import org.drools.ruleunits.dsl.patterns.PatternDef;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;

/**
 * The root of the fluent Java DSL to define a rule.
 */
public interface RuleFactory {

    <A> Pattern1Def<A> on(DataSource<A> dataSource);

    RuleFactory not(Function1<RuleFactory, PatternDef> patternBuilder);

    RuleFactory exists(Function1<RuleFactory, PatternDef> patternBuilder);

    <A, B> Pattern1Def<B> accumulate(Function1<RuleFactory, PatternDef> patternBuilder, Accumulator1<A, B> acc);

    <A, K, V> Pattern2Def<K, V> groupBy(Function1<RuleFactory, PatternDef> patternBuilder, Function1<A, K> groupingFunction, Accumulator1<A, V> acc);

    <T> void execute(T globalObject, Block1<T> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block);
}
