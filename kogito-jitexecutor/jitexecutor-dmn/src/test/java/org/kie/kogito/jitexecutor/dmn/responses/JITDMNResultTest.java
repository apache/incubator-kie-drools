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
package org.kie.kogito.jitexecutor.dmn.responses;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jitexecutor.dmn.TestingUtils.getModelFromIoUtils;

class JITDMNResultTest {

    private static final ObjectMapper MAPPER;
    private static DMNModel DMN_MODEL;
    static {
        final var jitModule = new SimpleModule().addAbstractTypeMapping(DMNResult.class, JITDMNResult.class)
                .addAbstractTypeMapping(DMNDecisionResult.class, JITDMNDecisionResult.class)
                .addAbstractTypeMapping(DMNMessage.class, JITDMNMessage.class);

        MAPPER = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(jitModule);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @BeforeAll
    static void setup() throws IOException {
        String modelXML = getModelFromIoUtils("valid_models/DMNv1_5/Sample.dmn");
        Resource modelResource = ResourceFactory.newReaderResource(new StringReader(modelXML), "UTF-8");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource)).getOrElseThrow(RuntimeException::new);
        DMN_MODEL = dmnRuntime.getModels().get(0);
    }

    @Test
    void serializeDeserialize() throws JsonProcessingException {
        JITDMNDecisionResult decisionResult = getJITDMNDecisionResult();
        DMNResultImpl dmnResult = new DMNResultImpl(DMN_MODEL);
        dmnResult.setContext(createContext());
        dmnResult.addDecisionResult(decisionResult);

        JITDMNResult jitdmnResult = JITDMNResult.of("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example", dmnResult, Collections.emptyMap());
        String retrieved = MAPPER.writeValueAsString(jitdmnResult);
        assertThat(retrieved).isNotNull().isNotBlank();
        JITDMNResult result = MAPPER.readValue(retrieved, JITDMNResult.class);
        assertThat(result).isNotNull().isEqualTo(jitdmnResult);

    }

    @Test
    void deserialize() throws JsonProcessingException {
        String json = "{\n" +
                "  \"namespace\": \"http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4\",\n" +
                "  \"modelName\": \"Chapter 11 Example\",\n" +
                "  \"dmnContext\": {\n" +
                "    \"Pre-bureau risk category table\": \"function Pre-bureau risk category table( Existing Customer, Application Risk Score )\",\n" +
                "    \"Installment calculation\": \"function Installment calculation( Product Type, Rate, Term, Amount )\",\n" +
                "    \"Bureau call type table\": \"function Bureau call type table( Pre-Bureau Risk Category )\",\n" +
                "    \"Application risk score model\": \"function Application risk score model( Age, Marital Status, Employment Status )\",\n" +
                "    \"Routing rules\": \"function Routing rules( Post-bureau risk category, Post-bureau affordability, Bankrupt, Credit score )\",\n" +
                "    \"Financial\": {\n" +
                "      \"PMT\": \"function PMT( Rate, Term, Amount )\"\n" +
                "    },\n" +
                "    \"Requested product\": {\n" +
                "      \"Rate\": 0.08,\n" +
                "      \"Amount\": 10000,\n" +
                "      \"ProductType\": \"STANDARD LOAN\",\n" +
                "      \"Term\": 36\n" +
                "    },\n" +
                "    \"Credit contingency factor table\": \"function Credit contingency factor table( Risk Category )\",\n" +
                "    \"Affordability calculation\": \"function Affordability calculation( Monthly Income, Monthly Repayments, Monthly Expenses, Risk Category, Required Monthly Installment )\",\n" +
                "    \"Post-bureau risk category table\": \"function Post-bureau risk category table( Existing Customer, Application Risk Score, Credit Score )\",\n" +
                "    \"Required monthly installment\": 333.3636546143084985132842970339110,\n" +
                "    \"Bureau data\": {\n" +
                "      \"Bankrupt\": false,\n" +
                "      \"CreditScore\": 600\n" +
                "    },\n" +
                "    \"Eligibility rules\": \"function Eligibility rules( Pre-Bureau Risk Category, Pre-Bureau Affordability, Age )\"\n" +
                "  },\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Required dependency 'Applicant data' not found on node 'Pre-bureau risk category'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_9997fcfd-0f50-4933-939e-88a235b5e2a0\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Required dependency 'Applicant data' not found on node 'Application risk score'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_e905f02c-c5d9-4f2a-ba57-7912ff523b46\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Pre-bureau risk category' as it depends on decision 'Application risk score'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_9997fcfd-0f50-4933-939e-88a235b5e2a0\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Bureau call type' as it depends on decision 'Pre-bureau risk category'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_5b8356f3-2cf2-40e8-8f80-324937e8b276\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Strategy' as it depends on decision 'Bureau call type'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_8b838f06-968a-4c66-875e-f5412fd692cf\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Eligibility' as it depends on decision 'Pre-bureau risk category'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_b5e759df-f662-44cd-94f5-55c3c81f0ee3\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Required dependency 'Applicant data' not found on node 'Eligibility'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_b5e759df-f662-44cd-94f5-55c3c81f0ee3\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Pre-bureau affordability' as it depends on decision 'Pre-bureau risk category'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_ed60265c-25e2-400f-a99f-fafd3b489838\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Required dependency 'Applicant data' not found on node 'Pre-bureau affordability'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_ed60265c-25e2-400f-a99f-fafd3b489838\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Eligibility' as it depends on decision 'Pre-bureau affordability'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_b5e759df-f662-44cd-94f5-55c3c81f0ee3\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Strategy' as it depends on decision 'Eligibility'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_8b838f06-968a-4c66-875e-f5412fd692cf\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Required dependency 'Applicant data' not found on node 'Post-bureau risk category'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_40b45659-9299-43a6-af30-04c948c5c0ec\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Post-bureau risk category' as it depends on decision 'Application risk score'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_40b45659-9299-43a6-af30-04c948c5c0ec\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Post-bureau affordability' as it depends on decision 'Post-bureau risk category'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_728e3a50-f00f-42c0-b3ee-1ee5aabd5474\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Required dependency 'Applicant data' not found on node 'Post-bureau affordability'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_728e3a50-f00f-42c0-b3ee-1ee5aabd5474\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Routing' as it depends on decision 'Post-bureau affordability'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_ca1e6032-12eb-428a-a80b-49028a88c0b5\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Routing' as it depends on decision 'Post-bureau risk category'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_ca1e6032-12eb-428a-a80b-49028a88c0b5\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Required dependency 'Applicant data' not found on node 'Adjudication'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Unable to evaluate decision 'Adjudication' as it depends on decision 'Routing'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"severity\": \"ERROR\",\n" +
                "      \"message\": \"Required dependency 'Supporting documents' not found on node 'Adjudication'\",\n" +
                "      \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "      \"sourceId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "      \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "      \"level\": \"ERROR\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"decisionResults\": [\n" +
                "    {\n" +
                "      \"decisionId\": \"_5b8356f3-2cf2-40e8-8f80-324937e8b276\",\n" +
                "      \"decisionName\": \"Bureau call type\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Bureau call type' as it depends on decision 'Pre-bureau risk category'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_5b8356f3-2cf2-40e8-8f80-324937e8b276\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"decisionId\": \"_b5e759df-f662-44cd-94f5-55c3c81f0ee3\",\n" +
                "      \"decisionName\": \"Eligibility\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Eligibility' as it depends on decision 'Pre-bureau risk category'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_b5e759df-f662-44cd-94f5-55c3c81f0ee3\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Required dependency 'Applicant data' not found on node 'Eligibility'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_b5e759df-f662-44cd-94f5-55c3c81f0ee3\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Eligibility' as it depends on decision 'Pre-bureau affordability'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_b5e759df-f662-44cd-94f5-55c3c81f0ee3\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"decisionId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "      \"decisionName\": \"Adjudication\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Required dependency 'Applicant data' not found on node 'Adjudication'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Adjudication' as it depends on decision 'Routing'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Required dependency 'Supporting documents' not found on node 'Adjudication'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"decisionId\": \"_e905f02c-c5d9-4f2a-ba57-7912ff523b46\",\n" +
                "      \"decisionName\": \"Application risk score\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Required dependency 'Applicant data' not found on node 'Application risk score'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_e905f02c-c5d9-4f2a-ba57-7912ff523b46\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"decisionId\": \"_ed60265c-25e2-400f-a99f-fafd3b489838\",\n" +
                "      \"decisionName\": \"Pre-bureau affordability\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Pre-bureau affordability' as it depends on decision 'Pre-bureau risk category'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_ed60265c-25e2-400f-a99f-fafd3b489838\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Required dependency 'Applicant data' not found on node 'Pre-bureau affordability'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_ed60265c-25e2-400f-a99f-fafd3b489838\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"decisionId\": \"_40b45659-9299-43a6-af30-04c948c5c0ec\",\n" +
                "      \"decisionName\": \"Post-bureau risk category\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Required dependency 'Applicant data' not found on node 'Post-bureau risk category'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_40b45659-9299-43a6-af30-04c948c5c0ec\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Post-bureau risk category' as it depends on decision 'Application risk score'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_40b45659-9299-43a6-af30-04c948c5c0ec\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"decisionId\": \"_728e3a50-f00f-42c0-b3ee-1ee5aabd5474\",\n" +
                "      \"decisionName\": \"Post-bureau affordability\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Post-bureau affordability' as it depends on decision 'Post-bureau risk category'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_728e3a50-f00f-42c0-b3ee-1ee5aabd5474\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Required dependency 'Applicant data' not found on node 'Post-bureau affordability'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_728e3a50-f00f-42c0-b3ee-1ee5aabd5474\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"decisionId\": \"_3c8cee68-99dd-418c-847d-0b54697354f2\",\n" +
                "      \"decisionName\": \"Required monthly installment\",\n" +
                "      \"result\": 333.3636546143084985132842970339110,\n" +
                "      \"messages\": [],\n" +
                "      \"evaluationHitIds\": {\"_1FA12B9F-288C-42E8-B77F-BE2D3702B7B6\": 1},\n" +
                "      \"evaluationStatus\": \"SUCCEEDED\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"decisionId\": \"_ca1e6032-12eb-428a-a80b-49028a88c0b5\",\n" +
                "      \"decisionName\": \"Routing\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Routing' as it depends on decision 'Post-bureau affordability'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_ca1e6032-12eb-428a-a80b-49028a88c0b5\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Routing' as it depends on decision 'Post-bureau risk category'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_ca1e6032-12eb-428a-a80b-49028a88c0b5\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"decisionId\": \"_9997fcfd-0f50-4933-939e-88a235b5e2a0\",\n" +
                "      \"decisionName\": \"Pre-bureau risk category\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Required dependency 'Applicant data' not found on node 'Pre-bureau risk category'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_9997fcfd-0f50-4933-939e-88a235b5e2a0\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Pre-bureau risk category' as it depends on decision 'Application risk score'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_9997fcfd-0f50-4933-939e-88a235b5e2a0\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"decisionId\": \"_8b838f06-968a-4c66-875e-f5412fd692cf\",\n" +
                "      \"decisionName\": \"Strategy\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Strategy' as it depends on decision 'Bureau call type'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_8b838f06-968a-4c66-875e-f5412fd692cf\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Strategy' as it depends on decision 'Eligibility'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_8b838f06-968a-4c66-875e-f5412fd692cf\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JITDMNResult result = MAPPER.readValue(json, JITDMNResult.class);
        assertThat(result).isNotNull();
    }

    private DMNContext createContext() {
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("Credit Score", Map.of("FICO", 700));

        Map<String, Object> monthly = new HashMap<>();
        monthly.put("Income", 121233);
        monthly.put("Repayments", 33);
        monthly.put("Expenses", 123);
        monthly.put("Tax", 32);
        monthly.put("Insurance", 55);
        Map<String, Object> applicantData = new HashMap<>();
        applicantData.put("Age", 32);
        applicantData.put("Marital Status", "S");
        applicantData.put("Employment Status", "Employed");
        applicantData.put("Monthly", monthly);
        contextMap.put("Applicant Data", applicantData);

        Map<String, Object> requestedProduct = new HashMap<>();
        requestedProduct.put("Type", "Special Loan");
        requestedProduct.put("Rate", 1);
        requestedProduct.put("Term", 2);
        requestedProduct.put("Amount", 333);
        contextMap.put("Requested Product", requestedProduct);

        contextMap.put("id", "_0A185BAC-7692-45FA-B722-7C86C626BD51");
        return new DMNContextImpl(contextMap);
    }

    private JITDMNDecisionResult getJITDMNDecisionResult() throws JsonProcessingException {
        String json = "{\n" +
                "      \"decisionId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "      \"decisionName\": \"Adjudication\",\n" +
                "      \"result\": null,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Required dependency 'Applicant data' not found on node 'Adjudication'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Unable to evaluate decision 'Adjudication' as it depends on decision 'Routing'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"severity\": \"ERROR\",\n" +
                "          \"message\": \"Required dependency 'Supporting documents' not found on node 'Adjudication'\",\n" +
                "          \"messageType\": \"REQ_NOT_FOUND\",\n" +
                "          \"sourceId\": \"_4bd33d4a-741b-444a-968b-64e1841211e7\",\n" +
                "          \"path\": \"invalid_models/DMNv1_x/multiple/Chapter 11 Example.dmn\",\n" +
                "          \"level\": \"ERROR\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"evaluationHitIds\": {},\n" +
                "      \"evaluationStatus\": \"SKIPPED\"\n" +
                "    }";
        return MAPPER.readValue(json, JITDMNDecisionResult.class);
    }

}
