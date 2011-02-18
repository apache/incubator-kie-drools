package org.drools.ontology;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.KnowledgeRuntime;
import org.junit.Ignore;

@Ignore
public class CollectionTest {

    public static class Person {
        private Set<Pet>         pets;

        private KnowledgeRuntime kruntime;

        // 1 person to many pets
        // IsOwnerOf() Pet() // $p : Person() Pet ( owner == $p )
        public Set<Pet> getPets() {
            return this.pets;
        }
    }

    public static class Pet {
        // 1 pet to 1 owner
        // IsOwnedBy  Person() // rewrite to  $p : Person() Pet ( owner == $p )
        private Person owner;
    }

    public static class Cell {
        private Map<Cell, ManyToManyRelation> neighbours;

    }

    // Cell() NeighborTo() Cell()
    public static class ManyToManyRelation {
        private FactHandle factHandle;
        private Object     object1;
        private Object     object2;

        public ManyToManyRelation(Object object1,
                                  Object object2) {
            this.object1 = object1;
            this.object2 = object2;
        }

        public Object getObject1() {
            return object1;
        }

        public Object getObject2() {
            return object2;
        }

        public void setFactHandle(FactHandle factHandle) {
            this.factHandle = factHandle;
        }

        public FactHandle getFactHandle() {
            return this.factHandle;
        }

    }

    public static class RelationalSet<E>
        implements
        Set<E> {
        private Object           object;
        private Map              map;
        private KnowledgeRuntime kruntime;

        public boolean add(E o) {
            ManyToManyRelation relation = new ManyToManyRelation( this.object,
                                                                  o );
            if ( !this.map.containsKey( o ) ) {
                this.map.put( o,
                              relation );
                relation.setFactHandle( kruntime.insert( relation ) );
                return true;
            } else {
                return false;
            }
        }

        public boolean addAll(Collection< ? extends E> c) {
            throw new UnsupportedOperationException( "" );
            //return this.set.addAll( c );
        }

        public void clear() {
            this.map.clear();
        }

        public boolean contains(Object o) {
            return this.map.containsKey( o );
        }

        public boolean containsAll(Collection< ? > c) {
            throw new UnsupportedOperationException( "" );
            //return this.set.containsAll( c );
        }

        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        public Iterator<E> iterator() {
            return this.map.keySet().iterator();
        }

        public boolean remove(Object o) {
            ManyToManyRelation relation = (ManyToManyRelation) this.map.remove( o );
            if ( relation != null ) {
                this.kruntime.retract( relation.getFactHandle() );
                return true;
            } else {
                return false;
            }
        }

        public boolean removeAll(Collection< ? > c) {
            throw new UnsupportedOperationException( "" );
            //return this.set.removeAll( c );
        }

        public boolean retainAll(Collection< ? > c) {
            throw new UnsupportedOperationException( "" );
            //return this.set.retainAll( c );
        }

        public int size() {
            return this.map.size();
        }

        public Object[] toArray() {
            return this.map.keySet().toArray();
        }

        public <T> T[] toArray(T[] a) {
            return (T[]) this.map.keySet().toArray( a );
        }

    }

    //    public static class RelationalList<E> implements List<E> {
    //        private List list;
    //        private KnowledgeRuntime kruntime;
    //        
    //        public RelationalList(List list, KnowledgeRuntime kruntime) {
    //            this.list = list;
    //            this.kruntime = kruntime;
    //        }
    //        
    //        public boolean add(E o) {
    //            return list.add( o );
    //        }
    //
    //        public void add(int index,
    //                        E element) {
    //            this.list.add( index, element );
    //        }
    //
    //        public boolean addAll(Collection< ? extends E> c) {
    //            return this.list.addAll( c  );
    //        }
    //
    //        public boolean addAll(int index,
    //                              Collection< ? extends E> c) {
    //            return this.list.addAll(  index, c );
    //        }
    //
    //        public void clear() {
    //            this.list.clear();
    //        }
    //
    //        public boolean contains(Object o) {
    //            return this.list.contains( o );
    //        }
    //
    //        public boolean containsAll(Collection< ? > c) {
    //            return this.list.containsAll( c );
    //        }
    //
    //        public E get(int index) {
    //            return ( E ) this.list.get( index );
    //        }
    //
    //        public int indexOf(Object o) {
    //            return this.list.indexOf( o );
    //        }
    //
    //        public boolean isEmpty() {
    //            return this.list.isEmpty();
    //        }
    //
    //        public Iterator<E> iterator() {
    //            return this.list.iterator();
    //        }
    //
    //        public int lastIndexOf(Object o) {
    //            return this.list.lastIndexOf( o );
    //        }
    //
    //        public ListIterator<E> listIterator() {
    //            return this.list.listIterator();
    //        }
    //
    //        public ListIterator<E> listIterator(int index) {
    //            return this.list.listIterator(index);
    //        }
    //
    //        public boolean remove(Object o) {
    //            return this.list.remove( o );
    //        }
    //
    //        public E remove(int index) {         
    //            return ( E ) this.list.remove( index );
    //        }
    //
    //        public boolean removeAll(Collection< ? > c) {
    //            return this.list.removeAll( c );
    //        }
    //
    //        public boolean retainAll(Collection< ? > c) {
    //            return this.list.retainAll(  c  );
    //        }
    //
    //        public E set(int index,
    //                     E element) {
    //            return ( E ) this.list.set( index, element );
    //        }
    //
    //        public int size() {
    //            return this.list.size();
    //        }
    //
    //        public List<E> subList(int fromIndex,
    //                               int toIndex) {
    //            return this.list.subList( fromIndex, toIndex );
    //        }
    //
    //        public Object[] toArray() {
    //            return this.list.toArray();
    //        }
    //
    //        public <T> T[] toArray(T[] a) {
    //            return ( T[] ) this.list.toArray( a );
    //        }
    //        
    //    }
}
