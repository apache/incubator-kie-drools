package org.drools.ruleunits.dsl.accumulate;

import org.drools.model.DSL;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.view.ExprViewItem;
import org.drools.ruleunits.dsl.patterns.Pattern1;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.declarationOf;

public class AccumulatePattern1<A, B> extends Pattern1<B> {

    private final Pattern1<A> pattern;
    private final Accumulator1<A, B> acc;

    public AccumulatePattern1(RuleDefinition rule, Pattern1<A> pattern, Accumulator1<A, B> acc) {
        super(rule, declarationOf( acc.getAccClass() ));
        this.pattern = pattern;
        this.acc = acc;
    }

    @Override
    public RuleItemBuilder toExecModelItem() {
        return toAccumulate1Item(pattern, getVariable(), acc);
    }

    static ExprViewItem<Object> toAccumulate1Item(Pattern1 pattern, Variable variable, Accumulator1 acc) {
        PatternDSL.PatternDef patternDef = (PatternDSL.PatternDef) pattern.toExecModelItem();
        Variable boundVar = declarationOf( acc.getAccClass());
        patternDef.bind(boundVar, acc.getBindingFunc());
        return DSL.accumulate(patternDef, accFunction(acc.getAccFuncSupplier(), boundVar).as(variable));
    }
}