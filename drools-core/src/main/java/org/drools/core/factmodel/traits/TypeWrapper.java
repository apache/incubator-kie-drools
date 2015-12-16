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

package org.drools.core.factmodel.traits;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TypeWrapper implements Externalizable {

    private transient Class klass;
    private String name;

    public TypeWrapper() {
    }

    public TypeWrapper( Class klass ) {
        this.klass = klass;
        this.name = klass.getName();
    }

    public Class getKlass() {
        return klass;
    }

    public void setKlass( Class klass ) {
        this.klass = klass;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        TypeWrapper that = (TypeWrapper) o;

        if ( !name.equals( that.name ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject( name );
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
    }

    @Override
    public String toString() {
        return "Wrapper{" + name + "}";
    }
}
