package org.drools.builder.conf.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.JaxbConfiguration;

import com.sun.tools.xjc.Options;

public class JaxbConfigurationImpl implements JaxbConfiguration {
    
    private Options xjcOpts;
    private String systemId;
    
    private List<String> classes;
    
    public JaxbConfigurationImpl(Options xjcOpts,
                                 String systemId) {
        this.xjcOpts = xjcOpts;
        this.systemId = systemId;
        this.classes = new ArrayList<String>();
    }


    public Options getXjcOpts() {
        return xjcOpts;
    }
    
    
    public String getSystemId() {
        return systemId;
    }


    public List<String> getClasses() {
        return classes;
    }


    public void setClasses(List<String> classes) {
        this.classes = classes;
    }
   
}
