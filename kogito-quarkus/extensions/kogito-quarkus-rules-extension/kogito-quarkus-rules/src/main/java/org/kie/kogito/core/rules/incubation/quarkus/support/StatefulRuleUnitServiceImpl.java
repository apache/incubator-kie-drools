/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.core.rules.incubation.quarkus.support;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.kie.kogito.incubation.common.*;
import org.kie.kogito.incubation.rules.InstanceQueryId;
import org.kie.kogito.incubation.rules.RuleUnitId;
import org.kie.kogito.incubation.rules.RuleUnitInstanceId;
import org.kie.kogito.incubation.rules.services.StatefulRuleUnitService;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.RuleUnitInstance;
import org.kie.kogito.rules.RuleUnits;

class StatefulRuleUnitServiceImpl implements StatefulRuleUnitService {

    private final RuleUnits ruleUnits;

    public StatefulRuleUnitServiceImpl(RuleUnits ruleUnits) {
        this.ruleUnits = ruleUnits;
    }

    @Override
    public MetaDataContext create(LocalId localId, ExtendedReferenceContext extendedDataContext) {
        RuleUnitId ruleUnitId;
        if (localId instanceof RuleUnitId) {
            ruleUnitId = (RuleUnitId) localId;
        } else
            throw new IllegalArgumentException("cannot parse rule unit id");

        ReferenceContext refCtx = extendedDataContext.data();
        if (!(refCtx instanceof RuleUnitData)) {
            throw new IllegalArgumentException("ExtendedReferenceContext#data must be a RuleUnitData");
        }
        RuleUnitData ruleUnitData = (RuleUnitData) refCtx;

        Class<RuleUnitData> aClass = toClass(ruleUnitId);
        RuleUnit<RuleUnitData> ruleUnit = ruleUnits.create(aClass);
        RuleUnitInstance<RuleUnitData> instance = ruleUnit.createInstance(ruleUnitData);
        String instanceId = UUID.randomUUID().toString();
        ruleUnits.register(instanceId, instance);
        RuleUnitInstanceId ruleUnitInstanceId = ruleUnitId.instances().get(instanceId);
        return MapDataContext.of(Map.of("id", ruleUnitInstanceId.asLocalUri().path()));
    }

    private Class<RuleUnitData> toClass(RuleUnitId ruleUnitId) {
        try {
            return (Class<RuleUnitData>) Thread.currentThread().getContextClassLoader().loadClass(ruleUnitId.ruleUnitId());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public MetaDataContext dispose(LocalId localId) {
        RuleUnitInstanceId ruleUnitInstanceId;
        if (localId instanceof RuleUnitInstanceId) {
            ruleUnitInstanceId = (RuleUnitInstanceId) localId;
        } else
            throw new IllegalArgumentException("cannot parse rule unit id");
        RuleUnitInstance<?> instance = ruleUnits.getRegisteredInstance(ruleUnitInstanceId.ruleUnitInstanceId());
        if (instance == null)
            throw new IllegalArgumentException("Unknown instance " + localId);
        instance.dispose();
        return EmptyMetaDataContext.Instance;
    }

    @Override
    public MetaDataContext fire(LocalId localId) {
        RuleUnitInstanceId ruleUnitInstanceId;
        if (localId instanceof RuleUnitInstanceId) {
            ruleUnitInstanceId = (RuleUnitInstanceId) localId;
        } else
            throw new IllegalArgumentException("cannot parse rule unit id");
        RuleUnitInstance<?> instance = ruleUnits.getRegisteredInstance(ruleUnitInstanceId.ruleUnitInstanceId());
        instance.fire();
        return EmptyMetaDataContext.Instance;
    }

    @Override
    public Stream<ExtendedDataContext> query(LocalId localId, ExtendedReferenceContext params) {
        RuleUnitInstanceId ruleUnitInstanceId;
        // must add a QueryId for instances!
        InstanceQueryId queryId;
        if (localId instanceof InstanceQueryId) {
            queryId = (InstanceQueryId) localId;
            ruleUnitInstanceId = queryId.ruleUnitInstanceId();
        } else {
            // LocalDecisionId.parse(decisionId);
            throw new IllegalArgumentException(
                    "Not a valid instance query id " + localId);
        }

        RuleUnitInstance<?> instance = ruleUnits.getRegisteredInstance(ruleUnitInstanceId.ruleUnitInstanceId());
        if (instance == null)
            throw new IllegalArgumentException("Unknown instance " + localId);
        List<Map<String, Object>> results = instance.executeQuery(queryId.queryId());

        return results.stream().map(r -> ExtendedDataContext.of(EmptyMetaDataContext.Instance, MapDataContext.of(r)));

    }

}
