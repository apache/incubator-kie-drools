package org.drools.ruleflow.core.impl;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;

import org.drools.ruleflow.common.datatype.IDataType;
import org.drools.ruleflow.common.datatype.impl.type.UndefinedDataType;
import org.drools.ruleflow.core.IVariable;

/**
 * Default implementation of a variable.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class Variable implements IVariable, Serializable {

    private static final long serialVersionUID = 320L;

    private String name;
    private IDataType type;
    private Serializable value;    
    
    public Variable() {
    	type = UndefinedDataType.getInstance();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public IDataType getType() {
        return type;
    }
    
    public void setType(IDataType type) {
    	if (type == null) {
    		throw new IllegalArgumentException("type is null");
    	}
        this.type = type;
    }
    
    public Serializable getValue() {
        return value;
    }
    
    public void setValue(Serializable value) {
    	if (this.type.verifyDataType(value)) {
    		this.value = value;
    	} else {
    		StringBuffer sb = new StringBuffer();
    		sb.append("Value <");
    		sb.append(value);
    		sb.append("> is not valid for datatype: ");
    		sb.append(type);
    		throw new IllegalArgumentException(sb.toString());
    	}
    }
    
    public String toString() {
        return name;
    }
}
