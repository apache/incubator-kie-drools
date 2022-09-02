/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.impl.ModelImpl;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.model.DSL.globalOf;

public class RulesFactory {

    private final RuleUnitDefinition unit;

    private final List<RuleDefinition> rules = new ArrayList<>();
    private final UnitGlobals globals;

    public RulesFactory(RuleUnitDefinition unit) {
        this.unit = unit;
        this.globals = new UnitGlobals(unit);
    }

    public RuleFactory rule() {
        return rule(UUID.randomUUID().toString());
    }

    public RuleFactory rule(String name) {
        RuleDefinition rule = new RuleDefinition(name, unit, globals);
        rules.add(rule);
        return rule;
    }

    Model toModel() {
        ModelImpl model = new ModelImpl();
        getGlobals().values().forEach(model::addGlobal);
        rules.stream().map(RuleDefinition::toRule).forEach(model::addRule);
        return model;
    }

    Map<Object, Global> getGlobals() {
        return globals.getGlobals();
    }

    public static class UnitGlobals {
        private final Map<Object, Global> globals = new IdentityHashMap<>();

        private final RuleUnitDefinition unit;

        public UnitGlobals(RuleUnitDefinition unit) {
            this.unit = unit;
        }

        Map<Object, Global> getGlobals() {
            return globals;
        }

        public <T> Global asGlobal(T globalObject) {
            return globals.computeIfAbsent(globalObject, o -> globalOf(o.getClass(), unit.getClass().getCanonicalName(), UUID.randomUUID().toString()));
        }
    }
}
