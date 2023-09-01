package org.drools.mvel.integrationtests.phreak;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TupleMemory;

import java.util.ArrayList;
import java.util.List;

public class LeftMemory {

    private Scenario scenario;

    public LeftMemory(Scenario scenario, Object... objects) {
        this.scenario = scenario;
        scenario.getLeftMemory().addAll( getLeftTuples( objects ) );
    }

    public List<LeftTuple> getLeftTuples(Object... objects) {
        BetaNode node = scenario.getBetaNode();
        BetaMemory bm = scenario.getBm();
        TupleMemory ltm = bm.getLeftTupleMemory();
        InternalWorkingMemory wm = scenario.getWorkingMemory();
        
        if ( objects == null ) {
            objects = new Object[0];
        }

        List<LeftTuple> list = new ArrayList<LeftTuple>();
        for ( Object object : objects ) {
            InternalFactHandle fh = (InternalFactHandle) wm.insert( object );
            LeftTuple expectedLeftTuple = node.createLeftTuple(fh, true );
            expectedLeftTuple.setPropagationContext( new PhreakPropagationContext() );
            list.add( expectedLeftTuple );
           
        }
        
        scenario.setTestLeftMemory( true );
        return list;
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
    
    public Scenario run() {
        return this.scenario.run();
    }   
}
