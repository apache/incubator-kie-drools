package org.drools.phreak;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.StagedLeftTuples;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleImpl;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.SegmentMemory;

public class BaseLeftTuplesBuilder<T extends BaseLeftTuplesBuilder> {
    protected InternalWorkingMemory wm;
    protected LeftTupleSink         sink;
    protected StagedLeftTuples      leftTuples;
    protected Scenario              scenario;
    
    private boolean testStagedInsert;
    private boolean testStagedDelete;
    private boolean testStagedUpdate;    

    public BaseLeftTuplesBuilder(Scenario scenario, StagedLeftTuples leftTuples) {
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
                LeftTuple leftTuple2 = sink.createLeftTuple( leftTuple1, new RightTuple( fh2 ), sink );

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
                LeftTuple leftTuple2 = sink.createLeftTuple( leftTuple1, new RightTuple( fh2 ), sink );

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
                LeftTuple leftTuple2 = sink.createLeftTuple( leftTuple1, new RightTuple( fh2 ), sink );

                leftTuples.addUpdate( leftTuple2 );                
            }
        }

        return (T) this ;
    }    
    
    StagedLeftTuples get() {
        return this.leftTuples;
    }

    public Scenario run() {
        return this.scenario.run();
    }
}