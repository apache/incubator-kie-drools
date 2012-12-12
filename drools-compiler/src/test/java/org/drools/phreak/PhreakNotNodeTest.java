package org.drools.phreak;

import java.beans.IntrospectionException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.RuleBaseConfiguration;
import org.drools.base.ArrayElements;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.StagedLeftTuples;
import org.drools.common.StagedRightTuples;
import org.drools.phreak.RuleNetworkEvaluatorActivation.PhreakJoinNode;
import org.drools.phreak.RuleNetworkEvaluatorActivation.PhreakNotNode;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.MockLeftTupleSink;
import org.drools.reteoo.MockTupleSource;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.ObjectSink;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.RightTupleSink;
import org.drools.reteoo.SegmentMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.test.dsl.NodeTestCase;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Rule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import static org.drools.phreak.A.*;
import static org.drools.phreak.B.*;

@Ignore
public class PhreakNotNodeTest {

    PhreakJoinNode        phreakJoinNode;
    BuildContext          buildContext;
    NotNode               notNode;
    JoinNode              sinkNode;
    InternalWorkingMemory wm;
    BetaMemory            bm;

    public void setupNotNode() {
        buildContext = createContext();

        notNode = (NotNode) BetaNodeBuilder.create( NodeTypeEnums.NotNode, buildContext )
                                             .setLeftType( A.class )
                                             .setBinding( "object", "$object" )
                                             .setRightType( B.class )
                                             .setConstraint( "object", "!=", "$object" ).build();

        sinkNode = new JoinNode();
        sinkNode.setId( 1 );
        sinkNode.setConstraints( new EmptyBetaConstraints() );
        
        notNode.addTupleSink( sinkNode );

        wm = (InternalWorkingMemory) buildContext.getRuleBase().newStatefulSession( true );
        
        bm =(BetaMemory)  wm.getNodeMemory( notNode );
        
        BetaMemory bm1 =(BetaMemory)  wm.getNodeMemory( sinkNode );
        
        SegmentMemory smem = new SegmentMemory( notNode ) ;
        bm.setSegmentMemory( smem );
        
        SegmentMemory childSmem = new SegmentMemory( sinkNode ) ;
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
    @Ignore
    public void test1() throws IntrospectionException {
        setupNotNode();

        // @formatter:off
        test().left().insert( a0, a1, a2 )
        
              .result().insert( a2,
                                a1,
                                a0 )
                       .left(a2, a1, a0)
               .run().getActualResultLeftTuples().clear();
        
        test().left().delete( a2 )
              .right().insert( b1 )
                      
              .result().delete( a2, a0 )
                       .left( a1 )
                       .right( b1 )
         .run().getActualResultLeftTuples().clear();   
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
