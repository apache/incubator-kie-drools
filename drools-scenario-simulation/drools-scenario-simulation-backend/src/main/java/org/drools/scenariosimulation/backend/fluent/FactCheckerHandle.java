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
package org.drools.scenariosimulation.backend.fluent;

import java.util.function.Function;

import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;

public class FactCheckerHandle {

    private final Class<?> clazz;
    private final Function<Object, ValueWrapper> checkFuction;
    private final ScenarioResult scenarioResult;

    public FactCheckerHandle(Class<?> clazz, Function<Object, ValueWrapper> checkFuction, ScenarioResult scenarioResult) {
        this.clazz = clazz;
        this.checkFuction = checkFuction;
        this.scenarioResult = scenarioResult;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Function<Object, ValueWrapper> getCheckFuction() {
        return checkFuction;
    }

    public ScenarioResult getScenarioResult() {
        return scenarioResult;
    }
}
