package org.drools.phreak;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.LeftTupleSets;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.SegmentMemory;

public class LeftBuilder {
    /**
     * 
     */
    private InternalWorkingMemory wm;
    private LeftTupleSink    sink;
    private LeftTupleSets leftTuples;
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
            LeftTuple leftTuple = sink.createLeftTuple( fh, sink, true );
            leftTuple.setPropagationContext( new PropagationContextImpl() );
            leftTuples.addInsert( leftTuple );
        }
        return this;
    }

    public LeftBuilder update(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh = (InternalFactHandle) wm.getFactHandle( object );
            LeftTuple leftTuple = fh.getFirstLeftTuple();
            leftTuple.setPropagationContext( new PropagationContextImpl() );
            leftTuples.addUpdate( leftTuple );
        }
        return this;
    }

    public LeftBuilder delete(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh = (InternalFactHandle) wm.getFactHandle( object );
            LeftTuple leftTuple = fh.getFirstLeftTuple();
            leftTuple.setPropagationContext( new PropagationContextImpl() );
            leftTuples.addDelete( leftTuple );
        }
        return this;
    }

    LeftTupleSets get() {
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