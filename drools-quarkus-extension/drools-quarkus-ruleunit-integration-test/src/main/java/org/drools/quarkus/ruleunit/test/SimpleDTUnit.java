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
package org.drools.quarkus.ruleunit.test;

import java.util.concurrent.atomic.AtomicReference;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.SingletonStore;
import org.drools.ruleunits.dsl.RuleUnitDefinition;
import org.drools.ruleunits.dsl.RulesFactory;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.GREATER_OR_EQUAL;
import static org.drools.model.Index.ConstraintType.LESS_THAN;

public class SimpleDTUnit implements RuleUnitDefinition {
    
    private SingletonStore<Number> age;
    private SingletonStore<Boolean> incidents;
    
    private AtomicReference<Number> basePrice;

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesFactory.rule()
                    .on(age)
                    .filter(LESS_THAN, 21) // when no extractor is provided "this" is implicit
                    .join(rule -> rule.on(incidents).filter(EQUAL, false))
                    .execute(basePrice, (r, c1, c2) -> r.set(800));
        rulesFactory.rule()
                    .on(age)
                    .filter(LESS_THAN, 21) 
                    .join(rule -> rule.on(incidents).filter(EQUAL, true))
                    .execute(basePrice, (r, c1, c2) -> r.set(1000));
        rulesFactory.rule()
                    .on(age)
                    .filter(GREATER_OR_EQUAL, 21)
                    .join(rule -> rule.on(incidents).filter(EQUAL, false))
                    .execute(basePrice, (r, c1, c2) -> r.set(500));
        rulesFactory.rule()
                    .on(age)
                    .filter(GREATER_OR_EQUAL, 21) 
                    .join(rule -> rule.on(incidents).filter(EQUAL, true))
                    .execute(basePrice, (r, c1, c2) -> r.set(600));
    }

    public SimpleDTUnit() {
        this(DataSource.createSingleton(), DataSource.createSingleton(), null);
    }

    public SimpleDTUnit(SingletonStore<Number> age, SingletonStore<Boolean> incidents, Number basePrice) {
        this.age = age;
        this.incidents = incidents;
        this.basePrice = new AtomicReference<>(basePrice);
    }

    public SingletonStore<Number> getAge() {
        return age;
    }

    public SingletonStore<Boolean> getIncidents() {
        return incidents;
    }

    public AtomicReference<Number> getBasePrice() {
        return basePrice;
    }

    public void setAge(SingletonStore<Number> age) {
        this.age = age;
    }

    public void setIncidents(SingletonStore<Boolean> incidents) {
        this.incidents = incidents;
    }

    public void setBasePrice(AtomicReference<Number> basePrice) {
        this.basePrice = basePrice;
    }
}