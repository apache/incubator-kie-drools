package org.drools.ruleunits.dsl.accumulate;

import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.patterns.Pattern1DefImpl;
import org.drools.ruleunits.dsl.patterns.Pattern2DefImpl;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.ruleunits.dsl.accumulate.AccumulatePattern1.toAccumulate1Item;

public class AccumulatePattern2<A, B, C> extends Pattern2DefImpl<A, C> {

    private final Accumulator1<B, C> acc;

    public AccumulatePattern2(RuleDefinition rule, Pattern1DefImpl<A> patternA, Pattern1DefImpl<C> patternC, Accumulator1<B, C> acc) {
        super(rule, patternA, patternC);
        this.acc = acc;
    }

    @Override
    public ViewItem toExecModelItem() {
        return toAccumulate1Item(patternB, getVariable(), acc);
    }

    public static <A, B, C> AccumulatePattern2<A, B, C> createAccumulatePattern2(RuleDefinition rule, Pattern1DefImpl<A> patternA, Pattern1DefImpl<B> patternB, Accumulator1<B, C> acc) {
        return new AccumulatePattern2<>(rule, patternA, (Pattern1DefImpl<C>) patternB, acc);
    }
}