package org.drools.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.spi.FieldExtractor;
import org.drools.util.AbstractHashTable.FactEntryImpl;
import org.drools.util.AbstractHashTable.FieldIndex;
import org.drools.util.FactHandleIndexHashTable.FieldIndexEntry;
import org.drools.util.ObjectHashMap.ObjectEntry;

public class FieldIndexHashTableTest extends TestCase {
    ClassFieldExtractorCache cache = ClassFieldExtractorCache.getInstance();
    EqualityEvaluatorsDefinition equals = new EqualityEvaluatorsDefinition();

    public void testSingleEntry() throws Exception {
        final FieldExtractor extractor = cache.getExtractor( Cheese.class,
                                                             "type",
                                                             getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ) );

        final FactHandleIndexHashTable map = new FactHandleIndexHashTable( new FieldIndex[]{fieldIndex} );

        final Cheese cheddar = new Cheese( "cheddar",
                                           10 );
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle( 0,
                                                                         cheddar );

        assertEquals( 0,
                      map.size() );
        assertNull( map.get( new ReteTuple( cheddarHandle1 ) ) );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle stiltonHandle1 = new DefaultFactHandle( 1,
                                                                         stilton1 );
        map.add( stiltonHandle1 );

        assertEquals( 1,
                      map.size() );
        assertEquals( 1,
                      tablePopulationSize( map ) );

        final Cheese stilton2 = new Cheese( "stilton",
                                            80 );
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle( 2,
                                                                         stilton2 );

