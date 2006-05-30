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
import org.drools.common.DefaultFactHandle;

/**
 * DefaultObjectSinkList
 * A default implementation for DefaultObjectSinkList
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 06/march/2006
 */
public class DefaultObjectSinkList extends ArrayList
    implements
    ObjectSinkList {

    /**
     * 
     */
    private static final long serialVersionUID = -414815245412273791L;

    public DefaultObjectSinkList() {
        super();
    }

    public DefaultObjectSinkList(final int size) {
        super( size );
    }

    public ObjectSink getLastObjectSink() {
        return (ObjectSink) super.get( this.size() - 1 );
    }

    public List getObjectsAsList() {
        return this;
    }

    public boolean add(final ObjectSink objectSink) {
        return super.add( objectSink );
    }

    public boolean contains(final ObjectSink objectSink) {
        return super.contains( objectSink );
    }

    public boolean remove(final ObjectSink objectSink) {
        return super.remove( objectSink );
    }

    public Iterator iterator(final WorkingMemory workingMemory,
                             final DefaultFactHandle handle) {
        return super.iterator();
    }

}
