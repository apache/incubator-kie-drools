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

public class TripleImpl implements Triple {
    private Entry next;
    
    private Object instance;
    private Object property;
    private Object value;

    protected TripleImpl(Object instance,
                  String property,
                  Object value) {
        super();
        this.instance = instance;
        this.property = property;
        this.value = value;
    }

    protected TripleImpl(Object instance,
                  Object property,
                  Object value) {
        super();
        this.instance = instance;
        this.property = property;
        this.value = value;
    }

    public void setNext(Entry next) {
        this.next = next;
    }

    public Entry getNext() {
        return this.next;
    }

    /* (non-Javadoc)
     * @see org.kie.core.util.Triple#getInstance()
     */
    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    /* (non-Javadoc)
     * @see org.kie.core.util.Triple#getProperty()
     */
    public Object getProperty() {
        return property;
    }

    public void setProperty(Object property) {
        this.property = property;
    }

    /* (non-Javadoc)
     * @see org.kie.core.util.Triple#getValue()
     */
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instance == null) ? 0 : instance.hashCode());
        result = prime * result + ((property == null) ? 0 : property.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        TripleImpl other = (TripleImpl) obj;

        if ( instance == null ) {
            if ( other.instance != null ) return false;
        } else if ( ! this.instance.equals( other.instance ) )
            return false;

        if ( property == null ) {
            if ( other.property != null ) return false;
        } else if ( !property.equals( other.property ) )
            return false;

        if ( value == null ) {
            if ( other.value != null ) return false;
        } else if ( !value.equals( other.value ) )
            return false;

        return true;
    }

    public String toString() {
        return "Triple [instance=" + instance + ", property=" + property + ", value=" + value + "]";
    }
 
}