        final FieldIndexEntry stiltonEntry = map.get( new ReteTuple( stiltonHandle2 ) );
        assertSame( stiltonHandle1,
                    stiltonEntry.getFirst().getFactHandle() );
        assertNull( stiltonEntry.getFirst().getNext() );
    }

    public void testTwoDifferentEntries() throws Exception {
        final FieldExtractor extractor = cache.getExtractor( Cheese.class,
                                                             "type",
                                                             getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ) );

        final FactHandleIndexHashTable map = new FactHandleIndexHashTable( new FieldIndex[]{fieldIndex} );

        assertEquals( 0,
                      map.size() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle stiltonHandle1 = new DefaultFactHandle( 1,
                                                                         stilton1 );
        map.add( stiltonHandle1 );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            35 );
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle( 2,
                                                                         cheddar1 );
        map.add( cheddarHandle1 );

        assertEquals( 2,
                      map.size() );
        assertEquals( 2,
                      tablePopulationSize( map ) );

        final Cheese stilton2 = new Cheese( "stilton",
                                            77 );
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle( 2,
                                                                         stilton2 );
        final FieldIndexEntry stiltonEntry = map.get( new ReteTuple( stiltonHandle2 ) );
        assertSame( stiltonHandle1,
                    stiltonEntry.getFirst().getFactHandle() );
        assertNull( stiltonEntry.getFirst().getNext() );

        final Cheese cheddar2 = new Cheese( "cheddar",
                                            5 );
        final InternalFactHandle cheddarHandle2 = new DefaultFactHandle( 2,
                                                                         cheddar2 );
        final FieldIndexEntry cheddarEntry = map.get( new ReteTuple( cheddarHandle2 ) );
        assertSame( cheddarHandle1,
                    cheddarEntry.getFirst().getFactHandle() );
        assertNull( cheddarEntry.getFirst().getNext() );
    }

    public void testTwoEqualEntries() throws Exception {
        final FieldExtractor extractor = cache.getExtractor( Cheese.class,
                                                             "type",
                                                             getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ) );

        final FactHandleIndexHashTable map = new FactHandleIndexHashTable( new FieldIndex[]{fieldIndex} );

        assertEquals( 0,
                      map.size() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle stiltonHandle1 = new DefaultFactHandle( 1,
                                                                         stilton1 );
        map.add( stiltonHandle1 );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            35 );
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle( 2,
                                                                         cheddar1 );
        map.add( cheddarHandle1 );

        final Cheese stilton2 = new Cheese( "stilton",
                                            81 );
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle( 3,
                                                                         stilton2 );
        map.add( stiltonHandle2 );

        assertEquals( 3,
                      map.size() );
        assertEquals( 2,
                      tablePopulationSize( map ) );

        // Check they are correctly chained to the same FieldIndexEntry
        final Cheese stilton3 = new Cheese( "stilton",
                                            89 );
        final InternalFactHandle stiltonHandle3 = new DefaultFactHandle( 4,
                                                                         stilton2 );

        final FieldIndexEntry stiltonEntry = map.get( new ReteTuple( stiltonHandle3 ) );
        assertSame( stiltonHandle2,
                    stiltonEntry.getFirst().getFactHandle() );
        assertSame( stiltonHandle1,
                    ((FactEntryImpl) stiltonEntry.getFirst().getNext()).getFactHandle() );
    }

    public void testTwoDifferentEntriesSameHashCode() throws Exception {
        final FieldExtractor extractor = cache.getExtractor( TestClass.class,
                                                             "object",
                                                             getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( TestClass.class ) );

        final Declaration declaration = new Declaration( "theObject",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.OBJECT_TYPE, Operator.EQUAL ) );

        final FactHandleIndexHashTable map = new FactHandleIndexHashTable( new FieldIndex[]{fieldIndex} );

        final TestClass c1 = new TestClass( 0,
                                            new TestClass( 20,
                                                           "stilton" ) );

        final InternalFactHandle ch1 = new DefaultFactHandle( 1,
                                                              c1 );

        map.add( ch1 );

        final TestClass c2 = new TestClass( 0,
                                            new TestClass( 20,
                                                           "cheddar" ) );
        final InternalFactHandle ch2 = new DefaultFactHandle( 2,
                                                              c2 );
        map.add( ch2 );

        // same hashcode, but different values, so it should result in  a size of 2
        assertEquals( 2,
                      map.size() );

        // however both are in the same table bucket
        assertEquals( 1,
                      tablePopulationSize( map ) );

        // this table bucket will have two FieldIndexEntries, as they are actually two different values
        final FieldIndexEntry entry = (FieldIndexEntry) getEntries( map )[0];

    }

    public void testRemove() throws Exception {
        final FieldExtractor extractor = cache.getExtractor( Cheese.class,
                                                             "type",
                                                             getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ) );

        final FactHandleIndexHashTable map = new FactHandleIndexHashTable( new FieldIndex[]{fieldIndex} );

        assertEquals( 0,
                      map.size() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle stiltonHandle1 = new DefaultFactHandle( 1,
                                                                         stilton1 );
        map.add( stiltonHandle1 );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            35 );
        final InternalFactHandle cheddarHandle1 = new DefaultFactHandle( 2,
                                                                         cheddar1 );
        map.add( cheddarHandle1 );

        final Cheese stilton2 = new Cheese( "stilton",
                                            81 );
        final InternalFactHandle stiltonHandle2 = new DefaultFactHandle( 3,
                                                                         stilton2 );
        map.add( stiltonHandle2 );

        assertEquals( 3,
                      map.size() );
        assertEquals( 2,
                      tablePopulationSize( map ) );

        // cheddar is in its own buccket, which should be removed once empty. We cannot have
        // empty FieldIndexEntries in the Map, as they get their value  from the first FactEntry.
        map.remove( cheddarHandle1 );
        assertEquals( 2,
                      map.size() );
        assertEquals( 1,
                      tablePopulationSize( map ) );

        // We remove t he stiltonHandle2, but there is still  one more stilton, so size  should be the same
        map.remove( stiltonHandle2 );
        assertEquals( 1,
                      map.size() );
        assertEquals( 1,
                      tablePopulationSize( map ) );

        //  No more stiltons, so the table should be empty
        map.remove( stiltonHandle1 );
        assertEquals( 0,
                      map.size() );
        assertEquals( 0,
                      tablePopulationSize( map ) );
    }

    public void testResize() throws Exception {
        final FieldExtractor extractor = cache.getExtractor( Cheese.class,
                                                             "type",
                                                             getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ) );

        final FactHandleIndexHashTable map = new FactHandleIndexHashTable( new FieldIndex[]{fieldIndex} );

        assertEquals( 0,
                      map.size() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        map.add( new DefaultFactHandle( 1,
                                        stilton1 ) );

        final Cheese stilton2 = new Cheese( "stilton",
                                            81 );
        map.add( new DefaultFactHandle( 2,
                                        stilton2 ) );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            35 );
        map.add( new DefaultFactHandle( 3,
                                        cheddar1 ) );

        final Cheese cheddar2 = new Cheese( "cheddar",
                                            38 );
        map.add( new DefaultFactHandle( 4,
                                        cheddar2 ) );

        final Cheese brie = new Cheese( "brie",
                                        293 );
        map.add( new DefaultFactHandle( 5,
                                        brie ) );

        final Cheese mozerella = new Cheese( "mozerella",
                                             15 );
        map.add( new DefaultFactHandle( 6,
                                        mozerella ) );

        final Cheese dolcelatte = new Cheese( "dolcelatte",
                                              284 );
        map.add( new DefaultFactHandle( 7,
                                        dolcelatte ) );

        final Cheese camembert1 = new Cheese( "camembert",
                                              924 );
        map.add( new DefaultFactHandle( 8,
                                        camembert1 ) );

        final Cheese camembert2 = new Cheese( "camembert",
                                              765 );
        map.add( new DefaultFactHandle( 9,
                                        camembert2 ) );

        final Cheese redLeicestor = new Cheese( "red leicestor",
                                                23 );
        map.add( new DefaultFactHandle( 10,
                                        redLeicestor ) );

        final Cheese wensleydale = new Cheese( "wensleydale",
                                               20 );
        map.add( new DefaultFactHandle( 11,
                                        wensleydale ) );

        final Cheese edam = new Cheese( "edam",
                                        12 );
        map.add( new DefaultFactHandle( 12,
                                        edam ) );

        final Cheese goude1 = new Cheese( "goude",
                                          93 );
        map.add( new DefaultFactHandle( 13,
                                        goude1 ) );

        final Cheese goude2 = new Cheese( "goude",
                                          88 );
        map.add( new DefaultFactHandle( 14,
                                        goude2 ) );

        final Cheese gruyere = new Cheese( "gruyere",
                                           82 );
        map.add( new DefaultFactHandle( 15,
                                        gruyere ) );

        final Cheese emmental = new Cheese( "emmental",
                                            98 );
        map.add( new DefaultFactHandle( 16,
                                        emmental ) );

        // At this point we have 16 facts but only 12 different types of cheeses
        // so no table resize and thus its size is 16

        assertEquals( 16,
                      map.size() );

        Entry[] table = map.getTable();
        assertEquals( 16,
                      table.length );

        final Cheese feta = new Cheese( "feta",
                                        48 );
        map.add( new DefaultFactHandle( 2,
                                        feta ) );

        // This adds our 13th type of cheese. The map is set with an initial capacity of 16 and
        // a threshold of 75%, that after 12 it should resize the map to 32.
        assertEquals( 17,
                      map.size() );

        table = map.getTable();
        assertEquals( 32,
                      table.length );

        final Cheese haloumi = new Cheese( "haloumi",
                                           48 );
        map.add( new DefaultFactHandle( 2,
                                        haloumi ) );

        final Cheese chevre = new Cheese( "chevre",
                                          48 );
        map.add( new DefaultFactHandle( 2,
                                        chevre ) );

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
        final FieldExtractor extractor = cache.getExtractor( Cheese.class,
                                                             "type",
                                                             getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractor,
                                                         pattern );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      declaration,
                                                      equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ) );

        final FactHandleIndexHashTable map = new FactHandleIndexHashTable( new FieldIndex[]{fieldIndex} );

        final Cheese stilton = new Cheese( "stilton",
                                           55 );
        final InternalFactHandle stiltonHandle = new DefaultFactHandle( 2,
                                                                        stilton );

        final Iterator it = map.iterator( new ReteTuple( stiltonHandle ) );
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            fail( "Map is empty, there should be no iteration" );
        }
    }

}
