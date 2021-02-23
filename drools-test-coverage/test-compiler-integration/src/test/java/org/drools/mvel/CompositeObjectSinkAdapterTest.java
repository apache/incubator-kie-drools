/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ValueType;
import org.drools.core.base.field.LongFieldImpl;
import org.drools.core.base.field.ObjectFieldImpl;
import org.drools.core.common.DisconnectedWorkingMemoryEntryPoint;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.PropagationContext;
import org.drools.mvel.model.Cheese;
import org.drools.mvel.model.MockObjectSource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class CompositeObjectSinkAdapterTest {
    private InternalKnowledgeBase        kBase;
    private BuildContext                 buildContext;

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();

        this.buildContext = new BuildContext( kBase );
        this.buildContext.setRule(new RuleImpl("test"));
    }

    public int    la;
    public int    blah;

    @Test
    public void testBeta() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        final MockBetaNode beta = new MockBetaNode( buildContext.getNextId(),
                                                    new MockBetaNode( ),
                                                    new MockObjectSource(),
                                                    buildContext );
        ad.addObjectSink( beta );
        assertEquals( 1,
                      ad.getSinks().length );
        assertEquals( beta,
                      ad.getSinks()[0] );

        assertEquals( 1,
                      ad.getOtherSinks().size() );
        assertEquals( beta,
                      ad.getOtherSinks().getFirst() );

        assertNull( ad.getHashableSinks() );
        assertNull( ad.getHashedFieldIndexes() );
        assertNull( ad.getHashedSinkMap() );

        ad.removeObjectSink( beta );
        assertNull( ad.getOtherSinks() );
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
                      ad.getOtherSinks().size() );
        assertEquals( al,
                      ad.getOtherSinks().getFirst() );

        ad.removeObjectSink( al );
        assertEquals( 0,
                      ad.getSinks().length );
        assertNull( ad.getOtherSinks() );

    }

    @Test
    public void testSingleAlpha() {

        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();

        final MVELConstraint lit = new MVELConstraintTestUtil( "type == \"stilton\"",
                                                                new ObjectFieldImpl( "stilton" ),
                                                                new MockExtractor() );

        final AlphaNode al = new AlphaNode( buildContext.getNextId(),
                                            lit,
                                            new MockObjectSource( 0 ),
                                            buildContext );

        ad.addObjectSink( al );

        assertNull( ad.getOtherSinks() );
        assertNotNull( ad.getHashedFieldIndexes() );
        assertEquals( 1,
                      ad.getHashableSinks().size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        ad.removeObjectSink( al );
        assertNull( ad.getOtherSinks() );
        assertNull( ad.getHashableSinks() );

    }

    @Test
    public void testDoubleAlphaWithBeta() {

        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();

        final MVELConstraint lit = new MVELConstraintTestUtil( "type == \"stilton\"",
                                                               new ObjectFieldImpl( "stilton" ),
                                                               new MockExtractor() );

        final AlphaNode al = new AlphaNode( buildContext.getNextId(),
                                            lit,
                                            new MockObjectSource( 0 ),
                                            buildContext );

        ad.addObjectSink( al );

        assertNull( ad.getOtherSinks() );
        assertNotNull( ad.getHashedFieldIndexes() );
        assertEquals( 1,
                      ad.getHashableSinks().size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        final MVELConstraint lit2 = new MVELConstraintTestUtil( "type == \"cheddar\"",
                                                               new ObjectFieldImpl( "cheddar" ),
                                                               new MockExtractor() );

        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al2 );

        assertNull( ad.getOtherSinks() );
        assertEquals( 2,
                      ad.getHashableSinks().size() );
        assertEquals( al,
                      ad.getSinks()[0] );
        assertEquals( al2,
                      ad.getSinks()[1] );

        //add a beta, just for good measure, make sure it leaves others alone
        final MockBetaNode beta = new MockBetaNode( buildContext.getNextId(),
                                                    new MockBetaNode( ),
                                                    new MockObjectSource(),
                                                    buildContext );
        ad.addObjectSink( beta );
        assertNotNull( ad.getOtherSinks() );
        assertEquals( 2,
                      ad.getHashableSinks().size() );

        assertEquals( 1,
                      ad.getOtherSinks().size() );
        assertEquals( beta,
                      ad.getOtherSinks().getFirst() );

        ad.removeObjectSink( beta );
        assertNull( ad.getOtherSinks() );
        assertEquals( 2,
                      ad.getHashableSinks().size() );

    }

    @Test
    public void testTripleAlpha() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                          "type" );

        final MVELConstraint lit = new MVELConstraintTestUtil( "type == \"stilton\"",
                                                                new ObjectFieldImpl( "stilton" ),
                                                                new MockExtractor() );

        final AlphaNode al = new AlphaNode( buildContext.getNextId(),
                                            lit,
                                            new MockObjectSource( buildContext.getNextId() ),
                                            buildContext );

        ad.addObjectSink( al );

        assertNull( ad.getOtherSinks() );
        assertNotNull( ad.getHashedFieldIndexes() );
        assertEquals( 1,
                      ad.getHashableSinks().size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        final MVELConstraint lit2 = new MVELConstraintTestUtil( "type == \"cheddar\"",
                                                                new ObjectFieldImpl( "cheddar" ),
                                                                new MockExtractor() );

        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al2 );

        assertNull( ad.getHashedSinkMap() );
        assertEquals( 2,
                      ad.getHashableSinks().size() );

        final MVELConstraint lit3 = new MVELConstraintTestUtil( "type == \"stinky\"",
                                                                new ObjectFieldImpl( "stinky" ),
                                                                new MockExtractor() );

        final AlphaNode al3 = new AlphaNode( buildContext.getNextId(),
                                             lit3,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );
        ad.addObjectSink( al3 );

        //this should now be nicely hashed.
        assertNotNull( ad.getHashedSinkMap() );
        assertNull( ad.getHashableSinks() );

        //now remove one, check the hashing is undone
        ad.removeObjectSink( al2 );
        assertNotNull( ad.getHashableSinks() );
        assertEquals( 2,
                      ad.getHashableSinks().size() );
        assertNull( ad.getHashedSinkMap() );

    }

    @Test
    public void testTripleAlphaCharacterConstraint() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                          "charType" );

        final MVELConstraint lit = new MVELConstraintTestUtil( "charType == 65",
                                                               new LongFieldImpl( 65 ),
                                                               extractor );

        final AlphaNode al = new AlphaNode( buildContext.getNextId(),
                                            lit,
                                            new MockObjectSource( buildContext.getNextId() ),
                                            buildContext );

        ad.addObjectSink( al );

        assertNull( ad.getOtherSinks() );
        assertNotNull( ad.getHashedFieldIndexes() );
        assertEquals( 1,
                      ad.getHashableSinks().size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        final MVELConstraint lit2 = new MVELConstraintTestUtil( "charType == 66",
                                                                new LongFieldImpl( 66 ),
                                                                extractor );

        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al2 );

        assertNull( ad.getHashedSinkMap() );
        assertEquals( 2,
                      ad.getHashableSinks().size() );

        final MVELConstraint lit3 = new MVELConstraintTestUtil( "charType == 67",
                                                                new LongFieldImpl( 67 ),
                                                                extractor );

        final AlphaNode al3 = new AlphaNode( buildContext.getNextId(),
                                             lit3,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );
        ad.addObjectSink( al3 );

        //this should now be nicely hashed.
        assertNotNull( ad.getHashedSinkMap() );
        assertNull( ad.getHashableSinks() );

        // test propagation
        Cheese cheese = new Cheese();
        cheese.setCharType( 'B' );
        CompositeObjectSinkAdapter.HashKey hashKey = new CompositeObjectSinkAdapter.HashKey();

        // should find this
        hashKey.setValue( extractor.getIndex(),
                          cheese,
                          extractor );
        ObjectSink sink = (ObjectSink) ad.getHashedSinkMap().get( hashKey );
        assertSame( al2,
                    sink );

        // should not find this one
        cheese.setCharType( 'X' );
        hashKey.setValue( extractor.getIndex(),
                          cheese,
                          extractor );
        sink = (ObjectSink) ad.getHashedSinkMap().get( hashKey );
        assertNull( sink );

        //now remove one, check the hashing is undone
        ad.removeObjectSink( al2 );
        assertNotNull( ad.getHashableSinks() );
        assertEquals( 2,
                      ad.getHashableSinks().size() );
        assertNull( ad.getHashedSinkMap() );
    }
    
    @Test
    public void testTripleAlphaObjectCharacterConstraint() {
        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                          "charObjectType" );

        final MVELConstraint lit = new MVELConstraintTestUtil( "charObjectType == 65",
                                                               new LongFieldImpl( 65 ),
                                                               extractor );

        final AlphaNode al = new AlphaNode( buildContext.getNextId(),
                                            lit,
                                            new MockObjectSource( buildContext.getNextId() ),
                                            buildContext );

        ad.addObjectSink( al );

        assertNull( ad.getOtherSinks() );
        assertNotNull( ad.getHashedFieldIndexes() );
        assertEquals( 1,
                      ad.getHashableSinks().size() );
        assertEquals( al,
                      ad.getSinks()[0] );

        final MVELConstraint lit2 = new MVELConstraintTestUtil( "charObjectType == 66",
                                                                new LongFieldImpl( 66 ),
                                                                extractor );

        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        ad.addObjectSink( al2 );

        assertNull( ad.getHashedSinkMap() );
        assertEquals( 2,
                      ad.getHashableSinks().size() );

        final MVELConstraint lit3 = new MVELConstraintTestUtil( "charObjectType == 67",
                                                                new LongFieldImpl( 67 ),
                                                                extractor );

        final AlphaNode al3 = new AlphaNode( buildContext.getNextId(),
                                             lit3,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );
        ad.addObjectSink( al3 );

        //this should now be nicely hashed.
        assertNotNull( ad.getHashedSinkMap() );
        assertNull( ad.getHashableSinks() );

        // test propagation
        Cheese cheese = new Cheese();
        cheese.setCharObjectType( 'B' );
        CompositeObjectSinkAdapter.HashKey hashKey = new CompositeObjectSinkAdapter.HashKey();

        // should find this
        hashKey.setValue( extractor.getIndex(),
                          cheese,
                          extractor );
        ObjectSink sink = (ObjectSink) ad.getHashedSinkMap().get( hashKey );
        assertSame( al2,
                    sink );

        // should not find this one
        cheese.setCharObjectType( 'X' );
        hashKey.setValue( extractor.getIndex(),
                          cheese,
                          extractor );
        sink = (ObjectSink) ad.getHashedSinkMap().get( hashKey );
        assertNull( sink );

        //now remove one, check the hashing is undone
        ad.removeObjectSink( al2 );
        assertNotNull( ad.getHashableSinks() );
        assertEquals( 2,
                      ad.getHashableSinks().size() );
        assertNull( ad.getHashedSinkMap() );
    }    

    @Test
    public void testPropagationWithNullValue() {

        final CompositeObjectSinkAdapter ad = new CompositeObjectSinkAdapter();
        InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                          "type" );

        final MVELConstraint lit1 = new MVELConstraintTestUtil( "type == \"stilton\"",
                                                                new ObjectFieldImpl( "stilton" ),
                                                                new MockExtractor() );

        final AlphaNode al1 = new AlphaNode( buildContext.getNextId(),
                                             lit1,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        final MVELConstraint lit2 = new MVELConstraintTestUtil( "type == \"brie\"",
                new ObjectFieldImpl( "brie" ),
                new MockExtractor() );

        final AlphaNode al2 = new AlphaNode( buildContext.getNextId(),
                                             lit2,
                                             new MockObjectSource( buildContext.getNextId() ),
                                             buildContext );

        final MVELConstraint lit3 = new MVELConstraintTestUtil( "type == \"muzzarela\"",
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
            return 0;
        }

        public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                       final Object object) {
            return false;
        }

        public byte getByteValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            return 0;
        }

        public char getCharValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            return 0;
        }

        public double getDoubleValue(InternalWorkingMemory workingMemory,
                                     final Object object) {
            return 0;
        }

        public Class getExtractToClass() {
            return null;
        }

        public String getExtractToClassName() {
            return null;
        }

        public float getFloatValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
            return 0;
        }

        public int getIntValue(InternalWorkingMemory workingMemory,
                               final Object object) {
            return 0;
        }

        public long getLongValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            return 0;
        }

        public Method getNativeReadMethod() {
            return null;
        }

        public String getNativeReadMethodName() {
            return null;
        }

        public short getShortValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
            return 0;
        }

        public Object getValue(InternalWorkingMemory workingMemory,
                               final Object object) {
            return null;
        }

        public boolean isNullValue(final Object object,
                                   InternalWorkingMemory workingMemory) {
            return false;
        }

        public ValueType getValueType() {
            return ValueType.STRING_TYPE;
        }

        public int getHashCode(InternalWorkingMemory workingMemory,
                               final Object object) {
            return 0;
        }

        public boolean isGlobal() {
            return false;
        }

        public boolean isNullValue(InternalWorkingMemory workingMemory,
                                   Object object) {
            return false;
        }

        public boolean getBooleanValue(Object object) {
            return false;
        }

        public byte getByteValue(Object object) {
            return 0;
        }

        public char getCharValue(Object object) {
            return 0;
        }

        public double getDoubleValue(Object object) {
            return 0;
        }

        public float getFloatValue(Object object) {
            return 0;
        }

        public int getHashCode(Object object) {
            return 0;
        }

        public int getIntValue(Object object) {
            return 0;
        }

        public long getLongValue(Object object) {
            return 0;
        }

        public short getShortValue(Object object) {
            return 0;
        }

        public Object getValue(Object object) {
            return null;
        }

        public boolean isNullValue(Object object) {
            return false;
        }

        public boolean isSelfReference() {
            return false;
        }

        public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory,
                                             Object object) {
            return null;
        }

        public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory,
                                             Object object) {
            return null;
        }

        public BigDecimal getBigDecimalValue(Object object) {
            return null;
        }

        public BigInteger getBigIntegerValue(Object object) {
            return null;
        }

    }

    public static class MockBetaNode extends BetaNode {
        
        public MockBetaNode() {
            
        }

        @Override
        protected boolean doRemove( RuleRemovalContext context, ReteooBuilder builder) {
            return true;
        }

        MockBetaNode(final int id,
                     final LeftTupleSource leftInput,
                     final ObjectSource rightInput,
                     BuildContext buildContext) {
            super( id,
                   leftInput,
                   rightInput,
                   EmptyBetaConstraints.getInstance(),
                   buildContext );
        }        

        MockBetaNode(final int id,
                     final LeftTupleSource leftInput,
                     final ObjectSource rightInput) {
            super( id,
                   leftInput,
                   rightInput,
                   EmptyBetaConstraints.getInstance(),
                   null );
        }

        public void assertObject(final InternalFactHandle factHandle,
                                 final PropagationContext pctx,
                                 final InternalWorkingMemory workingMemory) {
        }

        @Override
        public void modifyObject( InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory) {
        }

        public void retractRightTuple(final RightTuple rightTuple,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        }

        public short getType() {
            return 0;
        }

        public void modifyRightTuple(RightTuple rightTuple,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory) {
        }

        public LeftTuple createLeftTuple( InternalFactHandle factHandle,
                                          boolean leftTupleMemoryEnabled) {
            return new LeftTupleImpl(factHandle, this, leftTupleMemoryEnabled );
        }    
        
        public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                         Sink sink,
                                         PropagationContext pctx, boolean leftTupleMemoryEnabled) {
            return new LeftTupleImpl(leftTuple,sink, pctx, leftTupleMemoryEnabled );
        }

        public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                         final LeftTuple leftTuple,
                                         final Sink sink) {
            return new LeftTupleImpl(factHandle,leftTuple, sink );
        }

        public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                         RightTuple rightTuple,
                                         Sink sink) {
            return new LeftTupleImpl(leftTuple, rightTuple, sink );
        }   
        
        public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                         RightTuple rightTuple,
                                         LeftTuple currentLeftChild,
                                         LeftTuple currentRightChild,
                                         Sink sink,
                                         boolean leftTupleMemoryEnabled) {
            return new LeftTupleImpl(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
        }
        public Memory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
            return super.createMemory( config, wm);
        }

        @Override
        public LeftTuple createPeer(LeftTuple original) {
            return null;
        }                
    }
}
