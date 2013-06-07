package org.jbpm.bpmn2.objects;

import java.io.Serializable;

public class StringHolder implements Serializable {

    private static final long serialVersionUID = -4824571232463620777L;
    private String val = null;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
    
    
}
