package org.drools.modelcompiler.constraints;

import java.io.Serializable;
import java.util.Arrays;

import org.drools.core.WorkingMemory;
import org.drools.core.base.accumulators.AbstractAccumulateFunction;
import org.drools.core.base.accumulators.IntegerSumAccumulateFunction;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;
import org.drools.model.AccumulateFunction;
import org.drools.model.Binding;
import org.drools.model.Pattern;
import org.drools.model.functions.accumulate.Sum;

public class LambdaAccumulator implements Accumulator {

    private final AccumulateFunction accumulateFunction;
    private final AbstractAccumulateFunction originalAccumulateFunction;
    private final Pattern sourcePattern;

    public LambdaAccumulator(AccumulateFunction accumulateFunction, Pattern sourcePattern) {
        this.accumulateFunction = accumulateFunction;
        if(accumulateFunction instanceof Sum) {
            originalAccumulateFunction = new IntegerSumAccumulateFunction();
        } else {
            originalAccumulateFunction = null;
        }
        this.sourcePattern = sourcePattern;
    }

    @Override
    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }

    @Override
    public Serializable createContext() {
        final Serializable init = accumulateFunction.init();
        if(originalAccumulateFunction != null) {
            try {
                final Serializable originalContext = originalAccumulateFunction.createContext();
                originalAccumulateFunction.init(originalContext);
                return originalContext;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return init;
    }

    @Override
    public void init(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) throws Exception {
    }

    @Override
    public void accumulate(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) throws Exception {
        final Object accumulateObject = handle.getObject();
        final Object returnObject;
        if (accumulateObject instanceof SubnetworkTuple) {
            Declaration declaration = Arrays.stream(innerDeclarations).filter(d -> d.getIdentifier().equals(sourcePattern.getPatternVariable().getName())).findFirst().orElseThrow(RuntimeException::new);
            Binding b = (Binding)sourcePattern.getBindings().iterator().next();
            Object object = ((SubnetworkTuple) accumulateObject).getObject(declaration);
            Object result = b.getBindingFunction().apply(object);
            returnObject = result;
        } else {
            Binding b = (Binding)sourcePattern.getBindings().iterator().next();
            Object result = b.getBindingFunction().apply(accumulateObject);
            returnObject = result;
        }
        if(accumulateFunction instanceof Sum) {
//            accumulateFunction.action((Serializable)context, returnObject);
            originalAccumulateFunction.accumulate((Serializable)context, returnObject);
        } else {
            accumulateFunction.action((Serializable)context, returnObject);
        }
    }

    private Declaration declarationWithName(Declaration[] innerDeclarations, String variableName) {
        return Arrays.stream(innerDeclarations)
                .filter(d -> d.getIdentifier().equals(variableName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find declaration with name: " + variableName));
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public void reverse(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) throws Exception {
        accumulateFunction.reverse((Serializable)context, handle.getObject());
    }

    @Override
    public Object getResult(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) throws Exception {
        if(originalAccumulateFunction != null) {
            return originalAccumulateFunction.getResult((Serializable) context);
        }
        return accumulateFunction.result((Serializable)context);
    }
}
