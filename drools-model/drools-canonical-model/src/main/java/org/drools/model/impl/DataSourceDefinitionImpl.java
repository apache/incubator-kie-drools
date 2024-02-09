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

import org.drools.model.DataSourceDefinition;
import org.drools.model.DeclarationSource;

public class DataSourceDefinitionImpl implements DeclarationSource, DataSourceDefinition {

    public static final DataSourceDefinition DEFAULT = new DataSourceDefinitionImpl("DEFAULT", false);

    private final String name;
    private final boolean observable;

    public DataSourceDefinitionImpl(String name, boolean observable) {
        this.name = name;
        this.observable = observable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isObservable() {
        return observable;
    }
}
