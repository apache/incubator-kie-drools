package org.drools.phreak;

import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.StagedLeftTuples;
import org.drools.core.util.FastIterator;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.SegmentMemory;

import static org.junit.Assert.fail;

public class LeftMemory {

    private Scenario scenario;

    public LeftMemory(Scenario scenario, Object... objects) {
        this.scenario = scenario;
        scenario.getLeftMemory().addAll( getLeftTuples( objects ) );
    }

    public List<LeftTuple> getLeftTuples(Object... objects) {
        BetaNode node = scenario.getBetaNode();
        BetaMemory bm = scenario.getBm();
        LeftTupleMemory ltm = bm.getLeftTupleMemory();
        InternalWorkingMemory wm = scenario.getWorkingMemory();
        
        if ( objects == null ) {
            objects = new Object[0];
        }

        List<LeftTuple> list = new ArrayList<LeftTuple>();
        for ( Object object : objects ) {
            InternalFactHandle fh = (InternalFactHandle) wm.insert( object );
            LeftTuple expectedLeftTuple = node.createLeftTuple( fh, node, true );
            expectedLeftTuple.setPropagationContext( new PropagationContextImpl() );
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