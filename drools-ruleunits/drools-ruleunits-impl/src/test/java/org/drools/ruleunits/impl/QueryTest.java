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
package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.impl.domain.Location;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryTest {

    @Test
    public void testQuery() {
        // DROOLS-7520
        LocationUnit locationUnit = new LocationUnit();
        try (RuleUnitInstance<LocationUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(locationUnit)) {
            locationUnit.getLocations().add( new Location("office", "house") );
            locationUnit.getLocations().add( new Location("kitchen", "house") );
            locationUnit.getLocations().add( new Location("knife", "kitchen") );
            locationUnit.getLocations().add( new Location("cheese", "kitchen") );
            locationUnit.getLocations().add( new Location("desk", "office") );
            locationUnit.getLocations().add( new Location("chair", "office") );
            locationUnit.getLocations().add( new Location("computer", "desk") );
            locationUnit.getLocations().add( new Location("drawer", "desk") );

            locationUnit.getGo().set("go1");
            unitInstance.fire();

            QueryResults results = unitInstance.executeQuery("getLocations", new Object[]{Variable.v});
            results.size();
            for (QueryResultsRow row : results) {
                assertThat(row.get("$x").toString()).isIn(
                        "office in house",
                        "desk in office",
                        "desk in house",
                        "computer in desk",
                        "computer in office",
                        "computer in house",
                        "kitchen in house",
                        "cheese in kitchen",
                        "cheese in house",
                        "chair in office",
                        "chair in house",
                        "knife in kitchen",
                        "knife in house",
                        "drawer in desk",
                        "drawer in office",
                        "drawer in house"
                );
            }
        }
    }
}
