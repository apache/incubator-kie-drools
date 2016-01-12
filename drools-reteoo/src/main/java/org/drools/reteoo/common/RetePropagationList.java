/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.reteoo.common;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.PropagationList;

import java.util.Collections;
import java.util.Iterator;

public class RetePropagationList implements PropagationList {

    private final InternalWorkingMemory workingMemory;

    public RetePropagationList(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    @Override
    public void addEntry(PropagationEntry propagationEntry) {
        propagationEntry.execute(workingMemory);
    }

    @Override
    public PropagationEntry takeAll() {
        return null;
    }

    @Override
    public void flush() { }

    @Override
    public void flush(PropagationEntry currentHead) {

    }

    @Override
    public void waitOnRest() {

    }

    @Override
    public void onEngineInactive() {

    }

    @Override
    public void dispose() { }

    @Override
    public void notifyWaitOnRest() {
        synchronized ( this ) {
            this.notifyAll();
        }
    }

    @Override
    public void reset() { }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Iterator<PropagationEntry> iterator() {
        return Collections.<PropagationEntry>emptyList().iterator();
    }

}
