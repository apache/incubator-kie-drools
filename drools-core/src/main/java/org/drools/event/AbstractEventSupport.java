/*
 * Copyright 2010 JBoss Inc
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base class for Thread-safe Event Support in Drools. Note that subclasses wishing to access
 * the listeners should do so via the <method>getEventListenersIterator</method> method. This
 * will provide an Iterator accessing the current snapshot of the underlying list, freeing the
 * subclasss of thread problems.
 * <p/>
 * Please note that for lists of small sizes, and few modifications, the CopyOnWriteArrayList
 * provides best performance. If the list is modified more often, than a simple ArrayList
 * with synchonized operations, and copying of the array for iteration is faster.
 *
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 */
public abstract class AbstractEventSupport<E extends EventListener> implements Externalizable {

    private static final long serialVersionUID = 510l;

    private List<E> listeners = new CopyOnWriteArrayList<E>();

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        listeners = (List<E>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(listeners);
    }

    protected final Iterator<E> getEventListenersIterator() {
        return listeners.iterator();
    }

    /**
     * Adds the specified listener to the list of listeners. Note that this method needs to be
     * synchonized because it performs two independent operations on the underlying list
     *
     * @param listener to add
     */
    public final synchronized void addEventListener(final E listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    /**
     * Removes all event listeners of the specified class. Note that this method needs to be
     * synchonized because it performs two independent operations on the underlying list
     *
     * @param cls class of listener to remove
     */
    public final synchronized void removeEventListener(final Class cls) {
        for (int listenerIndex = 0; listenerIndex < this.listeners.size();) {
            E listener = this.listeners.get(listenerIndex);
            
            if (cls.isAssignableFrom(listener.getClass())) {
                this.listeners.remove(listenerIndex);
            } else {
                listenerIndex++;
            }
        }
    }

    public final void removeEventListener(final E listener) {
        this.listeners.remove(listener);
    }

    public List<E> getEventListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    public final int size() {
        return this.listeners.size();
    }

    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }
        
    public void clear() {
        this.listeners.clear();
    }
}
