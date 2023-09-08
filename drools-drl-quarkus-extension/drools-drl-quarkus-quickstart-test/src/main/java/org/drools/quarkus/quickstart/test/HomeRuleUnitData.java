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
package org.drools.quarkus.quickstart.test;

import org.drools.quarkus.quickstart.test.model.Alert;
import org.drools.quarkus.quickstart.test.model.CCTV;
import org.drools.quarkus.quickstart.test.model.Light;
import org.drools.quarkus.quickstart.test.model.Smartphone;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class HomeRuleUnitData implements RuleUnitData {
    
    private final DataStore<Light> lights;
    private final DataStore<CCTV> cctvs;
    private final DataStore<Smartphone> smartphones;

    private final DataStore<Alert> alerts = DataSource.createStore();

    public HomeRuleUnitData() {
        this(DataSource.createStore(), DataSource.createStore(), DataSource.createStore());
    }

    public HomeRuleUnitData(DataStore<Light> lights, DataStore<CCTV> cctvs, DataStore<Smartphone> smartphones) {
		this.lights = lights;
		this.cctvs = cctvs;
		this.smartphones = smartphones;
	}

	public DataStore<Light> getLights() {
		return lights;
	}

	public DataStore<CCTV> getCctvs() {
		return cctvs;
	}

	public DataStore<Smartphone> getSmartphones() {
		return smartphones;
	}

	public DataStore<Alert> getAlerts() {
		return alerts;
	}
}
