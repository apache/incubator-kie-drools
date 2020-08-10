package org.jbpm.process.codegen;

import java.util.Map;


public class XXXTaskInput {

    private String _id;
    private String _name;
    
    public void setId(String id) {
        this._id = id;
    }
    
    public String getId() {
        return this._id;
    }
    
    public void setName(String name) {
        this._name = name;
    }
    
    public String getName() {
        return this._name;
    }

    public static XXXTaskInput from(org.kie.kogito.process.WorkItem workItem) {
        
    }
}