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
package org.drools.ruleunits.dsl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.util.DataSourceDefinition;

public class SyntheticRuleUnitBuilder {
    private final String unitName;

    private Map<String, DataSourceDefinition> dataSources = new HashMap<>();
    private Map<String, Object> globals = new HashMap<>();

    private SyntheticRuleUnitBuilder(String unitName) {
        this.unitName = unitName;
    }

    public static SyntheticRuleUnitBuilder build(String unitName) {
        return new SyntheticRuleUnitBuilder(unitName);
    }

    public SyntheticRuleUnitBuilder registerDataSource(String name, DataSource dataSource, Class<?> dataClass) {
        dataSources.put(name, new DataSourceDefinition(dataSource, dataClass));
        return this;
    }

    public SyntheticRuleUnitBuilder registerGlobal(String name, Object global) {
        globals.put(name, global);
        return this;
    }

    public SyntheticRuleUnit defineRules(Consumer<RulesFactory> rulesDefinition) {
        return new SyntheticRuleUnit(unitName, dataSources, globals, rulesDefinition);
    }
}
