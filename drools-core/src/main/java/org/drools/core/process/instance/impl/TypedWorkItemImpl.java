/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.core.process.instance.impl;

import java.util.Map;
import java.util.function.Supplier;

import org.drools.core.process.instance.TypedWorkItem;

/**
 * A TypedWorkItem implementation, using arbitrary classes
 * to represent parameters and results.
 */
public class TypedWorkItemImpl<P, R> extends WorkItemImpl implements TypedWorkItem<P, R> {

    private final OverflowingBeanMap<P> parametersMap;
    private final OverflowingBeanMap<R> resultsMap;

    public TypedWorkItemImpl(P parameters) {
        this(parameters, null);
    }

    public TypedWorkItemImpl(P parameters, R results) {
        super();
        this.parametersMap = new OverflowingBeanMap<>(parameters);
        this.resultsMap = new OverflowingBeanMap<>(results);
        super.setParameters(parametersMap);
        super.setResults(resultsMap);
    }

    public TypedWorkItemImpl(Supplier<P> parametersSupplier, Supplier<R> resultsSupplier) {
        this(parametersSupplier.get(),
             resultsSupplier.get());
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        parametersMap.putAll(parameters);
    }

    @Override
    public void setResults(Map<String, Object> results) {
        resultsMap.putAll(results);
    }

    @Override
    public P getTypedParameters() {
        return parametersMap.getBean();
    }

    @Override
    public R getTypedResults() {
        return resultsMap.getBean();
    }
}
