/*
 * Copyright 2005 JBoss Inc
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

package org.drools.event;

import java.util.Iterator;

import org.drools.FactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 */
public class WorkingMemoryEventSupport extends AbstractEventSupport<WorkingMemoryEventListener> {

    public void fireObjectInserted(final PropagationContext propagationContext,
                                   final FactHandle handle,
                                   final Object object,
                                   final InternalWorkingMemory workingMemory) {
        final Iterator<WorkingMemoryEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ObjectInsertedEvent event = new ObjectInsertedEvent(workingMemory,
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
        final Iterator<WorkingMemoryEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ObjectUpdatedEvent event = new ObjectUpdatedEvent(workingMemory,
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
        final Iterator<WorkingMemoryEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final ObjectRetractedEvent event = new ObjectRetractedEvent(workingMemory,
                    propagationContext,
                    handle,
                    oldObject);

            do {
                iter.next().objectRetracted(event);
            } while (iter.hasNext());
        }
    }

    public void reset() {
        this.clear();
    }
}
