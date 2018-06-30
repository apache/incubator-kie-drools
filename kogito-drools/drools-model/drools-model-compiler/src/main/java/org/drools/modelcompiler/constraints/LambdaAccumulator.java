package org.drools.modelcompiler.constraints;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;

public abstract class LambdaAccumulator implements Accumulator {

    private final org.kie.api.runtime.rule.AccumulateFunction accumulateFunction;
    protected final List<String> sourceVariables;

    protected LambdaAccumulator(org.kie.api.runtime.rule.AccumulateFunction accumulateFunction, List<String> sourceVariables) {
        this.accumulateFunction = accumulateFunction;
        this.sourceVariables = sourceVariables;
    }

    @Override
    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }

    @Override
    public Serializable createContext() {
        try {
            return accumulateFunction.createContext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) throws Exception {
        accumulateFunction.init((Serializable) context);
    }

    @Override
    public void accumulate(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) throws Exception {
        accumulateFunction.accumulate((Serializable) context, getAccumulatedObject( declarations, innerDeclarations, handle, leftTuple, ( InternalWorkingMemory ) workingMemory ));
    }

    protected abstract Object getAccumulatedObject( Declaration[] declarations, Declaration[] innerDeclarations, InternalFactHandle handle, Tuple tuple, InternalWorkingMemory wm );

    @Override
    public boolean supportsReverse() {
        return accumulateFunction.supportsReverse();
    }

    @Override
    public void reverse(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) throws Exception {
        accumulateFunction.reverse((Serializable) context, getAccumulatedObject( declarations, innerDeclarations, handle, leftTuple, ( InternalWorkingMemory ) workingMemory ));
    }

    @Override
    public Object getResult(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) throws Exception {
        return accumulateFunction.getResult((Serializable) context);
    }

    public static class BindingAcc extends LambdaAccumulator {
        private final BindingEvaluator binding;

        public BindingAcc(org.kie.api.runtime.rule.AccumulateFunction accumulateFunction, List<String> sourceVariables, BindingEvaluator binding) {
            super(accumulateFunction, sourceVariables);
            this.binding = binding;
        }

        @Override
        protected Object getAccumulatedObject( Declaration[] declarations, Declaration[] innerDeclarations, InternalFactHandle handle, Tuple tuple, InternalWorkingMemory wm ) {
            Object accumulateObject = handle.getObject();
            if (accumulateObject instanceof SubnetworkTuple ) {
                Object[] args = new Object[ sourceVariables.size() ];
                for (int i = 0; i < sourceVariables.size(); i++) {
                    for (Declaration d : innerDeclarations) {
                        if (d.getIdentifier().equals( sourceVariables.get(i) )) {
                            args[i] = (( SubnetworkTuple ) accumulateObject).getObject(d);
                            break;
                        }
                    }
                }
                return binding.evaluate(args);
            } else {
                return binding.evaluate(handle, tuple, wm);
            }
        }
    }

    public static class NotBindingAcc extends LambdaAccumulator {

        public NotBindingAcc(org.kie.api.runtime.rule.AccumulateFunction accumulateFunction, List<String> sourceVariables) {
            super(accumulateFunction, sourceVariables);
        }

        @Override
        protected Object getAccumulatedObject( Declaration[] declarations, Declaration[] innerDeclarations, InternalFactHandle handle, Tuple tuple, InternalWorkingMemory wm ) {
            Object accumulateObject = handle.getObject();
            if (accumulateObject instanceof SubnetworkTuple) {
                return (((SubnetworkTuple) accumulateObject)).getObject(declarations[0]);
            } else {
                return accumulateObject;
            }
        }
    }

    public static class FixedValueAcc extends LambdaAccumulator {

        private final Object value;

        public FixedValueAcc(org.kie.api.runtime.rule.AccumulateFunction accumulateFunction, Object value) {
            super(accumulateFunction, Collections.emptyList());
            this.value = value;
        }

        @Override
        protected Object getAccumulatedObject( Declaration[] declarations, Declaration[] innerDeclarations, InternalFactHandle handle, Tuple tuple, InternalWorkingMemory wm ) {
            return value;
        }
    }
}
