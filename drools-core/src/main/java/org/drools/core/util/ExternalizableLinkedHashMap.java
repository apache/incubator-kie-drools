/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class ExternalizableLinkedHashMap<K extends Comparable,T> extends LinkedHashMap<K,T> implements Externalizable {

    public ExternalizableLinkedHashMap() {

    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeInt( this.size() );
        List<K> keys = new ArrayList<K>( this.keySet() );
        Collections.sort( keys );
        for ( K k : keys ) {
            out.writeObject( k );
            out.writeObject( this.get( k ) );
        }

    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        int n = in.readInt();
        for ( int j = 0; j < n; j++ ) {
            K k = (K) in.readObject();
            T t = (T) in.readObject();
            this.put( k, t );
        }
    }
}
