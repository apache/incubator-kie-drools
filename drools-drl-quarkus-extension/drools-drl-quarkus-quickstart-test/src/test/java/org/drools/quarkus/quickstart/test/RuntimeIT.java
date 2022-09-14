/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.quarkus.quickstart.test;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;

import org.drools.quarkus.quickstart.test.model.Light;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

@QuarkusTest
public class RuntimeIT {

    @Inject
    RuleUnit<HomeUnitData> ruleUnit;

    @Test
    public void testRuleUnit() {
        HomeUnitData homeUnitData = new HomeUnitData();
        homeUnitData.getLights().add(new Light("living room", true));
        homeUnitData.getLights().add(new Light("bedroom", false));
        homeUnitData.getLights().add(new Light("bathroom", false));

        RuleUnitInstance<HomeUnitData> unitInstance = ruleUnit.createInstance(homeUnitData);
        unitInstance.fire();
        
        List<Map<String, Object>> queryResults = unitInstance.executeQuery("AllAlerts");
        
        System.out.println(queryResults);

//        assertEquals(1, homeUnitData.getResults().size());
//        assertEquals("Hello Mario", homeUnitData.getResults().get(0));
    }
}
