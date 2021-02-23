/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.constraints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;
import org.kie.api.runtime.rule.AccumulateFunction;

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
        return Objects.equals(accumulateFunction, that.accumulateFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accumulateFunction);
    }

    private final AccumulateFunction accumulateFunction;

    protected LambdaAccumulator(AccumulateFunction accumulateFunction) {
        this.accumulateFunction = accumulateFunction;
    }

    @Override
    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }

    @Override
    public Object createContext() {
        return accumulateFunction.createContext();
    }

    @Override
    public Object init(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) {
        context = accumulateFunction.initContext( (Serializable) context );
        return context;
    }

    @Override
    public Object accumulate(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) {
        final Object accumulatedObject = getAccumulatedObject(declarations, innerDeclarations, handle, leftTuple, (InternalWorkingMemory) workingMemory);
        return accumulateFunction.accumulateValue( (Serializable) context, accumulatedObject);
    }

    protected abstract Object getAccumulatedObject( Declaration[] declarations, Declaration[] innerDeclarations, InternalFactHandle handle, Tuple tuple, InternalWorkingMemory wm );

    @Override
    public boolean supportsReverse() {
        return accumulateFunction.supportsReverse();
    }

    @Override
    public boolean tryReverse(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Object value,
                              Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) {
        if (value == null) {
            throw new IllegalStateException("Reversing a not existing accumulated object for fact " + handle);
        }
        return accumulateFunction.tryReverse( (Serializable) context, value);
    }

    @Override
    public Object getResult(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) {
        try {
            return accumulateFunction.getResult( (Serializable) context );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    public static class BindingAcc extends LambdaAccumulator {
        private final BindingEvaluator binding;
        private final Collection<String> sourceVariables;

        public BindingAcc(AccumulateFunction accumulateFunction, Collection<String> sourceVariables, BindingEvaluator binding) {
            super(accumulateFunction);
            this.binding = binding;
            this.sourceVariables = sourceVariables;
        }

        @Override
        protected Object getAccumulatedObject( Declaration[] declarations, Declaration[] innerDeclarations, InternalFactHandle handle, Tuple tuple, InternalWorkingMemory wm ) {
            Object accumulateObject = handle.getObject();
            if (accumulateObject instanceof SubnetworkTuple ) {
                Declaration[] bindingDeclarations = binding.getDeclarations();
                Object[] args;
                if (bindingDeclarations == null || bindingDeclarations.length == 0) {
                    args = new Object[ sourceVariables.size() ];
                    int i = 0;
                    for (String sourceVariable : sourceVariables) {
                        for (Declaration d : innerDeclarations) {
                            if (d.getIdentifier().equals( sourceVariable )) {
                                args[i] = d.getValue( ( SubnetworkTuple ) accumulateObject );
                                break;
                            }
                        }
                        i++;
                    }
                } else { // Return values in the order required by the binding.
                    args = new Object[ bindingDeclarations.length ];
                    for (int i = 0; i < bindingDeclarations.length; i++) {
                        args[i] = bindingDeclarations[i].getValue( ( SubnetworkTuple ) accumulateObject );
                    }
                }
                return binding.evaluate(args);
            } else {
                return binding.evaluate(handle, tuple, wm, declarations, innerDeclarations);
            }
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            final LambdaAccumulator.BindingAcc that = (LambdaAccumulator.BindingAcc) o;
            return Objects.equals(sourceVariables, that.sourceVariables);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), sourceVariables);
        }
    }

    public static class NotBindingAcc extends LambdaAccumulator {

        public NotBindingAcc(AccumulateFunction accumulateFunction) {
            super(accumulateFunction);
        }

        @Override
        protected Object getAccumulatedObject( Declaration[] declarations, Declaration[] innerDeclarations, InternalFactHandle handle, Tuple tuple, InternalWorkingMemory wm ) {
            Object accumulateObject = handle.getObject();
            if (accumulateObject instanceof SubnetworkTuple && declarations.length > 0) {
                return declarations[0].getValue( ( SubnetworkTuple ) accumulateObject );
            } else {
                return accumulateObject;
            }
        }
    }

    public static class FixedValueAcc extends LambdaAccumulator {

        private final Object value;

        public FixedValueAcc(AccumulateFunction accumulateFunction, Object value) {
            super(accumulateFunction);
            this.value = value;
        }

        @Override
        protected Object getAccumulatedObject( Declaration[] declarations, Declaration[] innerDeclarations, InternalFactHandle handle, Tuple tuple, InternalWorkingMemory wm ) {
            return value;
        }
    }

}
