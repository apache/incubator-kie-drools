/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.api.model;

import static java.util.stream.Collectors.toList;

/**
 * Scenario contains description and values to test in the defined scenario
 */
public class Scenario extends AbstractScesimData {

    @Override
    Scenario cloneInstance() {
        Scenario cloned = new Scenario();
        cloned.factMappingValues.addAll(factMappingValues.stream().map(FactMappingValue::cloneFactMappingValue).collect(toList()));
        return cloned;
    }
}