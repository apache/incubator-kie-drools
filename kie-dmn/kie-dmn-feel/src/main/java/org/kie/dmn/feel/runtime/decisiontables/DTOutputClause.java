/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.decisiontables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;

public class DTOutputClause implements DecisionTable.OutputClause  {
    private final String          name;
    private final String          id;
    private final String          defaultValue;
    private final List<UnaryTest> outputValues;
    private final Type            type;
    private final boolean collection;

    public DTOutputClause(String name, List<UnaryTest> outputValues) {
        this( name, null, outputValues, null );
    }
    
    public DTOutputClause(String name, String id, List<UnaryTest> outputValues, String defaultValue) {
        this(name, id, outputValues, defaultValue, BuiltInType.UNKNOWN, false);
    }

    /**
     * @param isCollection should consider the output can be a collection of feelType; helpful for expressing a DMN isCollection itemDefinition attribute. 
     */
    public DTOutputClause(String name, String id, List<UnaryTest> outputValues, String defaultValue, Type feelType, boolean isCollection) {
        this.name = name;
        this.id = id;
        this.defaultValue = defaultValue;

        if (outputValues != null) {
            this.outputValues = Collections.unmodifiableList(new ArrayList<UnaryTest>(outputValues));
        } else {
            this.outputValues = Collections.emptyList();
        }
        this.type = feelType;
        this.collection = isCollection;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<UnaryTest> getOutputValues() {
        return outputValues;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Type getType() {
        return type;
    }

    public boolean isCollection() {
        return collection;
    }
}
