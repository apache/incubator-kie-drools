package org.drools.ruleflow.common.core;

import org.drools.ruleflow.common.datatype.DataType;

public interface ParameterDefinition {
    
    String getName();
    void setName(String name);
    
    DataType getType();
    void setType(DataType type);
    
}
