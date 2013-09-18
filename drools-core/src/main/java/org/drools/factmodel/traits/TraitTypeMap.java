package org.drools.factmodel.traits;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class TraitTypeMap<T extends String, K extends Thing<C>, C>
        extends TypeHierarchy<Key<Thing<C>>>
        implements Map<String, Thing<C>>, Externalizable {

    private Map<String,Thing<C>> innerMap;

    private BitSet currentTypeCode = new BitSet();

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

    public Thing<C> get( Object key ) {
        return innerMap.get( key );
    }

    public Thing<C> put( String key, Thing<C> value ) {
        BitSet code = ((TraitType) value).getTypeCode();
        addMember( new Key<Thing<C>>( System.identityHashCode( value ), value ), code );
        innerMap.put( key, value );
        currentTypeCode.or( code );
        return value;
    }

    public void setBottomCode( BitSet code ) {
        if ( ! hasKey(code) ) {
            super.setBottomCode(code);
            addMember( new Key( 0, new NullTraitType(code) ), code );
        }
    }


    public Thing<C> putSafe( String key, Thing<C> value ) throws LogicalTypeInconsistencyException {
        BitSet code = ((TraitType) value).getTypeCode();
        addMember( new Key<Thing<C>>( System.identityHashCode( value ), value ), code );
        currentTypeCode.or( code );
        innerMap.put( key, value );
        return value;
    }

    public Thing<C> remove( Object key ) {
        Thing<C> t = innerMap.remove( key );
        if ( t instanceof TraitProxy ) {
            ((TraitProxy) t).shed();
        }
        removeMember( new Key<Thing<C>>( System.identityHashCode( t ), t ) );
        resetCurrentCode();
        return t;
    }

    public Collection<Thing<C>> removeCascade( String traitName ) {
        if ( ! innerMap.containsKey( traitName ) ) {
            return Collections.emptyList();
        }
        Thing<C> thing = innerMap.get( traitName );
        return removeCascade( ( (TraitType) thing ).getTypeCode() );
    }

    public Collection<Thing<C>> removeCascade( BitSet code ) {
        Collection<Key<Thing<C>>> subs = this.lowerDescendants( code );
        List<Thing<C>> ret = new ArrayList<Thing<C>>( subs.size() );
        for ( Key<Thing<C>> t : subs ) {
            TraitType tt = (TraitType) t.getValue();
            if ( ! tt.isVirtual() ) {
                ret.add( t.getValue() );
                removeMember( tt.getTypeCode() );
                Thing<C> thing = innerMap.remove( tt.getTraitName() );
                if ( thing instanceof TraitProxy ) {
                    ((TraitProxy) thing).shed();
                }
            }
        }
        resetCurrentCode();
        return ret;
    }

    private void resetCurrentCode() {
        currentTypeCode = new BitSet( currentTypeCode.length() );
        if ( ! this.values().isEmpty() ) {
            for ( Thing x : this.values() ) {
                currentTypeCode.or( ((TraitType) x).getTypeCode() );
            }
        }
    }

    public void putAll( Map<? extends String, ? extends Thing<C>> m ) {
        for ( String key : m.keySet() ) {
            Thing<C> proxy = m.get( key );
            addMember( new Key<Thing<C>>( System.identityHashCode( proxy ), proxy ), ((TraitProxy) proxy).getTypeCode());
        }
        innerMap.putAll( m );
    }

    public void clear() {
        innerMap.clear();
    }

    public Set<String> keySet() {
        return innerMap.keySet();
    }

    public Collection<Thing<C>> values() {
        return innerMap.values();
    }

    public Set<Entry<String, Thing<C>>> entrySet() {
        return innerMap.entrySet();
    }

    @Override
    public String toString() {
        return "VetoableTypedMap{" +
                "innerMap=" + innerMap + '}';
    }

    public void writeExternal( ObjectOutput objectOutput ) throws IOException {
        super.writeExternal( objectOutput );
        objectOutput.writeObject( innerMap );
        objectOutput.writeObject( currentTypeCode );
    }

    public void readExternal( ObjectInput objectInput ) throws IOException, ClassNotFoundException {
        super.readExternal( objectInput );
        innerMap = (Map<String, Thing<C>>) objectInput.readObject();
        currentTypeCode = (BitSet) objectInput.readObject();
    }


    public Collection<Key<Thing<C>>> getMostSpecificTraits() {
        if ( hasKey( getBottomCode() ) ) {
            Key<Thing<C>> b = getMember( getBottomCode() );
            if ( ((TraitType) b.getValue()).isVirtual() ) {
                Collection<Key<Thing<C>>> p =  parents( getBottomCode() );
                return p;
            } else {
                return Collections.singleton( b );
            }
        } else {
            Collection<Key<Thing<C>>> p =  immediateParents( getBottomCode() );
            return p;
        }
    }



    public BitSet getCurrentTypeCode() {
        return currentTypeCode;
    }
}
