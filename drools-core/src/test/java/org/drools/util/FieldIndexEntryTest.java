package org.drools.util;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.util.AbstractHashTable.FactEntryImpl;
import org.drools.util.AbstractHashTable.FieldIndex;
import org.drools.util.AbstractHashTable.SingleIndex;
import org.drools.util.RightTupleIndexHashTable.FieldIndexEntry;

public class FieldIndexEntryTest extends TestCase {
    ClassFieldExtractorCache cache = ClassFieldExtractorCache.getInstance();
    EqualityEvaluatorsDefinition equals = new EqualityEvaluatorsDefinition();

    public void testSingleEntry() {
        final ClassFieldExtractor extractor = cache.getExtractor( Cheese.class,
                                                                  "type",
                                                                  getClass().getClassLoader() );

        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      null,
                                                      equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ) );
        final SingleIndex singleIndex = new SingleIndex( new FieldIndex[]{fieldIndex},
                                                         1 );

        final FieldIndexEntry index = new FieldIndexEntry( singleIndex,
                                                           "stilton".hashCode() );

        // Test initial construction
        assertNull( index.getFirst() );
        assertEquals( "stilton".hashCode(),
                      index.hashCode() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );

        // test add
        index.add( h1 );

        final FactEntryImpl entry1 = index.getFirst();
        assertSame( h1,
                    entry1.getFactHandle() );
        assertNull( entry1.getNext() );
        assertSame( entry1,
                    index.get( h1 ) );

        // test get
        final FactEntryImpl entry2 = index.get( h1 );
        assertSame( entry1,
                    entry2 );

        // test remove
        index.remove( h1 );
        assertNull( index.getFirst() );
    }

    public void testTwoEntries() {
        final ClassFieldExtractor extractor = cache.getExtractor( Cheese.class,
                                                                  "type",
                                                                  getClass().getClassLoader() );
        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      null,
                                                      equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ) );
        final SingleIndex singleIndex = new SingleIndex( new FieldIndex[]{fieldIndex},
                                                         1 );

        final FieldIndexEntry index = new FieldIndexEntry( singleIndex,
                                                           "stilton".hashCode() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );
        final Cheese stilton2 = new Cheese( "stilton",
                                            59 );
        final InternalFactHandle h2 = new DefaultFactHandle( 2,
                                                             stilton2 );

        // test add
        index.add( h1 );
        index.add( h2 );
        assertEquals( h2,
                      index.getFirst().getFactHandle() );
        assertEquals( h1,
                      ((FactEntryImpl) index.getFirst().getNext()).getFactHandle() );

        // test get
        assertEquals( h1,
                      index.get( h1 ).getFactHandle() );
        assertEquals( h2,
                      index.get( h2 ).getFactHandle() );

        // test removal for combinations
        // remove first
        index.remove( h2 );
        assertEquals( h1,
                      index.getFirst().getFactHandle() );

        // remove second
        index.add( h2 );
        index.remove( h1 );
        assertEquals( h2,
                      index.getFirst().getFactHandle() );

        // check index type does not change, as this fact is removed
        stilton1.setType( "cheddar" );
    }

    public void testThreeEntries() {
        final ClassFieldExtractor extractor = cache.getExtractor( Cheese.class,
                                                                  "type",
                                                                  getClass().getClassLoader() );
        final FieldIndex fieldIndex = new FieldIndex( extractor,
                                                      null,
                                                      equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ) );
        final SingleIndex singleIndex = new SingleIndex( new FieldIndex[]{fieldIndex},
                                                         1 );

        final FieldIndexEntry index = new FieldIndexEntry( singleIndex,
                                                           "stilton".hashCode() );

        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );
        final Cheese stilton2 = new Cheese( "stilton",
                                            59 );
        final InternalFactHandle h2 = new DefaultFactHandle( 2,
                                                             stilton2 );
        final Cheese stilton3 = new Cheese( "stilton",
                                            59 );
        final InternalFactHandle h3 = new DefaultFactHandle( 3,
                                                             stilton3 );

        // test add
        index.add( h1 );
        index.add( h2 );
        index.add( h3 );
        assertEquals( h3,
                      index.getFirst().getFactHandle() );
        assertEquals( h2,
                      ((FactEntryImpl) index.getFirst().getNext()).getFactHandle() );
        assertEquals( h1,
                      ((FactEntryImpl) index.getFirst().getNext().getNext()).getFactHandle() );

        // test get
        assertEquals( h1,
                      index.get( h1 ).getFactHandle() );
        assertEquals( h2,
                      index.get( h2 ).getFactHandle() );
        assertEquals( h3,
                      index.get( h3 ).getFactHandle() );

        // test removal for combinations
        //remove first
        index.remove( h3 );
        assertEquals( h2,
                      index.getFirst().getFactHandle() );
        assertEquals( h1,
                      ((FactEntryImpl) index.getFirst().getNext()).getFactHandle() );

        index.add( h3 );
        index.remove( h2 );
        assertEquals( h3,
                      index.getFirst().getFactHandle() );
        assertEquals( h1,
                      ((FactEntryImpl) index.getFirst().getNext()).getFactHandle() );

        index.add( h2 );
        index.remove( h1 );
        assertEquals( h2,
                      index.getFirst().getFactHandle() );
        assertEquals( h3,
                      ((FactEntryImpl) index.getFirst().getNext()).getFactHandle() );

        index.remove( index.getFirst().getFactHandle() );
        // check index type does not change, as this fact is removed
        stilton2.setType( "cheddar" );
    }
}
