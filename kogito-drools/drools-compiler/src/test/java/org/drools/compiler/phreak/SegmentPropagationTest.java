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

import static org.drools.compiler.phreak.Pair.t;

public class SegmentPropagationTest {
    
    BuildContext          buildContext;
    JoinNode              joinNode;
    JoinNode              sinkNode0;
    JoinNode              sinkNode1;
    JoinNode              sinkNode2;    
    InternalWorkingMemory wm;
    
    BetaMemory            bm;
    SegmentMemory         smem;
    
    BetaMemory            bm0;
    BetaMemory            bm1;
    BetaMemory            bm2;
    
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

        sinkNode0 = new JoinNode();
        sinkNode0.setId( 1 );
        sinkNode0.setConstraints( new EmptyBetaConstraints() );        
        joinNode.addTupleSink( sinkNode0 );
        
        sinkNode1 = new JoinNode();
        sinkNode1.setId( 3 );
        sinkNode1.setConstraints( new EmptyBetaConstraints() );        
        joinNode.addTupleSink( sinkNode1 );   
        
        sinkNode2 = new JoinNode();
        sinkNode2.setId( 4 );
        sinkNode2.setConstraints( new EmptyBetaConstraints() );        
        joinNode.addTupleSink( sinkNode2 );

        wm = ((StatefulKnowledgeSessionImpl)buildContext.getKnowledgeBase().newStatefulKnowledgeSession());
        
        bm =(BetaMemory)  wm.getNodeMemory( joinNode );
        
        bm0 =(BetaMemory)  wm.getNodeMemory( sinkNode0 );
        bm1 =(BetaMemory)  wm.getNodeMemory( sinkNode1 );
        bm2 =(BetaMemory)  wm.getNodeMemory( sinkNode2 );
        
        smem = new SegmentMemory( joinNode ) ;
        bm.setSegmentMemory( smem );
        
        smem0 = new SegmentMemory( sinkNode0 ) ;
        bm0.setSegmentMemory( smem0 );       
        smem.add( smem0 );

        smem1 = new SegmentMemory( sinkNode1 ) ;
        bm1.setSegmentMemory( smem1 );       
        smem.add( smem1 );    
        
        smem2 = new SegmentMemory( sinkNode2 ) ;
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
        
        JoinNode parentNode = joinNode;
        JoinNode child1Node = new JoinNode();
        JoinNode child2Node = new JoinNode();
        JoinNode child3Node = new JoinNode();
        
        parentNode.addTupleSink( child1Node );
        parentNode.addTupleSink( child2Node );
        parentNode.addTupleSink( child3Node );
        
        SegmentMemory smem = new SegmentMemory( parentNode );
        smem.setTipNode( parentNode );
        
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
                                .delete( t(a1, b2),
                                         t(a0, b2) )
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
