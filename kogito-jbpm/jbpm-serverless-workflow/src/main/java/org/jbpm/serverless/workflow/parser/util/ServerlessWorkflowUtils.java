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
package org.jbpm.serverless.workflow.parser.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.drools.core.util.StringUtils;
import org.jbpm.serverless.workflow.api.Workflow;
import org.jbpm.serverless.workflow.api.branches.Branch;
import org.jbpm.serverless.workflow.api.choices.DefaultChoice;
import org.jbpm.serverless.workflow.api.events.EventDefinition;
import org.jbpm.serverless.workflow.api.interfaces.State;
import org.jbpm.serverless.workflow.api.mapper.BaseObjectMapper;
import org.jbpm.serverless.workflow.api.mapper.JsonObjectMapper;
import org.jbpm.serverless.workflow.api.mapper.YamlObjectMapper;
import org.jbpm.serverless.workflow.api.states.DefaultState;
import org.jbpm.serverless.workflow.api.states.ParallelState;
import org.jbpm.serverless.workflow.api.states.SubflowState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class ServerlessWorkflowUtils {

    public static final String DEFAULT_WORKFLOW_FORMAT = "json";
    public static final String ALTERNATE_WORKFLOW_FORMAT = "yml";
    public static final String DEFAULT_JSONPATH_CONFIG = "com.jayway.jsonpath.Configuration jsonPathConfig = com.jayway.jsonpath.Configuration.builder()" +
            ".mappingProvider(new com.jayway.jsonpath.spi.mapper.JacksonMappingProvider())" +
            ".jsonProvider(new com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider()).build(); ";


    private static final Logger LOGGER = LoggerFactory.getLogger(ServerlessWorkflowUtils.class);

    private ServerlessWorkflowUtils() {}

    public static BaseObjectMapper getObjectMapper(String workflowFormat) {
        if(workflowFormat != null && workflowFormat.equalsIgnoreCase(DEFAULT_WORKFLOW_FORMAT)) {
            return new JsonObjectMapper();
        }

        if(workflowFormat != null && workflowFormat.equalsIgnoreCase(ALTERNATE_WORKFLOW_FORMAT)) {
            return new YamlObjectMapper();
        }

        LOGGER.error("unable to determine workflow format {}", workflowFormat);
        throw new IllegalArgumentException("invalid workflow format");
    }

    public static String readWorkflowFile(Reader reader) {
        return StringUtils.readFileAsString(reader);
    }

    public static State getWorkflowStartState(Workflow workflow) {
        return workflow.getStates().stream()
                .filter(ws -> ws.getStart() != null)
                .findFirst().get();
    }

    public static List<State> getStatesByType(Workflow workflow, DefaultState.Type type) {
        return workflow.getStates().stream()
                .filter(ws -> ws.getType() == type)
                .collect(Collectors.toList());
    }

    public static List<State> getWorkflowEndStates(Workflow workflow) {
        return workflow.getStates().stream()
                .filter(ws -> ws.getEnd() != null)
                .collect(Collectors.toList());
    }

    public static boolean includesSupportedStates(Workflow workflow) {
        for(State state : workflow.getStates()) {
            if(!state.getType().equals(DefaultState.Type.EVENT)
                    && !state.getType().equals(DefaultState.Type.OPERATION)
                    && !state.getType().equals(DefaultState.Type.DELAY)
                    && !state.getType().equals(DefaultState.Type.SUBFLOW)
                    && !state.getType().equals(DefaultState.Type.RELAY)
                    && !state.getType().equals(DefaultState.Type.SWITCH)
                    && !state.getType().equals(DefaultState.Type.PARALLEL)) {
                return false;
            }

            if(state.getType().equals(DefaultState.Type.PARALLEL)) {
                if(!supportedParallelState((ParallelState) state)) {
                    LOGGER.warn("unsupported parallel state");
                    return false;
                }
            }

        }

        return true;
    }

    public static boolean supportedParallelState(ParallelState parallelState) {
        // currently branches must exist and states included can
        // be single subflow states only
        // this will be improved in future
        if(parallelState.getBranches() != null && parallelState.getBranches().size() > 0) {
            for(Branch branch : parallelState.getBranches()) {
                if(branch.getStates() == null || branch.getStates().size() != 1 || !(branch.getStates().get(0) instanceof SubflowState)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static EventDefinition getWorkflowEventFor(Workflow workflow, String eventName) {
        return workflow.getEvents().stream()
                .filter(wt -> wt.getName().equals(eventName))
                .findFirst().get();
    }


    public static String sysOutFunctionScript(String script) {
        String retStr = DEFAULT_JSONPATH_CONFIG;
        retStr += "java.lang.String toPrint = \"\";";
        retStr += getJsonPathScript(script);
        retStr += "System.out.println(toPrint);";

        return retStr;
    }

    public static String scriptFunctionScript(String script) {
        String retStr = DEFAULT_JSONPATH_CONFIG;
        retStr += getJsonPathScript(script);
        return retStr;
    }

    public static String conditionScript(String path, DefaultChoice.Operator operator, String value) {

        if(path.startsWith("$.")) {
            path = path.substring(2);
        }

        String workflowDataToInteger = "return java.lang.Integer.parseInt(workflowdata.get(\"";

        String retStr = "";
        if(operator == DefaultChoice.Operator.EQUALS) {
            retStr += "return workflowdata.get(\"" + path + "\").textValue().equals(\"" + value + "\");";
        } else if(operator == DefaultChoice.Operator.GREATER_THAN) {
            retStr += workflowDataToInteger + path + "\").textValue()) > " + value + ";";
        } else if(operator == DefaultChoice.Operator.GREATER_THAN_EQUALS) {
            retStr += workflowDataToInteger + path + "\").textValue()) >= " + value + ";";
        } else if(operator == DefaultChoice.Operator.LESS_THAN ) {
            retStr += workflowDataToInteger + path + "\").textValue()) < " + value + ";";
        } else if(operator == DefaultChoice.Operator.LESS_THAN_EQUALS) {
            retStr += workflowDataToInteger + path + "\").textValue()) <= " + value + ";";
        }

        return retStr;
    }

    public static String getJsonPathScript(String script) {

        if(script.indexOf("$") >= 0) {

            String replacement = "toPrint += com.jayway.jsonpath.JsonPath.using(jsonPathConfig)" +
                    ".parse(((com.fasterxml.jackson.databind.JsonNode)kcontext.getVariable(\"workflowdata\")))" +
                    ".read(\"@@.$1\", com.fasterxml.jackson.databind.JsonNode.class).textValue();";
            script =  script.replaceAll("\\$.([A-Za-z]+)", replacement);
            script = script.replaceAll("@@", Matcher.quoteReplacement("$"));
            return script;
        } else {
            return script;
        }
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

        } catch(JsonProcessingException e) {
            LOGGER.warn("unable to set inject script: {}", e.getMessage());
            return "";
        }
    }

}
