package org.drools.reteoo;

import java.util.Iterator;

import org.drools.ObjectFilter;
import org.drools.QueryResults;
import org.drools.StatelessSessionResult;
import org.drools.WorkingMemory;

public class ReteStatelessSessionResult implements StatelessSessionResult {
    private WorkingMemory workingMemory;
    
    public ReteStatelessSessionResult(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public QueryResults getQueryResults(String query) {
        return this.workingMemory.getQueryResults( query );
    }

    public Iterator iterateObjects() {
        return this.workingMemory.iterateObjects();
    }

    public Iterator iterateObjects(ObjectFilter filter) {
        return this.workingMemory.iterateObjects(filter);
    }

}
