package org.drools.modelcompiler.constraints;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.Accumulator;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Collection;
import java.util.function.Supplier;

public class GenericCollectAccumulator implements Accumulator {

    private final Supplier<? extends Collection> collectTargetSupplier;

    public GenericCollectAccumulator(Supplier<? extends Collection> collectTargetSupplier) {
        this.collectTargetSupplier = collectTargetSupplier;
    }

    @Override
    public Object createContext() {
        return null;
    }


    @Override
    public Object init(Object workingMemoryContext,
                       Object context,
                       BaseTuple leftTuple,
                       Declaration[] declarations,
                       ValueResolver valueResolver) {
        return collectTargetSupplier.get();
    }

    @Override
    public Object accumulate(Object workingMemoryContext,
                             Object context,
                             BaseTuple leftTuple,
                             FactHandle handle,
                             Declaration[] declarations,
                             Declaration[] innerDeclarations,
                             ValueResolver valueResolver) {
        Object value = handle.getObject();
        ((Collection) context).add( value );
        return value;
    }

    @Override
    public boolean tryReverse(Object workingMemoryContext,
                              Object context,
                              BaseTuple leftTuple,
                              FactHandle handle,
                              Object value,
                              Declaration[] declarations,
                              Declaration[] innerDeclarations,
                              ValueResolver valueResolver) {
        ((Collection) context).remove( value );
        return true;
    }

    @Override
    public Object getResult(Object workingMemoryContext,
                            Object context,
                            BaseTuple leftTuple,
                            Declaration[] declarations,
                            ValueResolver valueResolver) {
        return context;
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }
}
