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

import static org.drools.mvel.integrationtests.phreak.Pair.t;

public class SegmentPropagationTest {
    
    BuildContext          buildContext;
    JoinNode              joinNode;
    JoinNode              sinkNode0;
    JoinNode              sinkNode1;
    JoinNode              sinkNode2;    
    InternalWorkingMemory wm;
    
    BetaMemory bm;
    SegmentMemory  smem;
    
    BetaMemory bm0;
    BetaMemory bm1;
    BetaMemory bm2;
    
    SegmentMemory smem0;
    SegmentMemory smem1;
    SegmentMemory smem2;

    public void setupJoinNode() {
        buildContext = createContext();

        joinNode = (JoinNode) BetaNodeBuilder.create( NodeTypeEnums.JoinNode, buildContext )
                .setLeftType( A.class )
                .setBinding( "object", "$object" )
                .setRightType( B.class )
                .setConstraint( "object", "!=", "$object" ).build();

        sinkNode0 = (JoinNode) BetaNodeBuilder.create( NodeTypeEnums.JoinNode, buildContext ).build();
        joinNode.addTupleSink( sinkNode0 );
        
        sinkNode1 = (JoinNode) BetaNodeBuilder.create( NodeTypeEnums.JoinNode, buildContext ).build();
        joinNode.addTupleSink( sinkNode1 );   
        
        sinkNode2 = (JoinNode) BetaNodeBuilder.create( NodeTypeEnums.JoinNode, buildContext ).build();
        joinNode.addTupleSink( sinkNode2 );

        SegmentPrototype proto1 = new SegmentPrototype(joinNode, joinNode);
        proto1.setNodesInSegment(new LeftTupleNode[]{joinNode});
        proto1.setMemories(new MemoryPrototype[]{new BetaMemoryPrototype(0, null)});
        SegmentPrototype proto2 = new SegmentPrototype(sinkNode0, sinkNode0);
        proto2.setNodesInSegment(new LeftTupleNode[]{sinkNode0});
        proto2.setMemories(new MemoryPrototype[]{new BetaMemoryPrototype(0, null)});

        SegmentPrototype proto3 = new SegmentPrototype(sinkNode1, sinkNode1);
        proto3.setNodesInSegment(new LeftTupleNode[]{sinkNode1});
        proto3.setMemories(new MemoryPrototype[]{new BetaMemoryPrototype(0, null)});
        SegmentPrototype proto4 = new SegmentPrototype(sinkNode2, sinkNode2);
        proto4.setNodesInSegment(new LeftTupleNode[]{sinkNode2});
        proto4.setMemories(new MemoryPrototype[]{new BetaMemoryPrototype(0, null)});

        wm = (InternalWorkingMemory) KnowledgeBaseFactory.newKnowledgeBase(buildContext.getRuleBase()).newKieSession();;
        
        bm =(BetaMemory)  wm.getNodeMemory(joinNode);
        
        bm0 =(BetaMemory)  wm.getNodeMemory(sinkNode0);
        bm1 =(BetaMemory)  wm.getNodeMemory(sinkNode1);
        bm2 =(BetaMemory)  wm.getNodeMemory(sinkNode2);
        
        smem = proto1.newSegmentMemory(wm);
        bm.setSegmentMemory( smem );
        
        smem0 = proto2.newSegmentMemory(wm);
        bm0.setSegmentMemory( smem0 );       
        smem.add( smem0 );

        smem1 = proto3.newSegmentMemory(wm);
        bm1.setSegmentMemory( smem1 );       
        smem.add( smem1 );    
        
        smem2 = proto4.newSegmentMemory(wm);
        bm2.setSegmentMemory( smem2 );       
        smem.add( smem2 );          
    }
    
    A a0 = A.a(0);
    A a1 = A.a(1);
    A a2 = A.a(2);
    A a3 = A.a(3);
    A a4 = A.a(4);
    
    B b0 = B.b(0);
    B b1 = B.b(1);
    B b2 = B.b(2);
    B b3 = B.b(3);
    B b4 = B.b(4);
    
    @Test
    public void test1() {
        setupJoinNode();
        
        // @formatter:off
        test().left().insert( a0, a1 )
              .right().insert( b0, b1, b2 )
              .preStaged(smem0).insert( )
                               .delete( )
                               .update( )
              .postStaged(smem0).insert( t(a1, b2),
                                         t(a1, b0),
                                         t(a0, b2),
                                         t(a0, b1) )
                                .delete( )
                                .update( )
              .postStaged( smem1 ).insert( t(a0, b1),
                                           t(a0, b2),
                                           t(a1, b0),
                                           t(a1, b2) )
                                  .delete( )
                                  .update( )
              .postStaged( smem2 ).insert( t(a0, b1),
                                           t(a0, b2),
                                           t(a1, b0),
                                           t(a1, b2) )
                                 .delete( )
                                 .update( )
              .run();
        
        
        test().left().update( a0 )
              .preStaged(smem0).insert( t(a1, b2),
                                        t(a1, b0) )
                          .delete( )
                          .update( )
              .postStaged(smem0).insert( t(a0, b2),
                                         t(a0, b1),
                                         t(a1, b2),
                                         t(a1, b0) )
                                .delete( )
                                .update( )

              .postStaged( smem1 ).insert( t(a1, b0),
                                           t(a1, b2),
                                           t(a0, b1),
                                           t(a0, b2) )
                                  .delete( )
                                  .update( )

              .postStaged( smem2 ).insert( t(a1, b0),
                                           t(a1, b2),
                                           t(a0, b1),
                                           t(a0, b2) )
                                  .delete( )
                                  .update( )
              .run();
        test().right().delete( b2 )
              .preStaged(smem0).insert( t(a0, b1),
                                        t(a1, b0) )
                               .delete( )
                               .update( )
              .postStaged(smem0).insert( t(a0, b1),
                                         t(a1, b0) )
                                .delete( )
                                .update( )
              .postStaged( smem1 ).insert( t(a1, b0),
                                           t(a0, b1) )
              .postStaged( smem2 ).insert( t(a1, b0),
                                           t(a0, b1) )
              .run();
                 
        // @formatter:on
    }
    
    private Scenario test() {
        return test(PhreakJoinNode.class,
                    joinNode, sinkNode0,
                    bm, wm);
    }

    private Scenario test(Class phreakNode,
                          JoinNode joinNode,
                             LeftTupleSink sinkNode,
                             BetaMemory bm,
                             InternalWorkingMemory wm) {
        return new Scenario( phreakNode, joinNode, sinkNode, bm, wm ) ;
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
