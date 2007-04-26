package org.drools.reteoo;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.base.evaluators.StringFactory;
import org.drools.base.field.ObjectFieldImpl;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.PredicateConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.PropagationContext;

public class CompositeObjectSinkAdapterTest extends TestCase {

    public int    la;
    public int    blah;
    public String wah;

    public void testBeta() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        final MockBetaNode beta = new MockBetaNode( 0,
                                              null,
                                              null );
        ad.addObjectSink( beta );
        assertEquals( 1,
                      ad.getSinks().length );
        assertEquals( beta,
                      ad.getSinks()[0] );

        assertEquals( 1,
                      ad.otherSinks.size() );
        assertEquals( beta,
                      ad.otherSinks.getFirst() );

        assertNull( ad.hashableSinks );
        assertNull( ad.hashedFieldIndexes );
        assertNull( ad.hashedSinkMap );

        ad.removeObjectSink( beta );
        assertNull( ad.otherSinks );
        assertEquals( 0,
                      ad.getSinks().length );
    }

    public void testAlphaWithPredicate() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        final AlphaNode al = new AlphaNode( 0,
                                      new PredicateConstraint( null,
                                                               null ),
                                      null );
        ad.addObjectSink( al );

        assertEquals( 1,
                      ad.getSinks().length );
        assertEquals( 1,
                      ad.otherSinks.size() );
        assertEquals( al,
                      ad.otherSinks.getFirst() );

        ad.removeObjectSink( al );
        assertEquals( 0,
                      ad.getSinks().length );
        assertNull( ad.otherSinks );

    }

    public void testSingleAlpha() {

        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        final LiteralConstraint lit = new LiteralConstraint( new MockExtractor(),
                                                       StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                       new ObjectFieldImpl( "stilton" ) );
        final AlphaNode al = new AlphaNode( 0,
                                      lit,
                                      new MockObjectSource( 0 ) );

        ad.addObjectSink( al );

        assertNull( ad.otherSinks );
        assertNotNull( ad.hashedFieldIndexes );
        assertEquals( 1,
                      ad.hashableSinks.size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        ad.removeObjectSink( al );
        assertNull( ad.otherSinks );
        assertNull( ad.hashableSinks );

    }

    public void testDoubleAlphaWithBeta() {

        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        final LiteralConstraint lit = new LiteralConstraint( new MockExtractor(),
                                                       StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                       new ObjectFieldImpl( "stilton" ) );
        final AlphaNode al = new AlphaNode( 0,
                                      lit,
                                      new MockObjectSource( 0 ) );

        ad.addObjectSink( al );

        assertNull( ad.otherSinks );
        assertNotNull( ad.hashedFieldIndexes );
        assertEquals( 1,
                      ad.hashableSinks.size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        final LiteralConstraint lit2 = new LiteralConstraint( new MockExtractor(),
                                                        StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                        new ObjectFieldImpl( "cheddar" ) );
        final AlphaNode al2 = new AlphaNode( 1,
                                       lit2,
                                       new MockObjectSource( 0 ) );

        ad.addObjectSink( al2 );

        assertNull( ad.otherSinks );
        assertEquals( 2,
                      ad.hashableSinks.size() );
        assertEquals( al,
                      ad.getSinks()[0] );
        assertEquals( al2,
                      ad.getSinks()[1] );

        //add a beta, just for good measure, make sure it leaves others alone
        final MockBetaNode beta = new MockBetaNode( 0,
                                              null,
                                              null );
        ad.addObjectSink( beta );
        assertNotNull( ad.otherSinks );
        assertEquals( 2,
                      ad.hashableSinks.size() );

        assertEquals( 1,
                      ad.otherSinks.size() );
        assertEquals( beta,
                      ad.otherSinks.getFirst() );

        ad.removeObjectSink( beta );
        assertNull( ad.otherSinks );
        assertEquals( 2,
                      ad.hashableSinks.size() );

    }

    public void testTripleAlpha() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        final LiteralConstraint lit = new LiteralConstraint( new MockExtractor(),
                                                       StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                       new ObjectFieldImpl( "stilton" ) );
        final AlphaNode al = new AlphaNode( 0,
                                      lit,
                                      new MockObjectSource( 0 ) );

        ad.addObjectSink( al );

        assertNull( ad.otherSinks );
        assertNotNull( ad.hashedFieldIndexes );
        assertEquals( 1,
                      ad.hashableSinks.size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        final LiteralConstraint lit2 = new LiteralConstraint( new MockExtractor(),
                                                        StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                        new ObjectFieldImpl( "cheddar" ) );
        final AlphaNode al2 = new AlphaNode( 1,
                                       lit2,
                                       new MockObjectSource( 1 ) );

        ad.addObjectSink( al2 );

        assertNull( ad.hashedSinkMap );
        assertEquals( 2,
                      ad.hashableSinks.size() );

        final LiteralConstraint lit3 = new LiteralConstraint( new MockExtractor(),
                                                        StringFactory.getInstance().getEvaluator( Operator.EQUAL ),
                                                        new ObjectFieldImpl( "stinky" ) );
        final AlphaNode al3 = new AlphaNode( 1,
                                       lit3,
                                       new MockObjectSource( 2 ) );
        ad.addObjectSink( al3 );

        //this should now be nicely hashed.
        assertNotNull( ad.hashedSinkMap );
        assertNull( ad.hashableSinks );

        //now remove one, check the hashing is undone
        ad.removeObjectSink( al2 );
        assertNotNull( ad.hashableSinks );
        assertEquals( 2,
                      ad.hashableSinks.size() );
        assertNull( ad.hashedSinkMap );

    }

    static class MockExtractor
        implements
        FieldExtractor {

        public int getIndex() {
            //  Auto-generated method stub
            return 0;
        }

        public boolean getBooleanValue(final Object object) {
            //  Auto-generated method stub
            return false;
        }

        public byte getByteValue(final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public char getCharValue(final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public double getDoubleValue(final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public Class getExtractToClass() {
            //  Auto-generated method stub
            return null;
        }

        public float getFloatValue(final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public int getIntValue(final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public long getLongValue(final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public Method getNativeReadMethod() {
            //  Auto-generated method stub
            return null;
        }

        public short getShortValue(final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public Object getValue(final Object object) {
            //  Auto-generated method stub
            return null;
        }
        
        public boolean isNullValue(final Object object) {
            return false;
        }

        public ValueType getValueType() {
            //  Auto-generated method stub
            return null;
        }

        public int getHashCode(final Object object) {
            return 0;
        }

    }

    static class MockBetaNode extends BetaNode {

        MockBetaNode(final int id,
                     final TupleSource leftInput,
                     final ObjectSource rightInput) {
            super( id,
                   leftInput,
                   rightInput );
            //  Auto-generated constructor stub
        }

        public void updateSink(final TupleSink sink,
                               final PropagationContext context,
                               final InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub

        }

        public void assertTuple(final ReteTuple tuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub

        }

        public void retractTuple(final ReteTuple tuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub

        }

        public void assertObject(final InternalFactHandle handle,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub

        }

        public void retractObject(final InternalFactHandle handle,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub

        }

    }
}
