package org.drools.mvel.integrationtests.phreak;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.SegmentMemory;

public class LeftBuilder {
    /**
     * 
     */
    private InternalWorkingMemory wm;
    private LeftTupleSink    sink;
    private TupleSets<LeftTuple> leftTuples;
    private Scenario     scenario;

    public LeftBuilder(Scenario scenario) {
        this.scenario = scenario;
        this.wm = scenario.getWorkingMemory();
        this.sink = scenario.getBetaNode();
        this.leftTuples = scenario.getLeftTuples();
    }
    public LeftBuilder insert(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh = (InternalFactHandle) wm.insert( object );
            LeftTuple leftTuple = sink.createLeftTuple(fh, true );
            leftTuple.setPropagationContext( new PhreakPropagationContext() );
            leftTuples.addInsert( leftTuple );
        }
        return this;
    }

    public LeftBuilder update(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh = wm.getFactHandle(object);
            LeftTuple leftTuple = fh.getFirstLeftTuple();
            leftTuple.setPropagationContext( new PhreakPropagationContext() );
            leftTuples.addUpdate( leftTuple );
        }
        return this;
    }

    public LeftBuilder delete(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh = wm.getFactHandle(object);
            LeftTuple leftTuple = fh.getFirstLeftTuple();
            leftTuple.setPropagationContext( new PhreakPropagationContext() );
            leftTuples.addDelete( leftTuple );
        }
        return this;
    }

    TupleSets<LeftTuple> get() {
        return this.leftTuples;
    }

    public RightBuilder right() {
        return new RightBuilder( scenario );
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
