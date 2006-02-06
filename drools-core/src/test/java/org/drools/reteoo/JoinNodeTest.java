package org.drools.reteoo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Rule;
import org.drools.spi.BetaNodeBinder;
import org.drools.spi.ClassFieldExtractor;
import org.drools.spi.ClassObjectType;
import org.drools.spi.Extractor;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ObjectType;
import org.drools.spi.PredicateExpression;
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
                                  5 );

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        // check memories are empty
        assertEquals( 0,
                      iteratorSize(this.memory.leftTupleIterator( this.context, this.workingMemory )) );
        assertEquals( 0,
                      iteratorSize(this.memory.rightObjectIterator( ) ) );

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
                      iteratorSize(this.memory.leftTupleIterator( this.context, this.workingMemory )) );
        assertEquals( 0,
                      iteratorSize(this.memory.rightObjectIterator( ) ) );


        // assert tuple, should add left memory should be 2
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        ReteTuple tuple2 = new ReteTuple( 0,
                                          f1,
                                          this.workingMemory );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );
        assertEquals( 2,
                      iteratorSize(this.memory.leftTupleIterator( this.context, this.workingMemory )) );
        
        Iterator it = this.memory.leftTupleIterator( this.context, this.workingMemory );
        ReteTuple tuple = (ReteTuple) it.next();
        assertEquals(0, tuple.matchesSize() );
        tuple = (ReteTuple) it.next();
        assertEquals(0, tuple.matchesSize() );
        assertFalse(it.hasNext());
        assertLength( 0,
                      this.sink.getAsserted() );
    }

    /**
     * Test just object assertions
     * 
     * @throws Exception
     */
    public void testAssertObject() throws Exception {
        FactHandleImpl f0 = (FactHandleImpl) this.workingMemory.assertObject( "test0" );
        
        // assert tuple, should add one to left memory
        this.node.assertObject( f0,
                                this.context,
                                this.workingMemory );
        assertEquals( 0,
                      iteratorSize(this.memory.leftTupleIterator( this.context, this.workingMemory )) );
        assertEquals( 1,
                      iteratorSize(this.memory.rightObjectIterator( ) ) );

        // check new objects/handles still assert
        FactHandleImpl f1 = (FactHandleImpl) this.workingMemory.assertObject( "test1" );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );
        assertEquals( 2,
                      iteratorSize(this.memory.rightObjectIterator( ) ) );
        
        Iterator it = this.memory.rightObjectIterator();
        Set set = new HashSet();
        set.add(((ObjectMatches) it.next()).getFactHandle());
        set.add(((ObjectMatches) it.next()).getFactHandle());
        assertFalse(it.hasNext());
        
        assertContains( f0, set );
        assertContains( f1, set );

        // make sure there have been no left memory increases or propagation
        assertEquals( 0,
                      iteratorSize(this.memory.leftTupleIterator( this.context, this.workingMemory ) ) );
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
                
        // Assert second tuple
        FactHandleImpl f1 = new FactHandleImpl( 0 );
        ReteTuple tuple2 = new ReteTuple( 1,
                                          f1,
                                          this.workingMemory );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        // Assert an object and make sure we get matches and propogations
        FactHandleImpl f2 = (FactHandleImpl) this.workingMemory.assertObject( "test1" );
        this.node.assertObject( f2,
                                this.context,
                                this.workingMemory );    
        
        BetaMemory betaMemory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );
        
        ObjectMatches matches1 = (ObjectMatches) betaMemory.rightObjectIterator().next();
        
        assertEquals( 2,
                      iteratorSize(  matches1.iterator( context, workingMemory ) ) );
        assertLength( 1,
                      tuple1.getTupleMatches().values() );
        assertLength( 1,
                      tuple2.getTupleMatches().values() );        
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
        FactHandleImpl f4 = (FactHandleImpl) this.workingMemory.assertObject( "test2" );
        this.node.assertObject( f4,
                                this.context,
                                this.workingMemory );
               
        assertEquals( 3,
                      iteratorSize(  matches1.iterator( context, workingMemory ) ) );
        assertLength( 2,
                      tuple1.getTupleMatches().values() );
        assertLength( 2,
                      tuple2.getTupleMatches().values() );        
        assertLength( 6,
                      this.sink.getAsserted() );
    }

    /**
     * Test Tuple retraction
     * 
     * @throws Exception
     * @throws RetractionException
     */
    public void testRetractTuple() throws Exception {
        // Assert first tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          this.workingMemory );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );
                
        // Assert second tuple
        FactHandleImpl f1 = new FactHandleImpl( 0 );
        ReteTuple tuple2 = new ReteTuple( 1,
                                          f1,
                                          this.workingMemory );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        // Assert an object and make sure we get matches and propogations
        FactHandleImpl f2 = (FactHandleImpl) this.workingMemory.assertObject( "test1" );
        this.node.assertObject( f2,
                                this.context,
                                this.workingMemory );    
        
        // Assert another object and make sure there were three propagations
        FactHandleImpl f4 = (FactHandleImpl) this.workingMemory.assertObject( "test2" );
        this.node.assertObject( f4,
                                this.context,
                                this.workingMemory );
        
        BetaMemory betaMemory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );
        ObjectMatches matches1 = (ObjectMatches) betaMemory.rightObjectIterator().next();

        assertEquals( 2,
                      iteratorSize(  betaMemory.leftTupleIterator(this.context, this.workingMemory ) ) );
        assertEquals( 2,
                      iteratorSize(  matches1.iterator( context, workingMemory ) ) );
        assertLength( 2,
                      tuple1.getTupleMatches().values() );
        assertLength( 2,
                      tuple2.getTupleMatches().values() );        
        assertLength( 4,
                      this.sink.getAsserted() );        
        
        tuple1.remove( this.context, this.workingMemory );

        assertEquals( 1,
                      iteratorSize(  betaMemory.leftTupleIterator(this.context, this.workingMemory ) ) );
        
        assertEquals( 1,
                      iteratorSize(  matches1.iterator( context, workingMemory ) ) );
        assertEquals( tuple2,
                      betaMemory.leftTupleIterator(this.context, this.workingMemory ).next() );
        assertLength( 2,
                      tuple2.getTupleMatches().values() );          
        
    }

    /**
     * Test Object retraction
     * 
     * @throws Exception
     * @throws RetractionException
     */
    public void testRetractObject() throws Exception {
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          this.workingMemory );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );
        
        // assert object, should add one to right memory
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );        
                        
        BetaMemory betaMemory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );
        ObjectMatches objectMatches = (ObjectMatches) betaMemory.rightObjectIterator().next();
        assertEquals(1, iteratorSize( objectMatches.iterator( context, workingMemory ) ) );
        
        ReteTuple tuple = (ReteTuple) betaMemory.leftTupleIterator( context, workingMemory ).next();
        assertLength(1, tuple.getTupleMatches().values() );
        
        this.node.retractObject( f1, context, workingMemory );        
        
        assertEquals(0, iteratorSize( betaMemory.rightObjectIterator() ) );
        assertLength(0, tuple.getTupleMatches().values() );                       
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
        FieldExtractor priceExtractor = new ClassFieldExtractor( Cheese.class,
                                                                 Cheese.getIndex( Cheese.class,
                                                                                  "price" ) );

        // Bind the extractor to a decleration
        // Declarations know the column they derive their value form
        Declaration price1Declaration = new Declaration( 0,
                                                         "price1",
                                                         priceExtractor,
                                                         0 );

        // Bind the extractor to a decleration
        // Declarations know the column they derive their value form
        Declaration price2Declaration = new Declaration( 1,
                                                         "price2",
                                                         priceExtractor,
                                                         1 );

        PredicateExpression evaluator = new PredicateExpression() {

            public boolean evaluate(Tuple tuple,
                                    FactHandle factHandle,
                                    Declaration declaration,
                                    Declaration[] declarations,
                                    WorkingMemory workingMemory) {
                int price1 = ((Integer) declarations[0].getValue( workingMemory.getObject( tuple.get( declarations[0] ) ) )).intValue();
                int price2 = ( (Integer) declaration.getValue( workingMemory.getObject( factHandle ) ) ).intValue();

                return (price2 == (price1 * 2));

            }
        };

        PredicateConstraint constraint = new PredicateConstraint( evaluator,
                                                                  price2Declaration,
                                                                  new Declaration[]{price1Declaration} );

        // string1Declaration is bound to column 3
        this.node = new JoinNode( 15,
                                  new MockTupleSource( 5 ),
                                  new MockObjectSource( 8 ),
                                  0,
                                  new BetaNodeBinder( constraint ) );

        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );

        // assert tuple
        Cheese cheddar = new Cheese( "cheddar", 5 );
        FactHandleImpl f0 = (FactHandleImpl)this.workingMemory.assertObject( cheddar );        
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          this.workingMemory );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // assert object
        Cheese brie = new Cheese( "brie", 10 ); 
        FactHandleImpl f1 = (FactHandleImpl) this.workingMemory.assertObject( brie );
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );

        // Join should work
        assertLength( 1,
                      this.sink.getAsserted() );

        Object[] list = (Object[]) this.sink.getAsserted().get( 0 );
        ReteTuple joinedTuple = (ReteTuple) list[0];
        assertEquals( new Cheese("cheddar", 5),
                      this.workingMemory.getObject( joinedTuple.get( 0 ) ) );

        assertEquals( new Cheese("brie", 10),
                      this.workingMemory.getObject( joinedTuple.get( 1 ) ) );

        // now check that constraint blocks these assertions /* assert tuple
        Cheese stilton = new Cheese( "stilton", 12);        
        FactHandleImpl f2 = (FactHandleImpl) this.workingMemory.assertObject( stilton );
        ReteTuple tuple2 = new ReteTuple( 0,
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
                     iteratorSize( this.memory.leftTupleIterator( context, workingMemory ) ) );
        assertEquals( 1,
                      iteratorSize( this.memory.rightObjectIterator() ) );

        // assert object
        stilton = new Cheese( "stilton", 20);  
        FactHandleImpl f3 = new FactHandleImpl( 3 );
        this.workingMemory.putObject( f3,
                                      stilton );
        this.node.assertObject( f3,
                                this.context,
                                this.workingMemory );
        // nothing extra should be asserted
        assertLength( 1,
                      this.sink.getAsserted() );

        // Although it will remember the tuple for possible future matches
        assertEquals( 2,
                      iteratorSize( this.memory.leftTupleIterator( context, workingMemory ) ) );
        assertEquals( 2,
                      iteratorSize( this.memory.rightObjectIterator() ) );

    }



    public void testUpdateWithMemory() throws FactException {
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

        joinNode.assertObject( string1Handle,
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
    
    private int iteratorSize(Iterator it) {
        int count = 0;
        for (;it.hasNext();) {
            it.next();
            ++count;
        }
        return count;
    }
}
