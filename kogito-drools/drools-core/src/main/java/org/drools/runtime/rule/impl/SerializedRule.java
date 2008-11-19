package org.drools.runtime.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.definition.rule.Rule;

public class SerializedRule
    implements
    Rule,
    Externalizable {
    private String name;
    private String packageName;
    
    public SerializedRule() {
        
    }
    
    public SerializedRule(Rule rule) {
        this.name = rule.getName();
        this.packageName = rule.getPackageName();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( name );
        out.writeUTF( packageName );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        name = in.readUTF();
        packageName = in.readUTF();
    }

    public String getName() {
        return this.name;
    }

    public String getPackageName() {
        return this.packageName;
    }

}
