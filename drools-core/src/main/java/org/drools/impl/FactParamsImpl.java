package org.drools.impl;

import java.util.Collection;
import java.util.Map;

import org.drools.runtime.FactParams;

public class FactParamsImpl implements FactParams {
    Map<String, ? > in;
    Map<String, ? > inOut;
    Collection<String> out;
    
    public void setIn(Map<String, ? > in) {
        this.in = in;
    }
    
    public Map<String, ? > getIn() {
        return in;
    }    

    public void setInOut(Map<String, ? > inOut) {
        this.inOut = inOut;
    }
    
    public Map<String, ? > getInOut() {
        return inOut;
    }    

    public void setOut(Collection<String> out) {
        this.out = out;
    }

    public Collection<String> getOut() {
        return out;
    }

}
