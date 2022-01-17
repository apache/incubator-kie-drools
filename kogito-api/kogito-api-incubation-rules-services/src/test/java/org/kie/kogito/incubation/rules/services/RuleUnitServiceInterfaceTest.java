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

package org.kie.kogito.incubation.rules.services;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.kie.kogito.incubation.application.ReflectiveAppRoot;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.DefaultCastable;
import org.kie.kogito.incubation.common.Id;
import org.kie.kogito.incubation.common.MapDataContext;
import org.kie.kogito.incubation.rules.InstanceQueryId;
import org.kie.kogito.incubation.rules.QueryId;
import org.kie.kogito.incubation.rules.RuleUnitIds;
import org.kie.kogito.incubation.rules.RuleUnitInstanceId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuleUnitServiceInterfaceTest {
    public static class MyRuleUnitDefinition {
    }

    public static class MyDataContext implements DataContext, DefaultCastable {
        int someParam;
    }

    @Test
    public void ruleUnits() {

        // let's just make the compiler happy
        RuleUnitService svc = new RuleUnitService() {
            @Override
            public Stream<DataContext> evaluate(Id id, DataContext ctx) {
                return Stream.of(ctx);
            }
        };
        MapDataContext ctx = MapDataContext.create();

        ReflectiveAppRoot appRoot = new ReflectiveAppRoot();
        // RuleUnitId ruleUnitId = new RuleUnitId(MyRuleUnitDefinition.class);
        // QueryId someQuery = new QueryId(ruleUnitId, "someQuery");
        QueryId someQuery =
                appRoot.get(RuleUnitIds.class)
                        .get(MyRuleUnitDefinition.class)
                        .queries()
                        .get("someQuery");

        // evaluate the process
        Stream<DataContext> result =
                svc.evaluate(someQuery, ctx);

        // bind the data in the result to a typed bean
        Stream<MyDataContext> mdcs = result.map(r -> r.as(MyDataContext.class));

        assertEquals("/rule-units" +
                "/" + MyRuleUnitDefinition.class.getCanonicalName() +
                "/queries/someQuery",
                someQuery.toLocalId().asLocalUri().path());

    }

    @Test
    public void ruleUnitInstances() {
        ReflectiveAppRoot appRoot = new ReflectiveAppRoot();
        RuleUnitInstanceId instance = appRoot.get(RuleUnitIds.class).get("my-rule-unit").instances().get("my-instance-id");
        assertEquals("/rule-units/my-rule-unit/instances/my-instance-id", instance.asLocalUri().path());
    }

    @Test
    public void ruleUnitInstanceQuery() {
        ReflectiveAppRoot appRoot = new ReflectiveAppRoot();
        InstanceQueryId query = appRoot.get(RuleUnitIds.class).get("my-rule-unit").instances().get("my-instance-id").queries().get("my-query");
        assertEquals("/rule-units/my-rule-unit/instances/my-instance-id/queries/my-query", query.asLocalUri().path());
    }
}
