package org.drools.ruleunits.dsl.accumulate;

import org.drools.model.DSL;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.patterns.Pattern1DefImpl;
import org.drools.ruleunits.dsl.patterns.InternalPatternDef;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.declarationOf;

public class AccumulatePattern1<A, B> extends Pattern1DefImpl<B> {

    private final InternalPatternDef pattern;
    private final Accumulator1<A, B> acc;

    public AccumulatePattern1(RuleDefinition rule, InternalPatternDef pattern, Accumulator1<A, B> acc) {
        super(rule, declarationOf( acc.getAccClass() ));
        this.pattern = pattern;
        this.acc = acc;
    }

    @Override
    public ViewItem toExecModelItem() {
        return createAccumulate1Item(pattern, getVariable(), acc);
    }

    static ExprViewItem<Object> createAccumulate1Item(InternalPatternDef pattern, Variable variable, Accumulator1 acc) {
        ViewItem patternDef = pattern.toExecModelItem();
        Variable boundVar = declarationOf( acc.getAccClass());
        bindAccVar(acc, patternDef, boundVar);
        return DSL.accumulate(patternDef, accFunction(acc.getAccFuncSupplier(), boundVar).as(variable));
    }

    static void bindAccVar(Accumulator1 acc, ViewItem patternDef, Variable boundVar) {
        if (patternDef instanceof PatternDSL.PatternDef) {
            ((PatternDSL.PatternDef) patternDef).bind(boundVar, acc.getBindingFunc());
        } else {
            ViewItem[] items = ((CombinedExprViewItem) patternDef).getExpressions();
            bindAccVar(acc, items[items.length-1], boundVar);
        }
    }
}