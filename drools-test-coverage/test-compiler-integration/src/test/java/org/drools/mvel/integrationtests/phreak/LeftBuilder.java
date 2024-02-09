/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests.phreak;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TupleFactory;
import org.drools.core.reteoo.TupleImpl;

public class LeftBuilder {
    /**
     * 
     */
    private InternalWorkingMemory wm;
    private LeftTupleSink    sink;
    private TupleSets leftTuples;
    private Scenario     scenario;

    public LeftBuilder(Scenario scenario) {
        this.scenario = scenario;
        this.wm = scenario.getWorkingMemory();
        this.sink = scenario.getBetaNode();
        this.leftTuples = scenario.getLeftTuples();
    }
    public LeftBuilder insert(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh        = (InternalFactHandle) wm.insert( object );
            TupleImpl          leftTuple = TupleFactory.createLeftTuple(sink, fh, true);
            leftTuple.setPropagationContext( new PhreakPropagationContext() );
            leftTuples.addInsert( leftTuple );
        }
        return this;
    }

    public LeftBuilder update(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh = wm.getFactHandle(object);
            TupleImpl leftTuple = fh.getFirstLeftTuple();
            leftTuple.setPropagationContext( new PhreakPropagationContext() );
            leftTuples.addUpdate( leftTuple );
        }
        return this;
    }

    public LeftBuilder delete(Object... objects) {
        for ( Object object : objects ) {
            InternalFactHandle fh = wm.getFactHandle(object);
            TupleImpl leftTuple = fh.getFirstLeftTuple();
            leftTuple.setPropagationContext( new PhreakPropagationContext() );
            leftTuples.addDelete( leftTuple );
        }
        return this;
    }

    TupleSets get() {
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
