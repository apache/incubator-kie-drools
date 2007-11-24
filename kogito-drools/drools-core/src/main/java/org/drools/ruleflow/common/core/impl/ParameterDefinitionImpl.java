package org.drools.ruleflow.common.core.impl;

import java.io.Serializable;

import org.drools.ruleflow.common.core.ParameterDefinition;
import org.drools.ruleflow.common.datatype.DataType;

public class ParameterDefinitionImpl implements ParameterDefinition, Serializable {
   
    private static final long serialVersionUID = 3977297720245237814L;       private String name;    private DataType type;        public ParameterDefinitionImpl(String name, DataType type) {        setName(name);        setType(type);    }        public String getName() {        return name;    }        public void setName(String name) {        if (name == null) {            throw new IllegalArgumentException("Name cannot be null");        }        this.name = name;    }        public DataType getType() {        return type;    }        public void setType(DataType type) {        if (type == null) {            throw new IllegalArgumentException("Data type cannot be null");        }        this.type = type;    }        public String toString() {        return name;    }}