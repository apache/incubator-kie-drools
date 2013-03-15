package org.drools.compiler.phreak;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalWorkingMemory;
import org.drools.common.LeftTupleSets;
import org.drools.common.RightTupleSets;
import org.drools.core.util.FastIterator;
import org.drools.phreak.RuleNetworkEvaluator.PhreakJoinNode;
import org.drools.phreak.RuleNetworkEvaluator.PhreakNotNode;
import org.drools.phreak.RuleNetworkEvaluator.PhreakExistsNode;
import org.drools.phreak.SegmentPropagator;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.ExistsNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleMemory;
import org.drools.reteoo.SegmentMemory;
import org.junit.Assert;

public class Scenario {
    /**
     * 
     */
    Class                 phreakNode;
    BetaNode              betaNode;
    LeftTupleSink         sinkNode;
    BetaMemory            bm;
    InternalWorkingMemory wm;

    LeftTupleSets      leftTuples;
    RightTupleSets     rightRuples;
    
    StagedBuilder         expectedResultBuilder;
    
    LeftTupleSets      actualResultLeftTuples;
    
    LeftTupleSets      previousResultTuples;

    List<StagedBuilder>   preStagedBuilders;
    List<StagedBuilder>   postStagedBuilders;
    
    List<LeftTuple>       leftMemory;
    List<RightTuple>      rightMemory;
    
    private boolean       testLeftMemory;
    private boolean       testRightMemory;
    
    public Scenario(Class phreakNode,
                    BetaNode betaNode,
                    LeftTupleSink sinkNode,
                    BetaMemory bm,
                    InternalWorkingMemory wm) {
        this.phreakNode = phreakNode;
        this.betaNode = betaNode;
        this.sinkNode = sinkNode;
        this.bm = bm;
        this.wm = wm;
        this.leftTuples = new LeftTupleSets();
        this.rightRuples = new RightTupleSets();
        this.preStagedBuilders = new ArrayList<StagedBuilder>();
        this.postStagedBuilders = new ArrayList<StagedBuilder>();
        
        this.bm.setStagedRightTuples( rightRuples );
        this.leftMemory = new ArrayList<LeftTuple>();
        this.rightMemory = new ArrayList<RightTuple>();
    }

    public StagedBuilder getExpectedResultBuilder() {
        return expectedResultBuilder;
    }

    public void setExpectedResultBuilder(StagedBuilder stagedBuilder) {
        this.expectedResultBuilder = stagedBuilder;
    }


    public boolean isTestLeftMemory() {
        return testLeftMemory;
    }

    public void setTestLeftMemory(boolean testLeftMemory) {
        this.testLeftMemory = testLeftMemory;
    }

    public boolean isTestRightMemory() {
        return testRightMemory;
    }

    public void setTestRightMemory(boolean testRightMemory) {
        this.testRightMemory = testRightMemory;
    }

    public LeftTupleSets getActualResultLeftTuples() {
        return actualResultLeftTuples;
    }  
    
    public void addPreStagedBuilder(StagedBuilder stagedBuilder) {
        this.preStagedBuilders.add( stagedBuilder );
    }
    
    public void addPostStagedBuilder(StagedBuilder stagedBuilder) {
        this.postStagedBuilders.add( stagedBuilder );
    }    

    public LeftBuilder left() {
        return new LeftBuilder( this );
    }
    
    public RightBuilder right() {
        return new RightBuilder( this );
    }    

    public BetaNode getBetaNode() {
        return betaNode;
    }

    public LeftTupleSink getSinkNode() {
        return sinkNode;
    }

    public BetaMemory getBm() {
        return bm;
    }

    public InternalWorkingMemory getWorkingMemory() {
        return wm;
    }

    public RightTupleSets getRightRuples() {
        return rightRuples;
    }

    public LeftTupleSets getLeftTuples() {
        return leftTuples;
    }

    public RightTupleSets getRightTuples() {
        return rightRuples;
    }

    public List<LeftTuple> getLeftMemory() {
        return leftMemory;
    }

    public List<RightTuple> getRightMemory() {
        return rightMemory;
    }

    public Scenario run() {
        previousResultTuples = ( LeftTupleSets ) ((SegmentMemory) bm.getSegmentMemory().getFirst()).getStagedLeftTuples();
        actualResultLeftTuples = new LeftTupleSets();
        
        if ( phreakNode == PhreakJoinNode.class ) {
            new PhreakJoinNode().doNode( (JoinNode) betaNode, sinkNode,
                                          bm, wm, leftTuples, actualResultLeftTuples, previousResultTuples );
            
        } else if ( phreakNode == PhreakNotNode.class ) {
            new PhreakNotNode().doNode( (NotNode) betaNode, sinkNode,
                                        bm, wm, leftTuples, actualResultLeftTuples, previousResultTuples );            
        } else if ( phreakNode == PhreakExistsNode.class ) {
            new PhreakExistsNode().doNode( (ExistsNode) betaNode, sinkNode,
                                           bm, wm, leftTuples, actualResultLeftTuples, previousResultTuples );            
        }
        
        if ( expectedResultBuilder != null ) {
            assertEquals( expectedResultBuilder.get(), actualResultLeftTuples, 
                          expectedResultBuilder.isTestStagedInsert(),  expectedResultBuilder.isTestStagedDelete(),  expectedResultBuilder.isTestStagedUpdate() );
        }                     
        
        if ( !preStagedBuilders.isEmpty() ) {
            for ( StagedBuilder stagedBuilder : preStagedBuilders ) {
                LeftTupleSets expected = stagedBuilder.get();
                LeftTupleSets actual = stagedBuilder.getSegmentMemory().getStagedLeftTuples();
                
                assertEquals( expected, actual, stagedBuilder.isTestStagedInsert(), stagedBuilder.isTestStagedDelete(), stagedBuilder.isTestStagedUpdate() );    
            }
        }        
        
        SegmentMemory smem = bm.getSegmentMemory();
        SegmentPropagator.propagate(smem, actualResultLeftTuples, wm);
        if ( testLeftMemory ) {
            equalsLeftMemory( leftMemory );
        }
        if ( testRightMemory) {
            equalsRightMemory( rightMemory );
        }  
        
        if ( !postStagedBuilders.isEmpty() ) {
            for ( StagedBuilder stagedBuilder : postStagedBuilders ) {
                LeftTupleSets expected = stagedBuilder.get();
                LeftTupleSets actual = stagedBuilder.getSegmentMemory().getStagedLeftTuples();
                
                assertEquals( expected, actual, stagedBuilder.isTestStagedInsert(), stagedBuilder.isTestStagedDelete(), stagedBuilder.isTestStagedUpdate() );    
            }
        }
        
        return this;
    }
    
