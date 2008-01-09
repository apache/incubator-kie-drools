package org.drools.process.core;

import org.drools.process.core.datatype.DataType;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface ParameterDefinition {
    
    String getName();
    void setName(String name);
    
    DataType getType();
    void setType(DataType type);
    
}
