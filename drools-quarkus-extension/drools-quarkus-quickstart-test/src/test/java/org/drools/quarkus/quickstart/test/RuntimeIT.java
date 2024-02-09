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

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.drools.quarkus.quickstart.test.model.Alert;
import org.drools.quarkus.quickstart.test.model.CCTV;
import org.drools.quarkus.quickstart.test.model.Light;
import org.drools.quarkus.quickstart.test.model.Smartphone;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.QueryResults;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class RuntimeIT {

    @Inject
    RuleUnit<HomeRuleUnitData> ruleUnit;

    @Test
    public void testRuleOutside() {
        HomeRuleUnitData homeUnitData = new HomeRuleUnitData();
        homeUnitData.getLights().add(new Light("living room", true));
        homeUnitData.getLights().add(new Light("bedroom", false));
        homeUnitData.getLights().add(new Light("bathroom", false));

        try ( RuleUnitInstance<HomeRuleUnitData> unitInstance = ruleUnit.createInstance(homeUnitData) ) {
            QueryResults queryResults = unitInstance.executeQuery("All Alerts");
            assertThat(queryResults).isNotEmpty()
                    .anyMatch(kv -> kv.get("$a").equals(new Alert("You might have forgot one light powered on: living room")));
        }
    }

    @Test
    public void testRuleInside() {
        HomeRuleUnitData homeUnitData = new HomeRuleUnitData();
        homeUnitData.getLights().add(new Light("living room", true));
        homeUnitData.getLights().add(new Light("bedroom", false));
        homeUnitData.getLights().add(new Light("bathroom", false));
        homeUnitData.getCctvs().add(new CCTV("security camera 1", false));
        homeUnitData.getCctvs().add(new CCTV("security camera 2", true));
        homeUnitData.getSmartphones().add(new Smartphone("John Doe's phone"));

        try ( RuleUnitInstance<HomeRuleUnitData> unitInstance = ruleUnit.createInstance(homeUnitData) ) {
            QueryResults queryResults = unitInstance.executeQuery("All Alerts");
            assertThat(queryResults).isNotEmpty()
                    .anyMatch(kv -> kv.get("$a").equals(new Alert("One CCTV is still operating: security camera 2")));
        }
    }

    @Test
    public void testNoAlerts() {
        HomeRuleUnitData homeUnitData = new HomeRuleUnitData();
        homeUnitData.getLights().add(new Light("living room", false));
        homeUnitData.getLights().add(new Light("bedroom", false));
        homeUnitData.getLights().add(new Light("bathroom", false));
        homeUnitData.getCctvs().add(new CCTV("security camera 1", true));
        homeUnitData.getCctvs().add(new CCTV("security camera 2", true));

        try ( RuleUnitInstance<HomeRuleUnitData> unitInstance = ruleUnit.createInstance(homeUnitData) ) {
            QueryResults queryResults = unitInstance.executeQuery("All Alerts");
            assertThat(queryResults).isEmpty();
        }
    }
}
