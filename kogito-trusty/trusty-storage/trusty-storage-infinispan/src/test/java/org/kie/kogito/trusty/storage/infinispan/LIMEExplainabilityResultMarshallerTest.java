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
package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.FeatureImportanceModel;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.explainability.api.SaliencyModel;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LIMEExplainabilityResultMarshallerTest extends MarshallerTestTemplate {

    @Test
    public void testWriteAndRead() throws IOException {

        List<FeatureImportanceModel> featureImportanceModels = Collections.singletonList(new FeatureImportanceModel("aFeature", 0d));
        List<SaliencyModel> saliencyModels = Collections.singletonList(new SaliencyModel("outcomeName", featureImportanceModels));
        LIMEExplainabilityResult limeExplainabilityResult = new LIMEExplainabilityResult("executionId", ExplainabilityStatus.SUCCEEDED, "statusDetail", saliencyModels);
        LIMEExplainabilityResultMarshaller marshaller = new LIMEExplainabilityResultMarshaller(new ObjectMapper());

        marshaller.writeTo(writer, limeExplainabilityResult);
        LIMEExplainabilityResult retrieved = marshaller.readFrom(reader);

        Assertions.assertEquals(limeExplainabilityResult.getExecutionId(), retrieved.getExecutionId());
        Assertions.assertEquals(limeExplainabilityResult.getStatus(), retrieved.getStatus());
        Assertions.assertEquals(limeExplainabilityResult.getStatusDetails(), retrieved.getStatusDetails());
        Assertions.assertEquals(saliencyModels.get(0).getOutcomeName(), retrieved.getSaliencies().get(0).getOutcomeName());
        Assertions.assertEquals(featureImportanceModels.get(0).getFeatureName(), retrieved.getSaliencies().get(0).getFeatureImportance().get(0).getFeatureName());
        Assertions.assertEquals(featureImportanceModels.get(0).getFeatureScore(), retrieved.getSaliencies().get(0).getFeatureImportance().get(0).getFeatureScore());
    }
}
