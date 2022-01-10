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

import java.util.Optional;

import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.RuleUnitInstance;
import org.kie.kogito.rules.RuleUnitQuery;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;

public abstract class AbstractEventDrivenQueryExecutor<D extends RuleUnitData, R> implements EventDrivenQueryExecutor {

    private RuleUnit<D> ruleUnit;
    private String queryName;
    private Class<? extends RuleUnitQuery<R>> queryClass;
    private Class<D> dataClass;
    private ObjectMapper mapper;

    protected AbstractEventDrivenQueryExecutor() {
    }

    protected AbstractEventDrivenQueryExecutor(RuleUnit<D> ruleUnit, String queryName, Class<? extends RuleUnitQuery<R>> queryClass, Class<D> dataClass, ObjectMapper mapper) {
        this.ruleUnit = ruleUnit;
        this.queryName = queryName;
        this.queryClass = queryClass;
        this.dataClass = dataClass;
        this.mapper = mapper;
    }

    protected void setup(RuleUnit<D> ruleUnit, String queryName, Class<? extends RuleUnitQuery<R>> queryClass, Class<D> dataClass, ObjectMapper mapper) {
        this.ruleUnit = ruleUnit;
        this.queryName = queryName;
        this.queryClass = queryClass;
        this.dataClass = dataClass;
        this.mapper = mapper;
    }

    @Override
    public String getRuleUnitId() {
        return ruleUnit.id();
    }

    @Override
    public String getQueryName() {
        return queryName;
    }

    @Override
    public Object executeQuery(CloudEvent input) {
        return decodeData(input)
                .map(this::internalExecuteQuery)
                .orElseThrow(IllegalArgumentException::new);
    }

    private Optional<D> decodeData(CloudEvent input) {
        return mapper == null
                ? CloudEventUtils.decodeData(input, dataClass)
                : CloudEventUtils.decodeData(input, dataClass, mapper);
    }

    private R internalExecuteQuery(D input) {
        RuleUnitInstance<D> instance = ruleUnit.createInstance(input);
        R response = instance.executeQuery(queryClass);
        instance.dispose();
        return response;
    }
}
