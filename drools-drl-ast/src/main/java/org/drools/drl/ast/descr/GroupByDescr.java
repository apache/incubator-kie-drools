package org.drools.drl.ast.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class GroupByDescr extends AccumulateDescr {
    private String groupingKey;
    private String groupingFunction;

    public String getGroupingFunction() {
        return groupingFunction;
    }

    public void setGroupingFunction(String groupingFunction) {
        this.groupingFunction = groupingFunction;
    }

    public String getGroupingKey() {
        return groupingKey;
    }

    public void setGroupingKey(String groupingKey) {
        this.groupingKey = groupingKey;
    }

    public void readExternal(ObjectInput in ) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        groupingFunction = (String) in.readObject();
        groupingKey = (String) in.readObject();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( groupingFunction );
        out.writeObject( groupingKey );
    }
}
