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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.common.InternalFactHandle;

public abstract class DroolsAbstractGroupBy<InTuple, OutTuple> implements Serializable {

    private static final long serialVersionUID = 510l;
    private final Map<Long, Runnable> undoMap = new HashMap<>(0);
    private GroupByCollectorProcessor<InTuple, OutTuple> acc;

    public void init() {
        acc = newAccumulator();
        undoMap.clear();
    }

    public void accumulate(InternalFactHandle handle, InTuple input) {
        Runnable undo = acc.accumulate(input);
        Runnable oldUndo = this.undoMap.put(handle.getId(), undo);
        if (oldUndo != null) {
            throw new IllegalStateException("Undo for fact handle (" + handle.getId() + ") already exists.");
        }
    }

    public void reverse(InternalFactHandle handle) {
        final Runnable undo = this.undoMap.remove(handle.getId());
        if (undo == null) {
            throw new IllegalStateException("No undo for fact handle (" + handle.getId() + ")");
        }
        undo.run();
    }

    public Collection<OutTuple> getResult() {
        return acc.finish();
    }

    protected abstract GroupByCollectorProcessor<InTuple, OutTuple> newAccumulator();

}
