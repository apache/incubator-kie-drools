package org.drools.modelcompiler.constraints;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;
import org.drools.model.AccumulateFunction;
import org.drools.model.Variable;

public class LambdaAccumulator implements Accumulator {

    private final AccumulateFunction accumulateFunction;

    public LambdaAccumulator( AccumulateFunction accumulateFunction ) {
        this.accumulateFunction = accumulateFunction;
    }

    @Override
    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }

    @Override
    public Serializable createContext() {
        return accumulateFunction.init();
    }

    @Override
    public void init(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) throws Exception {
    }

    @Override
    public void accumulate(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) throws Exception {
        final Optional<Variable<?>> optSource = accumulateFunction.getOptSource();
        final Object object  = optSource.map((Variable<?> source) -> {
            final Declaration decl = declarationWithName(innerDeclarations, source.getName());

            return decl.getValue((InternalWorkingMemory) workingMemory, handle.getObject());
        }).orElseGet(() -> {
            final Object accumulateObject = handle.getObject();
            if(accumulateObject instanceof SubnetworkTuple) {
                final Optional<String> paramName = accumulateFunction.getParamName();
                final String variableName = paramName.orElseThrow(() -> new RuntimeException("Param name unknown"));
                final Declaration declaration = declarationWithName(innerDeclarations, variableName);
                return declaration.getValue((InternalWorkingMemory) workingMemory, ((SubnetworkTuple) accumulateObject).getObject(declaration) );
            } else {
                return accumulateObject;
            }
        });
        accumulateFunction.action((Serializable)context, object);
    }

    private Declaration declarationWithName(Declaration[] innerDeclarations, String variableName) {
        return Arrays.stream(innerDeclarations)
                .filter(d -> d.getIdentifier().equals(variableName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find declaration with name" + variableName));
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
        return accumulateFunction.result((Serializable)context);
    }
}
