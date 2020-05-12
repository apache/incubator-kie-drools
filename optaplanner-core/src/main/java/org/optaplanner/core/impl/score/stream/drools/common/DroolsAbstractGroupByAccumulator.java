/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.stream.drools.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;
import org.drools.model.Variable;

public abstract class DroolsAbstractGroupByAccumulator<InTuple> implements Accumulator {

    /**
     * This will memoize the map based on the input array of declarations. For each accumulate, there should only be
     * one, but this code is being called so often that the memoization has a meaningful impact.
     */
    private final Function<Declaration[], Map<String, Declaration>> memoizingDeclarationMapper = Memoizer
            .memoize(DroolsAbstractGroupByAccumulator::getDeclarationMap);

    private static Map<String, Declaration> getDeclarationMap(Declaration... declarations) {
        return Arrays.stream(declarations)
                .collect(Collectors.toMap(Declaration::getIdentifier, Function.identity()));
    }

    private static boolean isCorrectType(Object object, Variable var) {
        return Objects.equals(object.getClass(), var.getType());
    }

    protected static <X, X2> X materialize(Variable<X> var, Function<Variable<X2>, X2> valueFinder) {
        return (X) valueFinder.apply((Variable<X2>) var);
    }

    private Declaration getVariableDeclaration(Variable variable, Declaration... declarations) {
        if (declarations.length == 1) {
            return declarations[0];
        } else {
            Map<String, Declaration> declarationMap = memoizingDeclarationMapper.apply(declarations);
            return declarationMap.get(variable.getName());
        }
    }

    protected <X> X getValue(Variable<X> var, InternalWorkingMemory internalWorkingMemory, Object handleObject,
            Declaration... declarations) {
        if (isCorrectType(handleObject, var)) {
            return (X) handleObject;
        }
        Declaration varDeclaration = getVariableDeclaration(var, declarations);
        Object actualHandleObject = handleObject;
        if (handleObject instanceof SubnetworkTuple) {
            actualHandleObject = ((SubnetworkTuple) handleObject).getObject(varDeclaration);
            if (isCorrectType(actualHandleObject, var)) {
                return (X) actualHandleObject;
            }
        }
        // The variable is likely a result of applying a lambda on a FactTuple.
        return (X) varDeclaration.getValue(internalWorkingMemory, actualHandleObject);
    }

    @Override
    public Serializable createContext() {
        return newContext();
    }

    @Override
    public void init(Object workingMemoryContext, Object context, Tuple tuple, Declaration[] declarations,
            WorkingMemory workingMemory) {
        castContext(context).init();
    }

    @Override
    public void accumulate(Object workingMemoryContext, Object context, Tuple tuple, InternalFactHandle handle,
            Declaration[] declarations, Declaration[] innerDeclarations, final WorkingMemory workingMemory) {
        InternalWorkingMemory internalWorkingMemory = (InternalWorkingMemory) workingMemory;
        Object handleObject = handle.getObject();
        InTuple input = createInput(var -> getValue(var, internalWorkingMemory, handleObject, innerDeclarations));
        castContext(context).accumulate(handle, input);
    }

    private DroolsAbstractGroupBy<InTuple, ?> castContext(Object context) {
        return (DroolsAbstractGroupBy<InTuple, ?>) context;
    }

    @Override
    public void reverse(Object workingMemoryContext, Object context, Tuple tuple, InternalFactHandle handle,
            Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) {
        castContext(context).reverse(handle);
    }

    @Override
    public Object getResult(Object workingMemoryContext, Object context, Tuple tuple, Declaration[] declarations,
            WorkingMemory workingMemory) {
        return castContext(context).getResult();
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public Object createWorkingMemoryContext() {
        return null;
    }

    protected abstract DroolsAbstractGroupBy<InTuple, ?> newContext();

    protected abstract <X> InTuple createInput(Function<Variable<X>, X> valueFinder);

}
