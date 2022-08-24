package org.drools.ruleunits.dsl.accumulate;

import org.drools.model.DSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.patterns.InternalPatternDef;
import org.drools.ruleunits.dsl.patterns.Pattern1DefImpl;
import org.drools.ruleunits.dsl.patterns.Pattern2DefImpl;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.declarationOf;
import static org.drools.ruleunits.dsl.accumulate.AccumulatePattern1.bindAccVar;

public class GroupByPattern<A, K, V> extends Pattern2DefImpl<K, V> {

    private final InternalPatternDef pattern;
    private final Function1<A, K> groupingFunction;
    private final Accumulator1<A, V> acc;

    public GroupByPattern(RuleDefinition rule, InternalPatternDef pattern, Function1<A, K> groupingFunction, Accumulator1<A, V> acc) {
        super(rule, new Pattern1DefImpl(rule, declarationOf( Object.class )), new Pattern1DefImpl(rule, declarationOf( Object.class )));
        this.pattern = pattern;
        this.groupingFunction = groupingFunction;
        this.acc = acc;
    }

    @Override
    public ViewItem toExecModelItem() {
        ViewItem patternDef = pattern.toExecModelItem();
        Variable boundVar = declarationOf( acc.getAccClass() );
        bindAccVar(acc, patternDef, boundVar);
        return DSL.groupBy(
                // Patterns
                patternDef,
                // Grouping Function
                patternDef.getFirstVariable(), patternA.getVariable(), groupingFunction,
                // Accumulate Result
                accFunction(acc.getAccFuncSupplier(), boundVar).as(patternB.getVariable()));
    }
}
