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

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

import org.kie.api.runtime.rule.AccumulateFunction;

public abstract class DroolsAbstractAccumulateFunction<ResultContainer_, InTuple, OutTuple>
        implements AccumulateFunction<DroolsAccumulateContext<ResultContainer_>> {

    @Override
    public DroolsAccumulateContext<ResultContainer_> createContext() {
        return new DroolsAccumulateContext<>(newContainer());
    }

    @Override
    public void init(DroolsAccumulateContext<ResultContainer_> context) {
        context.getUndoMap().clear();
    }

    @Override
    public void accumulate(DroolsAccumulateContext<ResultContainer_> context, Object value) {
        Map<Object, Runnable> undoMap = context.getUndoMap();
        if (undoMap.containsKey(value)) {
            throw new IllegalStateException("Undo for (" + value + ") already exists.");
        }
        Runnable undo = accumulate(context.getContainer(), (InTuple) value);
        undoMap.put(value, undo);
    }

    @Override
    public void reverse(DroolsAccumulateContext<ResultContainer_> context, Object value) {
        Runnable undo = context.getUndoMap().remove(value);
        if (undo == null) {
            throw new IllegalStateException("Undo for (" + value + ") does not exist.");
        }
        undo.run();
    }

    @Override
    public Object getResult(DroolsAccumulateContext<ResultContainer_> context) {
        return getResult(context.getContainer());
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public Class<?> getResultType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeExternal(ObjectOutput out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readExternal(ObjectInput in) {
        throw new UnsupportedOperationException();
    }

    protected abstract ResultContainer_ newContainer();

    protected abstract Runnable accumulate(ResultContainer_ container, InTuple tuple);

    protected abstract OutTuple getResult(ResultContainer_ container);

}
