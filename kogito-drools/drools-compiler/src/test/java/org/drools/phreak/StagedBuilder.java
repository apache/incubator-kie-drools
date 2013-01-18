package org.drools.phreak;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.LeftTupleSets;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleImpl;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.SegmentMemory;

public class StagedBuilder extends BaseLeftTuplesBuilder<StagedBuilder> {
    private SegmentMemory sm;
    
    public StagedBuilder(Scenario scenario, SegmentMemory sm ) {
        super(scenario, new LeftTupleSets() );
        this.sm = sm;
    }
    
    public LeftMemory left(Object... objects) {
        return new LeftMemory( scenario, objects );
    }

    public RightMemory right(Object... objects) {
        return new RightMemory( scenario, objects );
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
    
    public SegmentMemory getSegmentMemory() {
        return this.sm;
    }  
    

}