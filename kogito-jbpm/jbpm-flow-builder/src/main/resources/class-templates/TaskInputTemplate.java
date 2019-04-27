package org.jbpm.process.codegen;

import java.util.Map;

public class XXXTaskInput {

    private Long _id;
    private String _name;
    
    public void setId(Long id) {
        this._id = id;
    }
    
    public Long getId() {
        return this._id;
    }
    
    public void setName(String name) {
        this._name = name;
    }
    
    public String getName() {
        return this._name;
    }

    public static XXXTaskInput fromMap(Long id, String name,  Map<String, Object> params) {
        
    }
}