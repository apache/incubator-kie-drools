package org.drools.reteoo;
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





import java.util.List;

import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.common.PropagationContextImpl;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;

public class EvalConditionNodeTest extends DroolsTestCase {
    private PropagationContext context;
    private WorkingMemoryImpl  workingMemory;

    public EvalConditionNodeTest(String name) {
        super( name );
    }

    public void setUp() {
        this.context = new PropagationContextImpl( 0,
                                                   PropagationContext.ASSERTION,
                                                   null,
                                                   null );

        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
    }

    public void testAttach() throws Exception {
        MockTupleSource source = new MockTupleSource( 12 );

        EvalConditionNode node = new EvalConditionNode( 18,
                                                        source,
                                                        new MockEvalCondition( true ) );

        assertEquals( 18,
                      node.getId() );

        assertLength( 0,
                      source.getTupleSinks() );

        node.attach();

        assertLength( 1,
                      source.getTupleSinks() );

        assertSame( node,
                    source.getTupleSinks().get( 0 ) );
    }

    public void testMemory() {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockTupleSource source = new MockTupleSource( 12 );

        EvalConditionNode node = new EvalConditionNode( 18,
                                                        source,
                                                        new MockEvalCondition( true ) );

        LinkedList memory = (LinkedList) workingMemory.getNodeMemory( node );

        assertNotNull( memory );
    }

    /**
     * If a eval allows an incoming Object, then the Object MUST be
     * propagated. This tests that the memory is updated
     * 
     * @throws FactException
     */
    public void testAssertedAllowed() throws FactException {
        MockEvalCondition eval = new MockEvalCondition( true );
        
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        eval  );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        f0.setObject( "stilton" );
        ReteTuple tuple0 = new ReteTuple( f0 );

        // Tuple should pass and propagate 
        node.assertTuple( tuple0,
                          this.context,
                          this.workingMemory );
                
        // Create the Tuple
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        f0.setObject( "cheddar" );
        ReteTuple tuple1 = new ReteTuple( f1 );   
        
        // Tuple should pass and propagate 
        node.assertTuple( tuple1,
                          this.context,
                          this.workingMemory );  

        // Check memory was populated
        LinkedList memory = (LinkedList) this.workingMemory.getNodeMemory( node );

        assertEquals( 2,
                      memory.size() );
        
        // Check list is in the correct order
        assertEquals( tuple0,
                      memory.getFirst() );
        assertEquals( tuple1,
                     tuple0.getNext() );      
        
