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
package org.jbpm.serverless.workflow.parser.util;

import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;

import org.drools.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.events.EventDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.mapper.BaseObjectMapper;
import io.serverlessworkflow.api.mapper.JsonObjectMapper;
import io.serverlessworkflow.api.mapper.YamlObjectMapper;

public class ServerlessWorkflowUtils {

    public static final String DEFAULT_WORKFLOW_FORMAT = "json";
    public static final String ALTERNATE_WORKFLOW_FORMAT = "yml";
    public static final String DEFAULT_JSONPATH_CONFIG = "com.jayway.jsonpath.Configuration jsonPathConfig = com.jayway.jsonpath.Configuration.builder()" +
            ".mappingProvider(new com.jayway.jsonpath.spi.mapper.JacksonMappingProvider())" +
            ".jsonProvider(new com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider()).build(); ";

    private static final String APP_PROPERTIES_BASE = "kogito.sw.";
    private static final String APP_PROPERTIES_FUNCTIONS_BASE = "functions.";
    private static final String APP_PROPERTIES_EVENTS_BASE = "events.";
    private static final String APP_PROPERTIES_STATES_BASE = "states.";

    public static final String OPENAPI_OPERATION_SEPARATOR = "#";

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerlessWorkflowUtils.class);

    private ServerlessWorkflowUtils() {
    }

    public static BaseObjectMapper getObjectMapper(String workflowFormat) {
        if (workflowFormat != null && workflowFormat.equalsIgnoreCase(DEFAULT_WORKFLOW_FORMAT)) {
            return new JsonObjectMapper();
        }

        if (workflowFormat != null && workflowFormat.equalsIgnoreCase(ALTERNATE_WORKFLOW_FORMAT)) {
            return new YamlObjectMapper();
        }

        LOGGER.error("unable to determine workflow format {}", workflowFormat);
        throw new IllegalArgumentException("invalid workflow format");
    }

    public static String readWorkflowFile(Reader reader) {
        return StringUtils.readFileAsString(reader);
    }

    public static EventDefinition getWorkflowEventFor(Workflow workflow, String eventName) {
        return workflow.getEvents().getEventDefs().stream()
                .filter(wt -> wt.getName().equals(eventName))
                .findFirst().orElseThrow(() -> new NoSuchElementException("No event for " + eventName));
    }

    public static String sysOutFunctionScript(String script) {
        String retStr = DEFAULT_JSONPATH_CONFIG;
        retStr += "java.lang.String toPrint = \"\";com.fasterxml.jackson.databind.JsonNode jsonNode;";
        retStr += getJsonPathScript(script);
        retStr += "System.out.println(toPrint);";

        return retStr;
    }

    public static String scriptFunctionScript(String script) {
        String retStr = DEFAULT_JSONPATH_CONFIG;
        retStr += getJsonPathScript(script);
        return retStr;
    }

    public static String conditionScript(String conditionStr) {
        if (conditionStr.startsWith("{{")) {
            conditionStr = conditionStr.substring(2);
        }
        if (conditionStr.endsWith("}}")) {
            conditionStr = conditionStr.substring(0, conditionStr.length() - 2);
        }

        conditionStr = conditionStr.trim();

        // check if we are calling a different workflow var
        String processVar = "workflowdata";
        String otherVar = conditionStr.substring(conditionStr.indexOf("$") + 1, conditionStr.indexOf("."));

        if (otherVar.trim().length() > 0) {
            processVar = otherVar;
            conditionStr = conditionStr.replaceAll(otherVar, "");

        }

        return "return !((java.util.List<java.lang.String>) com.jayway.jsonpath.JsonPath.parse(((com.fasterxml.jackson.databind.JsonNode)kcontext.getVariable(\"" + processVar
                + "\")).toString()).read(\"" + conditionStr + "\")).isEmpty();";
    }

    public static String getJsonPathScript(String script) {
        return script.contains("$") ? script.replaceAll("\\$.([A-Za-z]+)",
                "jsonNode = com.jayway.jsonpath.JsonPath.using(jsonPathConfig).parse(((com.fasterxml.jackson.databind.JsonNode)kcontext.getVariable(\"workflowdata\"))).read(\"@@.$1\", com.fasterxml.jackson.databind.JsonNode.class); toPrint+= jsonNode.isTextual() ? jsonNode.asText() : jsonNode;")
                .replaceAll("@@", Matcher.quoteReplacement("$")) : script;

    }

    public static String getInjectScript(JsonNode toInjectNode) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String injectStr = objectMapper.writeValueAsString(toInjectNode);

            return "com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();\n" +
                    "        com.fasterxml.jackson.databind.JsonNode updateNode2 = objectMapper.readTree(\"" + injectStr.replaceAll("\"", "\\\\\"") + "\");\n" +
                    "        com.fasterxml.jackson.databind.JsonNode mainNode2 = (com.fasterxml.jackson.databind.JsonNode)kcontext.getVariable(\"workflowdata\");\n" +
                    "        java.util.Iterator<String> fieldNames2 = updateNode2.fieldNames();\n" +
                    "        while(fieldNames2.hasNext()) {\n" +
                    "            String updatedFieldName = fieldNames2.next();\n" +
                    "            com.fasterxml.jackson.databind.JsonNode updatedValue = updateNode2.get(updatedFieldName);\n" +
                    "            if(mainNode2.get(updatedFieldName) != null) {\n" +
                    "                ((com.fasterxml.jackson.databind.node.ObjectNode) mainNode2).replace(updatedFieldName, updatedValue);\n" +
                    "            } else {\n" +
                    "                ((com.fasterxml.jackson.databind.node.ObjectNode) mainNode2).put(updatedFieldName, updatedValue);\n" +
                    "            }\n" +
                    "        }\n" +
                    "        kcontext.setVariable(\"workflowdata\", mainNode2);\n";

        } catch (JsonProcessingException e) {
            LOGGER.warn("unable to set inject script: {}", e.getMessage());
            return "";
        }
    }

    public static String resolveFunctionMetadata(FunctionDefinition function, String metadataKey, WorkflowAppContext workflowAppContext) {
        return resolveFunctionMetadata(function, metadataKey, workflowAppContext, "");
    }

    public static String resolveFunctionMetadata(FunctionDefinition function, String metadataKey, WorkflowAppContext workflowAppContext, String defaultValue) {
        if (function != null && function.getMetadata() != null && function.getMetadata().containsKey(metadataKey)) {
            return function.getMetadata().get(metadataKey);
        }

        if (function != null && workflowAppContext != null &&
                workflowAppContext.getApplicationProperties().containsKey(APP_PROPERTIES_BASE + APP_PROPERTIES_FUNCTIONS_BASE + function.getName() + "." + metadataKey)) {
            return workflowAppContext.getApplicationProperty(APP_PROPERTIES_BASE + APP_PROPERTIES_FUNCTIONS_BASE + function.getName() + "." + metadataKey);
        }

        LOGGER.warn("Could not resolve function metadata: {}", metadataKey);
        return defaultValue;
    }

    public static String resolveEvenDefinitiontMetadata(EventDefinition eventDefinition, String metadataKey, WorkflowAppContext workflowAppContext) {
        if (eventDefinition != null && eventDefinition.getMetadata() != null && eventDefinition.getMetadata().containsKey(metadataKey)) {
            return eventDefinition.getMetadata().get(metadataKey);
        }

        if (eventDefinition != null && workflowAppContext != null &&
                workflowAppContext.getApplicationProperties().containsKey(APP_PROPERTIES_BASE + APP_PROPERTIES_EVENTS_BASE + eventDefinition.getName() + "." + metadataKey)) {
            return workflowAppContext.getApplicationProperty(APP_PROPERTIES_BASE + APP_PROPERTIES_EVENTS_BASE + eventDefinition.getName() + "." + metadataKey);
        }

        LOGGER.warn("Could not resolve event definition metadata: {}", metadataKey);
        return "";
    }

    public static String resolveStatetMetadata(State state, String metadataKey, WorkflowAppContext workflowAppContext) {
        if (state != null && state.getMetadata() != null && state.getMetadata().containsKey(metadataKey)) {
            return state.getMetadata().get(metadataKey);
        }

        if (state != null && workflowAppContext != null &&
                workflowAppContext.getApplicationProperties().containsKey(APP_PROPERTIES_BASE + APP_PROPERTIES_STATES_BASE + state.getName() + "." + metadataKey)) {
            return workflowAppContext.getApplicationProperty(APP_PROPERTIES_BASE + APP_PROPERTIES_STATES_BASE + state.getName() + "." + metadataKey);
        }

        LOGGER.warn("Could not resolve state metadata: {}", metadataKey);
        return "";
    }

    public static String resolveWorkflowMetadata(Workflow workflow, String metadataKey, WorkflowAppContext workflowAppContext) {
        if (workflow != null && workflow.getMetadata() != null && workflow.getMetadata().containsKey(metadataKey)) {
            return workflow.getMetadata().get(metadataKey);
        }

        if (workflow != null && workflowAppContext != null &&
                workflowAppContext.getApplicationProperties().containsKey(APP_PROPERTIES_BASE + workflow.getId() + "." + metadataKey)) {
            return workflowAppContext.getApplicationProperty(APP_PROPERTIES_BASE + workflow.getId() + "." + metadataKey);
        }

        LOGGER.warn("Could not resolve state metadata: {}", metadataKey);
        return "";
    }

    /**
     * @see <a href="https://github.com/serverlessworkflow/specification/blob/master/specification.md#Using-Functions-For-RESTful-Service-Invocations">Using Functions For RESTful Service
     *      Invocations</a>
     * @param function to extract the OpenApi URI
     * @return the OpenApi URI if found, or an empty string if not
     */
    public static String getOpenApiURI(FunctionDefinition function) {
        if (isOpenApiOperation(function)) {
            return function.getOperation().substring(0, function.getOperation().indexOf(OPENAPI_OPERATION_SEPARATOR));
        }
        return "";
    }

    /**
     * @see <a href="https://github.com/serverlessworkflow/specification/blob/master/specification.md#Using-Functions-For-RESTful-Service-Invocations">Using Functions For RESTful Service
     *      Invocations</a>
     * @param function to extract the OpenApi operationId
     * @return the OpenApi operationId if found, otherwise an empty string
     */
    public static String getOpenApiOperationId(FunctionDefinition function) {
        final String uri = getOpenApiURI(function);
        if (uri.isEmpty()) {
            return "";
        }
        return function.getOperation().substring(uri.length() + 1);
    }

    /**
     * Checks whether or not the Function definition is an OpenApi operation
     *
     * @param function to verify
     * @return true if the given function refers to an OpenApi operation
     */
    public static boolean isOpenApiOperation(FunctionDefinition function) {
        return function.getOperation() != null && function.getOperation().contains(OPENAPI_OPERATION_SEPARATOR);
    }

}
