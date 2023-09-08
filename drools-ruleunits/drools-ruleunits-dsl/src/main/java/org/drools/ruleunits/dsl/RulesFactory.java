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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.impl.ModelImpl;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.model.DSL.globalOf;

/**
 * The starting point to create and define rules through the rule unit Java DSL.
 */
public class RulesFactory {

    private final RuleUnitDefinition unit;

    private final List<RuleDefinition> rules = new ArrayList<>();
    private final UnitGlobals globals;

    public RulesFactory(RuleUnitDefinition unit) {
        this.unit = unit;
        this.globals = new UnitGlobals(unit);
    }

    /**
     * Creates a new rule and automatically adds it to the ones belonging to the {@link RuleUnitDefinition}.
     */
    public RuleFactory rule() {
        return rule(UUID.randomUUID().toString());
    }

    /**
     * Creates a new rule with the given name and automatically adds it to the ones belonging to the {@link RuleUnitDefinition}.
     */
    public RuleFactory rule(String name) {
        RuleDefinition rule = new RuleDefinition(name, unit, globals);
        rules.add(rule);
        return rule;
    }

    Model toModel() {
        ModelImpl model = new ModelImpl();
        globals.getGlobals().values().forEach(model::addGlobal);
        rules.stream().map(RuleDefinition::toRule).forEach(model::addRule);
        return model;
    }

    UnitGlobalsResolver getUnitGlobalsResolver() {
        return new UnitGlobalsResolver(globals.fieldByGlobal);
    }

    public static class UnitGlobals {
        private final Map<Object, Global> globals = new IdentityHashMap<>();

        private final Map<String, RuleDefinition.FieldDefinition> fieldByGlobal = new HashMap<>();

        private final String unitName;

        private UnitGlobals(RuleUnitDefinition unit) {
            this.unitName = unit.getClass().getCanonicalName();
        }

        private Map<Object, Global> getGlobals() {
            return globals;
        }

        public <T> Global asGlobal(Supplier<RuleDefinition.FieldDefinition> globalField, T globalObject) {
            return globals.computeIfAbsent(globalObject, o -> registerGlobal(globalField, o));
        }

        private Global<?> registerGlobal(Supplier<RuleDefinition.FieldDefinition> globalField, Object globalObject) {
            String globalUUID = UUID.randomUUID().toString();
            fieldByGlobal.put(globalUUID, globalField.get());
            return globalOf(globalObject.getClass(), unitName, globalUUID);
        }
    }
}
