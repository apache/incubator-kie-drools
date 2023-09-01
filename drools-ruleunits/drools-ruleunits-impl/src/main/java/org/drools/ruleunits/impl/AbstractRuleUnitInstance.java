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
package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;

public abstract class AbstractRuleUnitInstance<E, T extends RuleUnitData> implements RuleUnitInstance<T> {

    private final T unitMemory;
    private final RuleUnit<T> unit;
    protected final E evaluator;
    protected RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();

    public AbstractRuleUnitInstance(RuleUnit<T> unit, T unitMemory, E evaluator) {
        this.unit = unit;
        this.evaluator = evaluator;
        this.unitMemory = unitMemory;
        bind(evaluator, unitMemory);
    }

    public AbstractRuleUnitInstance(RuleUnit<T> unit, T unitMemory, E evaluator, RuleConfig ruleConfig) {
        this.unit = unit;
        this.evaluator = evaluator;
        this.unitMemory = unitMemory;
        this.ruleConfig = ruleConfig;
        addEventListeners();
        bind(evaluator, unitMemory);
    }

    @Override
    public RuleUnit<T> unit() {
        return unit;
    }

    public T ruleUnitData() {
        return unitMemory;
    }

    public E getEvaluator() {
        return evaluator;
    }

    protected void addEventListeners() {
        // no-op by default
    }

    protected abstract void bind(E evaluator, T workingMemory);
}
