package org.drools.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.ConcurrentHashTable;
import org.drools.core.util.Entry;
import org.drools.core.util.RightTupleIndexHashTable;
import org.drools.core.util.RightTupleList;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.spi.InternalReadAccessor;

public class ConcurrentRightTupleIndexHashTableTest extends TestCase {
    EqualityEvaluatorsDefinition equals = new EqualityEvaluatorsDefinition();

    ClassFieldAccessorStore      store  = new ClassFieldAccessorStore();

    protected void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    public void testSingleEntry() throws Exception {
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE,
                                                                           Operator.EQUAL ) );

        final ConcurrentHashTable map = new ConcurrentHashTable( new FieldIndex[]{fieldIndex} );

        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle( 0,
                                                                         cheddar );

        assertEquals( 0,
                      map.size() );
        assertNull( map.get( new LeftTuple( cheddarHandle1,
                                            null,
                                            true ),cheddarHandle1 ) );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        RightTuple stiltonRighTuple = new RightTuple( new DefaultFactHandle( 1,
                                                                             stilton1 ),
                                                      null );

        map.add( stiltonRighTuple );

        assertEquals( 1,
                      map.size() );

        final Cheese stilton2 = new Cheese( "stilton",
                                            80 );
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle( 2,
                                                                         stilton2 );

        final RightTupleList list = map.get( new LeftTuple( stiltonHandle2,
                                                            null,
                                                            true ),stiltonHandle2 );
        assertSame( stiltonRighTuple.getFactHandle(),
                    list.first.getFactHandle() );
        assertNull( list.first.getNext() );
    }

    public void testTwoDifferentEntries() throws Exception {
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE,
                                                                           Operator.EQUAL ) );

        final ConcurrentHashTable map = new ConcurrentHashTable( new FieldIndex[]{fieldIndex} );

        assertEquals( 0,
                      map.size() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle stiltonHandle1 = new DefaultFactHandle( 1,
                                                                         stilton1 );
        map.add( new RightTuple( stiltonHandle1,
                                 null ) );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            35 );
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle( 2,
                                                                         cheddar1 );
        map.add( new RightTuple( cheddarHandle1,
                                 null ) );

        assertEquals( 2,
                      map.size() );

        final Cheese stilton2 = new Cheese( "stilton",
                                            77 );
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle( 2,
                                                                         stilton2 );
        RightTupleList list = map.get( new LeftTuple( stiltonHandle2,
                                                      null,
                                                      true ), stiltonHandle2 );
        assertSame( stiltonHandle1,
                    list.first.getFactHandle() );
        assertNull( list.first.getNext() );

        final Cheese cheddar2 = new Cheese( "cheddar",
                                            5 );
        final InternalFactHandle cheddarHandle2 = new DefaultFactHandle( 2,
                                                                         cheddar2 );
        list = map.get( new LeftTuple( cheddarHandle2,
                                       null,
                                       true ),
                                       cheddarHandle2 );
        assertSame( cheddarHandle1,
                    list.first.getFactHandle() );
        assertNull( list.first.getNext() );
    }

    public void testTwoEqualEntries() throws Exception {
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE,
                                                                           Operator.EQUAL ) );

        final ConcurrentHashTable map = new ConcurrentHashTable( new FieldIndex[]{fieldIndex} );

        assertEquals( 0,
                      map.size() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle stiltonHandle1 = new DefaultFactHandle( 1,
                                                                         stilton1 );
        map.add( new RightTuple( stiltonHandle1,
                                 null ) );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            35 );
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle( 2,
                                                                         cheddar1 );
        map.add( new RightTuple( cheddarHandle1,
                                 null ) );

        final Cheese stilton2 = new Cheese( "stilton",
                                            81 );
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle( 3,
                                                                         stilton2 );
        map.add( new RightTuple( stiltonHandle2,
                                 null ) );

        assertEquals( 3,
                      map.size() );

        // Check they are correctly chained to the same FieldIndexEntry
        final Cheese stilton3 = new Cheese( "stilton",
                                            89 );
        final InternalFactHandle stiltonHandle3 = new DefaultFactHandle( 4,
                                                                         stilton2 );

        final RightTupleList list = map.get( new LeftTuple( stiltonHandle3,
                                                            null,
                                                            true ), stiltonHandle3 );
        assertSame( stiltonHandle1,
                    list.first.getFactHandle() );
        assertSame( stiltonHandle2,
                    ((RightTuple) list.first.getNext()).getFactHandle() );
    }

    public void testTwoDifferentEntriesSameHashCode() throws Exception {
        final InternalReadAccessor extractor = store.getReader( TestClass.class,
                                                                "object",
                                                                getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( TestClass.class ) );

        final Declaration declaration = new Declaration( "theObject",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.OBJECT_TYPE,
                                                                           Operator.EQUAL ) );

        final ConcurrentHashTable map = new ConcurrentHashTable( new FieldIndex[]{fieldIndex} );

        final TestClass c1 = new TestClass( 0,
                                            new TestClass( 20,
                                                           "stilton" ) );

        final InternalFactHandle ch1 = new DefaultFactHandle( 1,
                                                              c1 );

        map.add( new RightTuple( ch1,
                                 null ) );

        final TestClass c2 = new TestClass( 0,
                                            new TestClass( 20,
                                                           "cheddar" ) );
        final InternalFactHandle ch2 = new DefaultFactHandle( 2,
                                                              c2 );
        map.add( new RightTuple( ch2,
                                 null ) );

        // same hashcode, but different values, so it should result in  a size of 2
        assertEquals( 2,
                      map.size() );

        // however both are in the same table bucket

        // this table bucket will have two FieldIndexEntries, as they are actually two different values
