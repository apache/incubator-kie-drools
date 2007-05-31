package org.drools.ruleflow.core;

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

import org.drools.ruleflow.common.datatype.DataType;

/**
 * Represents a global variable used in a RuleFlow.
 * A variable has a name (should be unique for this process), a datatype
 * and possibly an initial value.  
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface Variable {

    String getName();

    void setName(String name);

    DataType getType();

    void setType(DataType type);

    Serializable getValue();

    void setValue(Serializable value);

}
