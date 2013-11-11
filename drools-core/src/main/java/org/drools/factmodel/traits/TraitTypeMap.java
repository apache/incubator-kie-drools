package org.drools.factmodel.traits;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;


public class TraitTypeMap<T extends String, K extends Thing<C>, C>
        extends TypeHierarchy<K, Key<K>>
        implements Map<String, K>, Externalizable {

    private Map<String,K> innerMap;

    private BitSet currentTypeCode = new BitSet();

    public TraitTypeMap() {
    }

    public TraitTypeMap(Map map) {
        innerMap = map;

        // create "top" element placeholder. will be replaced by a Thing proxy later, should the core object don it
        ThingProxyPlaceHolder thingPlaceHolder = ThingProxyPlaceHolder.getThingPlaceHolder();
        addMember( new BitMaskKey( -1, thingPlaceHolder ), thingPlaceHolder.getTypeCode() );
    }

    @Override
    public void addMember(LatticeElement<K> val, BitSet key) {
        super.addMember(val, key);
        if(!mostSpecificTraits.isEmpty())
            updateMostSpecificTrait( val );
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

    public K get( Object key ) {
        return innerMap.get( key );
    }

    public K put( String key, K value ) {
        BitSet code = ((TraitType) value).getTypeCode();
        addMember( (new BitMaskKey<K>( System.identityHashCode( value ), value )), code );
//        addMember( value , code );
        innerMap.put( key, value );
        currentTypeCode.or( code );
        return value;
    }

    public void setBottomCode( BitSet code ) {
        if ( ! hasKey(code) ) {
            super.setBottomCode(code);
            addMember( new BitMaskKey( 0, new NullTraitType(code) ), code );
        }
    }


    public K putSafe( String key, K value ) throws LogicalTypeInconsistencyException {
        BitSet code = ((TraitType) value).getTypeCode();
        addMember( (new BitMaskKey<K>( System.identityHashCode( value ), value )), code );
//        addMember( value , code );
        currentTypeCode.or( code );
        innerMap.put( key, value );
        return value;
    }

    public K remove( Object key ) {
        K t = innerMap.remove( key );
        if ( t instanceof TraitProxy ) {
            ((TraitProxy) t).shed();
        }
        removeMember( new BitMaskKey<K>( System.identityHashCode( t ), t ) );
        mostSpecificTraits.clear();
        resetCurrentCode();
        return t;
    }

    public Collection<K> removeCascade( String traitName ) {
        if ( ! innerMap.containsKey( traitName ) ) {
            return Collections.emptyList();
        }
        K thing = innerMap.get( traitName );
        return removeCascade( ( (TraitType) thing ).getTypeCode() );
    }

    public Collection<K> removeCascade( BitSet code ) {
    Collection<LatticeElement<K>> subs = this.lowerDescendants( code );
    List<K> ret = new ArrayList<K>( subs.size() );
    for ( LatticeElement<K> k : subs ) {
        Key<K> t = new BitMaskKey<K>(System.identityHashCode(k),k.getValue());
        TraitType tt = (TraitType) t.getValue();
        if ( ! tt.isVirtual() ) {
            ret.add( t.getValue() );
            removeMember( tt.getTypeCode() );
            mostSpecificTraits.clear();
            K thing = innerMap.remove( tt.getTraitName() );
            if ( thing instanceof TraitProxy ) {
                ((TraitProxy) thing).shed(); //is this working?
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

    public void putAll( Map<? extends String, ? extends K> m ) {
        for ( String key : m.keySet() ) {
            K proxy = m.get( key );
            addMember( new BitMaskKey<K>( System.identityHashCode( proxy ), proxy ), ((TraitProxy) proxy).getTypeCode());
//            addMember( proxy, ((TraitProxy) proxy).getTypeCode());
        }
        innerMap.putAll( m );
    }

    public void clear() {
        innerMap.clear();
    }

    public Set<String> keySet() {
        return innerMap.keySet();
    }

    public Collection<K> values() {
        return innerMap.values();
    }

    public Set<Entry<String, K>> entrySet() {
        return innerMap.entrySet();
    }

    @Override
    public String toString() {
        return "VetoableTypedMap{" +
                "innerMap=" + innerMap + '}';
    }

    public void writeExternal( ObjectOutput objectOutput ) throws IOException {
        super.writeExternal( objectOutput );

        objectOutput.writeInt( innerMap.size() );
        List<String> keys = new ArrayList<String>( innerMap.keySet() );
        Collections.sort( keys );
        for ( String k : keys ) {
            objectOutput.writeObject( k );
            objectOutput.writeObject( innerMap.get( k ) );
        }

        objectOutput.writeObject( currentTypeCode );
    }

    public void readExternal( ObjectInput objectInput ) throws IOException, ClassNotFoundException {
        super.readExternal( objectInput );

        innerMap = new HashMap<String, K>();
        int n = objectInput.readInt();
        for ( int j = 0; j < n; j++ ) {
            String k = (String) objectInput.readObject();
            K tf = (K) objectInput.readObject();
            innerMap.put( k, tf );
        }

        currentTypeCode = (BitSet) objectInput.readObject();
    }


    public Collection<LatticeElement<K>> getMostSpecificTraits() {
        if ( getBottomCode() == null ) {
            // not yet initialized -> no trait donned yet
            return null;
        }
        if(!mostSpecificTraits.isEmpty())
            return mostSpecificTraits;
        if ( hasKey( getBottomCode() ) ) {
//            BitMaskKey<K> b = new BitMaskKey<K>(System.identityHashCode(getMember(getBottomCode())),
//                    getMember( getBottomCode()));
            LatticeElement<K> b = (BitMaskKey) getMember(getBottomCode());
            if ( ((TraitType) b.getValue()).isVirtual() ) {
                Collection<LatticeElement<K>> p =  immediateParents( getBottomCode() );
                mostSpecificTraits.clear();
                mostSpecificTraits.addAll(p);
                return p;
            } else {
                return Collections.singleton( b );
            }
        } else {
            Collection<LatticeElement<K>> p =  immediateParents( getBottomCode() );
            mostSpecificTraits.clear();
            mostSpecificTraits.addAll(p);
            return p;
        }
    }

//    public Collection<Key<K>> lowerDescendants( BitSet key ) {
//        List<Key<K>> vals = new LinkedList<Key<K>>();
//        int l = key.length();
//        if ( l == 0 ) {
//            return new ArrayList( getSortedMembers() );
////            vals.add(line.get(key));
////            return vals;
//        }
//        int n = line.lastKey().length();
//
//        if ( l > n ) { return vals; }
//
//        BitSet start = new BitSet( n );
//        BitSet end = new BitSet( n );
//        start.or( key );
//
//        start.set( l - 1 );
//        end.set( n );
//
//        for ( Key<K> val : line.subMap( start, end ).values() ) {
//            BitSet x = val.getBitMask();
//            if ( superset( x, key ) >= 0 ) {
//                vals.add( new BitMaskKey<K>(System.identityHashCode(val.getValue()),val.getValue()) );
//            }
//        }
//
//        start.clear( l - 1 );
//
//        return vals;
//    }


    public BitSet getCurrentTypeCode() {
        return currentTypeCode;
    }



}
