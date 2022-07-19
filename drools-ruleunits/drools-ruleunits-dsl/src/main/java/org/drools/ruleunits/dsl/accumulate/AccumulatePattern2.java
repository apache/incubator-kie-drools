package org.drools.ruleunits.dsl.accumulate;

import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.patterns.Pattern1Def;
import org.drools.ruleunits.dsl.patterns.Pattern2Def;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.ruleunits.dsl.accumulate.AccumulatePattern1.toAccumulate1Item;

public class AccumulatePattern2<A, B, C> extends Pattern2Def<A, C> {

    private final Accumulator1<B, C> acc;

    public AccumulatePattern2(RuleDefinition rule, Pattern1Def<A> patternA, Pattern1Def<C> patternC, Accumulator1<B, C> acc) {
        super(rule, patternA, patternC);
        this.acc = acc;
    }

    @Override
    public ViewItem toExecModelItem() {
        return toAccumulate1Item(patternB, getVariable(), acc);
    }

    public static <A, B, C> AccumulatePattern2<A, B, C> createAccumulatePattern2(RuleDefinition rule, Pattern1Def<A> patternA, Pattern1Def<B> patternB, Accumulator1<B, C> acc) {
        return new AccumulatePattern2<>(rule, patternA, (Pattern1Def<C>) patternB, acc);
    }
}