package org.drools.reteoo;

import java.beans.IntrospectionException;
import java.util.Iterator;

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

public class NotNodeTest extends DroolsTestCase {
    Rule               rule;
    PropagationContext context;
    WorkingMemoryImpl  workingMemory;
    MockObjectSource   objectSource;
    MockTupleSource    tupleSource;
    MockObjectSink      sink;
    NotNode           node;
    RightInputAdapterNode  ria;
    BetaMemory         memory;
    boolean            allowed = true;

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

        PredicateExpression evaluator = new PredicateExpression() {

            public boolean evaluate(Tuple tuple,
                                    FactHandle handle,
                                    Declaration declaration,
                                    Declaration[] declarations,
                                    WorkingMemory workingMemory) {
                return NotNodeTest.this.allowed;

            }
        };

        PredicateConstraint constraint = new PredicateConstraint( evaluator,
                                                                  null,
                                                                  new Declaration[]{} );        
                

        // string1Declaration is bound to column 3 
        this.node = new NotNode( 15,
                                 new MockTupleSource( 5 ),
                                 new MockObjectSource( 8 ),
                                 1,
                                 new BetaNodeBinder( constraint ) );
        
        this.ria = new RightInputAdapterNode(2, 0, this.node);
        this.ria.attach();
        
        this.sink = new MockObjectSink();
        this.ria.addObjectSink( this.sink );

        this.memory = (BetaMemory) this.workingMemory.getNodeMemory( this.node );
    }

    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws AssertionException
     */
    public void testNotStandard() throws FactException {
        // assert tuple
        Cheese cheddar = new Cheese( "cheddar", 10 );
        FactHandleImpl f0 = (FactHandleImpl) workingMemory.assertObject( cheddar );

        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          this.workingMemory );
        
        assertNull( tuple1.getNotTuple() );        
        
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );        

        // no matching objects, so should propagate
        assertLength( 1,
                      this.sink.getAsserted() );
        
        assertLength( 0,
                      this.sink.getRetracted() );        

        assertNotNull( tuple1.getNotTuple() );
        
        assertEquals( f0,
                      ((Object[])this.sink.getAsserted().get( 0 ))[0] );
        
        // assert will match, so propagated tuple should be retracted
        Cheese brie = new Cheese( "brie", 10 );        
        FactHandleImpl f1 = (FactHandleImpl) workingMemory.assertObject( brie );
        
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );        

        // check no as assertions, but should be one retraction
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 1,
                      this.sink.getRetracted() );        
        
        assertEquals( f0,
                      ((Object[])this.sink.getRetracted().get( 0 ))[0] );                        

        // assert tuple, will have matches, so no propagation
        FactHandleImpl f2 = (FactHandleImpl) workingMemory.assertObject( new Cheese( "gouda", 10 ) );
        ReteTuple tuple2 = new ReteTuple( 0,
                                          f2,
                                          this.workingMemory );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );

        // check no propagations 
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 1,
                      this.sink.getRetracted() );        
        

        // check memory sizes
        assertEquals( 2,
                      iteratorSize( this.memory.leftTupleIterator( context, workingMemory ) ) );
        assertEquals( 1,
                      iteratorSize( this.memory.rightObjectIterator() ) );
        
        // When this is retracter both tuples should assert
        this.node.retractObject( f1, context, workingMemory );
        
        // check no propagations 
        assertLength( 3,
                      this.sink.getAsserted() );

        assertLength( 1,
                      this.sink.getRetracted() );        
    }
    
    /**
     * Test assertion with both Objects and Tuples
     * 
     * @throws AssertionException
     */
    public void testNotWithConstraints() throws FactException {
        this.allowed = false;
        
        // assert tuple
        Cheese cheddar = new Cheese( "cheddar", 10 );
        FactHandleImpl f0 = (FactHandleImpl) workingMemory.assertObject( cheddar );

        ReteTuple tuple1 = new ReteTuple( 0,
                                          f0,
                                          this.workingMemory );
        
        assertNull( tuple1.getNotTuple() );        
        
        this.node.assertTuple( tuple1,
                               this.context,
                               this.workingMemory );        

        // no matching objects, so should propagate
        assertLength( 1,
                      this.sink.getAsserted() );
        
        assertLength( 0,
                      this.sink.getRetracted() );        

        assertNotNull( tuple1.getNotTuple() );
        
        assertEquals( f0,
                      ((Object[])this.sink.getAsserted().get( 0 ))[0] );
        
        // assert will not match, so activation should stay propagated
        Cheese brie = new Cheese( "brie", 10 );        
        FactHandleImpl f1 = (FactHandleImpl) workingMemory.assertObject( brie );
        
        this.node.assertObject( f1,
                                this.context,
                                this.workingMemory );        

        // check no as assertions, but should be one retraction
        assertLength( 1,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );      
        
        // assert tuple, will have no matches, so do assert propagation
        FactHandleImpl f2 = (FactHandleImpl) workingMemory.assertObject( new Cheese( "gouda", 10 ) );
        ReteTuple tuple2 = new ReteTuple( 0,
                                          f2,
                                          this.workingMemory );
        this.node.assertTuple( tuple2,
                               this.context,
                               this.workingMemory );        
        
        // check no as assertions, but should be one retraction
        assertLength( 2,
                      this.sink.getAsserted() );

        assertLength( 0,
                      this.sink.getRetracted() );           
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
