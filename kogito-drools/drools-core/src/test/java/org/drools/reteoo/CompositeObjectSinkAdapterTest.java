package org.drools.reteoo;

import java.lang.reflect.Method;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.base.field.LongFieldImpl;
import org.drools.base.field.ObjectFieldImpl;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.PredicateConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.PropagationContext;

public class CompositeObjectSinkAdapterTest extends TestCase {
    private ReteooRuleBase ruleBase;
    private BuildContext   buildContext;

    private EqualityEvaluatorsDefinition equals = new EqualityEvaluatorsDefinition();

    protected void setUp() throws Exception {
        this.ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        this.buildContext = new BuildContext( ruleBase,
                                              ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );
    }

    public int    la;
    public int    blah;
    public String wah;

    public void testBeta() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        final MockBetaNode beta = new MockBetaNode( buildContext.getNextId(),
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
        final AlphaNode al = new AlphaNode( buildContext.getNextId(),
                                            new PredicateConstraint( null,
                                                                     null ),
                                            null,
                                            buildContext );
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
                                                             equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ),
                                                             new ObjectFieldImpl( "stilton" ) );
        final AlphaNode al = new AlphaNode( buildContext.getNextId(),
                                            lit,
                                            new MockObjectSource( 0 ),
                                            buildContext );

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
                                                             equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ),
                                                             new ObjectFieldImpl( "stilton" ) );
        final AlphaNode al = new AlphaNode( buildContext.getNextId(),
                                            lit,
                                            new MockObjectSource( 0 ),
                                            buildContext );

        ad.addObjectSink( al );

        assertNull( ad.otherSinks );
        assertNotNull( ad.hashedFieldIndexes );
        assertEquals( 1,
                      ad.hashableSinks.size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        final LiteralConstraint lit2 = new LiteralConstraint( new MockExtractor(),
                                                              equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ),
                                                              new ObjectFieldImpl( "cheddar" ) );
        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al2 );

        assertNull( ad.otherSinks );
        assertEquals( 2,
                      ad.hashableSinks.size() );
        assertEquals( al,
                      ad.getSinks()[0] );
        assertEquals( al2,
                      ad.getSinks()[1] );

        //add a beta, just for good measure, make sure it leaves others alone
        final MockBetaNode beta = new MockBetaNode( buildContext.getNextId(),
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
        FieldExtractor extractor = ClassFieldExtractorCache.getInstance().getExtractor( Cheese.class,
                                                                                        "type",
                                                                                        this.getClass().getClassLoader() );

        final LiteralConstraint lit = new LiteralConstraint( extractor,
                                                             equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ),
                                                             new ObjectFieldImpl( "stilton" ) );
        final AlphaNode al = new AlphaNode( buildContext.getNextId(),
                                            lit,
                                            new MockObjectSource( buildContext.getNextId() ),
                                            buildContext );

        ad.addObjectSink( al );

        assertNull( ad.otherSinks );
        assertNotNull( ad.hashedFieldIndexes );
        assertEquals( 1,
                      ad.hashableSinks.size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        final LiteralConstraint lit2 = new LiteralConstraint( extractor,
                                                              equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ),
                                                              new ObjectFieldImpl( "cheddar" ) );
        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al2 );

        assertNull( ad.hashedSinkMap );
        assertEquals( 2,
                      ad.hashableSinks.size() );

        final LiteralConstraint lit3 = new LiteralConstraint( extractor,
                                                              equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ),
                                                              new ObjectFieldImpl( "stinky" ) );
        final AlphaNode al3 = new AlphaNode( buildContext.getNextId(),
                                             lit3,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );
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

    public void testTripleAlphaCharacterConstraint() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        FieldExtractor extractor = ClassFieldExtractorCache.getInstance().getExtractor( Cheese.class,
                                                                                        "charType",
                                                                                        this.getClass().getClassLoader() );

        final LiteralConstraint lit = new LiteralConstraint( extractor,
                                                             equals.getEvaluator( extractor.getValueType(), Operator.EQUAL ),
                                                             new LongFieldImpl( 65 ) ); // chars are handled as integers
        final AlphaNode al = new AlphaNode( buildContext.getNextId(),
                                            lit,
                                            new MockObjectSource( buildContext.getNextId() ),
                                            buildContext );

        ad.addObjectSink( al );

        assertNull( ad.otherSinks );
        assertNotNull( ad.hashedFieldIndexes );
        assertEquals( 1,
                      ad.hashableSinks.size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        final LiteralConstraint lit2 = new LiteralConstraint( extractor,
                                                              equals.getEvaluator( extractor.getValueType(), Operator.EQUAL ),
                                                              new LongFieldImpl( 66 ) );
        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al2 );

        assertNull( ad.hashedSinkMap );
        assertEquals( 2,
                      ad.hashableSinks.size() );

        final LiteralConstraint lit3 = new LiteralConstraint( extractor,
                                                              equals.getEvaluator( extractor.getValueType(), Operator.EQUAL ),
                                                              new LongFieldImpl( 67 ) );
        final AlphaNode al3 = new AlphaNode( buildContext.getNextId(),
                                             lit3,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );
        ad.addObjectSink( al3 );

        //this should now be nicely hashed.
        assertNotNull( ad.hashedSinkMap );
        assertNull( ad.hashableSinks );

        // test propagation
        Cheese cheese = new Cheese();
        cheese.setCharType( 'B' );
        CompositeObjectSinkAdapter.HashKey hashKey = new CompositeObjectSinkAdapter.HashKey();

        // should find this
        hashKey.setValue( extractor.getIndex(),
                          cheese,
                          extractor );
        ObjectSink sink = (ObjectSink) ad.hashedSinkMap.get( hashKey );
        assertSame( al2, sink );

        // should not find this one
        cheese.setCharType( 'X' );
        hashKey.setValue( extractor.getIndex(),
                          cheese,
                          extractor );
        sink = (ObjectSink) ad.hashedSinkMap.get( hashKey );
        assertNull( sink );

        //now remove one, check the hashing is undone
        ad.removeObjectSink( al2 );
        assertNotNull( ad.hashableSinks );
        assertEquals( 2,
                      ad.hashableSinks.size() );
        assertNull( ad.hashedSinkMap );

    }

    public void testPropagationWithNullValue() {

        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        FieldExtractor extractor = ClassFieldExtractorCache.getInstance().getExtractor( Cheese.class,
                                                                                        "type",
                                                                                        this.getClass().getClassLoader() );
        final LiteralConstraint lit1 = new LiteralConstraint( extractor,
                                                              equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ),
                                                              new ObjectFieldImpl( "stilton" ) );
        final AlphaNode al1 = new AlphaNode( buildContext.getNextId(),
                                             lit1,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        final LiteralConstraint lit2 = new LiteralConstraint( extractor,
                                                              equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ),
                                                              new ObjectFieldImpl( "brie" ) );
        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        final LiteralConstraint lit3 = new LiteralConstraint( extractor,
                                                              equals.getEvaluator( ValueType.STRING_TYPE, Operator.EQUAL ),
                                                              new ObjectFieldImpl( "muzzarela" ) );
        final AlphaNode al3 = new AlphaNode( buildContext.getNextId(),
                                             lit3,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al1 );
        ad.addObjectSink( al2 );
        ad.addObjectSink( al3 );

        InternalFactHandle handle = new ReteooFactHandleFactory().newFactHandle( new Cheese(), false, null );
        try {
            ad.propagateAssertObject( handle,
                                      null,
                                      null );
        } catch ( RuntimeException e ) {
            fail( "Not supposed to throw any exception: " + e.getMessage() );
        }

    }

    public static class MockExtractor
        implements
        FieldExtractor {

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }
        public int getIndex() {
            //  Auto-generated method stub
            return 0;
        }

        public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                       final Object object) {
            //  Auto-generated method stub
            return false;
        }

        public byte getByteValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public char getCharValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public double getDoubleValue(InternalWorkingMemory workingMemory,
                                     final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public Class getExtractToClass() {
            //  Auto-generated method stub
            return null;
        }

        public String getExtractToClassName() {
            //  Auto-generated method stub
            return null;
        }

        public float getFloatValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public int getIntValue(InternalWorkingMemory workingMemory,
                               final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public long getLongValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public Method getNativeReadMethod() {
            //  Auto-generated method stub
            return null;
        }

        public short getShortValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
            //  Auto-generated method stub
            return 0;
        }

        public Object getValue(InternalWorkingMemory workingMemory,
                               final Object object) {
            //  Auto-generated method stub
            return null;
        }

        public boolean isNullValue(final Object object,
                                   InternalWorkingMemory workingMemory) {
            return false;
        }

        public ValueType getValueType() {
            //  Auto-generated method stub
            return ValueType.STRING_TYPE;
        }

        public int getHashCode(InternalWorkingMemory workingMemory,
                               final Object object) {
            return 0;
        }

        public boolean isGlobal() {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isNullValue(InternalWorkingMemory workingMemory,
                                   Object object) {
            // TODO Auto-generated method stub
            return false;
        }

    }

    static class MockBetaNode extends BetaNode {

        MockBetaNode(final int id,
                     final LeftTupleSource leftInput,
                     final ObjectSource rightInput) {
            super( id,
                   leftInput,
                   rightInput,
                   EmptyBetaConstraints.getInstance() );
            //  Auto-generated constructor stub
        }

        public void updateSink(final LeftTupleSink sink,
                               final PropagationContext context,
                               final InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub

        }

        public void assertLeftTuple(final LeftTuple tuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub

        }

        public void retractLeftTuple(final LeftTuple tuple,
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