        // make sure assertions were propagated
        assertEquals( 2,
                      sink.getAsserted().size() );        
    }
    
    public void testAssertedAllowedThenRetract() throws FactException {
        MockEvalCondition eval = new MockEvalCondition( true );
        
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        eval  );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        f0.setObject( "stilton" );
        ReteTuple tuple0 = new ReteTuple( f0 );

        // Tuple should pass and propagate 
        node.assertTuple( tuple0,
                          this.context,
                          this.workingMemory );
        
        // we create and retract two tuples, checking the linkedtuples is null for JBRULES-246 "NPE on retract()"        
        // Create the Tuple
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        f0.setObject( "cheddar" );
        ReteTuple tuple1 = new ReteTuple( f1 );   
        
        // Tuple should pass and propagate 
        node.assertTuple( tuple1,
                          this.context,
                          this.workingMemory );  

        // Check memory was populated
        LinkedList memory = (LinkedList) this.workingMemory.getNodeMemory( node );

        assertEquals( 2,
                      memory.size() );
        assertEquals( tuple0,
                      memory.getFirst() );
        assertEquals( tuple1,
                      tuple0.getNext() );       

        // make sure assertions were propagated
        assertEquals( 2,
                      sink.getAsserted().size() ); 
        
        // Now test that the fact is retracted correctly
        node.retractTuple( tuple0,
                           context,
                           workingMemory );

        // Now test that the fact is retracted correctly
        assertEquals( 1,
                      memory.size() );
        
        assertEquals( tuple1,
                      memory.getFirst() );        
        
        // make sure retractions were propagated
        assertEquals( 1,
                      sink.getRetracted().size() );         
        
        // Now test that the fact is retracted correctly
        node.retractTuple( tuple1,
                           context,
                           workingMemory );        
        
        // Now test that the fact is retracted correctly
        assertEquals( 0,
                      memory.size() );     
        
        // make sure retractions were propagated
        assertEquals( 2,
                      sink.getRetracted().size() );  
    }    
    
    
    public void testAssertedAllowedThenModifyAllowed() throws FactException {
        MockEvalCondition eval = new MockEvalCondition( true );
        
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        eval  );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        f0.setObject( "stilton" );
        ReteTuple tuple0 = new ReteTuple( f0 );

        // Tuple should pass and propagate 
        node.assertTuple( tuple0,
                          this.context,
                          this.workingMemory );
        
        // we create and retract two tuples, checking the linkedtuples is null for JBRULES-246 "NPE on retract()"        
        // Create the Tuple
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        f0.setObject( "cheddar" );
        ReteTuple tuple1 = new ReteTuple( f1 );   
        
        // Tuple should pass and propagate 
        node.assertTuple( tuple1,
                          this.context,
                          this.workingMemory );  

        // Check memory was populated
        LinkedList memory = (LinkedList) this.workingMemory.getNodeMemory( node );

        assertEquals( 2,
                      memory.size() );
        assertEquals( tuple0,
                      memory.getFirst() );
        assertEquals( tuple1,
                      tuple0.getNext() );       
        
        // make sure assertions were propagated
        assertEquals( 2,
                      sink.getAsserted().size() );          

        
        
        // Now test that the fact is modified correctly
        node.modifyTuple( tuple0,
                           context,
                           workingMemory );
        assertEquals( 2,
                      memory.size() );
        
        // notice the order is reversed, as tuple0 was modified last and is more recent
        assertEquals( tuple1,
                      memory.getFirst() ); 
        
        assertEquals( tuple0,
                      tuple1.getNext() );         
        
        // make sure modifications were propagated
        assertEquals( 1,
                      sink.getModified().size() );    
        
        // Now test that the fact is modified correctly
        node.modifyTuple( tuple1,
                           context,
                           workingMemory );    
        
        // notice the order is reversed, as tuple0 was modified last and is more recent
        assertEquals( tuple0,
                      memory.getFirst() ); 
        
        assertEquals( tuple1,
                      tuple0.getNext() );         
        
        // make sure modifications were propagated
        assertEquals( 2,
                      sink.getModified().size() );          
    }        
    
    public void testAssertedAllowedThenModifyNotAllowed() throws FactException {
        MockEvalCondition eval = new MockEvalCondition( true );
        
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        eval  );

        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );

        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        f0.setObject( "stilton" );
        ReteTuple tuple0 = new ReteTuple( f0 );

        // Tuple should pass and propagate 
        node.assertTuple( tuple0,
                          this.context,
                          this.workingMemory );
        
        // we create and retract two tuples, checking the linkedtuples is null for JBRULES-246 "NPE on retract()"        
        // Create the Tuple
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        f0.setObject( "cheddar" );
        ReteTuple tuple1 = new ReteTuple( f1 );   
        
        // Tuple should pass and propagate 
        node.assertTuple( tuple1,
                          this.context,
                          this.workingMemory );  

        // Check memory was populated
        LinkedList memory = (LinkedList) this.workingMemory.getNodeMemory( node );

        assertEquals( 2,
                      memory.size() );
        assertEquals( tuple0,
                      memory.getFirst() );
        assertEquals( tuple1,
                      tuple0.getNext() );       
        
        // make sure assertions were propagated
        assertEquals( 2,
                      sink.getAsserted().size() );     
        
        eval.setIsAllowed( false );

        // Now test that the fact is modified correctly
        node.modifyTuple( tuple0,
                           context,
                           workingMemory );
        
        // tuple1 has now been removed
        assertEquals( 1,
                      memory.size() );        
        assertEquals( tuple1,
                      memory.getFirst() );          
        
        // make sure modifications were propagated as a retraction, as it is now no longer passing
        assertEquals( 1,
                      sink.getRetracted().size() );    
        
        // Now test that the fact is modified correctly
        node.modifyTuple( tuple1,
                          context,
                          workingMemory );    
        
        // tuple0 has now been removed
        assertEquals( 0,
                      memory.size() );   
        
        // make sure modifications were propagated as a retraction
        assertEquals( 2,
                      sink.getRetracted().size() );    
    }                  

    public void testAssertedNotAllowed() throws FactException {
        MockEvalCondition eval = new MockEvalCondition( false );
        
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        eval  );
    
        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );
    
        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        f0.setObject( "stilton" );
        ReteTuple tuple0 = new ReteTuple( f0 );
    
        // Tuple should fail and not propagate
        node.assertTuple( tuple0,
                          this.context,
                          this.workingMemory );
                
        // Create the Tuple
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        f0.setObject( "cheddar" );
        ReteTuple tuple1 = new ReteTuple( f1 );   
        
        // Tuple should fail and not propagate 
        node.assertTuple( tuple1,
                          this.context,
                          this.workingMemory );  
    
        // Check memory was not populated
        LinkedList memory = (LinkedList) this.workingMemory.getNodeMemory( node );
    
        assertEquals( 0,
                      memory.size() );      
        
        // test no propagations
        assertEquals( 0,
                      sink.getAsserted().size() );
        assertEquals( 0,
                      sink.getModified().size() );
        assertEquals( 0,
                      sink.getRetracted().size() );
    }

    public void testAssertedNotAllowedThenModifyNotAllowed() throws FactException {
        MockEvalCondition eval = new MockEvalCondition( false );
        
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        eval  );
    
        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );
    
        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        f0.setObject( "stilton" );
        ReteTuple tuple0 = new ReteTuple( f0 );
    
        // Tuple should fail and not propagate
        node.assertTuple( tuple0,
                          this.context,
                          this.workingMemory );
                
        // Create the Tuple
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        f0.setObject( "cheddar" );
        ReteTuple tuple1 = new ReteTuple( f1 );   
        
        // Tuple should fail and not propagate 
        node.assertTuple( tuple1,
                          this.context,
                          this.workingMemory );  
    
        // Check memory was not populated
        LinkedList memory = (LinkedList) this.workingMemory.getNodeMemory( node );
    
        assertEquals( 0,
                      memory.size() );      
        
        // Now test that the fact is modified correctly
        node.modifyTuple( tuple0,
                           context,
                           workingMemory );
        assertEquals( 0,
                      memory.size() );
                  
        // Now test that the fact is modified correctly
        node.modifyTuple( tuple1,
                          context,
                          workingMemory );            
            
        // make sure the memory wasn't populated
        assertEquals( 0,
                      memory.size() );      
        
        // test no propagations
        assertEquals( 0,
                      sink.getAsserted().size() );
        assertEquals( 0,
                      sink.getModified().size() );
        assertEquals( 0,
                      sink.getRetracted().size() );        
    }    

    public void testAssertedNotAllowedThenModifyAllowed() throws FactException {
        MockEvalCondition eval = new MockEvalCondition( false );
        
        // Create a test node that always returns false 
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        eval  );
    
        MockTupleSink sink = new MockTupleSink();
        node.addTupleSink( sink );
    
        // Create the Tuple
        FactHandleImpl f0 = new FactHandleImpl( 0 );
        f0.setObject( "stilton" );
        ReteTuple tuple0 = new ReteTuple( f0 );
    
        // Tuple should fail and not propagate
        node.assertTuple( tuple0,
                          this.context,
                          this.workingMemory );
                
        // Create the Tuple
        FactHandleImpl f1 = new FactHandleImpl( 1 );
        f0.setObject( "cheddar" );
        ReteTuple tuple1 = new ReteTuple( f1 );   
        
        // Tuple should fail and not propagate 
        node.assertTuple( tuple1,
                          this.context,
                          this.workingMemory );  
    
        // Check memory was not populated
        LinkedList memory = (LinkedList) this.workingMemory.getNodeMemory( node );
    
        assertEquals( 0,
                      memory.size() );      
        
        eval.setIsAllowed( true );
        
        // Now test that the fact is modified correctly
        node.modifyTuple( tuple1,
                           context,
                           workingMemory );
        assertEquals( 1,
                      memory.size() );
        
        // As this this  wasn't asserted and remember before, will propagate as an assert
        assertEquals( 1,
                      sink.getAsserted().size() );   
        
        assertSame( tuple1,
                    memory.getFirst() );
        
        // Now test that the fact is modified correctly
        node.modifyTuple( tuple0,
                           context,
                           workingMemory );
        assertEquals( 2,
                      memory.size() );
        
        // As this this  wasn't asserted and remember before, will propagate as an assert
        assertEquals( 2,
                      sink.getAsserted().size() );   
        
        assertSame( tuple1,
                    memory.getFirst() );  
        
        assertSame( tuple0,
                    tuple1.getNext() );         

        // test no propagations
        assertEquals( 0,
                      sink.getModified().size() );
        assertEquals( 0,
                      sink.getRetracted().size() );        
    }              

    public void testUpdateWithMemory() throws FactException {
        // If no child nodes have children then we need to re-process the left
        // and right memories
        // as a joinnode does not store the resulting tuples
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        // Creat the object source so we can detect the alphaNode telling it to
        // propate its contents
        MockTupleSource source = new MockTupleSource( 1 );

        /* Create a test node that always returns true */
        EvalConditionNode node = new EvalConditionNode( 1,
                                                        new MockTupleSource( 15 ),
                                                        new MockEvalCondition( true ) );

        // Add the first tuple sink and assert a tuple and object
        // The sink has no memory
        MockTupleSink sink1 = new MockTupleSink( 2 );
        node.addTupleSink( sink1 );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 "string0" );

        ReteTuple tuple1 = new ReteTuple( f0 );

        node.assertTuple( tuple1,
                          this.context,
                          workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );

        // Add the new sink, this should be updated from the re-processed
        // joinnode memory
        MockTupleSink sink2 = new MockTupleSink( 3 );
        node.addTupleSink( sink2 );
        assertLength( 0,
                      sink2.getAsserted() );

        node.updateNewNode( workingMemory,
                            this.context );

        assertLength( 1,
                      sink2.getAsserted() );
    }
}