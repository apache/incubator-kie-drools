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

package org.drools.compiler.phreak;

import org.kie.api.definition.type.Position;

public class A {

    @Position(0)
    Integer object;

    public A(Integer object) {
        super();
        this.object = object;
    }
    
    public static A a(Integer object) {
        return new A( object );
    }

    public static A[] a(Integer... objects) {
        A[] as = new A[objects.length];
        int i = 0;
        for ( Integer object : objects ) {
            as[i++] = new A( object );
        }
        return as;
    }        

    public Object getObject() {
        return object;
    }

    public void setObject(Integer object) {
        this.object = object;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        A other = (A) obj;
        if ( object == null ) {
            if ( other.object != null ) return false;
        } else if ( !object.equals( other.object ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "A[" + object + "]";
    }

}
