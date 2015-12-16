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

package org.drools.core.event;

import java.util.Iterator;

import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.event.rule.impl.ObjectDeletedEventImpl;
import org.drools.core.event.rule.impl.ObjectInsertedEventImpl;
import org.drools.core.event.rule.impl.ObjectUpdatedEventImpl;
import org.drools.core.spi.PropagationContext;
import org.kie.api.event.rule.RuleRuntimeEventListener;

public class RuleRuntimeEventSupport extends AbstractEventSupport<RuleRuntimeEventListener> {

    public void fireObjectInserted(final PropagationContext propagationContext,
                                   final FactHandle handle,
                                   final Object object,
                                   final InternalWorkingMemory workingMemory) {
        final Iterator<RuleRuntimeEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            ObjectInsertedEventImpl event = new ObjectInsertedEventImpl(workingMemory,
                                                                        propagationContext,
                                                                        handle,
                                                                        object);

            do {
                iter.next().objectInserted(event);
            } while (iter.hasNext());
        }
    }

    public void fireObjectUpdated(final PropagationContext propagationContext,
                                  final FactHandle handle,
                                  final Object oldObject,
                                  final Object object,
                                  final InternalWorkingMemory workingMemory) {
        final Iterator<RuleRuntimeEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            ObjectUpdatedEventImpl event = new ObjectUpdatedEventImpl(workingMemory,
                                                                      propagationContext,
                                                                      handle,
                                                                      oldObject,
                                                                      object);

            do {
                iter.next().objectUpdated(event);
            } while (iter.hasNext());
        }
    }

    public void fireObjectRetracted(final PropagationContext propagationContext,
                                    final FactHandle handle,
                                    final Object oldObject,
                                    final InternalWorkingMemory workingMemory) {
        final Iterator<RuleRuntimeEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            ObjectDeletedEventImpl event = new ObjectDeletedEventImpl(workingMemory,
                                                                      propagationContext,
                                                                      handle,
                                                                      oldObject);

            do {
                iter.next().objectDeleted(event);
            } while (iter.hasNext());
        }
    }

    public void reset() {
        this.clear();
    }
}
