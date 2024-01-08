/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.impl.domain.Measurement;
import org.drools.ruleunits.impl.domain.Sensor;

public class MultipleDataSourceUnit implements RuleUnitData {

    private final DataStore<Measurement> measurements;
    private final DataStore<Sensor> sensors;
    private final Set<String> controlSet = new HashSet<>();

    public MultipleDataSourceUnit() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public MultipleDataSourceUnit(DataStore<Measurement> measurements, DataStore<Sensor> sensors) {
        this.measurements = measurements;
        this.sensors = sensors;
    }

    public DataStore<Measurement> getMeasurements() {
        return measurements;
    }

    public DataStore<Sensor> getSensors() {
        return sensors;
    }

    public Set<String> getControlSet() {
        return controlSet;
    }
}
