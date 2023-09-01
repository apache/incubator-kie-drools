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

import java.util.Map;
import java.util.function.Consumer;

import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.SingletonStore;
import org.drools.ruleunits.dsl.util.DataSourceDefinition;
import org.drools.ruleunits.impl.NamedRuleUnitData;

public class SyntheticRuleUnit implements RuleUnitDefinition, NamedRuleUnitData {

    private final String unitName;

    private final Map<String, DataSourceDefinition> dataSources;

    private final Map<String, Object> globals;

    private final Consumer<RulesFactory> rulesDefinition;

    SyntheticRuleUnit(String unitName, Map<String, DataSourceDefinition> dataSources, Map<String, Object> globals, Consumer<RulesFactory> rulesDefinition) {
        this.unitName = unitName;
        this.dataSources = dataSources;
        this.globals = globals;
        this.rulesDefinition = rulesDefinition;
    }

    @Override
    public String getUnitName() {
        return unitName;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesDefinition.accept(rulesFactory);
    }

    public Map<String, DataSourceDefinition> getDataSourceDefinitions() {
        return dataSources;
    }

    public Map<String, Object> getGlobals() {
        return globals;
    }

    public <T> DataStore<T> getDataStore(String name, Class<T> clazz) {
        return (DataStore<T>) dataSources.get(name).getDataSource();
    }

    public <T> DataStream<T> getDataStream(String name, Class<T> clazz) {
        return (DataStream<T>) dataSources.get(name).getDataSource();
    }

    public <T> SingletonStore<T> getSingletonStore(String name, Class<T> clazz) {
        return (SingletonStore<T>) dataSources.get(name).getDataSource();
    }

    public <T> T getGlobal(String name, Class<T> clazz) {
        return (T)globals.get(name);
    }
}
