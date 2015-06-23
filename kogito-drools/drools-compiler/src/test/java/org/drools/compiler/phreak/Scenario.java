/*
 * Copyright 2015 JBoss Inc
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

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.common.LeftTupleSetsImpl;
import org.drools.core.common.RightTupleSets;
import org.drools.core.common.RightTupleSetsImpl;
import org.drools.core.phreak.PhreakExistsNode;
import org.drools.core.phreak.PhreakJoinNode;
import org.drools.core.phreak.PhreakNotNode;
import org.drools.core.phreak.SegmentPropagator;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleMemory;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.util.FastIterator;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class Scenario {
    /**
     *
     */
    Class                 phreakNode;
    BetaNode              betaNode;
    LeftTupleSink         sinkNode;
    BetaMemory            bm;
    InternalWorkingMemory wm;

    LeftTupleSets              leftTuples;
    RightTupleSets rightRuples;

    StagedBuilder expectedResultBuilder;

    LeftTupleSetsImpl actualResultLeftTuples;

    LeftTupleSets previousResultTuples;

    List<StagedBuilder> preStagedBuilders;
    List<StagedBuilder> postStagedBuilders;

    List<LeftTuple>  leftMemory;
    List<RightTuple> rightMemory;

    private boolean testLeftMemory;
    private boolean testRightMemory;

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
        this.leftTuples = new LeftTupleSetsImpl();
        this.rightRuples = new RightTupleSetsImpl(bm);
        this.preStagedBuilders = new ArrayList<StagedBuilder>();
        this.postStagedBuilders = new ArrayList<StagedBuilder>();

        this.bm.setStagedRightTuples(rightRuples);
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
        actualResultLeftTuples = new LeftTupleSetsImpl();
        
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
                    Assert.assertTrue("insert " + i, equals(expectedTuple, actualTuple));
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
                    Assert.assertTrue( "delete " + i, equals( expectedTuple, actualTuple ) );
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
                    Assert.assertTrue( "update " + i, equals( expectedTuple, actualTuple ) );
                    actualTuple = actualTuple.getStagedNext();
                    i++;
                }
                assertNull( "Update excpected more", actualTuple );
            } else if ( actual.getUpdateFirst() != null ) {
                fail( "Expected nothing, but update existed" );
            }
        }

    }

    public boolean equals(final LeftTuple expected, LeftTuple actual) {
        // we know the object is never null and always of the  type LeftTuple
        if ( expected == actual ) {
            return true;
        }

        if ( expected == null ) {
            if ( actual == null ) {
                return true;
            } else {
                return false;
            }
        } else if ( actual == null ) {
            if ( expected == null ) {
                return true;
            } else {
                return false;
            }
        }

        while ( actual.getLastHandle() == null ) {
            // skip exists, not, evals
            actual = actual.getParent();
        }

        // A LeftTuple is  only the same if it has the same hashCode, factId and parent
        if ( expected.hashCode() != actual.hashCode() ) {
            return false;
        }

        if ( expected.getLastHandle() != actual.getLastHandle() ) {
            return false;
        }

        return equals( expected.getParent(), actual.getParent() );
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
