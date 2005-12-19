package org.drools.reteoo;

import org.drools.AssertionException;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RetractionException;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.spi.PredicateExpressionConstraint;
import org.drools.spi.ClassObjectType;
import org.drools.spi.Extractor;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class NotNodeTest extends DroolsTestCase
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
        PredicateExpressionConstraint checkString = new PredicateExpressionConstraint() {
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
        PredicateConstraint constraint = new PredicateConstraint( checkString,
                                                              string1Declaration,
                                                              new Declaration[]{string2Declaration} );

        /* string1Declaration is bound to column 3 */
        this.node = new NotNode( 15,
                                 new MockTupleSource( 5 ),
                                 new MockObjectSource( 8 ),
                                 3,
                                 new BetaNodeBinder( constraint ) );
        this.sink = new MockTupleSink();
        node.addTupleSink( sink );

        this.memory = (BetaMemory) workingMemory.getNodeMemory( node );
    }

    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws AssertionException
     */
    public void testAssertPropagations() throws FactException
    {
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

        /* no matching objects, so should propagate */
        assertLength( 1,
                      sink.getAsserted() );

        /* assert will match, so dont propagate */
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        String string1 = "string1";
        workingMemory.putObject( f1,
                                 string1 );
        node.assertObject( string1,
                           f1,
                           context,
                           workingMemory );

        /* check no propagations */
        assertLength( 1,
                      sink.getAsserted() );

        /* assert tuple, will have matches, so no propagation */
        FactHandleImpl f2 = new FactHandleImpl( 2 );
        workingMemory.putObject( f2,
                                 "string2" );
        ReteTuple tuple2 = new ReteTuple( 9,
                                          f2,
                                          workingMemory );
        node.assertTuple( tuple2,
                          context,
                          workingMemory );

        /* check no propagations */
        assertLength( 1,
                      sink.getAsserted() );

        /* check memory sizes */
        assertEquals( 2,
                      memory.leftMemorySize() );
        assertEquals( 1,
                      memory.rightMemorySize() );

        /* assert tuple, no matches, so propagate */
        FactHandleImpl f3 = new FactHandleImpl( 3 );
        workingMemory.putObject( f3,
                                 "string3" );
        ReteTuple tuple3 = new ReteTuple( 9,
                                          f3,
                                          workingMemory );
        node.assertTuple( tuple3,
                          context,
                          workingMemory );
        /* check there was one propatation */
        assertLength( 2,
                      sink.getAsserted() );

        /* assert tuple, no matches, so propagate */
        FactHandleImpl f4 = new FactHandleImpl( 4 );
        workingMemory.putObject( f4,
                                 "string4" );
        ReteTuple tuple4 = new ReteTuple( 9,
                                          f4,
                                          workingMemory );
        node.assertTuple( tuple4,
                          context,
                          workingMemory );
        /* check there was one propatation */
        assertLength( 3,
                      sink.getAsserted() );

        /*
         * assert object. While this object is correct there are incorrect tuples on the left side which will cause no matches and those left tuples will propagate
         */
        FactHandleImpl f5 = new FactHandleImpl( 5 );
        String string5 = "string1";
        workingMemory.putObject( f5,
                                 string5 );
        node.assertObject( string5,
                           f5,
                           context,
                           workingMemory );

        /* above has two none-matches, so propagate */
        assertLength( 5,
                      sink.getAsserted() );

        /*
         * assert object. While this one is incorrect Two tuples already have matches so cannot propagate
         */
        FactHandleImpl f6 = new FactHandleImpl( 6 );
        String string6 = "string6";
        workingMemory.putObject( f6,
                                 string6 );
        node.assertObject( string6,
                           f6,
                           context,
                           workingMemory );

        /* above has no matches, so check propagation */
        assertLength( 7,
                      sink.getAsserted() );

        /* check memories */
        assertEquals( 4,
                      memory.leftMemorySize() );
        assertEquals( 3,
                      memory.rightMemorySize() );

    }

    /**
     * Test retractions with both tuples and objects
     * 
     * @throws AssertionException
     * @throws RetractionException
     */
    public void testRetractPropagations() throws FactException
    {
        /* assert object */
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        String string0 = "string1";
        workingMemory.putObject( f0,
                                 string0 );
        node.assertObject( string0,
                           f0,
                           context,
                           workingMemory );

        /* assert tuple, will match so no propagation */
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        workingMemory.putObject( f1,
                                 "string2" );
        ReteTuple tuple1 = new ReteTuple( 9,
                                          f1,
                                          workingMemory );
        node.assertTuple( tuple1,
                          context,
                          workingMemory );

        /* check no propagations */
        assertLength( 0,
                      sink.getAsserted() );
        assertLength( 0,
                      sink.getRetracted() );

        /*
         * retracting the object should mean no matches for the left tuple. So check left tuple propagated as assert
         */
        node.retractObject( f0,
                            context,
                            workingMemory );

        /* check left tuple asserted */
        assertLength( 1,
                      sink.getAsserted() );

        /*
         * make sure the asserted tuple is not joined with the right input object/handle
         */
        Object[] list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( tuple1,
                    list[0] );

        /* check nothing actually propagated as retract */
        assertLength( 0,
                      sink.getRetracted() );

        /*
         * Try again with two left tuples, original is still asserted
         */

        node.assertObject( string0,
                           f0,
                           context,
                           workingMemory );

        /* check no propagations */
        assertLength( 1,
                      sink.getAsserted() );

        /* assert tuple, will match so no propagation */
        FactHandleImpl f2 = new FactHandleImpl( 2 );
        workingMemory.putObject( f2,
                                 "string2" );
        ReteTuple tuple2 = new ReteTuple( 9,
                                          f2,
                                          workingMemory );
        node.assertTuple( tuple2,
                          context,
                          workingMemory );

        /* retracting the object should mean no matches for the two left tuples. */
        node.retractObject( f0,
                            context,
                            workingMemory );

        /* check both tuples where asserted */
        assertLength( 3,
                      sink.getAsserted() );

        /* put the object back in so we can check tuple retractions */
        node.assertObject( string0,
                           f0,
                           context,
                           workingMemory );

        /* Should retract and propagated the left tuple */
        node.retractTuples( tuple1.getKey(),
                            context,
                            workingMemory );
        assertLength( 1,
                      sink.getRetracted() );

        /*
         * retract the object, so we can later check a tuple retract with not matching objects
         */
        node.retractObject( f0,
                            context,
                            workingMemory );

        /* check tuple retract with no matches objects */
        node.retractTuples( tuple2.getKey(),
                            context,
                            workingMemory );
        /* check tuplekey was propagated */
        assertLength( 2,
                      sink.getRetracted() );

        /*
         * Make sure the propagated keys where not combined with the right input object/handle
         */
        list = (Object[]) sink.getRetracted().get( 0 );
        assertSame( tuple1.getKey(),
                    list[0] );

        list = (Object[]) sink.getRetracted().get( 1 );
        assertSame( tuple2.getKey(),
                    list[0] );
    }
}
