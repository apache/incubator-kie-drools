package org.drools.ruleunits.dsl.accumulate;

import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.patterns.Pattern1DefImpl;
import org.drools.ruleunits.dsl.patterns.Pattern2DefImpl;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.ruleunits.dsl.accumulate.AccumulatePattern1.createAccumulate1Item;

public class AccumulatePattern2<A, B, C> extends Pattern2DefImpl<A, C> {

    private final Accumulator1<B, C> acc;

    public AccumulatePattern2(RuleDefinition rule, Pattern1DefImpl<A> patternA, Pattern1DefImpl<C> patternC, Accumulator1<B, C> acc) {
        super(rule, patternA, patternC);
        this.acc = acc;
    }

    @Override
    public ViewItem toExecModelItem() {
        return createAccumulate1Item(patternB, getVariable(), acc);
    }
}