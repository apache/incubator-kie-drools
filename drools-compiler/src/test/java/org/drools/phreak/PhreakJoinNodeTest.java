package org.drools.phreak;

import static org.drools.phreak.A.a;
import static org.drools.phreak.B.b;

import java.beans.IntrospectionException;

import org.drools.RuleBaseConfiguration;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.phreak.RuleNetworkEvaluatorActivation.PhreakJoinNode;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.SegmentMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Rule;
import org.kie.runtime.rule.FactHandle;
import org.junit.Ignore;
import org.junit.Test;
import static org.drools.phreak.Pair.t;

@Ignore
public class PhreakJoinNodeTest {
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

        wm = (InternalWorkingMemory) buildContext.getRuleBase().newStatefulSession( true );
        
        bm =(BetaMemory)  wm.getNodeMemory( joinNode );
        
        bm0 =(BetaMemory)  wm.getNodeMemory( sinkNode );
        
        smem = new SegmentMemory( joinNode ) ;
        bm.setSegmentMemory( smem );
        
        smem0 = new SegmentMemory( sinkNode ) ;
        bm0.setSegmentMemory( smem0 );       
        smem.add( smem0 );

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
    public void testInsertDelete() throws IntrospectionException {
        setupJoinNode();

        // @formatter:off
        test().left().insert( a0, a1 )
                .right().insert( b0, b1 )
                .result().insert( t(a1, b0),
                                  t(a0, b1) )
                .left( a0, a1 )
                .right( b0, b1 )
                .run().getActualResultLeftTuples().clear();

        test().right().insert( b3 )
                .result().insert( t(a1, b3),
                                  t(a0, b3) )
                .left( a0, a1 )
                .right( b0, b1, b3 )
                .run().getActualResultLeftTuples().clear();

        test().left().insert( a2 )
                .delete( a1 )
                .right().insert( b4 )
                .delete( b0 )

                .result().insert( t(a0, b4),
                                  t(a2, b1),
                                  t(a2, b3),
                                  t(a2, b4) )
                         .delete( t(a1, b0),
                                  t(a1, b3) )

                .left( a0, a2 )
                .right( b1, b3, b4 )
                .run().getActualResultLeftTuples().clear();
        // @formatter:on        

    }

    @Test
    public void testStagedInsertDelete() throws IntrospectionException {
        setupJoinNode();

        // @formatter:off
        test().left().insert( a0, a1 )
              .right().insert( b0, b1 )
              .preStaged(smem0).insert( )      
                               .delete( )
                               .update( )
              .postStaged(smem0).insert( t(a0, b1),
                                         t(a1, b0) )
                                .delete( )
                                .update( )                                      
              .run();

        test().left().delete( a1 )
              .result().insert( )
                       .delete( t(a1, b0) )    
                       .update( )
              .preStaged(smem0).insert( t(a0, b1) )
                               .delete( )
                               .update( )
              .postStaged(smem0).insert( t(a0, b1) )
                                .delete( t(a1, b0) )
                                .update( )
              .run();
        
        test().left().update( a0 )
              .result().update( t(a0, b1) )        
              .preStaged(smem0).insert( )
                               .delete( t(a1,b0) )
                               .update( )
              .postStaged(smem0).insert(  )
                                .delete( t(a1, b0) )
                                .update( t(a0, b1) )
                             
              .run();        
       // @formatter:on        
    }

    @Test
    public void testStagedUpdate() throws IntrospectionException {
        setupJoinNode();

        // @formatter:off
        test().left().insert( a0, a1 )
              .right().insert( b0, b1, b2 )
              .preStaged(smem0).insert( )      
                               .delete( )
                               .update( )
              .postStaged(smem0).insert( t(a0, b1),
                                         t(a0, b2),
                                         t(a1, b0),
                                         t(a1, b2) )
                                .delete( )
                                .update( )                                      
              .run();

        InternalFactHandle fh = (InternalFactHandle) wm.getFactHandle( a0 );
        wm.getObjectStore().updateHandle( fh, a2 );
        
        test().left().update( a2 )
                .preStaged(smem0).insert( t(a1, b0),
                                          t(a1, b2) )
                                 .delete( )
                                 .update( )                                       
                .postStaged(smem0).insert( t(a1, b0),
                                      t(a1, b2),
                                      t(a2, b0) )                                      
                                  .delete( t(a2, b2) )
                                  .update( t(a2, b1) )                                      
                .run();             
     // @formatter:on        
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

        ReteooRuleBase rbase = new ReteooRuleBase( "ID",
                                                   conf );
        BuildContext buildContext = new BuildContext( rbase,
                                                      rbase.getReteooBuilder().getIdGenerator() );

        Rule rule = new Rule( "rule1", "org.pkg1", null );
        org.drools.rule.Package pkg = new org.drools.rule.Package( "org.pkg1" );
        pkg.getDialectRuntimeRegistry().setDialectData( "mvel", new MVELDialectRuntimeData() );
        pkg.addRule( rule );
        buildContext.setRule( rule );

        return buildContext;
    }

}
