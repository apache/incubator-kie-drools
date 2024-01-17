/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests.phreak;

import java.util.Collections;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.phreak.PhreakJoinNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SegmentMemory.BetaMemoryPrototype;
import org.drools.core.reteoo.SegmentMemory.MemoryPrototype;
import org.drools.core.reteoo.SegmentMemory.SegmentPrototype;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.internal.conf.CompositeBaseConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.mvel.integrationtests.phreak.B.b;
import static org.drools.mvel.integrationtests.phreak.Pair.t;

public class ScenarioTest {
    BuildContext          buildContext;
    JoinNode              joinNode;
    JoinNode              sinkNode;
    InternalWorkingMemory wm;
    BetaMemory bm;
    SegmentMemory         smem;
    
    BetaMemory bm0;
    SegmentMemory  smem0;

    public void setupJoinNode() {
        buildContext = createContext();

        joinNode = (JoinNode) BetaNodeBuilder.create( NodeTypeEnums.JoinNode, buildContext )
                .setLeftType( A.class )
                .setBinding( "object", "$object" )
                .setRightType( B.class )
                .setConstraint( "object", "!=", "$object" ).build();

        sinkNode = (JoinNode) BetaNodeBuilder.create( NodeTypeEnums.JoinNode, buildContext ).build();
        
        joinNode.addTupleSink( sinkNode );

        SegmentPrototype proto1 = new SegmentPrototype(joinNode, joinNode);
        proto1.setNodesInSegment(new LeftTupleNode[]{joinNode});
        proto1.setMemories(new MemoryPrototype[]{new BetaMemoryPrototype(0, null)});
        SegmentPrototype proto2 = new SegmentPrototype(sinkNode, sinkNode);
        proto2.setNodesInSegment(new LeftTupleNode[]{sinkNode});
        proto2.setMemories(new MemoryPrototype[]{new BetaMemoryPrototype(0, null)});

        wm = (InternalWorkingMemory) KnowledgeBaseFactory.newKnowledgeBase(buildContext.getRuleBase()).newKieSession();
        
        bm =(BetaMemory)  wm.getNodeMemory(joinNode);
        
        bm0 =(BetaMemory)  wm.getNodeMemory(sinkNode);
        
        smem = proto1.newSegmentMemory(wm);
        bm.setSegmentMemory( smem );
        
        smem0 = proto2.newSegmentMemory(wm);
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
            assertThat(e.getMessage().contains("insert existed")).isTrue();
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
            assertThat(e.getMessage().contains("Insert excpected more")).isTrue();
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
            assertThat(e.getMessage().contains("insert 0")).isTrue();
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
            assertThat(e.getMessage().contains("delete 0")).isTrue();
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
            assertThat(e.getMessage().contains("insert existed")).isTrue();
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
            assertThat(e.getMessage().contains("Insert excpected more")).isTrue();
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
            assertThat(e.getMessage().contains("Insert excpected more")).isTrue();
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
            assertThat(e.getMessage().contains("LeftTuple memory size did not match")).isTrue();
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
            assertThat(e.getMessage().contains("LeftTuple memory size did not match")).isTrue();
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
            assertThat(e.getMessage().contains("Could not find LeftTuple")).isTrue();
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
            assertThat(e.getMessage().contains("Could not find LeftTuple")).isTrue();
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
            assertThat(e.getMessage().contains("RightTuple memory size did not match")).isTrue();
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
            assertThat(e.getMessage().contains("RightTuple memory size did not match")).isTrue();
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
            assertThat(e.getMessage().contains("Could not find RightTuple")).isTrue();
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
            assertThat(e.getMessage().contains("Could not find RightTuple")).isTrue();
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
            assertThat(e.getMessage().contains("insert existed")).isTrue();
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
            assertThat(e.getMessage().contains("Insert excpected more")).isTrue();
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
            assertThat(e.getMessage().contains("insert 0")).isTrue();
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
            assertThat(e.getMessage().contains("insert 2")).isTrue();
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
            assertThat(e.getMessage().contains("delete 0")).isTrue();
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
            assertThat(e.getMessage().contains("insert existed")).isTrue();
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
            assertThat(e.getMessage().contains("Insert excpected more")).isTrue();
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
            assertThat(e.getMessage().contains("update 0")).isTrue();
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
            assertThat(e.getMessage().contains("insert existed")).isTrue();
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
            assertThat(e.getMessage().contains("delete 0")).isTrue();
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
            assertThat(e.getMessage().contains("insert existed")).isTrue();
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
            assertThat(e.getMessage().contains("Insert excpected more")).isTrue();
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
            assertThat(e.getMessage().contains("update 0")).isTrue();
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

        CompositeBaseConfiguration conf = (CompositeBaseConfiguration) RuleBaseFactory.newKnowledgeBaseConfiguration();

        KnowledgeBaseImpl rbase = new KnowledgeBaseImpl("ID",
                                                        conf );
        BuildContext buildContext = new BuildContext( rbase, Collections.emptyList() );

        RuleImpl rule = new RuleImpl( "rule1").setPackage( "org.pkg1" );
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "org.pkg1" );
        pkg.getDialectRuntimeRegistry().setDialectData( "java", new JavaDialectRuntimeData() );
        pkg.addRule( rule );
        buildContext.setRule( rule );

        return buildContext;
    }

}
