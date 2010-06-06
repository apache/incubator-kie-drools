package org.drools.builder;

import java.util.List;

import com.sun.tools.xjc.Options;

/**
 * 
 */
public interface JaxbConfiguration
    extends
    ResourceConfiguration {

    public Options getXjcOpts();
    
    
    public String getSystemId();


    public List<String> getClasses();
    
}
    
