/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.common.TupleSetsImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.SegmentMemory;

public class StagedBuilder extends BaseLeftTuplesBuilder<StagedBuilder> {
    private SegmentMemory sm;
    
    public StagedBuilder(Scenario scenario, SegmentMemory sm ) {
        super(scenario, new TupleSetsImpl<LeftTuple>() );
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
