/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.impl.factory;

import java.util.HashMap;
import java.util.Map;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.impl.InternalRuleUnit;
import org.drools.ruleunits.impl.RuleUnits;

public abstract class AbstractRuleUnits implements RuleUnits {

    private Map<String, RuleUnitInstance<?>> unitRegistry = new HashMap<>();

    @Override
    public <T extends RuleUnitData> RuleUnit<T> create(Class<T> clazz) {
        return (RuleUnit<T>) create(clazz.getCanonicalName());
    }

    protected abstract RuleUnit<?> create(String fqcn);

    @Override
    public void register(String name, RuleUnitInstance<?> unitInstance) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot register a RuleUnitInstance with a null name");
        }
        unitRegistry.put(name, unitInstance);
    }

    @Override
    public RuleUnitInstance<?> getRegisteredInstance(String name) {
        return unitRegistry.get(name);
    }


    public static class DummyRuleUnits extends AbstractRuleUnits {

        public static final DummyRuleUnits INSTANCE = new DummyRuleUnits();

        @Override
        protected RuleUnit<?> create(String fqcn) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void register(InternalRuleUnit<?> unit) {
            // ignore
        }
    }
}
