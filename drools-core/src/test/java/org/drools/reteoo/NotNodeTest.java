package org.drools.reteoo;

import java.beans.IntrospectionException;

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

public class NotNodeTest extends DroolsTestCase {
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
     * @throws IntrospectionException 
     */
    public void setUp() throws IntrospectionException {
        this.rule = new Rule( "test-rule" );
        this.context = new PropagationContextImpl( 0,
                                                   PropagationContext.ASSERTION,
                                                   null,
                                                   null );
        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );                       

        // just return the object 
        FieldExtractor priceExtractor = new ClassFieldExtractor(Cheese.class, Cheese.getIndex( Cheese.class, "price" ) );

        // Bind the extractor to a decleration               
        Declaration string1Declaration = new Declaration( 0,
                                                          "cheese1",
                                                          priceExtractor,
                                                          3 );

        // Bind the extractor to a decleration 
        Declaration string2Declaration = new Declaration( 0,
                                                          "cheese2",
                                                          priceExtractor,
                                                          9 );

        PredicateEvaluator evaluator = new PredicateEvaluator() {

            public boolean evaluate(Tuple tuple,
                                    Object object,
                                    FactHandle handle,
                                    Declaration declaration,
                                    Declaration[] declarations) {
                int price1 = ( (Integer) tuple.get( declarations[0] )).intValue();
                int price2 = ( (Integer) declaration.getValue( object )).intValue();

                return price1 == price2;

            }
        };

        PredicateConstraint constraint = new PredicateConstraint( evaluator,
                                                                  string2Declaration,
                                                                   new Declaration[]{string1Declaration} );        
                

