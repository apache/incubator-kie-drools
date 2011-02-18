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

package org.drools.marshalling.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;

public class IdentityPlaceholderResolverStrategy
    implements
    ObjectMarshallingStrategy {
    
    private Map<Integer, Object> ids;
    private Map<Object, Integer> objects;

    private ObjectMarshallingStrategyAcceptor acceptor;
    
    public IdentityPlaceholderResolverStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        this.acceptor = acceptor;
        this.ids = new HashMap<Integer, Object>();
        this.objects = new IdentityHashMap<Object, Integer>();
    }

    public Object read(ObjectInputStream os) throws IOException,
                                                       ClassNotFoundException {
        int id = os.readInt();
        return  ids.get( id );
    }

    public void write(ObjectOutputStream os,
                      Object object) throws IOException {
        Integer id = ( Integer ) objects.get( object );
        if ( id == null ) {
            id = ids.size();
            ids.put( id, object );
            objects.put(  object, id );
        }
        os.writeInt( id );
    }

    public boolean accept(Object object) {
        return this.acceptor.accept( object );
    }
}
