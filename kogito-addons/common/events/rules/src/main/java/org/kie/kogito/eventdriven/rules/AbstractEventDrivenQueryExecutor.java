/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.eventdriven.rules;

import java.util.function.Function;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.kie.kogito.event.DataEvent;

public abstract class AbstractEventDrivenQueryExecutor<D extends RuleUnitData> implements EventDrivenQueryExecutor<D> {

    private RuleUnit<D> ruleUnit;
    private String queryName;
    private Function<RuleUnitInstance<D>, Object> queryFunction;
    private Class<D> objectClass;

    protected AbstractEventDrivenQueryExecutor() {
    }

    protected AbstractEventDrivenQueryExecutor(EventDrivenRulesController controller, RuleUnit<D> ruleUnit, String queryName, Function<RuleUnitInstance<D>, Object> queryFunction,
            Class<D> objectClass) {
        setup(controller, ruleUnit, queryName, queryFunction, objectClass);
    }

    protected void setup(EventDrivenRulesController controller, RuleUnit<D> ruleUnit, String queryName, Function<RuleUnitInstance<D>, Object> queryFunction, Class<D> objectClass) {
        this.ruleUnit = ruleUnit;
        this.queryName = queryName;
        this.queryFunction = queryFunction;
        this.objectClass = objectClass;
        controller.subscribe(this, objectClass);
    }

    @Override
    public String getRuleUnitId() {
        return objectClass.getCanonicalName();
    }

    @Override
    public String getQueryName() {
        return queryName;
    }

    @Override
    public Object executeQuery(DataEvent<D> input) {
        return internalExecuteQuery(input.getData());
    }

    private Object internalExecuteQuery(D input) {
        try (RuleUnitInstance<D> instance = ruleUnit.createInstance(input)) {
            return queryFunction.apply(instance);
        }
    }

    @Override
    public String toString() {
        return "AbstractEventDrivenQueryExecutor [ruleUnit=" + ruleUnit + ", queryName=" + queryName + ", objectClass="
                + objectClass + "]";
    }
}
