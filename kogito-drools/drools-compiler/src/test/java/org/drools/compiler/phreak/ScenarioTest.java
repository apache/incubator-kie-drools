/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.phreak;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.phreak.PhreakJoinNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.junit.Test;

import static org.drools.compiler.phreak.B.b;
import static org.drools.compiler.phreak.Pair.t;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ScenarioTest {
    BuildContext          buildContext;
    JoinNode              joinNode;
    JoinNode              sinkNode;
    InternalWorkingMemory wm;
    BetaMemory            bm;
    SegmentMemory         smem;
    
    BetaMemory            bm0;
    SegmentMemory         smem0;

    public void setupJoinNode() {
        buildContext = createContext();

        joinNode = (JoinNode) BetaNodeBuilder.create( NodeTypeEnums.JoinNode, buildContext )
                .setLeftType( A.class )
                .setBinding( "object", "$object" )
                .setRightType( B.class )
                .setConstraint( "object", "!=", "$object" ).build();

        sinkNode = new JoinNode();
        sinkNode.setId( 1 );
        sinkNode.setConstraints( new EmptyBetaConstraints() );
        
        joinNode.addTupleSink( sinkNode );

        wm = ((StatefulKnowledgeSessionImpl)buildContext.getKnowledgeBase().newStatefulKnowledgeSession());
        
        bm =(BetaMemory)  wm.getNodeMemory( joinNode );
        
        bm0 =(BetaMemory)  wm.getNodeMemory( sinkNode );
        
        smem = new SegmentMemory( joinNode ) ;
        bm.setSegmentMemory( smem );
        
        smem0 = new SegmentMemory( sinkNode ) ;
        bm0.setSegmentMemory( smem0 );       
        smem.add( smem0 );

    }

    A a0 = A.a(0);
    A a1 = A.a(1);
    A a2 = A.a(2);
    A a3 = A.a(3);
    A a4 = A.a(4);

    B b0 = b( 0 );
    B b1 = b( 1 );
    B b2 = b( 2 );
    B b3 = b( 3 );
    B b4 = b( 4 );

    @Test
    public void testEmptyResultInsert() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .result().insert(   )
                  .run();
            // @formatter:on                   
            fail("Should not reach here");
     
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "insert existed" ) );
        }
    }
    
    @Test
    public void testMissingResultInsert() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .result().insert( t(a0, b1)  )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Insert excpected more" ) );
        }
    }  
    
    @Test
    public void testIncorrectResultInsert() {
        setupJoinNode();

        try {
            // @formatter:off                  
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .result().insert( t(a0, b0)  )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "insert 0" ) );
        }
    }        
    
    @Test
    public void testEmptyResultDelete() {
        setupJoinNode();

        // @formatter:off
        test().left().insert( a0, a1 )
              .right().insert( b0, b1 )
              .result()
              .run();

        test().left().delete( a0 )
              .result().delete(   )
              .run();
        // @formatter:on
    }
    
    @Test
    public void testMissingResultDelete() {
        setupJoinNode();     
        
        // @formatter:off
        test().left().insert( a0, a1 )
              .right().insert( b0, b1 )
              .result()
              .run();

        test().left().delete( a0, a1 )
              .result().delete( )
              .run();
        // @formatter:on
    }
    
    @Test
    public void testIncorrecResultDelete() {
        setupJoinNode();     
             
        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .result()
                  .run();
            
            test().left().delete( a0, a1 )                  
                  .result().delete( t(a0, b0)  )
                  .run();   
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "delete 0" ) );
        }
    }     
    
    @Test
    public void testEmptyResultUpdate() {
        setupJoinNode();

        try {
            // @formatter:off
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .result()
                  .run();
            
            test().left().update(a0)
                  .result().insert()
                  .run();  
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "insert existed" ) );
        }
    }   
    
    @Test
    public void testMissingResultUpdate() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .result()
                  .run();
            
            test().left().update( a0, a1 )                  
                  .result().insert( t(a0, b1)  )
                  .run();  
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Insert excpected more" ) );
        }
    }    
    
    @Test
    public void testIncorrectResultUpdate() {
        setupJoinNode();         
        
        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .result()
                  .run();
            
            test().left().update( a0, a1 )                  
                  .result().insert( t(a0, b1)  )
                  .run();  
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Insert excpected more" ) );
        }
    }        

    @Test
    public void testEmptyLeftMemory() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .result().left(  )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "LeftTuple memory size did not match" ) );
        }
    }    
    
    @Test    
    public void testMissingLeftMemory() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .result().left( a1 )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "LeftTuple memory size did not match" ) );
        }
    }    
    
    @Test    
    public void testIncorrectLeftMemory() {
        setupJoinNode();

        try {
            // @formatter:off            
            wm.insert( a2 );
            
            test().left().insert( a0, a1 )
                  .result().left( a1, a2 )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Could not find LeftTuple" ) );
        }
    }    
    
    @Test    
    public void testTooMuchLeftMemory() {
        setupJoinNode();

        try {
            // @formatter:off            
            wm.insert( a2 );
            
            test().left().insert( a0, a1 )
                  .result().left( a1, a0, a2 )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Could not find LeftTuple" ) );
        }
    }      
    
    
    @Test
    public void testEmptyRightMemory() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().right().insert( b0, b1 )
                  .result().right(  )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "RightTuple memory size did not match" ) );
        }
    }    
    
    @Test
    public void testMissingRightMemory() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().right().insert( b0, b1 )
                  .result().right( b1 )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "RightTuple memory size did not match" ) );
        }
    }    
    
    @Test
    public void testIncorrectRightMemory() {
        setupJoinNode();

        try {
            // @formatter:off            
            wm.insert( b2 );
            
            test().right().insert( b0, b1 )
                  .result().right( b1, b2 )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Could not find RightTuple" ) );
        }
    }   
    
    @Test
    public void testTooMuchRightMemory() {
        setupJoinNode();
        try {
            // @formatter:off            
            wm.insert( b2 );
            
            test().right().insert( b0, b1 )
                  .result().right( b1, b0, b2 )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Could not find RightTuple" ) );
        }
    }      
    
    @Test
    public void testEmptyPreStagedInsert() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().insert( a3 )
                  .preStaged(smem0).insert(  )
                  .run();            
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "insert existed" ) );
        }
    } 
    
    @Test
    public void testMissingPreStagedInsert() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().insert( a3 )
                  .preStaged(smem0).insert( t(a0, b1) )
                  .run();            
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            System.out.println( e.getMessage() );
            assertTrue( e.getMessage().contains( "Insert excpected more" ) );
        }
    }    
    
    @Test
    public void testIncorrectPreStagedInsert() {
        setupJoinNode();

        try {
            // @formatter:off            
            wm.insert( b2 );
            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().insert( a3 )
                  .preStaged(smem0).insert( t(a1, b2) )
                  .run();            
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "insert 0" ) );
        }
    } 
    
    @Test
    public void testTooMuchPreStagedInsert() {
        setupJoinNode();

        try {
            // @formatter:off            
            wm.insert( b2 );
            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().insert( a3 )
                  .preStaged(smem0).insert( t(a1, b2),
                                            t(a1, b0),
                                            t(a0, b1) )
                  .run();            
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            System.out.println( e.getMessage() );
            assertTrue( e.getMessage().contains( "insert 2" ) );
        }
    }     
    
    @Test
    public void testEmptyPreStagedDelete() {
        setupJoinNode();

        // @formatter:off
        test().left().insert( a0, a1 )
              .right().insert( b0, b1 )
              .run();

        test().left().delete( a0 )
              .run();

        test().left().delete( a1 )
              .preStaged(smem0).delete(  )
              .run();
        // @formatter:on
    }
    
    @Test
    public void testMissingPreStagedDelete() {
        setupJoinNode();

        // @formatter:off
        test().left().insert( a0, a1, a2, a3 )
              .right().insert( b0, b1 )
              .run();

        test().left().delete( a0, a1 )
              .run();

        test().left().delete( a2 )
              .preStaged(smem0).delete( )
              .run();
        // @formatter:on
    }
    
    @Test
    public void testIncorrectPreStagedDelete() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1, a2, a3 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().delete( a0, a1 )
                  .run();         
            
            test().left().delete( a2 )
                  .preStaged(smem0).delete( t(a1, b1) )
                  .run();               
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "delete 0" ) );
        }
    }     
    
    @Test
    public void testEmptyPreStagedUpdate() {
        setupJoinNode();        
        
        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .run();
      
            test().left().update( a0 )
                  .run();         
      
            test().left().update( a1 )
                  .preStaged(smem0).insert(  )
                  .run();              
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "insert existed" ) );
        }
    }      
    
    @Test
    public void testMissingPreStagedUpdate() {
        setupJoinNode();             
        
        try {
            // @formatter:off            
            test().left().insert( a0, a1, a2 )
                  .right().insert( b0, b1 )
                  .run();
      
            test().left().update( a0, a1 )
                  .run();         
      
            test().left().update( a2 )
                  .preStaged(smem0).insert( t(a0, b1) )
                  .run();              
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Insert excpected more" ) );
        }
    }
    
    @Test
    public void testIncorrectPreStagedUpdate() {
        setupJoinNode();             
                
        try {
            // @formatter:off            
            test().left().insert( a0, a1, a2 )
                  .right().insert( b0, b1 )
                  .run();
      
            test().left().update( a0, a1 )
                  .run();         
      
            test().left().update( a2 )
                  .preStaged(smem0).update( t(a1, b1) )
                  .run();              
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "update 0" ) );
        }
    }

    @Test
    public void testEmptyPostStagedInsert() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .postStaged(smem0).insert(  )
                  .run();
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "insert existed" ) );
        }
    }  
    
    @Test
    public void testMissingPostStagedDelete() {
        setupJoinNode();
              
        // @formatter:off
        test().left().insert( a0, a1, a2 )
              .right().insert( b0, b1 )
              .run();

        test().left().delete( a0, a1 )
              .postStaged(smem0).delete( )
              .run();
        // @formatter:on
    }
    
    @Test
    public void testIncorrectPostStagedDelete() {
        setupJoinNode();
              
        try {
            // @formatter:off            
            test().left().insert( a0, a1, a2 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().delete( a0, a1 )
                  .postStaged(smem0).delete( t(a1, b1) )
                  .run();            
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "delete 0" ) );
        }
    }    
    
    @Test
    public void testEmptyPostStagedUpdate() {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().update( a0 )
                  .postStaged(smem0).insert(  )
                  .run();            
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "insert existed" ) );
        }
    }
    
    @Test
    public void testMissingPostStagedUpdate() {
        setupJoinNode();
              
        try {
            // @formatter:off            
            test().left().insert( a0, a1, a2 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().update( a0, a1 )
                  .postStaged(smem0).insert( t(a2, b0) )
                  .run();            
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Insert excpected more" ) );
        }
    }    
    
    @Test
    public void testIncorrectPostStagedUpdate() {
        setupJoinNode();
              
        try {
            // @formatter:off
            test().left().insert( a0, a1, a2 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().update( a0, a1 )
                  .postStaged(smem0).update( t(a1, b1) )
                  .run();            
            // @formatter:on
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "update 0" ) );
        }
    }        
    
    private Scenario test() {
        return test( PhreakJoinNode.class,
                     joinNode, sinkNode,
                     bm, wm );
    }

    private Scenario test(Class phreakNode,
                          JoinNode joinNode,
                          LeftTupleSink sinkNode,
                          BetaMemory bm,
                          InternalWorkingMemory wm) {
        return new Scenario( phreakNode, joinNode, sinkNode, bm, wm );
    }

    public BuildContext createContext() {

        RuleBaseConfiguration conf = new RuleBaseConfiguration();

        KnowledgeBaseImpl rbase = new KnowledgeBaseImpl( "ID",
                                                   conf );
        BuildContext buildContext = new BuildContext( rbase,
                                                      rbase.getReteooBuilder().getIdGenerator() );

        RuleImpl rule = new RuleImpl( "rule1", "org.pkg1", null );
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.pkg1" );
        pkg.getDialectRuntimeRegistry().setDialectData( "mvel", new MVELDialectRuntimeData() );
        pkg.addRule( rule );
        buildContext.setRule( rule );

        return buildContext;
    }

}
