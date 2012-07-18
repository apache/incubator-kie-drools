/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reteoo;

import org.drools.Cheese;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ValueType;
import org.drools.base.field.LongFieldImpl;
import org.drools.base.field.ObjectFieldImpl;
import org.drools.common.DisconnectedWorkingMemoryEntryPoint;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.common.RuleBasePartitionId;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.MvelConstraintTestUtil;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.PropagationContext;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class CompositeObjectSinkAdapterTest {
    private ReteooRuleBase               ruleBase;
    private BuildContext                 buildContext;

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        this.ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        this.buildContext = new BuildContext( ruleBase,
                                              ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );
    }

    public int    la;
    public int    blah;

    @Test
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

    @Test
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

    @Test
    public void testSingleAlpha() {

        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();

        final MvelConstraint lit = new MvelConstraintTestUtil( "type == \"stilton\"",
                                                                new ObjectFieldImpl( "stilton" ),
                                                                new MockExtractor() );

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

    @Test
    public void testDoubleAlphaWithBeta() {

        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();

        final MvelConstraint lit = new MvelConstraintTestUtil( "type == \"stilton\"",
                                                               new ObjectFieldImpl( "stilton" ),
                                                               new MockExtractor() );

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

        final MvelConstraint lit2 = new MvelConstraintTestUtil( "type == \"cheddar\"",
                                                               new ObjectFieldImpl( "cheddar" ),
                                                               new MockExtractor() );

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

    @Test
    public void testTripleAlpha() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                          "type",
                                                          this.getClass().getClassLoader() );

        final MvelConstraint lit = new MvelConstraintTestUtil( "type == \"stilton\"",
                                                                new ObjectFieldImpl( "stilton" ),
                                                                new MockExtractor() );

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

        final MvelConstraint lit2 = new MvelConstraintTestUtil( "type == \"cheddar\"",
                                                                new ObjectFieldImpl( "cheddar" ),
                                                                new MockExtractor() );

        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al2 );

        assertNull( ad.hashedSinkMap );
        assertEquals( 2,
                      ad.hashableSinks.size() );

        final MvelConstraint lit3 = new MvelConstraintTestUtil( "type == \"stinky\"",
                                                                new ObjectFieldImpl( "stinky" ),
                                                                new MockExtractor() );

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

    @Test
    public void testTripleAlphaCharacterConstraint() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                          "charType",
                                                          this.getClass().getClassLoader() );

        final MvelConstraint lit = new MvelConstraintTestUtil( "charType == 65",
                                                               new LongFieldImpl( 65 ),
                                                               extractor );

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

        final MvelConstraint lit2 = new MvelConstraintTestUtil( "charType == 66",
                                                                new LongFieldImpl( 66 ),
                                                                extractor );

        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al2 );

        assertNull( ad.hashedSinkMap );
        assertEquals( 2,
                      ad.hashableSinks.size() );

        final MvelConstraint lit3 = new MvelConstraintTestUtil( "charType == 67",
                                                                new LongFieldImpl( 67 ),
                                                                extractor );

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
        assertSame( al2,
                    sink );

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
    
    @Test
    public void testTripleAlphaObjectCharacterConstraint() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                          "charObjectType",
                                                          this.getClass().getClassLoader() );

        final MvelConstraint lit = new MvelConstraintTestUtil( "charObjectType == 65",
                                                               new LongFieldImpl( 65 ),
                                                               extractor );

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

        final MvelConstraint lit2 = new MvelConstraintTestUtil( "charObjectType == 66",
                                                                new LongFieldImpl( 66 ),
                                                                extractor );

        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al2 );

        assertNull( ad.hashedSinkMap );
        assertEquals( 2,
                      ad.hashableSinks.size() );

        final MvelConstraint lit3 = new MvelConstraintTestUtil( "charObjectType == 67",
                                                                new LongFieldImpl( 67 ),
                                                                extractor );

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
        cheese.setCharObjectType( 'B' );
        CompositeObjectSinkAdapter.HashKey hashKey = new CompositeObjectSinkAdapter.HashKey();

        // should find this
        hashKey.setValue( extractor.getIndex(),
                          cheese,
                          extractor );
        ObjectSink sink = (ObjectSink) ad.hashedSinkMap.get( hashKey );
        assertSame( al2,
                    sink );

        // should not find this one
        cheese.setCharObjectType( 'X' );
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

    @Test
    public void testPropagationWithNullValue() {

        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                          "type",
                                                          this.getClass().getClassLoader() );

        final MvelConstraint lit1 = new MvelConstraintTestUtil( "type == \"stilton\"",
                                                                new ObjectFieldImpl( "stilton" ),
                                                                new MockExtractor() );

        final AlphaNode al1 = new AlphaNode( buildContext.getNextId(),
                                             lit1,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        final MvelConstraint lit2 = new MvelConstraintTestUtil( "type == \"brie\"",
                new ObjectFieldImpl( "brie" ),
                new MockExtractor() );

        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        final MvelConstraint lit3 = new MvelConstraintTestUtil( "type == \"muzzarela\"",
                                                                new ObjectFieldImpl( "muzzarela" ),
                                                                new MockExtractor() );

        final AlphaNode al3 = new AlphaNode( buildContext.getNextId(),
                                             lit3,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al1 );
        ad.addObjectSink( al2 );
        ad.addObjectSink( al3 );

        InternalFactHandle handle = new ReteooFactHandleFactory().newFactHandle( new Cheese(),
                                                                                 null,
                                                                                 null,
                                                                                 new DisconnectedWorkingMemoryEntryPoint( "DEFAULT" ) );
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
        InternalReadAccessor {

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
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

        public String getNativeReadMethodName() {
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

        public boolean getBooleanValue(Object object) {
            // TODO Auto-generated method stub
            return false;
        }

        public byte getByteValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public char getCharValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public double getDoubleValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public float getFloatValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getHashCode(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getIntValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public long getLongValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public short getShortValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public Object getValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isNullValue(Object object) {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isSelfReference() {
            return false;
        }

        public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory,
                                             Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory,
                                             Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigDecimal getBigDecimalValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigInteger getBigIntegerValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    static class MockBetaNode extends BetaNode {

        MockBetaNode(final int id,
                     final LeftTupleSource leftInput,
                     final ObjectSource rightInput) {
            super( id,
                   RuleBasePartitionId.MAIN_PARTITION,
                   false,
                   leftInput,
                   rightInput,
                   EmptyBetaConstraints.getInstance(),
                   null );
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

        public void assertObject(final InternalFactHandle factHandle,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub

        }

        public void retractRightTuple(final RightTuple rightTuple,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
            //  Auto-generated method stub

        }

        public short getType() {
            return NodeTypeEnums.JoinNode;
        }

        public void modifyLeftTuple(LeftTuple leftTuple,
                                    PropagationContext context,
                                    InternalWorkingMemory workingMemory) {
            // TODO Auto-generated method stub
            
        }

        public void modifyRightTuple(RightTuple rightTuple,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory) {
            // TODO Auto-generated method stub
            
        }

        public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                         LeftTupleSink sink,
                                         boolean leftTupleMemoryEnabled) {
            return new LeftTupleImpl(factHandle, sink, leftTupleMemoryEnabled );
        }    
        
        public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                         LeftTupleSink sink,
                                         boolean leftTupleMemoryEnabled) {
            return new LeftTupleImpl(leftTuple,sink, leftTupleMemoryEnabled );
        }

        public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                         RightTuple rightTuple,
                                         LeftTupleSink sink) {
            return new LeftTupleImpl(leftTuple, rightTuple, sink );
        }   
        
        public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                         RightTuple rightTuple,
                                         LeftTuple currentLeftChild,
                                         LeftTuple currentRightChild,
                                         LeftTupleSink sink,
                                         boolean leftTupleMemoryEnabled) {
            return new LeftTupleImpl(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
        }
    }
}
