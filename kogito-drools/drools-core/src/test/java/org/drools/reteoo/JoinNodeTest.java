package org.drools.reteoo;

import org.drools.AssertionException;
import org.drools.DroolsTestCase;
import org.drools.FactHandle;
import org.drools.RetractionException;
import org.drools.rule.BooleanConstraint;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.spi.BooleanExpressionConstraint;
import org.drools.spi.ClassObjectType;
import org.drools.spi.Extractor;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class JoinNodeTest extends DroolsTestCase
{
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
    public void setUp()
    {
        this.rule = new Rule( "test-rule" );
        this.context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                               null,
                                               null );
        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl( ) );

        this.tupleSource = new MockTupleSource( 4 );
        this.objectSource = new MockObjectSource( 4 );
        this.sink = new MockTupleSink();

        this.node = new JoinNode( 15,
                                  tupleSource,
                                  objectSource,
                                  5 );

        node.addTupleSink( sink );

        this.memory = (BetaMemory) workingMemory.getNodeMemory( node );

        /* check memories are empty */
        assertEquals( 0,
                      memory.leftMemorySize() );
        assertEquals( 0,
                      memory.rightMemorySize() );

    }

    public void testAttach() throws Exception
    {
        assertEquals( 15,
                      node.getId() );

        assertLength( 0,
                      objectSource.getObjectSinks() );

        assertLength( 0,
                      tupleSource.getTupleSinks() );

        node.attach();

        assertLength( 1,
                      objectSource.getObjectSinks() );

        assertLength( 1,
                      tupleSource.getTupleSinks() );

        assertSame( node,
                    objectSource.getObjectSinks().get( 0 ) );

        assertSame( node,
                    tupleSource.getTupleSinks().get( 0 ) );
    }

    /**
     * Test just tuple assertions
     * 
     * @throws AssertionException
     */
    public void testAssertTuple() throws Exception
    {
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          workingMemory );

        /* assert tuple, should add one to left memory */
        node.assertTuple( tuple1,
                          context,
                          workingMemory );
        assertEquals( 1,
                      memory.leftMemorySize() );
        assertEquals( 0,
                      memory.rightMemorySize() );

        /* tuple already exists, so memory shouldn't increase */
        node.assertTuple( tuple1,
                          context,
                          workingMemory );
        assertEquals( 1,
                      memory.leftMemorySize() );

        /* check new tuples still assert */
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        ReteTuple tuple2 = new ReteTuple( 0,
                                          f1,
                                          workingMemory );
        node.assertTuple( tuple2,
                          context,
                          workingMemory );
        assertEquals( 2,
                      memory.leftMemorySize() );

        /* make sure there have been no matches or propagation */
        TupleMatches betaMemory1 = memory.getBetaMemory( tuple1.getKey() );
        TupleMatches betaMemory2 = memory.getBetaMemory( tuple2.getKey() );
        assertLength( 0,
                      betaMemory1.getMatches() );
        assertLength( 0,
                      betaMemory2.getMatches() );
        assertLength( 0,
                      sink.getAsserted() );
    }

    /**
     * Test just object assertions
     * 
     * @throws Exception
     */
    public void testAssertObject() throws Exception
    {
        FactHandleImpl f0 = new FactHandleImpl( 0 );

        /* assert tuple, should add one to left memory */
        node.assertObject( "test2",
                           f0,
                           context,
                           workingMemory );
        assertEquals( 0,
                      memory.leftMemorySize() );
        assertEquals( 1,
                      memory.rightMemorySize() );

        /* object/handle already exists, so memory shouldn't increase */
        node.assertObject( "test0",
                           f0,
                           context,
                           workingMemory );
        assertEquals( 1,
                      memory.rightMemorySize() );

        /* check new objects/handles still assert */
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        node.assertObject( "test1",
                           f1,
                           context,
                           workingMemory );
        assertEquals( 2,
                      memory.rightMemorySize() );

        /* make sure there have been no left memory increases or propagation */
        assertEquals( 0,
                      memory.leftMemorySize() );
        assertLength( 0,
                      sink.getAsserted() );
    }

    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws Exception
     */
    public void testAssertPropagations() throws Exception
    {

        /* Assert first tuple */
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          workingMemory );
        node.assertTuple( tuple1,
                          context,
                          workingMemory );
        TupleMatches betaMemory1 = memory.getBetaMemory( tuple1.getKey() );

        /* Assert second tuple */
        FactHandleImpl f1 = new FactHandleImpl( 0 );
        ReteTuple tuple2 = new ReteTuple( 1,
                                          f1,
                                          workingMemory );
        node.assertTuple( tuple2,
                          context,
                          workingMemory );
        TupleMatches betaMemory2 = memory.getBetaMemory( tuple2.getKey() );

        /* Assert an object and make sure we get matches and propogations */
        FactHandleImpl f2 = new FactHandleImpl( 2 );
        node.assertObject( "test1",
                           f2,
                           context,
                           workingMemory );
        assertLength( 1,
                      betaMemory1.getMatches() );
        assertLength( 1,
                      betaMemory2.getMatches() );
        assertLength( 2,
                      sink.getAsserted() );

        /* Assert another tuple and make sure there was one propagation */
        FactHandleImpl f3 = new FactHandleImpl( 3 );
        ReteTuple tuple3 = new ReteTuple( 0,
                                          f3,
                                          workingMemory );

        node.assertTuple( tuple3,
                          context,
                          workingMemory );
        assertLength( 3,
                      sink.getAsserted() );

        /* Assert another object and make sure there were three propagations */
        FactHandleImpl f4 = new FactHandleImpl( 4 );
        node.assertObject( "test1",
                           f4,
                           context,
                           workingMemory );
        TupleMatches betaMemory3 = memory.getBetaMemory( tuple3.getKey() );

        assertLength( 2,
                      betaMemory1.getMatches() );
        assertLength( 2,
                      betaMemory2.getMatches() );
        assertLength( 2,
                      betaMemory3.getMatches() );
        assertLength( 6,
                      sink.getAsserted() );
    }

    /**
     * Test Tuple retraction
     * 
     * @throws Exception
     * @throws RetractionException
     */
    public void testRetractTuple() throws Exception,
                                  RetractionException
    {
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          workingMemory );

        /* Shouldn't propagate as there are no matched tuple/facts */
        node.retractTuples( tuple1.getKey(),
                            context,
                            workingMemory );
        assertLength( 0,
                      sink.getRetracted() );

        /* assert tuple, should add one to left memory */
        node.assertTuple( tuple1,
                          context,
                          workingMemory );
        assertEquals( 1,
                      memory.leftMemorySize() );

        /*
         * Shouldn't propagate as still no matched tuple/facts. Although it will remove the asserted tuple
         */
        node.retractTuples( tuple1.getKey(),
                            context,
                            workingMemory );
        assertLength( 0,
                      sink.getRetracted() );
        assertEquals( 0,
                      memory.leftMemorySize() );
        assertEquals( 0,
                      memory.rightMemorySize() );
    }

    /**
     * Test Object retraction
     * 
     * @throws Exception
     * @throws RetractionException
     */
    public void testRetractObject() throws Exception,
                                   RetractionException
    {
        FactHandleImpl f0 = new FactHandleImpl( 0 );

        /* Shouldn't propagate as there are no matched tuple/facts */
        node.retractObject( f0,
                            context,
                            workingMemory );
        assertLength( 0,
                      sink.getRetracted() );

        /* assert object, should add one to right memory */
        node.assertObject( "test0",
                           f0,
                           context,
                           workingMemory );
        assertEquals( 1,
                      memory.rightMemorySize() );

        /*
         * Shouldn't propagate as still no matched tuple/facts. Although it will remove the asserted object
         */
        node.retractObject( f0,
                            context,
                            workingMemory );
        assertLength( 0,
                      sink.getRetracted() );
        assertEquals( 0,
                      memory.leftMemorySize() );
        assertEquals( 0,
                      memory.rightMemorySize() );
    }

    /**
     * Test retractions with both tuples and objects
     * 
     * @throws Exception
     * @throws RetractionException
     */
    public void testRetractPropagations() throws Exception,
                                         RetractionException
    {
        /* assert tuple */
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          workingMemory );
        node.assertTuple( tuple1,
                          context,
                          workingMemory );

        /* assert object */
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        node.assertObject( "test1",
                           f1,
                           context,
                           workingMemory );

        /* assert object */
        FactHandleImpl f2 = new FactHandleImpl( 2 );
        node.assertObject( "test2",
                           f2,
                           context,
                           workingMemory );

        /* should have three asserted propagations */
        assertLength( 2,
                      sink.getAsserted() );

        /* check zero retracted propatations */
        assertLength( 0,
                      sink.getRetracted() );

        /* retract an object, should have one retracted propagations */
        node.retractObject( f2,
                            context,
                            workingMemory );
        assertLength( 1,
                      sink.getRetracted() );

        /* retract a tuple, should have one retracted propagations */
        node.retractTuples( tuple1.getKey(),
                            context,
                            workingMemory );
        assertLength( 2,
                      sink.getRetracted() );

        /* should be zero left tuples and 1 right handle in memory */
        assertEquals( 0,
                      memory.leftMemorySize() );
        assertEquals( 1,
                      memory.rightMemorySize() );

        /* clear out the final right memory item */
        node.retractObject( f1,
                            context,
                            workingMemory );

        assertEquals( 0,
                      memory.rightMemorySize() );

    }

    /**
     * Test that a correct join results from propatation
     */
    public void testJoin() throws Exception
    {
        /* assert tuple */
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 "test0" );
        ReteTuple tuple1 = new ReteTuple( 2,
                                          f0,
                                          workingMemory );
        node.assertTuple( tuple1,
                          context,
                          workingMemory );

        /* assert object */
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        workingMemory.putObject( f1,
                                 "test1" );
        node.assertObject( "test1",
                           f1,
                           context,
                           workingMemory );

        Object[] list = (Object[]) sink.getAsserted().get( 0 );
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
     * While all the previous tests work with the DefaultJoinNodeBinder, ie joins always succeed. This tests joins with BooleanExpressionConstraint. We only use one constraint, as Constraints are tested more thorougly else where, likewise for this
     * reason we use a very simple constraint.
     * 
     * @throws Exception
     * 
     */
    public void testJoinNodeWithConstraint() throws Exception
    {
        ObjectType stringObjectType = new ClassObjectType( String.class );

        /* just return the object */
        Extractor stringExtractor = new Extractor() {
            public Object getValue(Object object)
            {
                return object;
            }
        };

        /* Bind the extractor to a decleration */
        Declaration string1Declaration = new Declaration( 0,
                                                          "string1",
                                                          stringObjectType,
                                                          stringExtractor,
                                                          3 );

        /* Bind the extractor to a decleration */
        Declaration string2Declaration = new Declaration( 0,
                                                          "string2",
                                                          stringObjectType,
                                                          stringExtractor,
                                                          9 );

        /* create the boolean expression check */
        BooleanExpressionConstraint checkString = new BooleanExpressionConstraint() {
            public boolean isAllowed(Object object,
                                     FactHandle handle,
                                     Declaration declaration, // ?string1
                                     Declaration[] declarations, // ?string2
                                     Tuple tuple)
            {
                String string1 = (String) object;
                String string2 = (String) tuple.get( declarations[0] );

                return "string1string2".equals( string1 + string2 );

            }
        };

        /* create the constraint */
        BooleanConstraint constraint = new BooleanConstraint( checkString,
                                                              string1Declaration,
                                                              new Declaration[]{string2Declaration} );

        /* string1Declaration is bound to column 3 */
        this.node = new JoinNode( 15,
                                  new MockTupleSource( 5 ),
                                  new MockObjectSource( 8 ),
                                  3,
                                  new BetaNodeBinder( constraint ) );

        node.addTupleSink( sink );

        this.memory = (BetaMemory) workingMemory.getNodeMemory( node );

        /* assert tuple */
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 "string2" );
        ReteTuple tuple1 = new ReteTuple( 9,
                                          f0,
                                          workingMemory );
        node.assertTuple( tuple1,
                          context,
                          workingMemory );

        /* assert object */
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        String string1 = "string1";
        workingMemory.putObject( f1,
                                 string1 );
        node.assertObject( string1,
                           f1,
                           context,
                           workingMemory );

        /* Join should work */
        assertLength( 1,
                      sink.getAsserted() );

        Object[] list = (Object[]) sink.getAsserted().get( 0 );
        ReteTuple joinedTuple = (ReteTuple) list[0];
        assertEquals( "string1",
                      joinedTuple.get( 3 ) );

        assertEquals( "string2",
                      joinedTuple.get( 9 ) );

        /*
         * now check that constraint blocks these assertions /* assert tuple
         */
        FactHandleImpl f2 = new FactHandleImpl( 2 );
        workingMemory.putObject( f1,
                                 "string22" );
        ReteTuple tuple2 = new ReteTuple( 9,
                                          f2,
                                          workingMemory );
        node.assertTuple( tuple2,
                          context,
                          workingMemory );
        /* nothing extra should be asserted */
        assertLength( 1,
                      sink.getAsserted() );

        /* Although it will remember the tuple for possible future matches */
        assertEquals( 2,
                      memory.leftMemorySize() );
        assertEquals( 1,
                      memory.rightMemorySize() );

        /* assert object */
        FactHandleImpl f3 = new FactHandleImpl( 3 );
        String stringx = "stringx";
        workingMemory.putObject( f3,
                                 stringx );
        node.assertObject( stringx,
                           f3,
                           context,
                           workingMemory );
        /* nothing extra should be asserted */
        assertLength( 1,
                      sink.getAsserted() );

        /* Although it will remember the tuple for possible future matches */
        assertEquals( 2,
                      memory.leftMemorySize() );
        assertEquals( 2,
                      memory.rightMemorySize() );

    }

}
