package org.drools.compiler.phreak;

import static org.drools.compiler.phreak.A.a;
import static org.drools.compiler.phreak.B.b;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.IntrospectionException;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.PhreakJoinNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ReteooRuleBase;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Rule;
import org.junit.Test;

import static org.drools.compiler.phreak.Pair.t;

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

        wm = (InternalWorkingMemory) buildContext.getRuleBase().newStatefulSession( true );
        
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
    public void testEmptyResultInsert() throws IntrospectionException {
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
    public void testMissingResultInsert() throws IntrospectionException {
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
    public void testIncorrectResultInsert() throws IntrospectionException {
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
    public void testEmptyResultDelete() throws IntrospectionException {
        setupJoinNode();

        try {
            // @formatter:off
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .result()
                  .run();
            
            test().left().delete( a0 )                  
                  .result().delete(   )
                  .run();   
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "delete existed" ) );
        }
    }    
    
    @Test
    public void testMissingResultDelete() throws IntrospectionException {
        setupJoinNode();     
        
        try {
            // @formatter:off
            test().left().insert( a0, a1 )
                  .right().insert( b0, b1 )
                  .result()
                  .run();
            
            test().left().delete( a0, a1 )                  
                  .result().delete( t(a0, b1) )
                  .run();   
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Delete excpected more" ) );
        }
    }  
    
    @Test
    public void testIncorrecResultDelete() throws IntrospectionException {
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
    public void testEmptyResultUpdate() throws IntrospectionException {
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
    public void testMissingResultUpdate() throws IntrospectionException {
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
    public void testIncorrectResultUpdate() throws IntrospectionException {
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
    public void testEmptyLeftMemory() throws IntrospectionException {
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
    public void testMissingLeftMemory() throws IntrospectionException {
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
    public void testIncorrectLeftMemory() throws IntrospectionException {
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
    public void testTooMuchLeftMemory() throws IntrospectionException {
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
    public void testEmptyRightMemory() throws IntrospectionException {
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
    public void testMissingRightMemory() throws IntrospectionException {
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
    public void testIncorrectRightMemory() throws IntrospectionException {
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
    public void testTooMuchRightMemory() throws IntrospectionException {
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
    public void testEmptyPreStagedInsert() throws IntrospectionException {
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
    public void testMissingPreStagedInsert() throws IntrospectionException {
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
    public void testIncorrectPreStagedInsert() throws IntrospectionException {
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
    public void testTooMuchPreStagedInsert() throws IntrospectionException {
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
    public void testEmptyPreStagedDelete() throws IntrospectionException {
        setupJoinNode();

        try {
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
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "delete existed" ) );
        }
    }   
    
    @Test
    public void testMissingPreStagedDelete() throws IntrospectionException {
        setupJoinNode();

        try {
            // @formatter:off            
            test().left().insert( a0, a1, a2, a3 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().delete( a0, a1 )
                  .run();         
            
            test().left().delete( a2 )
                  .preStaged(smem0).delete( t(a0, b1) )
                  .run();               
            // @formatter:on
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Delete excpected more" ) );
        }
    }  
    
    @Test
    public void testIncorrectPreStagedDelete() throws IntrospectionException {
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
    public void testEmptyPreStagedUpdate() throws IntrospectionException {
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
    public void testMissingPreStagedUpdate() throws IntrospectionException {
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
    public void testIncorrectPreStagedUpdate() throws IntrospectionException {
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
    public void testEmptyPostStagedInsert() throws IntrospectionException {
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
    public void testMissingPostStagedDelete() throws IntrospectionException {
        setupJoinNode();
              
        try {
            // @formatter:off            
            test().left().insert( a0, a1, a2 )
                  .right().insert( b0, b1 )
                  .run();
            
            test().left().delete( a0, a1 )
                  .postStaged(smem0).delete( t(a0, b1) )
                  .run();            
            // @formatter:on            
            fail("Should not reach here");
        } catch ( AssertionError e ) {
            assertTrue( e.getMessage().contains( "Delete excpected more" ) );
        }
    }   
    
    @Test
    public void testIncorrectPostStagedDelete() throws IntrospectionException {
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
    public void testEmptyPostStagedUpdate() throws IntrospectionException {
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
    public void testMissingPostStagedUpdate() throws IntrospectionException {
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
    public void testIncorrectPostStagedUpdate() throws IntrospectionException {
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

        ReteooRuleBase rbase = new ReteooRuleBase( "ID",
                                                   conf );
        BuildContext buildContext = new BuildContext( rbase,
                                                      rbase.getReteooBuilder().getIdGenerator() );

        Rule rule = new Rule( "rule1", "org.pkg1", null );
        org.drools.core.rule.Package pkg = new org.drools.core.rule.Package( "org.pkg1" );
        pkg.getDialectRuntimeRegistry().setDialectData( "mvel", new MVELDialectRuntimeData() );
        pkg.addRule( rule );
        buildContext.setRule( rule );

        return buildContext;
    }

}
