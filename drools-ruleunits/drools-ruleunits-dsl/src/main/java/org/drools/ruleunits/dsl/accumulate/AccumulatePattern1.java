package org.drools.ruleunits.dsl.accumulate;

import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.patterns.Pattern1;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.declarationOf;

public class AccumulatePattern1<A, B> extends Pattern1<B> {

    private final Pattern1<A> pattern;
    private final Accumulator1<A, B> acc;

    public AccumulatePattern1(RuleFactory.RuleDefinition rule, Pattern1<A> pattern, Accumulator1<A, B> acc) {
        super(rule, declarationOf( (Class<B>) acc.getAccClass() ));
        this.pattern = pattern;
        this.acc = acc;
    }

    @Override
    public RuleItemBuilder toExecModelItem() {
        PatternDSL.PatternDef patternDef = (PatternDSL.PatternDef) pattern.toExecModelItem();
        Variable<B> boundVar = getVariable();
        patternDef.bind(boundVar, acc.getBindingFunc());
        return accumulate( patternDef, accFunction(acc.getAccFuncSupplier(), boundVar).as(getVariable()) );
    }
}