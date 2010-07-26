/**
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

package org.drools.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.runtime.Globals;
import org.drools.spi.GlobalResolver;

public class MapGlobalResolver
    implements
    GlobalResolver, Globals,
    Externalizable {

    private static final long serialVersionUID = 400L;

    private Map map;
    
    private Globals delegate;

    public MapGlobalResolver() {
        this.map = new HashMap();
    }

    public MapGlobalResolver(Map map) {
        this.map = map;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        map = (Map)in.readObject();
        delegate = ( Globals ) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( map );
        out.writeObject( delegate );
    }
    
    public void setDelegate(Globals delegate) {
        this.delegate = delegate;
    }    
    
    public Object get(String identifier) {
        return resolveGlobal( identifier );
    }

    public Object resolveGlobal(String identifier) {
        Object object = this.map.get( identifier );
        if ( object == null && this.delegate != null ) {
            object = this.delegate.get( identifier );
        }
        return object;
    }
    
    public void set(String identifier, Object value) {
        setGlobal( identifier, value );
    }

    public void setGlobal(String identifier, Object value) {
        this.map.put( identifier,
                      value );
    }

    public Entry[] getGlobals() {
    	return (Entry[]) this.map.entrySet().toArray(new Entry[this.map.size()]);
    }
    
    public GlobalResolver clone() {
        Map clone = new HashMap();
        
        for ( Entry entry : getGlobals() ) {
            clone.put( entry.getKey(), entry.getValue() );
        }
        return new MapGlobalResolver( clone );
    }

}
