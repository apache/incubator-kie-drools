package org.drools.model.codegen.execmodel.generator.operatorspec;

import org.drools.base.rule.accessor.Evaluator;
import org.drools.model.functions.Operator;

import static org.drools.core.common.InternalFactHandle.dummyFactHandleOf;

public class CustomOperatorWrapper implements Operator.SingleValue<Object, Object> {

    private final Evaluator evaluator;
    private final String name;

    public CustomOperatorWrapper( Evaluator evaluator, String name ) {
        this.evaluator = evaluator;
        this.name = name;
    }

    @Override
    public boolean eval( Object o1, Object o2 ) {
        return evaluator.evaluate(null, null, dummyFactHandleOf(o2), null, dummyFactHandleOf(o1));
    }

    @Override
    public String getOperatorName() {
        return name;
    }
}
