package org.drools.modelcompiler.constraints;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;
import org.drools.model.Binding;

public class LambdaAccumulator implements Accumulator {

    private final org.kie.api.runtime.rule.AccumulateFunction accumulateFunction;
    private final Binding binding;
    private final List<String> sourceVariables;

    public LambdaAccumulator(org.kie.api.runtime.rule.AccumulateFunction accumulateFunction, Binding binding, List<String> sourceVariables) {
        this.accumulateFunction = accumulateFunction;
        this.binding = binding;
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
            final Serializable originalContext = accumulateFunction.createContext();
            accumulateFunction.init(originalContext);
            return originalContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) throws Exception {
    }

    @Override
    public void accumulate(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) throws Exception {
        final Object accumulateObject = handle.getObject();
        final Object returnObject;

        if(binding != null) {
            if (accumulateObject instanceof SubnetworkTuple) {
                final Object[] args = Arrays.stream(innerDeclarations)
                        .filter(d -> sourceVariables.contains(d.getIdentifier()))
                        .map(d -> ((SubnetworkTuple) accumulateObject).getObject(d)).toArray();

                returnObject = binding.eval(args);
            } else {
                returnObject = binding.eval(accumulateObject);
            }
        } else {
            if (accumulateObject instanceof SubnetworkTuple) {
                returnObject = (((SubnetworkTuple) accumulateObject)).getObject(innerDeclarations[0]);
            } else {
                returnObject = accumulateObject;
            }
        }

        accumulateFunction.accumulate((Serializable) context, returnObject);
    }

    @Override
    public boolean supportsReverse() {
        return accumulateFunction.supportsReverse();
    }

    @Override
    public void reverse(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) throws Exception {
        accumulateFunction.reverse((Serializable) context, handle.getObject());
    }

    @Override
    public Object getResult(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) throws Exception {
        return accumulateFunction.getResult((Serializable) context);
    }
}
