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

package org.drools.reteoo;

import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;

/**
 * ObjectSinkList
 * An interface for object sink lists
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 06/march/2006
 */
public interface ObjectSinkList {

    /**
     * Returns true if the ObjectSinkList already contains the given object sink
     * @param element
     * @return
     */
    public boolean contains(ObjectSink objectSink);

    /**
     * Adds the given objectSink to the list
     * @param objectSink
     * @return
     */
    public boolean add(ObjectSink objectSink);

    /**
     * Removes the given objectSink from the list
     * @param objectSink
     * @return
     */
    public boolean remove(ObjectSink objectSink);
    
    /**
     * Returns the number of ObjectSinks in this list
     * @return
     */
    public int size();

    /**
     * Returns the last added object sink.
     * 
     * @return
     */
    public ObjectSink getLastObjectSink();

    /**
     * Iterates over all matching (in case of hashed list) object Sinks
     * 
     * @param workingMemory
     * @param handle
     * @return
     */
    public Iterator iterator(WorkingMemory workingMemory,
                             FactHandleImpl handle);

    /**
     * Iterates over all  object Sinks
     * 
     * @return
     */
    public Iterator iterator();

    /**
     * Returns a list with all object sinks
     * This may be an inneficient method to call, so we recomend using it only for
     * tests and debug purposes
     * Also, it returns an unmodifiable list to prevent misuse
     * 
     * @return
     */
    public List getObjectsAsList();

}
