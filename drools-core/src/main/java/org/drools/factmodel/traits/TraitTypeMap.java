package org.drools.factmodel.traits;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class TraitTypeMap<T extends String, K extends Thing>
        extends TypeHierarchy<Thing>
        implements Map<String, Thing>, Externalizable {

    private Map<String,Thing> innerMap;


    public TraitTypeMap() {
    }

    public TraitTypeMap(Map map) {
        innerMap = map;
    }

    public int size() {
        return innerMap.size();
    }

    public boolean isEmpty() {
        return innerMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return innerMap.containsKey( key );
    }

    public boolean containsValue(Object value) {
        return innerMap.containsValue( value );
    }

    public Thing get( Object key ) {
        return innerMap.get( key );
    }

    public Thing put( String key, Thing value ) {
        addMember(value, ((TraitType) value).getTypeCode());
        innerMap.put( key, value );
        return value;
    }

    public void setBottomCode( BitSet code ) {
        if ( ! hasKey(code) ) {
            super.setBottomCode(code);
            addMember(new NullTraitType(code), code);
        }
    }


    public Thing putSafe( String key, Thing value ) throws LogicalTypeInconsistencyException {
        addMember(value, ((TraitType) value).getTypeCode());

        innerMap.put( key, value );
        return value;
    }

    public Thing remove( Object key ) {
        removeMember(innerMap.get(key));
        return innerMap.remove( key );
    }

    public void putAll( Map<? extends String, ? extends Thing> m ) {
        for ( String key : m.keySet() ) {
            Thing proxy = m.get( key );
            addMember(proxy, ((TraitProxy) proxy).getTypeCode());
        }
        innerMap.putAll( m );
    }

    public void clear() {
        innerMap.clear();
    }

    public Set<String> keySet() {
        return innerMap.keySet();
    }

    public Collection<Thing> values() {
        return innerMap.values();
    }

    public Set<Entry<String, Thing>> entrySet() {
        return innerMap.entrySet();
    }

    @Override
    public String toString() {
        return "VetoableTypedMap{" +
                "innerMap=" + innerMap + '}';
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject( innerMap );
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        innerMap = (Map<String, Thing>) objectInput.readObject();
    }


    public Collection<Thing> getMostSpecificTraits() {
        if ( hasKey( getBottomCode() ) ) {
            Thing b = getMember( getBottomCode() );
            if ( ((TraitType) b).isVirtual() ) {
                return parents( getBottomCode() );
            } else {
                return Collections.singleton( b );
            }
        } else {
            return parents( getBottomCode() );
        }
    }

}
