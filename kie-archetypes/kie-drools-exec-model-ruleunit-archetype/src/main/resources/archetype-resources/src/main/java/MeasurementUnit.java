/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.util.HashSet;
import java.util.Set;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class MeasurementUnit implements RuleUnitData {

    private final DataStore<Measurement> measurements;
    private final Set<String> controlSet = new HashSet<>();

    public MeasurementUnit() {
        this(DataSource.createStore());
    }

    public MeasurementUnit(DataStore<Measurement> measurements) {
        this.measurements = measurements;
    }

    public DataStore<Measurement> getMeasurements() {
        return measurements;
    }

    public Set<String> getControlSet() {
        return controlSet;
    }
}
