package org.drools.ruleunits.dsl;

import org.drools.model.Rule;
import org.drools.model.functions.Block1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.patterns.Pattern1Def;
import org.drools.ruleunits.dsl.patterns.Pattern2Def;
import org.drools.ruleunits.dsl.util.RuleDefinition;

public class RuleFactory {

    private final RuleUnitDefinition unit;
    private final String name;

    private final RuleDefinition ruleDefinition;

    public RuleFactory(RuleUnitDefinition unit, RulesFactory.UnitGlobals globals, String name) {
        this.unit = unit;
        this.name = name;
        this.ruleDefinition = new RuleDefinition(unit, globals);
    }

    public <A> Pattern1Def<A> from(DataSource<A> dataSource) {
        return ruleDefinition.from(dataSource);
    }

    public <A, B> Pattern1Def<B> accumulate(Pattern1Def<A> pattern, Accumulator1<A, B> acc) {
        return ruleDefinition.accumulate(pattern, acc);
    }

    public <A, B, C> Pattern1Def<C> accumulate(Pattern2Def<A, B> pattern, Accumulator1<B, C> acc) {
        return ruleDefinition.accumulate(pattern, acc);
    }

    public <T> void execute(T globalObject, Block1<T> block) {
        ruleDefinition.setConsequence( globalObject, block );
    }

    Rule toRule() {
        return ruleDefinition.toRule(name);
    }
}
