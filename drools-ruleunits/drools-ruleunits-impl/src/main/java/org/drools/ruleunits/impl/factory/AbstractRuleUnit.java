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
package org.drools.ruleunits.impl.factory;

import java.util.function.Function;

import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.drools.ruleunits.impl.InternalRuleUnit;
import org.drools.ruleunits.api.RuleUnits;

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

    protected RuleUnitInstance<T> internalCreateInstance(T data) {
        return internalCreateInstance(data, RuleUnitProvider.get().newRuleConfig());
    }

    protected abstract RuleUnitInstance<T> internalCreateInstance(T data, RuleConfig ruleConfig);

    @Override
    public Class<T> getRuleUnitDataClass() {
        return ruleUnitDataClass;
    }

    @Override
    public RuleUnitInstance<T> createInstance(T data) {
        return createInstance(data, null, RuleUnitProvider.get().newRuleConfig());
    }

    @Override
    public RuleUnitInstance<T> createInstance(T data, String name) {
        return createInstance(data, name, RuleUnitProvider.get().newRuleConfig());
    }

    @Override
    public RuleUnitInstance<T> createInstance(T data, RuleConfig ruleConfig) {
        return createInstance(data, null, ruleConfig);
    }

    @Override
    public RuleUnitInstance<T> createInstance(T data, String name, RuleConfig ruleConfig) {
        RuleUnitInstance<T> instance = internalCreateInstance(data, ruleConfig);
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
