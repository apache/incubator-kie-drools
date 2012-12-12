package org.drools.phreak;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.StagedLeftTuples;
import org.drools.common.StagedRightTuples;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleSink;
import org.drools.reteoo.SegmentMemory;

public class RightBuilder {
    private InternalWorkingMemory wm;
    private RightTupleSink    sink;
    private StagedRightTuples rightTuples;
    private Scenario      scenario;

    public RightBuilder(Scenario scenario) {
        this.wm = scenario.getWorkingMemory();
        this.scenario = scenario;
        this.sink = scenario.getBetaNode();
        this.rightTuples = scenario.getRightTuples();            
    }

    public RightBuilder insert(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh = (InternalFactHandle) wm.insert( object );
            RightTuple rightTuple =  new RightTuple( fh, sink );
            rightTuple.setPropagationContext( new PropagationContextImpl() );
            rightTuples.addInsert( rightTuple );
        }
        return this;
    }

    public RightBuilder update(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh = (InternalFactHandle) wm.insert( object );
            RightTuple rightTuple = fh.getFirstRightTuple();
            rightTuple.setPropagationContext( new PropagationContextImpl() );
            rightTuples.addUpdate( rightTuple );
        }
        return this;
    }

    public RightBuilder delete(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh = (InternalFactHandle) wm.insert( object );
            RightTuple rightTuple = fh.getFirstRightTuple();
            rightTuple.setPropagationContext( new PropagationContextImpl() );
            rightTuples.addDelete( rightTuple );
        }
        return this;
    }

    StagedRightTuples get() {
        return this.rightTuples;
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