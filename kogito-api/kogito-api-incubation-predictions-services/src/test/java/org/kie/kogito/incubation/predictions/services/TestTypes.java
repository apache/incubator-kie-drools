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

package org.kie.kogito.incubation.predictions.services;

import org.junit.jupiter.api.Test;
import org.kie.kogito.incubation.application.ReflectiveAppRoot;
import org.kie.kogito.incubation.common.*;
import org.kie.kogito.incubation.predictions.LocalPredictionId;
import org.kie.kogito.incubation.predictions.PredictionIds;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTypes {
    public static class MyDataContext implements DataContext, DefaultCastable {
        int someParam;
    }

    @Test
    public void testDecisionEvaluationService() {

        // let's just make the compiler happy
        PredictionService svc = new PredictionService() {
            @Override
            public ExtendedDataContext evaluate(LocalId id, DataContext ctx) {
                // ... parses and resolves the id ...
                // .. then evaluates ...
                return ExtendedDataContext.ofData(ctx);
            }
        };
        MapDataContext ctx = MapDataContext.create();

        String fileNameNoSuffix = "somePrediction";
        String fileName = fileNameNoSuffix + ".pmml";

        String modelName = "/mypath/to/" + fileName;

        ReflectiveAppRoot appRoot = new ReflectiveAppRoot();

        // LocalPredictionId decisionId = new LocalPredictionId(modelName);
        LocalPredictionId decisionId = appRoot.get(PredictionIds.class).get(fileNameNoSuffix, modelName);

        assertEquals(
                "/predictions" +
                        "/%2Fmypath%2Fto%2FsomePrediction.pmml",
                decisionId.toLocalId().asLocalUri().path());

        // set a context using a Map-like interface
        ctx.set("someParam", 1);

        // evaluate the process
        DataContext result =
                svc.evaluate(decisionId, ctx);

        // bind the data in the result to a typed bean
        MyDataContext mdc = result.as(MyDataContext.class);
        assertEquals(1, mdc.someParam);

    }
}
