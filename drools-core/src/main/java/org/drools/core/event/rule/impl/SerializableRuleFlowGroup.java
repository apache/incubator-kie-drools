package org.drools.core.event.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.kie.api.runtime.rule.RuleFlowGroup;

public class SerializableRuleFlowGroup implements RuleFlowGroup, Externalizable {
    
    private String name;

    // This should be used just for deserialization purposes.
    public SerializableRuleFlowGroup() { }
    
    SerializableRuleFlowGroup(RuleFlowGroup ruleFlowGroup) {
        this.name = ruleFlowGroup.getName();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( this.name );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.name = in.readUTF();
    }
    
    public String getName() {
        return this.name;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

}
