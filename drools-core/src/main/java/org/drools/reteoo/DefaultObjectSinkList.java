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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;

/**
 * DefaultObjectSinkList
 * A default implementation for DefaultObjectSinkList
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a>
 *
 * Created: 06/march/2006
 */
public class DefaultObjectSinkList extends ArrayList
    implements
    ObjectSinkList {

    public DefaultObjectSinkList() {
        super();
    }

    public DefaultObjectSinkList(int size) {
        super( size );
    }

    public ObjectSink getLastObjectSink() {
        return (ObjectSink) super.get( this.size() - 1 );
    }

    public List getObjectsAsList() {
        return (List) this;
    }

    public boolean add(ObjectSink objectSink) {
        return super.add( (Object) objectSink );
    }

    public boolean contains(ObjectSink objectSink) {
        return super.contains( (Object) objectSink );
    }

    public boolean remove(ObjectSink objectSink) {
        return super.remove( (Object) objectSink );
    }

    public Iterator iterator(WorkingMemory workingMemory,
                             FactHandleImpl handle) {
        return super.iterator();
    }

}
