/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.serverless.workflow.api.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.jbpm.serverless.workflow.api.Workflow;
import org.jbpm.serverless.workflow.api.mapper.JsonObjectMapper;
import org.jbpm.serverless.workflow.api.mapper.YamlObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Workflow provides some extra functionality for the Workflow types
 */
public class BaseWorkflow {

    private static JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
    private static YamlObjectMapper yamlObjectMapper = new YamlObjectMapper();

    private static Logger logger = LoggerFactory.getLogger(BaseWorkflow.class);

    public static Workflow fromSource(String source) {
        // try it as json markup first, if fails try yaml
        try {
            return jsonObjectMapper.readValue(source,
                    Workflow.class);
        } catch (Exception e) {
            try {
                return yamlObjectMapper.readValue(source,
                        Workflow.class);
            } catch (Exception ee) {
                throw new IllegalArgumentException("Could not convert markup to Workflow: " + ee.getMessage());
            }
        }
    }

    public static String toJson(Workflow workflow) {
        try {
            return jsonObjectMapper.writeValueAsString(workflow);
        } catch (JsonProcessingException e) {
            logger.error("Error mapping to json: {}", e.getMessage());
            return null;
        }
    }

    public static String toYaml(Workflow workflow) {
        try {
            JsonNode jsonNode = jsonObjectMapper.valueToTree(workflow);
            YAMLFactory yamlFactory = new YAMLFactory()
                    .disable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                    .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
            return new YAMLMapper(yamlFactory).writeValueAsString(jsonNode);
        } catch (Exception e) {
            logger.error("Error mapping to yaml: {}", e.getMessage());
            return null;
        }
    }
}
