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

import org.drools.reteoo.Sink;

public class RightTupleKey {
    private final int  id;
    private final Sink sink;

    public RightTupleKey(int id,
                         Sink sink) {
        super();
        this.id = id;
        this.sink = sink;
    }

    public int getId() {
        return id;
    }

    public Sink getSink() {
        return sink;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((sink!=null) ? sink.getId() : 17 );
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        
        final RightTupleKey other = (RightTupleKey) obj;
        if ( id != other.id ) return false;
        if ( sink == null ) {
            if ( other.sink != null ) return false;
        } else if ( sink.getId() != other.sink.getId() ) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "RightTupleKey( id="+id+" sink="+sink+" )";
    }

}
