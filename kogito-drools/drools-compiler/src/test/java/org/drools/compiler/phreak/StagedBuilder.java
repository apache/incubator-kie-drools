package org.drools.compiler.phreak;

import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.SegmentMemory;

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
