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

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RightTupleImpl;

public class BaseLeftTuplesBuilder<T extends BaseLeftTuplesBuilder> {
    protected InternalWorkingMemory wm;
    protected LeftTupleSink         sink;
    protected TupleSets<LeftTuple>  leftTuples;
    protected Scenario              scenario;
    
    private boolean testStagedInsert;
    private boolean testStagedDelete;
    private boolean testStagedUpdate;    

    public BaseLeftTuplesBuilder(Scenario scenario, TupleSets<LeftTuple> leftTuples) {
        this.wm = scenario.getWorkingMemory();
        this.scenario = scenario;
        this.sink = scenario.getSinkNode();
        this.leftTuples = leftTuples;
    }
 
    public boolean isTestStagedInsert() {
        return testStagedInsert;
    }

    public boolean isTestStagedDelete() {
        return testStagedDelete;
    }

    public boolean isTestStagedUpdate() {
        return testStagedUpdate;
    }    

    public T insert(Object... objects) {        
        this.testStagedInsert = true;
        if ( objects == null ) {
            objects = new Object[0];
        }
        
        for ( int i = 0; i < objects.length; i++ ) {
            if ( !(objects[i] instanceof Pair) ) {
                Object o1 = objects[i];
                InternalFactHandle fh1 = (InternalFactHandle) wm.getFactHandle( o1 );
                LeftTuple leftTuple = new LeftTupleImpl( fh1, sink, true );
                leftTuples.addInsert( leftTuple );
            } else {
                Pair p = (Pair )objects[i];
                
                InternalFactHandle fh1 = (InternalFactHandle) wm.getFactHandle( p.getO1() );
                LeftTuple leftTuple1 = new LeftTupleImpl( fh1, sink, true );
                
                InternalFactHandle fh2 = (InternalFactHandle) wm.getFactHandle(  p.getO2()  );
                LeftTuple leftTuple2 = sink.createLeftTuple( leftTuple1, new RightTupleImpl( fh2 ), sink );

                leftTuples.addInsert( leftTuple2 );                
            }
        }

        return (T) this ;
    }

    public T delete(Object... objects) {
        this.testStagedDelete = true;
        if ( objects == null ) {
            objects = new Object[0];
        }
        
        for ( int i = 0; i < objects.length; i++ ) {
            if ( !(objects[i] instanceof Pair) ) {
                Object o1 = objects[i];
                InternalFactHandle fh1 = (InternalFactHandle) wm.getFactHandle( o1 );
                LeftTuple leftTuple = new LeftTupleImpl( fh1, sink, true );
                leftTuples.addDelete( leftTuple );
            } else {
                Pair p = (Pair )objects[i];
                
                InternalFactHandle fh1 = (InternalFactHandle) wm.getFactHandle( p.getO1() );
                LeftTuple leftTuple1 = new LeftTupleImpl( fh1, sink, true );
                
                InternalFactHandle fh2 = (InternalFactHandle) wm.getFactHandle(  p.getO2()  );
                LeftTuple leftTuple2 = sink.createLeftTuple( leftTuple1, new RightTupleImpl( fh2 ), sink );

                leftTuples.addDelete( leftTuple2 );                
            }
        }

        return (T) this ;
    }

    public T update(Object... objects) {
        this.testStagedUpdate = true;
        if ( objects == null ) {
            objects = new Object[0];
        }
        
        for ( int i = 0; i < objects.length; i++ ) {
            if ( !(objects[i] instanceof Pair) ) {
                Object o1 = objects[i];
                InternalFactHandle fh1 = (InternalFactHandle) wm.getFactHandle( o1 );
                LeftTuple leftTuple = new LeftTupleImpl( fh1, sink, true );
                leftTuples.addUpdate( leftTuple );
            } else {
                Pair p = (Pair )objects[i];
                
                InternalFactHandle fh1 = (InternalFactHandle) wm.getFactHandle( p.getO1() );
                LeftTuple leftTuple1 = new LeftTupleImpl( fh1, sink, true );
                
                InternalFactHandle fh2 = (InternalFactHandle) wm.getFactHandle(  p.getO2()  );
                LeftTuple leftTuple2 = sink.createLeftTuple( leftTuple1, new RightTupleImpl( fh2 ), sink );

                leftTuples.addUpdate( leftTuple2 );                
            }
        }

        return (T) this ;
    }

    TupleSets<LeftTuple> get() {
        return this.leftTuples;
    }

    public Scenario run() {
        return this.scenario.run();
    }
}
