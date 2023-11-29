/*
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
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;

import static java.util.stream.Collectors.toList;
@Path("/$endpointName$")
public class $unit$Query$name$Endpoint {

    @jakarta.inject.Inject
    RuleUnit<$UnitType$> ruleUnit;

    public $unit$Query$name$Endpoint() { }

    public $unit$Query$name$Endpoint(RuleUnit<$UnitType$> ruleUnit) {
        this.ruleUnit = ruleUnit;
    }

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<$ReturnType$> executeQuery($UnitTypeDTO$ unitDTO) {
        RuleUnitInstance<$UnitType$> instance = ruleUnit.createInstance();
        // Do not return the result directly to allow post execution codegen (like monitoring)
        List<$ReturnType$> response = $unit$Query$name$.execute(instance);
        instance.close();
        return response;
    }

    @POST()
    @Path("/first")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public $ReturnType$ executeQueryFirst($UnitTypeDTO$ unitDTO) {
        List<$ReturnType$> results = executeQuery(unitDTO);
        $ReturnType$ response = results.isEmpty() ? null : results.get(0);
        return response;
    }
}
