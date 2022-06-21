package org.drools.ruleunits.dsl;

import java.util.function.Supplier;

import org.drools.core.base.accumulators.IntegerSumAccumulateFunction;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.declarationOf;

public class Accumulators {

    public static <A, B> Accumulator1<A, B> sum(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, IntegerSumAccumulateFunction::new, Integer.class);
    }

    public static class Accumulator1<A, B> {
        private final Function1<A, B> bindingFunc;
        private final Supplier<?> accFuncSupplier;
        private final Class<?> accClass;

        public Accumulator1(Function1<A, B> bindingFunc, Supplier<?> accFuncSupplier, Class<?> accClass) {
            this.bindingFunc = bindingFunc;
            this.accFuncSupplier = accFuncSupplier;
            this.accClass = accClass;
        }
    }

    public static class AccumulatePattern1<A, B> extends RuleFactory.Pattern1<B> {

        private final RuleFactory.Pattern1<A> pattern;
        private final Accumulator1<A, B> acc;

        public AccumulatePattern1(RuleFactory rule, RuleFactory.Pattern1<A> pattern, Accumulator1<A, B> acc) {
            super(rule, declarationOf( (Class<B>) acc.accClass ));
            this.pattern = pattern;
            this.acc = acc;
        }

        @Override
        public RuleItemBuilder toExecModelItem() {
            PatternDSL.PatternDef patternDef = (PatternDSL.PatternDef) pattern.toExecModelItem();
            Variable<B> boundVar = declarationOf( (Class<B>) acc.accClass );
            patternDef.bind(boundVar, acc.bindingFunc);
            return accumulate( patternDef, accFunction(acc.accFuncSupplier, boundVar).as(getVariable()) );
        }
    }

}
