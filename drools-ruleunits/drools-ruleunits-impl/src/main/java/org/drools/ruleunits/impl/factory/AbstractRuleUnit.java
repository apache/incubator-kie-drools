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

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnits;

public abstract class AbstractRuleUnit<T extends RuleUnitData> implements RuleUnit<T> {

    private final String id;
    protected final RuleUnits ruleUnits;

    public AbstractRuleUnit(String id, RuleUnits ruleUnits) {
        this.id = id;
        this.ruleUnits = ruleUnits;
    }

    protected abstract RuleUnitInstance<T> internalCreateInstance(T data);

    @Override
    public String id() {
        return id;
    }

    @Override
    public RuleUnitInstance<T> createInstance(T data, String name) {
        RuleUnitInstance<T> instance = internalCreateInstance(data);
        if (name != null) {
            ruleUnits.register(name, instance);
        }
        return instance;
    }
}
