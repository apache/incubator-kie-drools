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

package org.drools.core.factmodel.traits;

import org.drools.core.util.HierarchyEncoderImpl;
import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;
import org.kie.api.runtime.rule.Variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class TraitProxy implements Externalizable, TraitType, Comparable<TraitProxy> {

    protected TripleFactory tripleFactory;

    private BitSet typeCode;
    private BitSet propagationTypeCode = new BitSet();

    private Set<BitSet> otns;

    public TraitProxy() {

    }

    protected Map<String, Object> fields;

    public boolean _isVirtual() {
        return false;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    protected void setFields( Map<String, Object> m ) {
        fields = m;
    }

    public abstract String _getTraitName();

    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( fields );
        out.writeObject( tripleFactory );
        out.writeObject( typeCode );
        out.writeObject( otns );
        out.writeObject( propagationTypeCode );
    }


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        fields = (Map<String,Object>) in.readObject();
        tripleFactory = (TripleFactory) in.readObject();
        typeCode = (BitSet) in.readObject();
        otns = (Set<BitSet>) in.readObject();
        propagationTypeCode = (BitSet) in.readObject();
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


    public abstract TraitableBean getObject();


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return getObject().equals( ( (TraitProxy) o ).getObject() );
    }


    public int hashCode() {
        return getClass().hashCode() ^ getObject().hashCode();
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

    public BitSet _getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(BitSet typeCode) {
        this.typeCode = typeCode;
    }

    public void shed() {

    }

    public int compareTo( TraitProxy o ) {
        if ( HierarchyEncoderImpl.supersetOrEqualset( this.typeCode, o.typeCode ) ) {
            return -1;
        } else {
            return 1;
        }
    }

    public BitSet computeInsertionVetoMask() {

        BitSet typeMask = new BitSet();

        for ( Object o : getObject()._getTraitMap().values() ) {
            if ( o != this ) {
                typeMask.or(((TraitProxy) o).propagationTypeCode);
            }
        }

        return typeMask;
    }

    public void assignOtn( BitSet typeCode ) {
        if ( otns == null ) {
            otns = new HashSet<BitSet>();
        }
        otns.add( typeCode );
        propagationTypeCode.or( typeCode );
    }

    public boolean hasOtns() {
        return otns != null && ! otns.isEmpty();
    }

    public void clearOtns() {
        if ( otns != null ) {
            otns.clear();
        }
        propagationTypeCode.clear();
    }

    public Set<BitSet> listAssignedOtnTypeCodes() {
        return otns != null ? Collections.unmodifiableSet( otns ) : Collections.EMPTY_SET;
    }

    @Override
    public boolean _hasTypeCode( BitSet typeCode ) {
        if ( otns == null ) {
            return false;
        }
        return otns.contains( typeCode );
    }
}


