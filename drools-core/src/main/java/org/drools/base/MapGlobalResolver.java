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

package org.drools.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.runtime.Globals;
import org.drools.spi.GlobalResolver;

public class MapGlobalResolver
    implements
    GlobalResolver, Globals,
    Externalizable {

    private static final long serialVersionUID = 510l;

    private Map<String,Object> map;
    
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

    public Collection<String> getGlobalKeys() {
        if ( delegate == null ) {
            return Collections.unmodifiableCollection( map.keySet() );
        } else if ( delegate != null && map.size() == 0 ) {
            return Collections.unmodifiableCollection( ((MapGlobalResolver) delegate).map.keySet() );
        } else {
            Collection<String> combined = new HashSet<String>( map.keySet() );
            combined.addAll( ((MapGlobalResolver) delegate).map.keySet() );
            return Collections.unmodifiableCollection( combined );
        }
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

    public Entry<String,Object>[] getGlobals() {
        if ( delegate == null ) {
            return (Entry[]) this.map.entrySet().toArray(new Entry[this.map.size()]);
        } else if ( delegate != null && map.size() == 0 ) {
            Map<String,Object> delegateMap = ((MapGlobalResolver) delegate).map;
            return (Entry[]) delegateMap.entrySet().toArray(new Entry[delegateMap.size()]);
        } else {
            Map<String,Object> combined = new HashMap<String,Object>( ((MapGlobalResolver) delegate).map );
            combined.putAll( map );
            return (Entry[]) combined.entrySet().toArray(new Entry[combined.size()]);
        }
    }
    
    public GlobalResolver clone() {
        Map<String,Object> clone = new HashMap();
        
        for ( Entry<String,Object> entry : getGlobals() ) {
            clone.put( entry.getKey(), entry.getValue() );
        }
        return new MapGlobalResolver( clone );
    }

    @Override
    public String toString() {
        return "MapGlobalResolver [map=" + map + ", delegate=" + delegate + "]";
    }

}
