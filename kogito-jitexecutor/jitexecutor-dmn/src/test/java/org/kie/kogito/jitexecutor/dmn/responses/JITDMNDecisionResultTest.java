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

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import static org.assertj.core.api.Assertions.assertThat;

class JITDMNDecisionResultTest {

    private static final ObjectMapper MAPPER;
    static {
        final var jitModule = new SimpleModule()
                .addAbstractTypeMapping(DMNDecisionResult.class, JITDMNDecisionResult.class)
                .addAbstractTypeMapping(DMNMessage.class, JITDMNMessage.class);

        MAPPER = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(jitModule);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void deserialize() throws JsonProcessingException {
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
        JITDMNDecisionResult retrieved = MAPPER.readValue(json, JITDMNDecisionResult.class);
        assertThat(retrieved).isNotNull();
    }

}
