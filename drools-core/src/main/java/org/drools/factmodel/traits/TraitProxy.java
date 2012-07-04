/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.factmodel.traits;

import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;
import org.drools.runtime.rule.Variable;

import java.io.*;
import java.util.Map;

public abstract class TraitProxy implements Externalizable {

    protected TripleFactory tripleFactory;

    public TraitProxy() { }

    protected Map<String, Object> fields;


    public Map<String, Object> getFields() {
        return fields;
    }

    protected void setFields( Map<String, Object> m ) {
        fields = m;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( fields );
        out.writeObject( tripleFactory );
    }


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        fields = (Map<String,Object>) in.readObject();
        tripleFactory = (TripleFactory) in.readObject();
    }


    public static Map.Entry<String, Object> buildEntry( final String k, final Object v ) {
        return new Map.Entry<String, Object>() {
            private String key = k;
            private Object obj = v;
            public String getKey() {
                return key;
            }

            public Object getValue() {
                return obj;
            }

            public Object setValue(Object value) {
                obj = value;
                return value;
            }

            public String toString() {
                return "<<" + key +"=" + obj + ">>";
            }
        };
    }


    public abstract Object getObject();


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TraitProxy that = (TraitProxy) o;

        if (fields != null ? !fields.equals(that.fields) : that.fields != null) return false;

        return true;
    }


    public int hashCode() {
        return fields != null ? fields.hashCode() : 0;
    }


    protected Triple propertyKey( String property ) {
        return getTripleFactory().newTriple( getObject(), property, Variable.v );
    }

    protected Triple property( String property, Object value ) {
        return getTripleFactory().newTriple( getObject(), property, value );
    }

    protected Triple propertyKey( Object property ) {
        return getTripleFactory().newTriple( getObject(), property.toString(), Variable.v );
    }


    public TripleFactory getTripleFactory() {
        return tripleFactory;
    }

    public void setTripleFactory( TripleFactory tripleFactory ) {
        this.tripleFactory = tripleFactory;
    }
}


