package org.drools.reteoo;

import java.util.Map;

import org.drools.AssertionException;
import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RetractionException;
import org.drools.rule.Declaration;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Rule;
import org.drools.spi.ClassFieldExtractor;
import org.drools.spi.ClassObjectType;
import org.drools.spi.Extractor;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ObjectType;
import org.drools.spi.PredicateEvaluator;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class JoinNodeTest extends DroolsTestCase {
    Rule               rule;
    PropagationContext context;
    WorkingMemoryImpl  workingMemory;
    MockObjectSource   objectSource;
    MockTupleSource    tupleSource;
    MockTupleSink      sink;
    BetaNode           node;
    BetaMemory         memory;

    /**
     * Setup the BetaNode used in each of the tests
     */
    public void setUp() {
        this.rule = new Rule( "test-rule" );
        this.context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                   null,
                                                   null );
        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        this.tupleSource = new MockTupleSource( 4 );
        this.objectSource = new MockObjectSource( 4 );
        this.sink = new MockTupleSink();

        this.node = new JoinNode( 15,
                                  this.tupleSource,
                                  this.objectSource,
                                  5 );

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        // check memories are empty
        assertEquals( 0,
                      this.memory.leftMemorySize() );
        assertEquals( 0,
                      this.memory.rightMemorySize() );

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
                                          objectSource,
                                          1 );

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
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          this.workingMemory );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );
        assertEquals( 1,
                      this.memory.leftMemorySize() );
        assertEquals( 0,
                      this.memory.rightMemorySize() );

        // tuple already exists, so memory shouldn't increase
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );
        assertEquals( 1,
                      this.memory.leftMemorySize() );

        // check new tuples still assert
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        ReteTuple tuple2 = new ReteTuple( 0,
                                          f1,
                                          this.workingMemory );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );
        assertEquals( 2,
                      this.memory.leftMemorySize() );

        // make sure there have been no matches or propagation
        TupleMatches betaMemory1 = this.memory.getBetaMemory( tuple1.getKey() );
        TupleMatches betaMemory2 = this.memory.getBetaMemory( tuple2.getKey() );
        assertLength( 0,
                      betaMemory1.getMatches() );
        assertLength( 0,
                      betaMemory2.getMatches() );
        assertLength( 0,
                      this.sink.getAsserted() );
    }

    /**
     * Test just object assertions
     * 
     * @throws Exception
     */
    public void testAssertObject() throws Exception {
        FactHandleImpl f0 = new FactHandleImpl( 0 );

        // assert tuple, should add one to left memory
        this.node.assertObject( "test2",
                                f0,
                                this.context,
                                this.workingMemory );
        assertEquals( 0,
                      this.memory.leftMemorySize() );
        assertEquals( 1,
                      this.memory.rightMemorySize() );

        // object/handle already exists, so memory shouldn't increase
        this.node.assertObject( "test0",
                                f0,
                                this.context,
                                this.workingMemory );
        assertEquals( 1,
                      this.memory.rightMemorySize() );

        // check new objects/handles still assert
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        this.node.assertObject( "test1",
                                f1,
                                this.context,
                                this.workingMemory );
        assertEquals( 2,
                      this.memory.rightMemorySize() );

        // make sure there have been no left memory increases or propagation
        assertEquals( 0,
                      this.memory.leftMemorySize() );
        assertLength( 0,
                      this.sink.getAsserted() );
    }

    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws Exception
     */
    public void testAssertPropagations() throws Exception {

        // Assert first tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          this.workingMemory );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );
        TupleMatches betaMemory1 = this.memory.getBetaMemory( tuple1.getKey() );

        // Assert second tuple
        FactHandleImpl f1 = new FactHandleImpl( 0 );
        ReteTuple tuple2 = new ReteTuple( 1,
                                          f1,
                                          this.workingMemory );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );
        TupleMatches betaMemory2 = this.memory.getBetaMemory( tuple2.getKey() );

        // Assert an object and make sure we get matches and propogations
        FactHandleImpl f2 = new FactHandleImpl( 2 );
        this.workingMemory.putObject( f2,
                                      "test1" );
        this.node.assertObject( "test1",
                                f2,
                                this.context,
                                this.workingMemory );
        assertLength( 1,
                      betaMemory1.getMatches() );
        assertLength( 1,
                      betaMemory2.getMatches() );
        assertLength( 2,
                      this.sink.getAsserted() );

        // Assert another tuple and make sure there was one propagation
        FactHandleImpl f3 = new FactHandleImpl( 3 );
        ReteTuple tuple3 = new ReteTuple( 0,
                                          f3,
                                          this.workingMemory );

        this.node.assertTuple( tuple3,
                               this.context,
                               this.workingMemory );
        assertLength( 3,
                      this.sink.getAsserted() );

        // Assert another object and make sure there were three propagations
        FactHandleImpl f4 = new FactHandleImpl( 4 );
        this.node.assertObject( "test1",
                                f4,
                                this.context,
                                this.workingMemory );
        TupleMatches betaMemory3 = this.memory.getBetaMemory( tuple3.getKey() );

        assertLength( 2,
                      betaMemory1.getMatches() );
        assertLength( 2,
                      betaMemory2.getMatches() );
        assertLength( 2,
                      betaMemory3.getMatches() );
        assertLength( 6,
                      this.sink.getAsserted() );
    }

    /**
     * Test Tuple retraction
     * 
     * @throws Exception
     * @throws RetractionException
     */
    public void testRetractTuple() throws Exception,
                                  RetractionException {
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          this.workingMemory );

        // Shouldn't propagate as there are no matched tuple/facts
        this.node.retractTuples( tuple1.getKey(),
                                 this.context,
                                 this.workingMemory );
        assertLength( 0,
                      this.sink.getRetracted() );

        // assert tuple, should add one to left memory
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );
        assertEquals( 1,
                      this.memory.leftMemorySize() );

        // Shouldn't propagate as still no matched tuple/facts. Although it will
        // remove the asserted tuple
        this.node.retractTuples( tuple1.getKey(),
                                 this.context,
                                 this.workingMemory );
        assertLength( 0,
                      this.sink.getRetracted() );
        assertEquals( 0,
                      this.memory.leftMemorySize() );
        assertEquals( 0,
                      this.memory.rightMemorySize() );
    }

    /**
     * Test Object retraction
     * 
     * @throws Exception
     * @throws RetractionException
     */
    public void testRetractObject() throws Exception,
                                   RetractionException {
        FactHandleImpl f0 = new FactHandleImpl( 0 );

        // Shouldn't propagate as there are no matched tuple/facts
        this.node.retractObject( f0,
                                 this.context,
                                 this.workingMemory );
        assertLength( 0,
                      this.sink.getRetracted() );

        // assert object, should add one to right memory
        this.node.assertObject( "test0",
                                f0,
                                this.context,
                                this.workingMemory );
        assertEquals( 1,
                      this.memory.rightMemorySize() );

        // Shouldn't propagate as still no matched tuple/facts. Although it will
        // remove the asserted object
        this.node.retractObject( f0,
                                 this.context,
                                 this.workingMemory );
        assertLength( 0,
                      this.sink.getRetracted() );
        assertEquals( 0,
                      this.memory.leftMemorySize() );
        assertEquals( 0,
                      this.memory.rightMemorySize() );
    }

    /**
     * Test retractions with both tuples and objects
     * 
     * @throws Exception
     * @throws RetractionException
     */
    public void testRetractPropagations() throws Exception,
                                         RetractionException {
        // assert tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          this.workingMemory );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // assert object
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        this.node.assertObject( "test1",
                                f1,
                                this.context,
                                this.workingMemory );

        // assert object
        FactHandleImpl f2 = new FactHandleImpl( 2 );
        this.node.assertObject( "test2",
                                f2,
                                this.context,
                                this.workingMemory );

        // should have three asserted propagations
        assertLength( 2,
                      this.sink.getAsserted() );

        // check zero retracted propatations
        assertLength( 0,
                      this.sink.getRetracted() );

        // retract an object, should have one retracted propagations
        this.node.retractObject( f2,
                                 this.context,
                                 this.workingMemory );
        assertLength( 1,
                      this.sink.getRetracted() );

        // retract a tuple, should have one retracted propagations
        this.node.retractTuples( tuple1.getKey(),
                                 this.context,
                                 this.workingMemory );
        assertLength( 2,
                      this.sink.getRetracted() );

        // should be zero left tuples and 1 right handle in memory
        assertEquals( 0,
                      this.memory.leftMemorySize() );
        assertEquals( 1,
                      this.memory.rightMemorySize() );

        // clear out the final right memory item
        this.node.retractObject( f1,
                                 this.context,
                                 this.workingMemory );

        assertEquals( 0,
                      this.memory.rightMemorySize() );

    }

    /**
     * Test that a correct join results from propatation
     */
    public void testJoin() throws Exception {
        // assert tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        this.workingMemory.putObject( f0,
                                      "test0" );
        ReteTuple tuple1 = new ReteTuple( 2,
                                          f0,
                                          this.workingMemory );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // assert object
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        this.workingMemory.putObject( f1,
                                      "test1" );
        this.node.assertObject( "test1",
                                f1,
                                this.context,
                                this.workingMemory );

        Object[] list = (Object[]) this.sink.getAsserted().get( 0 );
        ReteTuple joinedTuple = (ReteTuple) list[0];

        assertNull( joinedTuple.get( 1 ) );

        assertEquals( "test0",
                      joinedTuple.get( 2 ) );

        assertNull( joinedTuple.get( 3 ) );
        assertNull( joinedTuple.get( 4 ) );

        assertEquals( "test1",
                      joinedTuple.get( 5 ) );
    }

    /**
     * While all the previous tests work with the DefaultJoinNodeBinder, ie
     * joins always succeed. This tests joins with
     * PredicateExpressionConstraint. We only use one constraint, as Constraints
     * are tested more thorougly else where, likewise for this reason we use a
     * very simple constraint.
     * 
     * @throws Exception
     * 
     */
    public void testJoinNodeWithConstraint() throws Exception {
//        ObjectType stringObjectType = new ClassObjectType( String.class, "price" );

//        // just return the object
//        Extractor stringExtractor = new Extractor() {
//            public Object getValue(Object object) {
//                return object;
//            }
//        };

        FieldExtractor priceExtractor = new ClassFieldExtractor(Cheese.class, Cheese.getIndex( Cheese.class, "price" ) );
        
        // Bind the extractor to a decleration
        Declaration string1Declaration = new Declaration( 0,
                                                          "price1",
                                                          priceExtractor,
                                                          3 );

        // Bind the extractor to a decleration
        Declaration string2Declaration = new Declaration( 0,
                                                          "price2",
                                                          priceExtractor,
                                                          9 );

        // create the boolean expression check
        PredicateEvaluator checkString = new PredicateEvaluator() {
            public boolean evaluate(Tuple tuple,
                                     Object object,
                                     FactHandle handle,
                                     Declaration declaration, // ?string1
                                     Declaration[] declarations ) { // ?string2
                int price1 = ((Integer) tuple.get( declarations[0] )).intValue();
                int price2 = ((Integer) declaration.getValue( object )).intValue();
                return (price2 == (price1 * 2));                
            }
        };

        // create the constraint
        PredicateConstraint constraint = new PredicateConstraint( checkString,
                                                                  string1Declaration,
                                                                  new Declaration[]{string2Declaration} );

        // string1Declaration is bound to column 3
        this.node = new JoinNode( 15,
                                  new MockTupleSource( 5 ),
                                  new MockObjectSource( 8 ),
                                  3,
                                  new BetaNodeBinder( constraint ) );

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        // assert tuple
        Cheese cheddar = new Cheese( "cheddar", 5 );
        FactHandleImpl f0 = new FactHandleImpl( 0 );        
        this.workingMemory.putObject( f0,
                                      cheddar );
        ReteTuple tuple1 = new ReteTuple( 9,
                                          f0,
                                          this.workingMemory );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // assert object
        Cheese brie = new Cheese( "brie", 10 );        
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        this.workingMemory.putObject( f1,
                                      brie );
        this.node.assertObject( brie,
                                f1,
                                this.context,
                                this.workingMemory );

        // Join should work
        assertLength( 1,
                      this.sink.getAsserted() );

        Object[] list = (Object[]) this.sink.getAsserted().get( 0 );
        ReteTuple joinedTuple = (ReteTuple) list[0];
        assertEquals( new Cheese("brie", 10),
                      joinedTuple.get( 3 ) );

        assertEquals( new Cheese("cheddar", 5),
                      joinedTuple.get( 9 ) );

        // now check that constraint blocks these assertions /* assert tuple
        Cheese stilton = new Cheese( "stilton", 12);        
        FactHandleImpl f2 = new FactHandleImpl( 2 );
        this.workingMemory.putObject( f2,
                                      stilton );
        ReteTuple tuple2 = new ReteTuple( 9,
                                          f2,
                                          this.workingMemory );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );
        // nothing extra should be asserted
        assertLength( 1,
                      this.sink.getAsserted() );

        // Although it will remember the tuple for possible future matches
        assertEquals( 2,
                      this.memory.leftMemorySize() );
        assertEquals( 1,
                      this.memory.rightMemorySize() );

        // assert object
        stilton = new Cheese( "stilton", 20);  
        FactHandleImpl f3 = new FactHandleImpl( 3 );
        this.workingMemory.putObject( f3,
                                      stilton );
        this.node.assertObject( stilton,
                                f3,
                                this.context,
                                this.workingMemory );
        // nothing extra should be asserted
        assertLength( 1,
                      this.sink.getAsserted() );

        // Although it will remember the tuple for possible future matches
        assertEquals( 2,
                      this.memory.leftMemorySize() );
        assertEquals( 2,
                      this.memory.rightMemorySize() );

    }

    public void testUpdateWithChildNodeMemory() throws FactException {
        // Join nodes check children to see if one has memory, if it has memory
        // it
        // propagate the contents of that memory to the new child
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        JoinNode joinNode = new JoinNode( 1,
                                          this.tupleSource,
                                          this.objectSource,
                                          1 );

        // Add the new sink with memory and assert a tuple which will be
        // repropagated to the new child node
        MockTupleSink sink1 = new MockTupleSink( 2 );
        sink1.setHasMemory( true );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 "string2" );
        ReteTuple tuple1 = new ReteTuple( 9,
                                          f0,
                                          workingMemory );

        sink1.assertTuple( tuple1,
                           this.context,
                           workingMemory );

        // Make sure the memory was correctly updated
        Map map = (Map) workingMemory.getNodeMemory( sink1 );
        assertLength( 1,
                      map.keySet() );
        assertLength( 1,
                      sink1.getAsserted() );

        joinNode.addTupleSink( sink1 );

        // Add the new sink which should have its data upated from the first
        // sink that has memory
        MockTupleSink sink2 = new MockTupleSink( 3 );
        joinNode.addTupleSink( sink2 );

        assertLength( 0,
                      sink2.getAsserted() );

        joinNode.updateNewNode( workingMemory,
                                this.context );

        assertLength( 1,
                      sink2.getAsserted() );

    }

    public void testUpdateWithMemory() throws FactException {
        // If no child nodes have children then we need to re-process the left
        // and right memories
        // as a joinnode does not store the resulting tuples
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        JoinNode joinNode = new JoinNode( 1,
                                          this.tupleSource,
                                          this.objectSource,
                                          1 );

        // Add the first tuple sink and assert a tuple and object
        // The sink has no memory
        MockTupleSink sink1 = new MockTupleSink( 2 );
        joinNode.addTupleSink( sink1 );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 "string0" );

        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          workingMemory );

        joinNode.assertTuple( tuple1,
                              this.context,
                              workingMemory );

        String string1 = "string1";
        FactHandleImpl string1Handle = new FactHandleImpl( 1 );
        workingMemory.putObject( string1Handle,
                                 string1 );

        joinNode.assertObject( string1,
                               string1Handle,
                               this.context,
                               workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );

        // Add the new sink, this should be updated from the re-processed
        // joinnode memory
        MockTupleSink sink2 = new MockTupleSink( 3 );
        joinNode.addTupleSink( sink2 );
        assertLength( 0,
                      sink2.getAsserted() );

        joinNode.updateNewNode( workingMemory,
                                this.context );

        assertLength( 1,
                      sink2.getAsserted() );
    }
}
