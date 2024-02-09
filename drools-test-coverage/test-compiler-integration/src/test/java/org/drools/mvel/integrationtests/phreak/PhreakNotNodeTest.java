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
import org.drools.core.phreak.PhreakNotNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SegmentMemory.BetaMemoryPrototype;
import org.drools.core.reteoo.SegmentMemory.MemoryPrototype;
import org.drools.core.reteoo.SegmentMemory.SegmentPrototype;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.internal.conf.CompositeBaseConfiguration;

import static org.drools.mvel.integrationtests.phreak.A.a;
import static org.drools.mvel.integrationtests.phreak.B.b;

public class PhreakNotNodeTest {

    BuildContext          buildContext;
    NotNode               notNode;
    JoinNode              sinkNode;
    InternalWorkingMemory wm;
    BetaMemory bm;

    private void setupNotNode(String operator) {
        buildContext = createContext();

        notNode = (NotNode) BetaNodeBuilder.create( NodeTypeEnums.NotNode, buildContext )
                                             .setLeftType( A.class )
                                             .setBinding( "object", "$object" )
                                             .setRightType( B.class )
                                             .setConstraint( "object", operator, "$object" ).build();

        sinkNode = (JoinNode) BetaNodeBuilder.create( NodeTypeEnums.JoinNode, buildContext ).build();

        notNode.addTupleSink( sinkNode );

        SegmentPrototype proto1 = new SegmentPrototype(notNode, notNode);
        proto1.setNodesInSegment(new LeftTupleNode[]{notNode});
        proto1.setMemories(new MemoryPrototype[]{new BetaMemoryPrototype(0, null)});
        SegmentPrototype proto2 = new SegmentPrototype(sinkNode, sinkNode);
        proto2.setNodesInSegment(new LeftTupleNode[]{sinkNode});
        proto2.setMemories(new MemoryPrototype[]{new BetaMemoryPrototype(0, null)});

        wm = (InternalWorkingMemory) KnowledgeBaseFactory.newKnowledgeBase(buildContext.getRuleBase()).newKieSession();
        
        bm =(BetaMemory)  wm.getNodeMemory(notNode);
        
        BetaMemory bm1 =(BetaMemory)  wm.getNodeMemory(sinkNode);
        
        SegmentMemory smem = proto1.newSegmentMemory(wm);
        bm.setSegmentMemory( smem );
        
        SegmentMemory childSmem = proto2.newSegmentMemory(wm);
        bm1.setSegmentMemory( childSmem );       
        smem.add( childSmem );     

    }

    A a0 = a( 0 );
    A a1 = a( 1 );
    A a2 = a( 2 );
    A a3 = a( 3 );
    A a4 = a( 4 );

    B b0 = b( 0 );
    B b1 = b( 1 );
    B b2 = b( 2 );
    B b3 = b( 3 );
    B b4 = b( 4 );

    @Test
    public void test1() {
        setupNotNode("!=");

        // @formatter:off
        test().left().insert( a0, a1, a2 )
        
              .result().insert( a2,
                                a1,
                                a0 )
                       .left(a2, a1, a0)
               .run().getActualResultLeftTuples().resetAll();
        
        test().left().delete( a2 )
              .right().insert( b1 )
                      
              .result().delete( )
                       .left( a1 )
                       .right( b1 )
         .run().getActualResultLeftTuples().resetAll();
        // @formatter:on
    }

    @Test
    public void test2() {
        setupNotNode("<");

        // @formatter:off
        test().left().insert( a0, a1, a2 )

              .result().insert( a2, a1, a0 )
                       .left(a2, a1, a0)
              .run().getActualResultLeftTuples().resetAll();

        test().right().insert( b1 )

              .result().delete( )
                       .left( a0, a1 )
                       .right( b1 )
              .run().getActualResultLeftTuples().resetAll();
        // @formatter:on
    }

    private Scenario test() {
        return test(notNode, sinkNode,
                    bm, wm);
    }

    private Scenario test(NotNode notNode,
                             LeftTupleSink sinkNode,
                             BetaMemory bm,
                             InternalWorkingMemory wm) {
        return new Scenario( PhreakNotNode.class, notNode, sinkNode, bm, wm ) ;
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
