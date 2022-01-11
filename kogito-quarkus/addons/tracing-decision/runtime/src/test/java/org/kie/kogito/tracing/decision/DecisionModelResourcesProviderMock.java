/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.tracing.decision;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.kie.kogito.KogitoGAV;
import org.kie.kogito.decision.DecisionModelMetadata;
import org.kie.kogito.decision.DecisionModelResource;
import org.kie.kogito.decision.DecisionModelResourcesProvider;
import org.kie.kogito.dmn.DefaultDecisionModelResource;

import io.quarkus.test.Mock;

import static org.kie.kogito.tracing.decision.QuarkusDecisionTracingTest.TEST_MODEL_NAME;
import static org.kie.kogito.tracing.decision.QuarkusDecisionTracingTest.TEST_MODEL_NAMESPACE;

@Mock
public class DecisionModelResourcesProviderMock implements DecisionModelResourcesProvider {

    private static final String CONTENT = "content";

    @Override
    public List<DecisionModelResource> get() {
        DecisionModelResource resource = new DefaultDecisionModelResource(
                new KogitoGAV("test", "test", "test"),
                TEST_MODEL_NAMESPACE,
                TEST_MODEL_NAME,
                new DecisionModelMetadata(
                        "http://www.omg.org/spec/DMN/20151101/dmn.xsd"),
                new InputStreamReader(new ByteArrayInputStream(CONTENT.getBytes())));

        return Collections.singletonList(resource);
    }
}
