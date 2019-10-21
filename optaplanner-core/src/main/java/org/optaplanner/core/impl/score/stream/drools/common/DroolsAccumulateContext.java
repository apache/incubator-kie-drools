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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.stream.drools.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

/**
 * Each context is uniquely identified by its {@link System#identityHashCode(Object)}.
 * This is necessary so that the Drools accumulate function can properly undo in
 * {@link DroolsUniAccumulateFunctionBridge} and its Bi, Tri, ... alternatives.
 * @param <ResultContainer_> The same type from {@link UniConstraintCollector} and its Bi, Tri, ... alternatives.
 */
public final class DroolsAccumulateContext<ResultContainer_> implements Serializable {

    private final ResultContainer_ container;
    private final Map<Object, Runnable> undoMap = new HashMap<>();

    public DroolsAccumulateContext(ResultContainer_ container) {
        this.container = container;
    }

    public ResultContainer_ getContainer() {
        return container;
    }

    public Map<Object, Runnable> getUndoMap() {
        return undoMap;
    }
}
