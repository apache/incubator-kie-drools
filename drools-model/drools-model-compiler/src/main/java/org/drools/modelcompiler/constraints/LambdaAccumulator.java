package org.drools.modelcompiler.constraints;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;

public abstract class LambdaAccumulator implements Accumulator {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LambdaAccumulator that = (LambdaAccumulator) o;
        return Objects.equals(accumulateFunction, that.accumulateFunction) &&
                Objects.equals(sourceVariables, that.sourceVariables) &&
                Objects.equals(reverseSupport, that.reverseSupport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accumulateFunction, sourceVariables, reverseSupport);
    }

    private final org.kie.api.runtime.rule.AccumulateFunction accumulateFunction;
    protected final List<String> sourceVariables;
    private Map<Long, Object> reverseSupport;


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
        if(supportsReverse()) {
            reverseSupport = new HashMap<>();
        }
    }

    @Override
    public void accumulate(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) throws Exception {
        final Object accumulatedObject = getAccumulatedObject(declarations, innerDeclarations, handle, leftTuple, (InternalWorkingMemory) workingMemory);
        if (supportsReverse()) {
            reverseSupport.put(handle.getId(), accumulatedObject);
        }
        accumulateFunction.accumulate((Serializable) context, accumulatedObject);
    }

    protected abstract Object getAccumulatedObject( Declaration[] declarations, Declaration[] innerDeclarations, InternalFactHandle handle, Tuple tuple, InternalWorkingMemory wm );

    @Override
    public boolean supportsReverse() {
        return accumulateFunction.supportsReverse();
    }

    @Override
    public void reverse(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) throws Exception {
        final Object accumulatedObject = reverseSupport.remove(handle.getId());
        if(accumulatedObject == null) {
            final Object accumulatedObject2 = getAccumulatedObject(declarations, innerDeclarations, handle, leftTuple, (InternalWorkingMemory) workingMemory);
            accumulateFunction.reverse((Serializable) context, accumulatedObject2);
        } else {
            accumulateFunction.reverse((Serializable) context, accumulatedObject);
        }
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
                return binding.evaluate(handle, tuple, wm, declarations, innerDeclarations);
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
            if (accumulateObject instanceof SubnetworkTuple && declarations.length > 0) {
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
