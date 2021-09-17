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

package org.kie.kogito.incubation.decisions.services;

import org.junit.jupiter.api.Test;
import org.kie.kogito.incubation.application.ReflectiveAppRoot;
import org.kie.kogito.incubation.common.*;
import org.kie.kogito.incubation.decisions.DecisionIds;
import org.kie.kogito.incubation.decisions.LocalDecisionId;
import org.kie.kogito.incubation.decisions.LocalDecisionServiceId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTypes {
    public static class MyDataContext implements DataContext, DefaultCastable {
        int someParam;
    }

    @Test
    public void testDecisionEvaluationService() {

        // let's just make the compiler happy
        DecisionService svc = new DecisionService() {
            @Override
            public DataContext evaluate(LocalId id, DataContext ctx) {
                // ... parses and resolves the id ...
                // .. then evaluates ...
                return ctx;
            }
        };
        MapDataContext ctx = MapDataContext.create();

        String namespace = "http://www.redhat.com/_c7328033-c355-43cd-b616-0aceef80e52a";
        String modelName = "dmn-movieticket-ageclassification";

        ReflectiveAppRoot appRoot = new ReflectiveAppRoot();
        LocalDecisionId decisionId = appRoot.get(DecisionIds.class).get(namespace, modelName);

        assertEquals(
                "/decisions" +
                        "/http%3A%2F%2Fwww.redhat.com%2F_c7328033-c355-43cd-b616-0aceef80e52a" +
                        "%23" + "dmn-movieticket-ageclassification",
                decisionId.toLocalId().asLocalUri().path());

        // set a context using a Map-like interface
        ctx.set("someParam", 1);

        // evaluate the process
        DataContext result =
                svc.evaluate(decisionId, ctx);

        // bind the data in the result to a typed bean
        MyDataContext mdc = result.as(MyDataContext.class);
        assertEquals(1, mdc.someParam);

        // the same method is used for services
        String serviceName = "my-service";
        // LocalDecisionServiceId decisionServiceId = new LocalDecisionServiceId(decisionId, serviceName);
        LocalDecisionServiceId decisionServiceId = decisionId.services().get(serviceName);
        assertEquals(
                "/decisions" +
                        "/http%3A%2F%2Fwww.redhat.com%2F_c7328033-c355-43cd-b616-0aceef80e52a" +
                        "%23" + "dmn-movieticket-ageclassification" +
                        "/services/" + serviceName,
                decisionServiceId.toLocalId().asLocalUri().path());

    }
}
