package org.drools.reteoo;

import java.util.Iterator;
import java.util.Map;

import org.drools.DroolsTestCase;
import org.drools.common.BetaNodeBinder;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.beta.BetaRightMemory;
import org.drools.rule.Rule;
import org.drools.spi.FieldConstraint;
import org.drools.spi.MockConstraint;
import org.drools.spi.PropagationContext;

public class JoinNodeTest extends DroolsTestCase {
    Rule               rule;
    PropagationContext context;
    WorkingMemoryImpl  workingMemory;
    MockObjectSource   objectSource;
    MockTupleSource    tupleSource;
    MockTupleSink      sink;
    BetaNode           node;
    BetaMemory         memory;
    MockConstraint     constraint = new MockConstraint();

    /**
     * Setup the BetaNode used in each of the tests
     */
    public void setUp() {
        this.rule = new Rule( "test-rule" );
        this.context = new PropagationContextImpl( 0,
                                                   PropagationContext.ASSERTION,
                                                   null,
                                                   null );
        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        this.tupleSource = new MockTupleSource( 4 );
        this.objectSource = new MockObjectSource( 4 );
        this.sink = new MockTupleSink();

        this.node = new JoinNode( 15,
                                  this.tupleSource,
                                  this.objectSource,
                                  new BetaNodeBinder( new FieldConstraint[]{this.constraint} ) );

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        // check memories are empty
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightObjectMemory().size() );

    }

    public void testAttach() throws Exception {
        assertEquals( 15,
                      this.node.getId() );

        assertLength( 0,
                      this.objectSource.getObjectSinks() );

        assertLength( 0,
                      this.tupleSource.getTupleSinks() );

        this.node.attach();

        assertLength( 1,
                      this.objectSource.getObjectSinks() );

        assertLength( 1,
                      this.tupleSource.getTupleSinks() );

        assertSame( this.node,
                    this.objectSource.getObjectSinks().get( 0 ) );

        assertSame( this.node,
                    this.tupleSource.getTupleSinks().get( 0 ) );
    }

    public void testMemory() {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockObjectSource objectSource = new MockObjectSource( 1 );
        MockTupleSource tupleSource = new MockTupleSource( 1 );

        JoinNode joinNode = new JoinNode( 2,
                                          tupleSource,
                                          objectSource );

        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( joinNode );

        assertNotNull( memory );
    }

    /**
     * Test just tuple assertions
     * 
     * @throws AssertionException
     */
    public void testAssertTuple() throws Exception {
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple0 = new ReteTuple( f0 );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple0,
                               this.context,
                               this.workingMemory );
        // check memories are empty
        assertEquals( 1,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      this.memory.getRightObjectMemory().size() );

        // assert tuple, should add left memory should be 2
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( f1 );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );
        assertEquals( 2,
                      this.memory.getLeftTupleMemory().size() );

        ReteTuple tuple = (ReteTuple) this.memory.getLeftTupleMemory()
                                      .iterator(this.workingMemory, f0).next();
        assertEquals( tuple0,
                      tuple );
        assertEquals( tuple1,
                      tuple.getNext() );
    }

    /**
     * Test just object assertions
     * 
     * @throws Exception
     */
    public void testAssertObject() throws Exception {
        FactHandleImpl f0 = (FactHandleImpl) this.workingMemory.assertObject( "test0" );

        // assert object, should add one to right memory
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );
        assertEquals( 0,
                      this.memory.getLeftTupleMemory().size() );
        assertEquals( 1,
                      this.memory.getRightObjectMemory().size() );

        // check new objects/handles still assert
        FactHandleImpl f1 = (FactHandleImpl) this.workingMemory.assertObject( "test1" );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );
        assertEquals( 2,
                      this.memory.getRightObjectMemory().size() );

        BetaRightMemory rightMemory = this.memory.getRightObjectMemory(); 
        Iterator it = rightMemory.iterator(this.workingMemory, new ReteTuple( f0 ));
        
        FactHandleImpl rf0 = ((ObjectMatches) it.next()).getFactHandle();
        FactHandleImpl rf1 = ((ObjectMatches) it.next()).getFactHandle();

        assertEquals( f0, rf0 );
        assertEquals( f1, rf1 );
    }

    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws Exception
     */
    public void testAssertPropagations() throws Exception {
        // assert first right object
        FactHandleImpl f0 = (FactHandleImpl) this.workingMemory.assertObject( "test0" );
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        // assert tuple, should add left memory should be 2
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( f1 );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // check bi directional match to resulting TupleMatch
        Map map = tuple1.getTupleMatches();
        TupleMatch match = (TupleMatch) map.get( f0 );

        ObjectMatches matches = (ObjectMatches) this.memory
                                 .rightObjectIterator(this.workingMemory, tuple1).next();
        assertSame( match,
                    matches.getFirstTupleMatch() );

        // check reference form TupleMatch to propgated ReteTuple
        assertSame( match.getJoinedTuples().get( 0 ),
                    ((Object[]) this.sink.getAsserted().get( 0 ))[0] );

        // check objectmatches correct references second asserted tuple
        FactHandleImpl f2 = new FactHandleImpl( 2 );
        ReteTuple tuple2 = new ReteTuple( f2 );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        assertSame( ((Map) tuple2.getTupleMatches()).get( f0 ),
                    matches.getFirstTupleMatch().getNext() );

        FactHandleImpl f3 = (FactHandleImpl) this.workingMemory.assertObject( "test2" );
        this.node.assertObject( f3,
                                this.context,
                                this.workingMemory );
        
        matches = getMatchesFor(tuple1, f3);
        assertSame( ((Map) tuple1.getTupleMatches()).get( f3 ),
                    matches.getFirstTupleMatch() );
        assertSame( ((Map) tuple2.getTupleMatches()).get( f3 ),
                    matches.getFirstTupleMatch().getNext() );
    }


    /**
     * Test Tuple retraction
     * 
     * @throws Exception
     * @throws RetractionException
     */
    public void testRetractTuple() throws Exception {
        // setup 2 tuples 3 fact handles
        FactHandleImpl f0 = (FactHandleImpl) this.workingMemory.assertObject( "test0" );
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        FactHandleImpl f1 = (FactHandleImpl) this.workingMemory.assertObject( "test1" );
        ReteTuple tuple1 = new ReteTuple( f1 );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        FactHandleImpl f2 = (FactHandleImpl) this.workingMemory.assertObject( "test2" );
        ReteTuple tuple2 = new ReteTuple( f2 );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        FactHandleImpl f3 = (FactHandleImpl) this.workingMemory.assertObject( "test3" );
        this.node.assertObject( f3,
                                this.context,
                                this.workingMemory );

        FactHandleImpl f4 = (FactHandleImpl) this.workingMemory.assertObject( "test4" );
        this.node.assertObject( f4,
                                this.context,
                                this.workingMemory );

        assertLength( 6,
                      this.sink.getAsserted() );

        // Retract an object and make sure its removed from the previous
        // matching ReteTuples
        this.node.retractObject( f0,
                                 this.context,
                                 this.workingMemory );
        assertLength( 2,
                      this.sink.getRetracted() );

        //assertNull( this.memory.getRightFactHandleMemory().get( f0 ) );
        assertNull( tuple1.getTupleMatches().get( f0 ) );
        assertNull( tuple2.getTupleMatches().get( f0 ) );

        this.node.retractTuple( tuple2,
                                this.context,
                                this.workingMemory );
        ObjectMatches matches = this.getMatchesFor( tuple2, f3 );
        for ( TupleMatch match = matches.getFirstTupleMatch(); match != null; match = (TupleMatch) match.getNext() ) {
            assertNotSame( tuple2,
                           match.getTuple() );
        }

        matches = this.getMatchesFor( tuple2, f4 );
        for ( TupleMatch match = matches.getFirstTupleMatch(); match != null; match = (TupleMatch) match.getNext() ) {
            assertNotSame( tuple2,
                           match.getTuple() );
        }
        assertLength( 4,
                      this.sink.getRetracted() );
    }

    public void testConstraintPropagations() throws Exception {
        this.constraint.isAllowed = false;
        // assert first right object
        FactHandleImpl f0 = (FactHandleImpl) this.workingMemory.assertObject( "test0" );
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );

        // assert tuple, should add left memory should be 2
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( f1 );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        assertLength( 0,
                      this.sink.getAsserted() );

        // check no matches
        Map map = tuple1.getTupleMatches();
        assertLength( 0,
                      map.keySet() );

        //assertNull( ((ObjectMatches) this.memory.getRightFactHandleMemory().get( f0 )).getFirstTupleMatch() );
    }

    //
    // public void testUpdateWithMemory() throws FactException {
    // WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new
    // RuleBaseImpl() );
    //
    // JoinNode joinNode = new JoinNode( 1,
    // this.tupleSource,
    // this.objectSource,
    // 1 );
    //
    // // Add the first tuple sink and assert a tuple and object
    // // The sink has no memory
    // MockTupleSink sink1 = new MockTupleSink( 2 );
    // joinNode.addTupleSink( sink1 );
    //
    // FactHandleImpl f0 = new FactHandleImpl( 0 );
    // workingMemory.putObject( f0,
    // "string0" );
    //
    // ReteTuple tuple1 = new ReteTuple( 0,
    // f0,
    // workingMemory );
    //
    // joinNode.assertTuple( tuple1,
    // this.context,
    // workingMemory );
    //
    // String string1 = "string1";
    // FactHandleImpl string1Handle = new FactHandleImpl( 1 );
    // workingMemory.putObject( string1Handle,
    // string1 );
    //
    // joinNode.assertObject( string1Handle,
    // this.context,
    // workingMemory );
    //
    // assertLength( 1,
    // sink1.getAsserted() );
    //
    // // Add the new sink, this should be updated from the re-processed
    // // joinnode memory
    // MockTupleSink sink2 = new MockTupleSink( 3 );
    // joinNode.addTupleSink( sink2 );
    // assertLength( 0,
    // sink2.getAsserted() );
    //
    // joinNode.updateNewNode( workingMemory,
    // this.context );
    //
    // assertLength( 1,
    // sink2.getAsserted() );
    // }
    
    /**
     * @param tuple1
     * @param matches
     * @param f3
     * @return
     */
    private ObjectMatches getMatchesFor(ReteTuple tuple1, FactHandleImpl f3) {
      ObjectMatches matches = null;
      for(Iterator i = this.memory.rightObjectIterator(this.workingMemory, tuple1); i.hasNext(); ) {
          matches = (ObjectMatches) i.next();
          if(matches.getFactHandle().equals( f3 )) {
            break;
          }
      }
      return matches;
    }
    
}