//        Entry[] entries = getEntries( map );
//        assertEquals( 1,
//                      entries.length );
//        RightTupleList list = (RightTupleList) entries[0];
//        assertSame( ch2,
//                    list.first.getFactHandle() );
//        assertNull( list.first.getNext() );
//
//        assertSame( ch1,
//                    ((RightTupleList) list.next).first.getFactHandle() );
//        assertNull( ((RightTupleList) list.next).first.getNext() );
//        assertNull( ((RightTupleList) list.next).next );
    }

    public void testRemove() throws Exception {
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE,
                                                                           Operator.EQUAL ) );

        final ConcurrentHashTable map = new ConcurrentHashTable( new FieldIndex[]{fieldIndex} );

        assertEquals( 0,
                      map.size() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle stiltonHandle1 = new DefaultFactHandle( 1,
                                                                         stilton1 );
        RightTuple stiltonRightTuple1 = new RightTuple( stiltonHandle1,
                                                        null );
        map.add( stiltonRightTuple1 );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            35 );
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle( 2,
                                                                         cheddar1 );
        RightTuple cheddarRightTuple1 = new RightTuple( cheddarHandle1,
                                                        null );
        map.add( cheddarRightTuple1 );

        final Cheese stilton2 = new Cheese( "stilton",
                                            81 );
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle( 3,
                                                                         stilton2 );
        RightTuple stiltonRightTuple2 = new RightTuple( stiltonHandle2,
                                                        null );
        map.add( stiltonRightTuple2 );

        assertEquals( 3,
                      map.size() );
//        assertEquals( 2,
//                      tablePopulationSize( map ) );

        // cheddar is in its own bucket, which should be removed once empty. We cannot have
        // empty FieldIndexEntries in the Map, as they get their value  from the first FactEntry.
        map.remove( cheddarRightTuple1 );
        assertEquals( 2,
                      map.size() );
//        assertEquals( 1,
//                      tablePopulationSize( map ) );

        // We remove t he stiltonHandle2, but there is still  one more stilton, so size  should be the same
        map.remove( stiltonRightTuple2 );
        assertEquals( 1,
                      map.size() );
//        assertEquals( 1,
//                      tablePopulationSize( map ) );

        //  No more stiltons, so the table should be empty
        map.remove( stiltonRightTuple1 );
        assertEquals( 0,
                      map.size() );
