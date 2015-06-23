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

import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleMemory;
import org.drools.core.reteoo.SegmentMemory;

import static org.junit.Assert.fail;

public class RightMemory {

    private Scenario scenario;

    public RightMemory(Scenario scenario, Object... objects) {
        this.scenario = scenario;
        scenario.getRightMemory().addAll( getRightTuples( objects ) );
    }

    public List<RightTuple> getRightTuples(Object... objects) {
        BetaNode node = scenario.getBetaNode();
        BetaMemory bm = scenario.getBm();
        RightTupleMemory rtm = bm.getRightTupleMemory();
        InternalWorkingMemory wm = scenario.getWorkingMemory();

        if ( objects == null ) {
            objects = new Object[0];
        }
        
        List<RightTuple> rightTuples = new ArrayList<RightTuple>();
        for ( Object object : objects ) {
            InternalFactHandle fh = (InternalFactHandle) wm.insert( object );
            RightTuple expectedRightTuple = new RightTuple( fh, node ); //node.createLeftTuple( fh, node, true );
            expectedRightTuple.setPropagationContext( new PhreakPropagationContext() );
            rightTuples.add( expectedRightTuple );
        }
        
        scenario.setTestRightMemory( true );
        
        return rightTuples;
    }
    
    public StagedBuilder result() {
        StagedBuilder stagedBuilder = new StagedBuilder( scenario, null );
        scenario.setExpectedResultBuilder( stagedBuilder );
        return stagedBuilder; 
    }    
    
    public StagedBuilder preStaged(SegmentMemory sm) {
        StagedBuilder stagedBuilder = new StagedBuilder( scenario, sm );
        scenario.addPreStagedBuilder( stagedBuilder );
        return stagedBuilder;        
    }    
    
    public StagedBuilder postStaged(SegmentMemory sm) {
        StagedBuilder stagedBuilder = new StagedBuilder( scenario, sm );
        scenario.addPostStagedBuilder( stagedBuilder );
        return stagedBuilder;        
    }     

    public Scenario run() {
        return this.scenario.run();
    }
    
}
