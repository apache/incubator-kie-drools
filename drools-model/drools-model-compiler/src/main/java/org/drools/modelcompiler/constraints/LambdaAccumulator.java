package org.drools.modelcompiler.constraints;

import java.io.Serializable;
import java.util.Arrays;

import org.drools.core.WorkingMemory;
import org.drools.core.base.accumulators.AbstractAccumulateFunction;
import org.drools.core.base.accumulators.AverageAccumulateFunction;
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
import org.drools.model.functions.accumulate.UserDefinedAccumulateFunction;

public class LambdaAccumulator implements Accumulator {

    private AccumulateFunction accumulateFunction;
    private final AbstractAccumulateFunction originalAccumulateFunction;
    private UserDefinedAccumulateFunction userDefinedAccumulateFunction;
    private final Pattern sourcePattern;

    public LambdaAccumulator(AccumulateFunction accumulateFunction, Pattern sourcePattern) {
        this.accumulateFunction = accumulateFunction;
        if (accumulateFunction instanceof Sum) {
            originalAccumulateFunction = new IntegerSumAccumulateFunction();
        } else {
            originalAccumulateFunction = null;
        }
        this.sourcePattern = sourcePattern;
    }

    public LambdaAccumulator(UserDefinedAccumulateFunction userDefinedAccumulateFunction, Pattern sourcePattern) {
        this.userDefinedAccumulateFunction = userDefinedAccumulateFunction;
        if (userDefinedAccumulateFunction.getFunctionName().equals("sum")) {
            originalAccumulateFunction = new IntegerSumAccumulateFunction();
        } else if (userDefinedAccumulateFunction.getFunctionName().equals("average")) {
            originalAccumulateFunction = new AverageAccumulateFunction();
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
        if (originalAccumulateFunction != null) {
            try {
                final Serializable originalContext = originalAccumulateFunction.createContext();
                originalAccumulateFunction.init(originalContext);
                return originalContext;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (accumulateFunction != null) {
            final Serializable init = accumulateFunction.init();
            return init;
        }
        return null;
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
            Binding b = (Binding) sourcePattern.getBindings().iterator().next();
            Object object = ((SubnetworkTuple) accumulateObject).getObject(declaration);
            Object result = b.getBindingFunction().apply(object);
            returnObject = result;
        } else {
            Binding b = (Binding) sourcePattern.getBindings().iterator().next();
            Object result = b.getBindingFunction().apply(accumulateObject);
            returnObject = result;
        }
        if (originalAccumulateFunction != null) {
//            accumulateFunction.action((Serializable)context, returnObject);
            originalAccumulateFunction.accumulate((Serializable) context, returnObject);
        } else {
            accumulateFunction.action((Serializable) context, returnObject);
        }
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public void reverse(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) throws Exception {
        accumulateFunction.reverse((Serializable) context, handle.getObject());
    }

    @Override
    public Object getResult(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) throws Exception {
        if (originalAccumulateFunction != null) {
            return originalAccumulateFunction.getResult((Serializable) context);
        }
        return accumulateFunction.result((Serializable) context);
    }
}