//        assertEquals( 0,
//                      tablePopulationSize( map ) );
    }

    public void testResize() throws Exception {
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE,
                                                                           Operator.EQUAL ) );

        final ConcurrentHashTable map = new ConcurrentHashTable( new FieldIndex[]{fieldIndex}, 16,
                                                                 0.75f );

        assertEquals( 0,
                      map.size() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        map.add( newRightTuple( 1,
                                stilton1 ) );

        final Cheese stilton2 = new Cheese( "stilton",
                                            81 );
        map.add( newRightTuple( 2,
                                stilton2 ) );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            35 );
        map.add( newRightTuple( 3,
                                cheddar1 ) );

        final Cheese cheddar2 = new Cheese( "cheddar",
                                            38 );
        map.add( newRightTuple( 4,
                                cheddar2 ) );

        final Cheese brie = new Cheese( "brie",
                                        293 );
        map.add( newRightTuple( 5,
                                brie ) );

        final Cheese mozerella = new Cheese( "mozerella",
                                             15 );
        map.add( newRightTuple( 6,
                                mozerella ) );

        final Cheese dolcelatte = new Cheese( "dolcelatte",
                                              284 );
        map.add( newRightTuple( 7,
                                dolcelatte ) );

        final Cheese camembert1 = new Cheese( "camembert",
                                              924 );
        map.add( newRightTuple( 8,
                                camembert1 ) );

        final Cheese camembert2 = new Cheese( "camembert",
                                              765 );
        map.add( newRightTuple( 9,
                                camembert2 ) );

        final Cheese redLeicestor = new Cheese( "red leicestor",
                                                23 );
        map.add( newRightTuple( 10,
                                redLeicestor ) );

        final Cheese wensleydale = new Cheese( "wensleydale",
                                               20 );
        map.add( newRightTuple( 11,
                                wensleydale ) );

        final Cheese edam = new Cheese( "edam",
                                        12 );
        map.add( newRightTuple( 12,
                                edam ) );

        final Cheese goude1 = new Cheese( "goude",
                                          93 );
        map.add( newRightTuple( 13,
                                goude1 ) );

        final Cheese goude2 = new Cheese( "goude",
                                          88 );
        map.add( newRightTuple( 14,
                                goude2 ) );

        final Cheese gruyere = new Cheese( "gruyere",
                                           82 );
        map.add( newRightTuple( 15,
                                gruyere ) );

        final Cheese emmental = new Cheese( "emmental",
                                            98 );
        map.add( newRightTuple( 16,
                                emmental ) );

        // At this point we have 16 facts but only 12 different types of cheeses
        // so no table resize and thus its size is 16

        assertEquals( 16,
                      map.size() );

//        Entry[] table = map.getTable();
//        assertEquals( 16,
//                      table.length );

        final Cheese feta = new Cheese( "feta",
                                        48 );
        map.add( newRightTuple( 2,
                                feta ) );

        // This adds our 13th type of cheese. The map is set with an initial capacity of 16 and
        // a threshold of 75%, that after 12 it should resize the map to 32.
        assertEquals( 17,
                      map.size() );

//        table = map.getTable();
//        assertEquals( 32,
//                      table.length );

        final Cheese haloumi = new Cheese( "haloumi",
                                           48 );
        map.add( newRightTuple( 2,
                                haloumi ) );

        final Cheese chevre = new Cheese( "chevre",
                                          48 );
        map.add( newRightTuple( 2,
                                chevre ) );

    }

    private RightTuple newRightTuple(int id,
                                     Object object) {
        return new RightTuple( new DefaultFactHandle( id,
                                                      object ),
                               null );

    }

    public static class TestClass {
        private int    hashCode;
        private Object object;

        public TestClass() {

        }

        public TestClass(final int hashCode,
                         final Object object) {
            this.hashCode = hashCode;
            this.object = object;
        }

        public Object getObject() {
            return this.object;
        }

        public void setObject(final Object object) {
            this.object = object;
        }

        public void setHashCode(final int hashCode) {
            this.hashCode = hashCode;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(final Object obj) {
            if ( this == obj ) {
                return true;
            }
            if ( obj == null ) {
                return false;
            }
            if ( getClass() != obj.getClass() ) {
                return false;
            }
            final TestClass other = (TestClass) obj;

            if ( this.object == null ) {
                if ( other.object != null ) {
                    return false;
                }
            } else if ( !this.object.equals( other.object ) ) {
                return false;
            }
            return true;
        }
    }

    private int tablePopulationSize(final AbstractHashTable map) throws Exception {
        final Field field = AbstractHashTable.class.getDeclaredField( "table" );
        field.setAccessible( true );
        final Entry[] array = (Entry[]) field.get( map );
        int size = 0;
        for ( int i = 0, length = array.length; i < length; i++ ) {
            if ( array[i] != null ) {
                size++;
            }
        }
        return size;
    }

    private Entry[] getEntries(final AbstractHashTable map) throws Exception {
        final Field field = AbstractHashTable.class.getDeclaredField( "table" );
        field.setAccessible( true );
        final List list = new ArrayList();

        final Entry[] array = (Entry[]) field.get( map );
        for ( int i = 0, length = array.length; i < length; i++ ) {
            if ( array[i] != null ) {
                list.add( array[i] );
            }
        }
        return (Entry[]) list.toArray( new Entry[list.size()] );
    }

    public void testEmptyIterator() {
        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE,
                                                                           Operator.EQUAL ) );

        final RightTupleIndexHashTable map = new RightTupleIndexHashTable( new FieldIndex[]{fieldIndex} );

        final Cheese stilton = new Cheese( "stilton",
                                           55 );
        final InternalFactHandle stiltonHandle = new DefaultFactHandle( 2,
                                                                        stilton );

        assertNull( map.getFirst( new LeftTuple( stiltonHandle,
                                                 null,
                                                 true ), null ) );
    }


}