        // string1Declaration is bound to column 3 
        this.node = new NotNode( 15,
                                 new MockTupleSource( 5 ),
                                 new MockObjectSource( 8 ),
                                 3,
                                 new BetaNodeBinder( constraint ) );
        this.sink = new MockTupleSink();
        this.node.addTupleSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );
    }

    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws AssertionException
     */
    public void testAssertPropagations() throws FactException {
        // assert tuple
        Cheese cheddar = new Cheese( "cheddar", 10 );       
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        this.workingMemory.putObject( f0,
                                      cheddar );
        ReteTuple tuple1 = new ReteTuple( 3,
                                          f0,
                                          this.workingMemory );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // no matching objects, so should propagate
        assertLength( 1,
                      this.sink.getAsserted() );

        // assert will match, so dont propagate
        Cheese brie = new Cheese( "brie", 10 );        
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        this.workingMemory.putObject( f1,
                                      brie );
        this.node.assertObject( brie,
                                f1,
                                this.context,
                                this.workingMemory );

        // check no propagations 
        assertLength( 1,
                      this.sink.getAsserted() );

        // assert tuple, will have matches, so no propagation
        FactHandleImpl f2 = new FactHandleImpl( 2 );
        this.workingMemory.putObject( f2,
                                      new Cheese( "gouda", 10 ) );
        ReteTuple tuple2 = new ReteTuple( 3,
                                          f2,
                                          this.workingMemory );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        // check no propagations
        assertLength( 1,
                      this.sink.getAsserted() );

        // check memory sizes
        assertEquals( 2,
                      this.memory.leftMemorySize() );
        assertEquals( 1,
                      this.memory.rightMemorySize() );

        // assert tuple, no matches, so propagate
        FactHandleImpl f3 = new FactHandleImpl( 3 );
        this.workingMemory.putObject( f3,
                                      new Cheese( "cheddar", 9 ) );
        ReteTuple tuple3 = new ReteTuple( 3,
                                          f3,
                                          this.workingMemory );
        this.node.assertTuple( tuple3,
                               this.context,
                               this.workingMemory );
        // check there was one propatation
        assertLength( 2,
                      this.sink.getAsserted() );

        // assert tuple, no matches, so propagate
        FactHandleImpl f4 = new FactHandleImpl( 4 );
        this.workingMemory.putObject( f4,
                                      new Cheese("stilton", 5) );
        ReteTuple tuple4 = new ReteTuple( 3,
                                          f4,
                                          this.workingMemory );
        this.node.assertTuple( tuple4,
                               this.context,
                               this.workingMemory );
        
        // check there was one propatation
        assertLength( 3,
                      this.sink.getAsserted() );
        
        // assert object. There should  be no propagations as the matching tuples
        // are already satisfied.
        FactHandleImpl f5 = new FactHandleImpl( 5 );
        Cheese gouda = new Cheese("gouda", 10);
        this.workingMemory.putObject( f5,
                                      gouda );
        this.node.assertObject( gouda,
                                f5,
                                this.context,
                                this.workingMemory );

        // above has two none-matches, so propagate
        assertLength( 3,
                      this.sink.getAsserted() );
        
        // assert object. While this one is incorrect Two tuples already have
        // matches so cannot propagate        
        FactHandleImpl f6 = new FactHandleImpl( 6 );
        Cheese edam = new Cheese( "edam", 4 );
        this.workingMemory.putObject( f6,
                                      edam );
        this.node.assertObject( edam,
                                f6,
                                this.context,
                                this.workingMemory );

        // above has no matches but tuples are  already satisfied, so check  no propagation
        assertLength( 3,
                      this.sink.getAsserted() );

        // check memories
        assertEquals( 4,
                      this.memory.leftMemorySize() );
        assertEquals( 3,
                      this.memory.rightMemorySize() );

    }

    /**
     * Test retractions with both tuples and objects
     * 
     * @throws AssertionException
     * @throws RetractionException
     */
    public void testRetractPropagations() throws FactException {
        // assert object 
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        Cheese gouda = new Cheese( "gouda", 10);
        this.workingMemory.putObject( f0,
                                      gouda );
        this.node.assertObject( gouda,
                                f0,
                                this.context,
                                this.workingMemory );

        // assert tuple, will match so no propagation 
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        Cheese cheddar = new Cheese( "cheddar", 10);
        this.workingMemory.putObject( f1,
                                      cheddar );
        
        ReteTuple tuple1 = new ReteTuple( 3,
                                          f1,
                                          this.workingMemory );
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );

        // check no propagations 
        assertLength( 0,
                      this.sink.getAsserted() );
        assertLength( 0,
                      this.sink.getRetracted() );

         // retracting the object should mean no matches for the left tuple. So
         // check left tuple propagated as assert
        this.node.retractObject( f0,
                                 this.context,
                                 this.workingMemory );

        // check left tuple asserted
        assertLength( 1,
                      this.sink.getAsserted() );

         // make sure the asserted tuple is not joined with the right input
         // object/handle
        Object[] list = (Object[]) this.sink.getAsserted().get( 0 );
        assertSame( tuple1,
                    list[0] );

        // check nothing actually propagated as retract
        assertLength( 0,
                      this.sink.getRetracted() );

        // Try again with two left tuples, original is still asserted
        this.node.assertObject( gouda,
                                f0,
                                this.context,
                                this.workingMemory );

        // check no propagations 
        assertLength( 1,
                      this.sink.getAsserted() );

        // assert tuple, will match so no propagation 
        FactHandleImpl f2 = new FactHandleImpl( 2 );        
        this.workingMemory.putObject( f2,
                                      cheddar );
        ReteTuple tuple2 = new ReteTuple( 3,
                                          f2,
                                          this.workingMemory );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        // retracting the object should mean no matches for the two left tuples.
        this.node.retractObject( f0,
                                 this.context,
                                 this.workingMemory );

        // check both tuples where asserted 
        assertLength( 3,
                      this.sink.getAsserted() );

        // put the object back in, this should cause the two asserted tuples to be retracted
        this.node.assertObject( gouda,
                                f0,
                                this.context,
                                this.workingMemory );
        // check we have two more retractions
        assertLength( 3,
                      this.sink.getRetracted() );        

        // Should retract and propagated the left tuple
        this.node.retractTuples( tuple1.getKey(),
                                 this.context,
                                 this.workingMemory );        
        
         // retract the object, so we can later check a tuple retract with not
         // matching objects
        this.node.retractObject( f0,
                                 this.context,
                                 this.workingMemory );

        // check tuple retract with no matches objects
        this.node.retractTuples( tuple2.getKey(),
                                 this.context,
                                 this.workingMemory );

        // check tuplekey was propagated
        assertLength( 5,
                      this.sink.getRetracted() );
       
         // Make sure the propagated keys where not combined with the right input
         // object/handle
        list = (Object[]) this.sink.getRetracted().get( 0 );
        assertSame( tuple1.getKey(),
                    list[0] );

        list = (Object[]) this.sink.getRetracted().get( 1 );
        assertSame( tuple2.getKey(),
                    list[0] );
    }
}
