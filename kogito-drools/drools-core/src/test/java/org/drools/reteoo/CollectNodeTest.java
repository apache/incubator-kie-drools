/*
 * Copyright 2005 JBoss Inc
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.drools.DroolsTestCase;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Collect;
import org.drools.rule.Column;
import org.drools.rule.Rule;
import org.drools.spi.MockConstraint;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * @author etirelli
 *
 */
public class CollectNodeTest extends DroolsTestCase {
    Rule                rule;
    PropagationContext  context;
    ReteooWorkingMemory workingMemory;
    MockObjectSource    objectSource;
    MockTupleSource     tupleSource;
    MockTupleSink       sink;
    BetaNode            node;
    BetaMemory          memory;
    MockConstraint      constraint = new MockConstraint();
    Collect             collect;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new Rule( "test-rule" );
        this.context = new PropagationContextImpl( 0,
                                                   PropagationContext.ASSERTION,
                                                   null,
                                                   null );
        this.workingMemory = new ReteooWorkingMemory( 1,
                                                      (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        this.tupleSource = new MockTupleSource( 4 );
        this.objectSource = new MockObjectSource( 4 );
        this.sink = new MockTupleSink();

        final ObjectType srcObjType = new ClassObjectType( String.class );
        final Column sourceColumn = new Column( 0,
                                          srcObjType );
        final ObjectType resultObjType = new ClassObjectType( LinkedList.class );
        final Column resultColumn = new Column( 1,
                                          resultObjType );
        this.collect = new Collect( sourceColumn,
                                    resultColumn );

        this.node = new CollectNode( 15,
                                     this.tupleSource,
                                     this.objectSource,
                                     this.collect );

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        // check memories are empty
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightObjectMemory().size() );
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUpdateNewNode() {
        this.node.updateNewNode( this.workingMemory,
                                 this.context );
        Assert.assertEquals( "No tuple should be propagated",
                             0,
                             this.sink.getAsserted().size() );

        this.node.assertTuple( new ReteTuple( this.workingMemory.getFactHandleFactory().newFactHandle( "cheese" ) ),
                               this.context,
                               this.workingMemory );
        this.node.assertTuple( new ReteTuple( this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese" ) ),
                               this.context,
                               this.workingMemory );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );

        final MockTupleSink otherSink = new MockTupleSink();

        this.node.addTupleSink( otherSink );
        this.node.updateNewNode( this.workingMemory,
                                 this.context );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             otherSink.getAsserted().size() );
    }

    public void testGetPropagatedTuples() {

        this.node.assertTuple( new ReteTuple( this.workingMemory.getFactHandleFactory().newFactHandle( "cheese" ) ),
                               this.context,
                               this.workingMemory );
        this.node.assertTuple( new ReteTuple( this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese" ) ),
                               this.context,
                               this.workingMemory );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );

        final Tuple t1 = (Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0];
        final Tuple t2 = (Tuple) ((Object[]) this.sink.getAsserted().get( 1 ))[0];

        final List propagated = this.node.getPropagatedTuples( this.workingMemory,
                                                         this.sink );
        Assert.assertEquals( "Wrong Tuple propagated",
                             t1,
                             propagated.get( 0 ) );
        Assert.assertEquals( "Wrong Tuple propagated",
                             t2,
                             propagated.get( 1 ) );

    }

