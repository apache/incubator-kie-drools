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
package org.drools.quarkus.ruleunit.test;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;

@Path("/test")
public class TestableResource {
    
    @Inject
    RuleUnit<HelloWorldUnit> ruleUnit;
    
    @GET
    @Path("testRuleUnit")
    public Response testRuleUnit() {
        HelloWorldUnit unit = new HelloWorldUnit();
        unit.getStrings().add("Mario");

        try ( RuleUnitInstance<HelloWorldUnit> instance = ruleUnit.createInstance(unit)  ) {
            instance.fire();
        }

        assertThat(unit.getResults()).hasSize(1)
            .containsExactly("Hello Mario");
        
        return Response.ok().build();
    }
}
