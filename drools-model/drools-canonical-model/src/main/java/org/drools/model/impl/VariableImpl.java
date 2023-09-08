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
package org.drools.model.impl;

import org.drools.model.Variable;

import static org.drools.model.impl.NamesGenerator.generateName;

public abstract class VariableImpl<T> implements Variable<T>, ModelComponent {
    public static final String GENERATED_VARIABLE_PREFIX = "GENERATED_";

    private final Class<T> type;
    private final String name;

    public VariableImpl(Class<T> type) {
        this(type, generateName("var"));
    }

    public VariableImpl(Class<T> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Variable " + name + " of type " + type;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Variable var = ( Variable ) o;
        return type.getName().equals( var.getType().getName() );
    }
}
