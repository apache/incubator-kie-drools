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

import java.util.function.Function;

import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.impl.RuleUnits;
import org.drools.ruleunits.impl.InternalRuleUnit;

public abstract class AbstractRuleUnit<T extends RuleUnitData> implements InternalRuleUnit<T> {

    private final Class<T> ruleUnitDataClass;
    protected final RuleUnits ruleUnits;

    protected Function<ReteEvaluator, ReteEvaluator> evaluatorConfigurator = Function.identity();

    public AbstractRuleUnit(Class<T> ruleUnitDataClass) {
        this(ruleUnitDataClass, AbstractRuleUnits.DummyRuleUnits.INSTANCE);
    }

    public AbstractRuleUnit(Class<T> ruleUnitDataClass, RuleUnits ruleUnits) {
        this.ruleUnitDataClass = ruleUnitDataClass;
        this.ruleUnits = ruleUnits == null ? AbstractRuleUnits.DummyRuleUnits.INSTANCE : ruleUnits;
    }

    protected abstract RuleUnitInstance<T> internalCreateInstance(T data);

    @Override
    public Class<T> getRuleUnitDataClass() {
        return ruleUnitDataClass;
    }

    @Override
    public RuleUnitInstance<T> createInstance(T data) {
        return createInstance(data, null);
    }

    @Override
    public RuleUnitInstance<T> createInstance(T data, String name) {
        RuleUnitInstance<T> instance = internalCreateInstance(data);
        if (name != null) {
            ruleUnits.register(name, instance);
        }
        return instance;
    }

    @Override
    public void setEvaluatorConfigurator(Function<ReteEvaluator, ReteEvaluator> evaluatorConfigurator) {
        this.evaluatorConfigurator = evaluatorConfigurator;
    }
}
