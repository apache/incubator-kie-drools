package org.drools.ruleunits.dsl.accumulate;

import org.drools.model.functions.Function1;
import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.patterns.InternalPatternDef;
import org.drools.ruleunits.dsl.patterns.Pattern1DefImpl;
import org.drools.ruleunits.dsl.patterns.Pattern3DefImpl;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.model.DSL.declarationOf;
import static org.drools.ruleunits.dsl.accumulate.GroupByPattern1.createGroupByItem;

public class GroupByPattern2<A, B, K, V> extends Pattern3DefImpl<A, K, V> {

    private final InternalPatternDef pattern;
    private final Function1<B, K> groupingFunction;
    private final Accumulator1<B, V> acc;

    public GroupByPattern2(RuleDefinition rule, Pattern1DefImpl<A> patternA, InternalPatternDef pattern, Function1<B, K> groupingFunction, Accumulator1<B, V> acc) {
        super(rule, patternA, new Pattern1DefImpl(rule, declarationOf( Object.class )), new Pattern1DefImpl(rule, declarationOf( Object.class )));
        this.pattern = pattern;
        this.groupingFunction = groupingFunction;
        this.acc = acc;
    }

    @Override
    public ViewItem toExecModelItem() {
        return createGroupByItem(pattern, groupingFunction, acc, patternB.getVariable(), patternC.getVariable());
    }
}