    public void testAssertTuple() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese" );
        final ReteTuple tuple0 = new ReteTuple( f0 );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple0,
                               this.context,
                               this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightObjectMemory().size() );
        Assert.assertTrue( "An empty collection should be propagated",
                           ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).isEmpty() );

        // assert tuple, should add left memory 
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese" );

        final ReteTuple tuple1 = new ReteTuple( f1 );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );
        assertEquals( 2,
                      this.memory.getLeftTupleMemory().size() );
        Assert.assertTrue( "An empty collection should be propagated",
                           ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 1 ))[0]).get( 1 )).getObject()).isEmpty() );

        final ReteTuple tuple = (ReteTuple) this.memory.getLeftTupleMemory().iterator( this.workingMemory,
                                                                                       f0 ).next();
        assertEquals( tuple0,
                      tuple );
        assertEquals( tuple1,
                      tuple.getNext() );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

    public void testAssertTupleWithObjects() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese" );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese" );

        final ReteTuple tuple0 = new ReteTuple( f0 );

        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple0,
                               this.context,
                               this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 2,
                      this.memory.getRightObjectMemory().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).size() );

        // assert tuple, should add left memory 
        final ReteTuple tuple1 = new ReteTuple( f1 );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );
        assertEquals( 2,
                      this.memory.getLeftTupleMemory().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 1 ))[0]).get( 1 )).getObject()).size() );

        final ReteTuple tuple = (ReteTuple) this.memory.getLeftTupleMemory().iterator( this.workingMemory,
                                                                                       f0 ).next();
        assertEquals( tuple0,
                      tuple );
        assertEquals( tuple1,
                      tuple.getNext() );

        Assert.assertEquals( "Two tuples should have been propagated",
                             2,
                             this.sink.getAsserted().size() );
    }

    public void testModifyTuple() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese" );

        final ReteTuple tuple0 = new ReteTuple( f0 );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple0,
                               this.context,
                               this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightObjectMemory().size() );
        Assert.assertTrue( "An empty collection should be propagated",
                           ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).isEmpty() );

        this.node.modifyTuple( tuple0,
                               this.context,
                               this.workingMemory );
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 1,
                      this.sink.getRetracted().size() );
        assertEquals( 2,
                      this.sink.getAsserted().size() );
        Assert.assertTrue( "An empty collection should be propagated",
                           ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 1 ))[0]).get( 1 )).getObject()).isEmpty() );
    }

    public void testRetractTuple() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese" );

        final ReteTuple tuple0 = new ReteTuple( f0 );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple0,
                               this.context,
                               this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightObjectMemory().size() );
        Assert.assertTrue( "An empty collection should be propagated",
                           ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).isEmpty() );

        this.node.retractTuple( tuple0,
                                this.context,
                                this.workingMemory );
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 1,
                      this.sink.getRetracted().size() );
        assertEquals( 1,
                      this.sink.getAsserted().size() );
    }

    public void testAssertObject() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese" );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese" );

        final ReteTuple tuple0 = new ReteTuple( f0 );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple0,
                               this.context,
                               this.workingMemory );

        // check memory 
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 1,
                      this.sink.getAsserted().size() );
        Assert.assertTrue( "An empty collection should be propagated",
                           ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).isEmpty() );

        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );
        assertEquals( 1,
                      this.memory.getRightObjectMemory().size() );
        assertEquals( 2,
                      this.sink.getAsserted().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             1,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 1 ))[0]).get( 1 )).getObject()).size() );

        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        assertEquals( 2,
                      this.memory.getRightObjectMemory().size() );
        assertEquals( 3,
                      this.sink.getAsserted().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 2 ))[0]).get( 1 )).getObject()).size() );

    }

    public void testModifyObject() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese" );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese" );

        final ReteTuple tuple0 = new ReteTuple( f0 );

        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple0,
                               this.context,
                               this.workingMemory );
        // check memories 
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 2,
                      this.memory.getRightObjectMemory().size() );
        assertEquals( 1,
                      this.sink.getAsserted().size() );
        assertEquals( 0,
                      this.sink.getRetracted().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).size() );

        // assert tuple, should add left memory 
        this.node.modifyObject( f0,
                                this.context,
                                this.workingMemory );
        assertEquals( 2,
                      this.memory.getRightObjectMemory().size() );
        assertEquals( 2,
                      this.sink.getAsserted().size() );
        assertEquals( 1,
                      this.sink.getRetracted().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 1 ))[0]).get( 1 )).getObject()).size() );

        final Tuple tuple = (Tuple) this.memory.getLeftTupleMemory().iterator( this.workingMemory,
                                                                               f0 ).next();
        assertEquals( tuple0,
                      tuple );

    }

    public void testRetractObject() {
        final DefaultFactHandle f0 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "cheese" );
        final DefaultFactHandle f1 = (DefaultFactHandle) this.workingMemory.getFactHandleFactory().newFactHandle( "other cheese" );

        final ReteTuple tuple0 = new ReteTuple( f0 );

        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );
        assertEquals( 2,
                      this.memory.getRightObjectMemory().size() );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple0,
                               this.context,
                               this.workingMemory );

        // check memory 
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.sink.getRetracted().size() );
        assertEquals( 1,
                      this.sink.getAsserted().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             2,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 0 ))[0]).get( 1 )).getObject()).size() );

        this.node.retractObject( f1,
                                 this.context,
                                 this.workingMemory );
        assertEquals( 1,
                      this.memory.getRightObjectMemory().size() );
        assertEquals( 1,
                      this.sink.getRetracted().size() );
        assertEquals( 2,
                      this.sink.getAsserted().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             1,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 1 ))[0]).get( 1 )).getObject()).size() );

        this.node.retractObject( f0,
                                 this.context,
                                 this.workingMemory );
        assertEquals( 0,
                      this.memory.getRightObjectMemory().size() );
        assertEquals( 2,
                      this.sink.getRetracted().size() );
        assertEquals( 3,
                      this.sink.getAsserted().size() );
        Assert.assertEquals( "Wrong number of elements in matching objects list ",
                             0,
                             ((Collection) ((DefaultFactHandle) ((Tuple) ((Object[]) this.sink.getAsserted().get( 2 ))[0]).get( 1 )).getObject()).size() );

    }

    public void testAttach() throws Exception {
        assertEquals( 15,
                      this.node.getId() );

        assertLength( 0,
                      this.objectSource.getObjectSinksAsList() );

        assertLength( 0,
                      this.tupleSource.getTupleSinks() );

        this.node.attach();

        assertLength( 1,
                      this.objectSource.getObjectSinksAsList() );

        assertLength( 1,
                      this.tupleSource.getTupleSinks() );

        assertSame( this.node,
                    this.objectSource.getObjectSinks().getLastObjectSink() );

        assertSame( this.node,
                    this.tupleSource.getTupleSinks().get( 0 ) );
    }

    public void testMemory() {
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        final MockObjectSource objectSource = new MockObjectSource( 1 );
        final MockTupleSource tupleSource = new MockTupleSource( 1 );

        final CollectNode collectNode = new CollectNode( 2,
                                                         tupleSource,
                                                         objectSource,
                                                         this.collect );

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( collectNode );

        assertNotNull( memory );
    }

}