    public StagedBuilder result() {
        StagedBuilder stagedBuilder = new StagedBuilder( this, null );
        setExpectedResultBuilder( stagedBuilder );
        return stagedBuilder; 
    }    
    
    public StagedBuilder preStaged(SegmentMemory sm) {
        StagedBuilder stagedBuilder = new StagedBuilder( this, sm );
        addPreStagedBuilder( stagedBuilder );
        return stagedBuilder;        
    }    
    
    public StagedBuilder postStaged(SegmentMemory sm) {
        StagedBuilder stagedBuilder = new StagedBuilder( this, sm );
        addPostStagedBuilder( stagedBuilder );
        return stagedBuilder;        
    }      

    public void assertEquals(LeftTupleSets expected,
                             LeftTupleSets actual,
                             boolean testInsert,
                             boolean testDelete,
                             boolean testUpdate) {

        LeftTuple expectedTuple = null;
        LeftTuple actualTuple = null;
        
        if ( testInsert ) {
            if ( expected.getInsertFirst() != null ) {
                expectedTuple = expected.getInsertFirst();
                actualTuple = actual.getInsertFirst();        
                int i = 0;
                for ( ; expectedTuple != null; expectedTuple = expectedTuple.getStagedNext() ) {
                    Assert.assertEquals( "insert " + i, expectedTuple, actualTuple );
                    actualTuple = actualTuple.getStagedNext();
                    i++;
                }
                assertNull( "Insert excpected more", actualTuple );
            } else if ( actual.getInsertFirst() != null ) {
                fail( "Expected nothing, but insert existed" );
            }
        }

        if ( testDelete ) {
            if ( expected.getDeleteFirst() != null ) {        
                expectedTuple = expected.getDeleteFirst();
                actualTuple = actual.getDeleteFirst();
                int i = 0;
                for ( ; expectedTuple != null; expectedTuple = expectedTuple.getStagedNext() ) {
                    Assert.assertEquals( "delete " + i, expectedTuple, actualTuple );
                    actualTuple = actualTuple.getStagedNext();
                    i++;
                }
                assertNull( "Delete excpected more", actualTuple );
            } else if ( actual.getDeleteFirst() != null ) {
                fail( "Expected nothing, but delete existed" );
            }
        }
        
        if ( testUpdate ) {
            if ( expected.getUpdateFirst() != null ) {
                expectedTuple = expected.getUpdateFirst();
                actualTuple = actual.getUpdateFirst();
                int i = 0;
                for ( ; expectedTuple != null; expectedTuple = expectedTuple.getStagedNext() ) {
                    Assert.assertEquals( "update " + i, expectedTuple, actualTuple );
                    actualTuple = actualTuple.getStagedNext();
                    i++;
                }
                assertNull( "Update excpected more", actualTuple );
            } else if ( actual.getUpdateFirst() != null ) {
                fail( "Expected nothing, but update existed" );
            }
        }

    }

    public void equalsLeftMemory(List<LeftTuple> leftTuples) {
        LeftTupleMemory ltm = bm.getLeftTupleMemory();

        int length = 0;
        for ( LeftTuple expectedLeftTuple : leftTuples ) {
            FastIterator it = betaNode.getLeftIterator( ltm );
            LeftTuple actualLeftTuple = null;
            for ( actualLeftTuple = betaNode.getFirstLeftTuple( ltm, it ); actualLeftTuple != null; actualLeftTuple = (LeftTuple) it.next( actualLeftTuple ) ) {
                if ( expectedLeftTuple.equals( actualLeftTuple ) ) {
                    length++;
                    break;
                }
            }
            if ( actualLeftTuple == null ) {
                fail( "Could not find LeftTuple: " + expectedLeftTuple );
            }
        }
        if ( leftTuples.size() != ltm.size() ) {
            fail( "LeftTuple memory size did not match: " + length );
        }
    }

    public void equalsRightMemory(List<RightTuple> rightTuples) {
        RightTupleMemory rtm = bm.getRightTupleMemory();

        int length = 0;
        for ( RightTuple expectedRightTuple : rightTuples ) {
            FastIterator it = betaNode.getRightIterator( rtm );
            RightTuple actualRightTuple = null;
            for ( actualRightTuple = betaNode.getFirstRightTuple( rtm, it ); actualRightTuple != null; actualRightTuple = (RightTuple) it.next( actualRightTuple ) ) {
                if ( expectedRightTuple.equals( actualRightTuple ) ) {
                    length++;
                    break;
                }
            }
            if ( actualRightTuple == null ) {
                fail( "Could not find RightTuple: " + expectedRightTuple );
            }
        }

        if ( rightTuples.size() != rtm.size() ) {
            fail( "RightTuple memory size did not match: " + length );
        }
    }

}
