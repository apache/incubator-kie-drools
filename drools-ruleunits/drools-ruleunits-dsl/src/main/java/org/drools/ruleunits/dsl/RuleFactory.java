package org.drools.ruleunits.dsl;

import org.drools.model.functions.Block1;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.patterns.Pattern1Def;
import org.drools.ruleunits.dsl.patterns.PatternDef;

public interface RuleFactory {

    <A> Pattern1Def<A> from(DataSource<A> dataSource);

    RuleFactory not(Function1<RuleFactory, PatternDef> patternBuilder);

    RuleFactory exists(Function1<RuleFactory, PatternDef> patternBuilder);

    <A, B> Pattern1Def<B> accumulate(Function1<RuleFactory, PatternDef> patternBuilder, Accumulator1<A, B> acc);

    <T> void execute(T globalObject, Block1<T> block);
}
