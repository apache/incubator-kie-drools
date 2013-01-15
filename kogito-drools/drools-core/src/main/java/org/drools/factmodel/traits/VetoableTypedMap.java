package org.drools.factmodel.traits;

import java.io.Serializable;
import java.util.*;


public class VetoableTypedMap<T extends String, K extends Thing> implements Map<String, Thing>, Serializable {

    private Map<String,Thing> innerMap;

    private PriorityQueue<Perm> vetos;

    public VetoableTypedMap(Map map) {
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

    public Thing get(Object key) {
        return innerMap.get( key );
    }

    public Thing put(String key, Thing value) {
        innerMap.put( key, value );
        return value;
    }

    public Thing putSafe( String key, Thing value ) throws LogicalTypeInconsistencyException {
        if ( vetos != null ) {
            for ( Perm ward : vetos ) {
                if ( ward.getType().isAssignableFrom( value.getClass() ) ) {
                    if ( ward.isWard() ) {
                        throw new LogicalTypeInconsistencyException( "An object of type " + value.getCore().getClass() + " has been prevented from donning type " + ward.getType(),
                                                                     value.getCore().getClass(),
                                                                     ward.getType() );
                    } else {
                        break;
                    }
                }
            }
        }
        innerMap.put( key, value );
        return value;
    }

    public Thing remove(Object key) {
        return innerMap.remove( key );
    }

    public void putAll(Map<? extends String, ? extends Thing> m) {
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

    public void addToVetoable(Class trait) throws LogicalTypeInconsistencyException {
        if ( vetos == null ) {
            vetos = new PriorityQueue<Perm>();
        }
        if ( ! vetos.contains( new Perm( trait ) ) ) {
            vetos.add( new Perm( trait, true ) );
            for ( Object t : innerMap.values() ) {
                if ( trait.isAssignableFrom( t.getClass() ) ) {
                    Class c1 = t.getClass();
                    Class c2 = trait;
                    throw new LogicalTypeInconsistencyException( "An object of type " + c1 + " has been prevented from donning type " + c2, c1, c2 );
                }
            }
        }
    }

    public void removeFromVetoable( Class trait ) {
        if ( vetos == null ) {
            vetos = new PriorityQueue<Perm>();
        }
        Perm test = new Perm( trait );
        if ( vetos.contains( test ) ) {
            vetos.remove( test );
        } else {
            vetos.add( new Perm( trait, false ) );
        }
    }

    @Override
    public String toString() {
        return "VetoableTypedMap{" +
                "innerMap=" + innerMap +
                ", vetos=" + vetos +
                '}';
    }

    private static class Perm implements Comparable<Perm>, Serializable {
        private Class<? extends Thing> type;
        private boolean ward;
        private int depth;

        /**
         * Internal constructor, used for containment check
         * @param type
         */
        private Perm(Class<? extends Thing> type) {
            this.type = type;
        }

        private Perm( Class<? extends Thing> type, boolean ward ) {
            this.type = type;
            this.ward = ward;
            depth = 1;
            Class sup = type.getInterfaces()[0];
            while ( sup != null && ! Thing.class.equals( sup ) ) {
                depth++;
                sup = sup.getInterfaces()[0];
            }
        }

        public Class<? extends Thing> getType() {
            return type;
        }

        public void setType(Class<? extends Thing> type) {
            this.type = type;
        }

        public boolean isWard() {
            return ward;
        }

        public void setWard(boolean ward) {
            this.ward = ward;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Perm perm = (Perm) o;

            if (type != null ? !type.equals(perm.type) : perm.type != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return type != null ? type.hashCode() : 0;
        }

        /**
         * The heuristic here is as follows:
         * Subclasses come first, as it's more likely that one wants to restrict/unrestrict a subclass
         * If two classes are not directly related, classes higher in the hierarchy come first.
         * Notice that the depth is heuristic as well, since it only considers the first path to root
         * and is exact only for single inheritance hierarchies. The idea is not to waste too much
         * time exploring the full hierarchy only to get a minor perf improvement.
         * This might be changed in the future
         */
        public int compareTo(Perm o) {
            if ( getType().isAssignableFrom( o.getType() ) ) {
                return 1;
            } else if ( o.getType().isAssignableFrom( getType() ) ) {
                return -1;
            }
            return depth - o.depth;
        }

        @Override
        public String toString() {
            return "Perm{" +
                    "type=" + type.getName() +
                    ", ward=" + ward +
                    ", depth=" + depth +
                    '}';
        }
    }
}
