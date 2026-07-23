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
package org.kie.kogito.trusty.service.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.kogito.explainability.api.CounterfactualDomain;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainStructureValue;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainUnitValue;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionOutcome;

import com.fasterxml.jackson.databind.JsonNode;

public class TypedValueTestUtils {

    private TypedValueTestUtils() {
    }

    public static DecisionInput buildInputUnit(String name, String typeRef, JsonNode value) {
        return new DecisionInput("id", name, new UnitValue(typeRef, typeRef, value));
    }

    public static DecisionInput buildInputStructure(String name, String typeRef, List<DecisionInput> items) {
        return new DecisionInput("id", name, new StructureValue(typeRef,
                items.stream().collect(Collectors.toMap(DecisionInput::getName, DecisionInput::getValue))));
    }

    public static DecisionOutcome buildOutcomeUnit(String name, String typeRef, JsonNode value) {
        return new DecisionOutcome("id",
                name,
                "SUCCESS",
                new UnitValue(typeRef, typeRef, value),
                Collections.emptyList(),
                Collections.emptyList());
    }

    public static DecisionOutcome buildOutcomeStructure(String name, String typeRef, Map<String, TypedValue> value) {
        return new DecisionOutcome("id",
                name,
                "SUCCESS",
                new StructureValue(typeRef, value),
                Collections.emptyList(),
                Collections.emptyList());
    }

    public static NamedTypedValue buildGoalUnit(String name, String typeRef, JsonNode value) {
        return new NamedTypedValue(name,
                new UnitValue(typeRef, typeRef, value));
    }

    public static NamedTypedValue buildGoalStructure(String name, String typeRef, Map<String, TypedValue> value) {
        return new NamedTypedValue(name,
                new StructureValue(typeRef, value));
    }

    public static CounterfactualSearchDomain buildSearchDomainUnit(String name, String typeRef, CounterfactualDomain domain) {
        return new CounterfactualSearchDomain(name,
                new CounterfactualSearchDomainUnitValue(typeRef, typeRef, true, domain));
    }

    public static CounterfactualSearchDomain buildSearchDomainStructure(String field, String typeRef, List<CounterfactualSearchDomain> domains) {
        return new CounterfactualSearchDomain(field,
                new CounterfactualSearchDomainStructureValue(typeRef,
                        domains.stream().collect(Collectors.toMap(CounterfactualSearchDomain::getName, CounterfactualSearchDomain::getValue))));
    }
}
