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
package org.kie.kogito.jitexecutor.bpmn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.drools.io.InputStreamResource;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.compiler.xml.core.SemanticModules;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.kie.kogito.jitexecutor.bpmn.responses.JITBPMNValidationResult;
import org.kie.kogito.jitexecutor.common.requests.MultipleResourcesPayload;
import org.kie.kogito.jitexecutor.common.requests.ResourceWithURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JITBPMNServiceImpl implements JITBPMNService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JITBPMNServiceImpl.class);

    private static final RuleFlowProcessValidator PROCESS_VALIDATOR = RuleFlowProcessValidator.getInstance();

    private static final SemanticModules BPMN_SEMANTIC_MODULES = new SemanticModules();

    private static String ERROR_TEMPLATE = "Uri: %s - Process id: %s - name : %s - error : %s";

    private static final String NODE_ERROR_TEMPLATE = "Uri: %s - Process id: %s - name: %s - Node: %s [%s] - error: %s";

    // REST Work Item Handler validation constants
    private static final String REST_TASK_TYPE = "Rest";
    private static final String ACCESS_TOKEN_STRATEGY_PARAM = "AccessTokenAcquisitionStrategy";
    private static final String REST_SERVICE_CALL_TASK_ID_PARAM = "RestServiceCallTaskId";
    private static final String URL_PARAM = "Url";
    private static final String METHOD_PARAM = "Method";
    private static final String PROTOCOL_PARAM = "Protocol";
    private static final String HOST_PARAM = "Host";
    private static final String PORT_PARAM = "Port";
    private static final String CONTENT_DATA_PARAM = "ContentData";
    private static final String REQUEST_TIMEOUT_PARAM = "RequestTimeout";

    private static final Set<String> VALID_STRATEGIES = new HashSet<>(
            Arrays.asList("propagated", "configured", "none"));
    private static final Set<String> VALID_HTTP_METHODS = new HashSet<>(
            Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
    private static final Set<String> METHODS_WITH_BODY = new HashSet<>(
            Arrays.asList("POST", "PUT", "PATCH"));
    private static final Set<String> VALID_PROTOCOLS = new HashSet<>(
            Arrays.asList("http", "https"));

    private static final Pattern VALID_TASK_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("^#\\{.+\\}$");
    private static final Pattern URL_PLACEHOLDER_PATTERN = Pattern.compile("\\{[^}]+\\}");
    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("^QUERY_(.+)$");
    private static final Pattern HEADER_PARAM_PATTERN = Pattern.compile("^HEADER_(.+)$");

    static {
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNSemanticModule());
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNExtensionsSemanticModule());
        BPMN_SEMANTIC_MODULES.addSemanticModule(new BPMNDISemanticModule());
    }

    @Override
    public JITBPMNValidationResult validatePayload(MultipleResourcesPayload payload) {
        Collection<String> errors = new ArrayList<>();
        for (ResourceWithURI resourceWithURI : payload.getResources()) {
            errors.addAll(collectErrors(resourceWithURI.getContent(), resourceWithURI.getURI()));
        }
        return new JITBPMNValidationResult(errors);
    }

    @Override
    public JITBPMNValidationResult validateModel(String modelXML) {
        LOGGER.trace("Received\n{}", modelXML);
        Collection<String> errors = collectErrors(modelXML, null);
        return new JITBPMNValidationResult(errors);
    }

    static Collection<String> collectErrors(String modelXML, String resourceUri) {
        LOGGER.trace("Received\n{}", modelXML);
        Collection<String> toReturn;
        Collection<Process> processes;
        try {
            processes = parseModelXml(modelXML);
            if (processes.isEmpty()) {
                String error = "No process found";
                if (resourceUri != null) {
                    error += " on resource " + resourceUri;
                }
                toReturn = Collections.singleton(error);
            } else {
                toReturn = new ArrayList<>();
                ProcessValidationError[] processValidationErrors = validateProcesses(processes);
                for (ProcessValidationError processValidationError : processValidationErrors) {
                    toReturn.add(getErrorString(processValidationError, resourceUri));
                }
                // REST Work Item Handler validation - parse XML to get parameter values
                Map<String, Map<String, String>> nodeParameters = parseNodeParametersFromXml(modelXML);
                for (Process process : processes) {
                    toReturn.addAll(validateRestWorkItems(process, resourceUri, nodeParameters));
                }
            }
        } catch (Throwable e) {
            String error = e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : e.toString();
            toReturn = Collections.singleton(error);
            LOGGER.error("Fail to validate", e);
        }
        return toReturn;
    }

    static ProcessValidationError[] validateProcesses(Collection<Process> processes) {
        ProcessValidationError[] toReturn = new ProcessValidationError[0];
        for (Process toValidate : processes) {
            ProcessValidationError[] toAdd = PROCESS_VALIDATOR.validateProcess(toValidate);
            ProcessValidationError[] temp = new ProcessValidationError[toReturn.length + toAdd.length];
            System.arraycopy(toReturn, 0, temp, 0, toReturn.length);
            System.arraycopy(toAdd, 0, temp, (temp.length - toAdd.length), toAdd.length);
            toReturn = temp;
        }
        return toReturn;
    }

    static Collection<Process> parseModelXml(String modelXML) {
        Resource r = new InputStreamResource(new ByteArrayInputStream(modelXML.getBytes()));
        return parseModelResource(r);
    }

    static Collection<Process> parseModelResource(Resource r) {
        try (Reader reader = r.getReader()) {
            XmlProcessReader xmlReader = new XmlProcessReader(
                    BPMN_SEMANTIC_MODULES,
                    Thread.currentThread().getContextClassLoader());
            return xmlReader.read(reader);
        } catch (SAXException | IOException e) {
            throw new RuntimeException("Could not parse " + r, e);
        }
    }

    static String getErrorString(ProcessValidationError processValidationError, String resourceUri) {
        Process failed = processValidationError.getProcess();
        String uri = resourceUri != null ? resourceUri : "(unknown)";
        return String.format(ERROR_TEMPLATE, uri, failed.getId(), failed.getName(), processValidationError.getMessage());
    }

    private static Collection<String> validateRestWorkItems(Process process, String resourceUri, Map<String, Map<String, String>> nodeParameters) {
        Collection<String> errors = new ArrayList<>();

        if (process == null) {
            return errors;
        }

        org.kie.api.definition.process.Node[] nodes = ((org.jbpm.workflow.core.WorkflowProcess) process).getNodes();
        if (nodes == null) {
            return errors;
        }

        for (org.kie.api.definition.process.Node node : nodes) {
            if (node instanceof WorkItemNode) {
                WorkItemNode workItemNode = (WorkItemNode) node;
                Work work = workItemNode.getWork();

                if (work != null && REST_TASK_TYPE.equals(work.getName())) {
                    // Try to get parameters using the node's UUID from metadata
                    String nodeUuid = (String) workItemNode.getMetaData().get("UniqueId");
                    Map<String, String> params = null;

                    if (nodeUuid != null) {
                        params = nodeParameters.get(nodeUuid);
                    }

                    if (params == null) {
                        params = nodeParameters.get(String.valueOf(workItemNode.getId()));
                    }

                    errors.addAll(validateRestWorkItem(workItemNode, process, resourceUri, params));
                }
            }
        }

        return errors;
    }

    private static Collection<String> validateRestWorkItem(WorkItemNode node, Process process, String resourceUri, Map<String, String> nodeParams) {
        Collection<String> errors = new ArrayList<>();
        Work work = node.getWork();

        errors.addAll(validateMethod(work, node, process, resourceUri, nodeParams));

        errors.addAll(validateUrl(work, node, process, resourceUri, nodeParams));

        errors.addAll(validateProtocolHostPort(work, node, process, resourceUri, nodeParams));

        errors.addAll(validateContentData(work, node, process, resourceUri, nodeParams));

        errors.addAll(validateQueryAndHeaderParams(work, node, process, resourceUri, nodeParams));

        errors.addAll(validateRequestTimeout(work, node, process, resourceUri, nodeParams));

        errors.addAll(validateAccessTokenStrategy(work, node, process, resourceUri, nodeParams));

        errors.addAll(validateRestServiceCallTaskId(work, node, process, resourceUri, nodeParams));

        return errors;
    }

    private static Collection<String> validateMethod(Work work, WorkItemNode node, Process process, String resourceUri, Map<String, String> nodeParams) {
        Collection<String> errors = new ArrayList<>();
        String method = getParameterValue(work, nodeParams, METHOD_PARAM);

        if (method != null && !method.trim().isEmpty()) {

            if (isExpression(method)) {
                return errors;
            }

            String upperMethod = method.toUpperCase();
            if (!VALID_HTTP_METHODS.contains(upperMethod)) {
                errors.add(formatNodeError(resourceUri, process, node,
                        String.format("Method must be one of: GET, POST, PUT, PATCH, DELETE, or an expression like #{expr}. Found: '%s'", method)));
            }
        }

        return errors;
    }

    private static Collection<String> validateUrl(Work work, WorkItemNode node, Process process, String resourceUri, Map<String, String> nodeParams) {
        Collection<String> errors = new ArrayList<>();
        String url = getParameterValue(work, nodeParams, URL_PARAM);

        if (url == null || url.trim().isEmpty()) {
            errors.add(formatNodeError(resourceUri, process, node,
                    "Url is required for REST service call tasks"));
        } else if (!isExpression(url)) {

            if (!isValidUrlFormat(url)) {
                errors.add(formatNodeError(resourceUri, process, node,
                        String.format("Url must be a valid URL format. Found: '%s'", url)));
            } else {
                boolean isRelativeUrl = url.startsWith("/") && !url.startsWith("//");
                if (isRelativeUrl) {
                    String protocol = getParameterValue(work, nodeParams, PROTOCOL_PARAM);
                    String host = getParameterValue(work, nodeParams, HOST_PARAM);

                    if ((protocol == null || protocol.trim().isEmpty()) && (host == null || host.trim().isEmpty())) {
                        errors.add(formatNodeError(resourceUri, process, node,
                                String.format("Url is a relative path '%s'. Either provide a complete URL (e.g., 'https://example.com/users') or specify Protocol and Host parameters", url)));
                    }
                }
            }
        }

        return errors;
    }

    private static Collection<String> validateProtocolHostPort(Work work, WorkItemNode node, Process process, String resourceUri, Map<String, String> nodeParams) {
        Collection<String> errors = new ArrayList<>();
        String url = getParameterValue(work, nodeParams, URL_PARAM);
        String protocol = getParameterValue(work, nodeParams, PROTOCOL_PARAM);
        String host = getParameterValue(work, nodeParams, HOST_PARAM);
        String port = getParameterValue(work, nodeParams, PORT_PARAM);

        if (protocol != null && !protocol.trim().isEmpty() && !isExpression(protocol)) {
            String lowerProtocol = protocol.toLowerCase();
            if (!VALID_PROTOCOLS.contains(lowerProtocol)) {
                errors.add(formatNodeError(resourceUri, process, node,
                        String.format("Protocol must be 'http' or 'https'. Found: '%s'", protocol)));
            }

            if (url != null && !url.trim().isEmpty() && !isExpression(url)) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    errors.add(formatNodeError(resourceUri, process, node,
                            "Protocol should not be specified when Url already contains the protocol"));
                }
            }
        }

        if (host != null && !host.trim().isEmpty()) {
            if (url != null && !url.trim().isEmpty() && !isExpression(url)) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    errors.add(formatNodeError(resourceUri, process, node,
                            "Host should not be specified when Url already contains the host"));
                }
            }
        }

        if (port != null && !port.trim().isEmpty() && !isExpression(port)) {
            try {
                int portNum = Integer.parseInt(port);
                if (portNum < 1 || portNum > 65535) {
                    errors.add(formatNodeError(resourceUri, process, node,
                            String.format("Port must be between 1 and 65535. Found: %d", portNum)));
                }
            } catch (NumberFormatException e) {
                errors.add(formatNodeError(resourceUri, process, node,
                        String.format("Port must be a valid integer. Found: '%s'", port)));
            }

            if (url != null && !url.trim().isEmpty() && !isExpression(url)) {
                if (url.matches(".*:\\d+.*")) {
                    errors.add(formatNodeError(resourceUri, process, node,
                            "Port should not be specified when Url already contains the port"));
                }
            }
        }

        return errors;
    }

    private static Collection<String> validateContentData(Work work, WorkItemNode node, Process process, String resourceUri, Map<String, String> nodeParams) {
        Collection<String> errors = new ArrayList<>();
        String contentData = getParameterValue(work, nodeParams, CONTENT_DATA_PARAM);
        String method = getParameterValue(work, nodeParams, METHOD_PARAM);

        if (contentData != null && !contentData.trim().isEmpty()) {

            String effectiveMethod = (method != null && !method.trim().isEmpty()) ? method.toUpperCase() : "GET";

            if (!isExpression(effectiveMethod) && !METHODS_WITH_BODY.contains(effectiveMethod)) {
                errors.add(formatNodeError(resourceUri, process, node,
                        String.format("ContentData should only be used with methods that support request bodies (POST, PUT, PATCH). Current method: '%s'", effectiveMethod)));
            }
        }

        return errors;
    }

    private static Collection<String> validateQueryAndHeaderParams(Work work, WorkItemNode node, Process process, String resourceUri, Map<String, String> nodeParams) {
        Collection<String> errors = new ArrayList<>();

        Set<String> allParamNames = new HashSet<>(work.getParameters().keySet());
        if (nodeParams != null) {
            allParamNames.addAll(nodeParams.keySet());
        }

        for (String paramName : allParamNames) {

            if (QUERY_PARAM_PATTERN.matcher(paramName).matches()) {
                String value = getParameterValue(work, nodeParams, paramName);
                if (value == null || value.trim().isEmpty()) {
                    errors.add(formatNodeError(resourceUri, process, node,
                            String.format("Query parameter '%s' has an empty value", paramName)));
                }
            }

            if (HEADER_PARAM_PATTERN.matcher(paramName).matches()) {
                String value = getParameterValue(work, nodeParams, paramName);
                if (value == null || value.trim().isEmpty()) {
                    errors.add(formatNodeError(resourceUri, process, node,
                            String.format("Header parameter '%s' has an empty value", paramName)));
                }
            }
        }

        return errors;
    }

    private static Collection<String> validateRequestTimeout(Work work, WorkItemNode node, Process process, String resourceUri, Map<String, String> nodeParams) {
        Collection<String> errors = new ArrayList<>();
        String timeout = getParameterValue(work, nodeParams, REQUEST_TIMEOUT_PARAM);

        if (timeout != null && !timeout.trim().isEmpty() && !isExpression(timeout)) {
            try {
                long timeoutValue = Long.parseLong(timeout);
                if (timeoutValue < 0) {
                    errors.add(formatNodeError(resourceUri, process, node,
                            String.format("RequestTimeout must be a non-negative number. Found: %d", timeoutValue)));
                }
            } catch (NumberFormatException e) {
                errors.add(formatNodeError(resourceUri, process, node,
                        String.format("RequestTimeout must be a valid number. Found: '%s'", timeout)));
            }
        }

        return errors;
    }

    private static Collection<String> validateAccessTokenStrategy(Work work, WorkItemNode node, Process process, String resourceUri, Map<String, String> nodeParams) {
        Collection<String> errors = new ArrayList<>();
        String strategy = getParameterValue(work, nodeParams, ACCESS_TOKEN_STRATEGY_PARAM);

        if (strategy == null || strategy.trim().isEmpty()) {
            errors.add(formatNodeError(resourceUri, process, node,
                    "AccessTokenAcquisitionStrategy is required for REST service call tasks"));
        } else {
            // Expressions are not allowed for this parameter
            if (isExpression(strategy)) {
                errors.add(formatNodeError(resourceUri, process, node,
                        String.format("AccessTokenAcquisitionStrategy cannot be an expression. Must be one of: propagated, configured, none. Found: '%s'", strategy)));
            } else if (!VALID_STRATEGIES.contains(strategy.toLowerCase())) {
                errors.add(formatNodeError(resourceUri, process, node,
                        String.format("AccessTokenAcquisitionStrategy must be one of: propagated, configured, none. Found: '%s'", strategy)));
            }
        }

        return errors;
    }

    private static Collection<String> validateRestServiceCallTaskId(Work work, WorkItemNode node, Process process, String resourceUri, Map<String, String> nodeParams) {
        Collection<String> errors = new ArrayList<>();
        String strategy = getParameterValue(work, nodeParams, ACCESS_TOKEN_STRATEGY_PARAM);
        String taskId = getParameterValue(work, nodeParams, REST_SERVICE_CALL_TASK_ID_PARAM);

        // RestServiceCallTaskId is required when strategy is "configured"
        if (strategy != null && "configured".equalsIgnoreCase(strategy.trim())) {
            if (taskId == null || taskId.trim().isEmpty()) {
                errors.add(formatNodeError(resourceUri, process, node,
                        "RestServiceCallTaskId is required when AccessTokenAcquisitionStrategy is 'configured'"));
            } else {
                // Expressions are not allowed for this parameter
                if (isExpression(taskId)) {
                    errors.add(formatNodeError(resourceUri, process, node,
                            String.format("RestServiceCallTaskId cannot be an expression. Found: '%s'", taskId)));
                } else if (!VALID_TASK_ID_PATTERN.matcher(taskId).matches()) {
                    errors.add(formatNodeError(resourceUri, process, node,
                            String.format("RestServiceCallTaskId must contain only alphanumeric characters and underscores. Found: '%s'", taskId)));
                }
            }
        }

        return errors;
    }

    private static String getParameterValue(Work work, Map<String, String> nodeParams, String paramName) {
        Object value = work.getParameter(paramName);
        if (value != null) {
            return value.toString();
        }

        if (nodeParams != null && nodeParams.containsKey(paramName)) {
            return nodeParams.get(paramName);
        }

        return null;
    }

    private static Map<String, Map<String, String>> parseNodeParametersFromXml(String modelXML) {
        Map<String, Map<String, String>> result = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            // Disable DOCTYPE declarations to prevent XXE attacks
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new org.xml.sax.InputSource(new StringReader(modelXML)));

            org.w3c.dom.NodeList tasks = doc.getElementsByTagNameNS("*", "task");
            if (tasks.getLength() == 0) {
                tasks = doc.getElementsByTagName("task");
            }

            for (int i = 0; i < tasks.getLength(); i++) {
                org.w3c.dom.Element task = (org.w3c.dom.Element) tasks.item(i);
                String taskId = task.getAttribute("id");

                if (taskId != null && !taskId.isEmpty()) {
                    Map<String, String> params = new HashMap<>();

                    // Find dataInputAssociation elements
                    org.w3c.dom.NodeList associations = task.getElementsByTagNameNS("*", "dataInputAssociation");
                    if (associations.getLength() == 0) {
                        associations = task.getElementsByTagName("dataInputAssociation");
                    }

                    for (int j = 0; j < associations.getLength(); j++) {
                        org.w3c.dom.Element association = (org.w3c.dom.Element) associations.item(j);

                        // Get targetRef (parameter name)
                        org.w3c.dom.NodeList targetRefs = association.getElementsByTagNameNS("*", "targetRef");
                        if (targetRefs.getLength() == 0) {
                            targetRefs = association.getElementsByTagName("targetRef");
                        }

                        if (targetRefs.getLength() > 0) {
                            String targetRef = targetRefs.item(0).getTextContent().trim();

                            // Extract parameter name from targetRef
                            String paramName = getParameterNameFromDataInputId(task, targetRef);

                            // Get the value from assignment/from element
                            org.w3c.dom.NodeList assignments = association.getElementsByTagNameNS("*", "assignment");
                            if (assignments.getLength() == 0) {
                                assignments = association.getElementsByTagName("assignment");
                            }

                            if (assignments.getLength() > 0) {
                                org.w3c.dom.Element assignment = (org.w3c.dom.Element) assignments.item(0);
                                org.w3c.dom.NodeList froms = assignment.getElementsByTagNameNS("*", "from");
                                if (froms.getLength() == 0) {
                                    froms = assignment.getElementsByTagName("from");
                                }

                                if (froms.getLength() > 0) {
                                    String value = froms.item(0).getTextContent().trim();
                                    if (paramName != null && value != null && !value.isEmpty()) {
                                        params.put(paramName, value);
                                    }
                                }
                            }
                        }
                    }

                    if (!params.isEmpty()) {
                        result.put(taskId, params);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse BPMN XML for parameter extraction", e);
        }

        return result;
    }

    private static String getParameterNameFromDataInputId(org.w3c.dom.Element task, String dataInputId) {
        try {

            org.w3c.dom.NodeList ioSpecs = task.getElementsByTagNameNS("*", "ioSpecification");
            if (ioSpecs.getLength() == 0) {
                ioSpecs = task.getElementsByTagName("ioSpecification");
            }

            if (ioSpecs.getLength() > 0) {
                org.w3c.dom.Element ioSpec = (org.w3c.dom.Element) ioSpecs.item(0);

                org.w3c.dom.NodeList dataInputs = ioSpec.getElementsByTagNameNS("*", "dataInput");
                if (dataInputs.getLength() == 0) {
                    dataInputs = ioSpec.getElementsByTagName("dataInput");
                }

                for (int i = 0; i < dataInputs.getLength(); i++) {
                    org.w3c.dom.Element dataInput = (org.w3c.dom.Element) dataInputs.item(i);
                    String id = dataInput.getAttribute("id");
                    String name = dataInput.getAttribute("name");

                    if (dataInputId.equals(id) && name != null && !name.isEmpty()) {
                        return name;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to map dataInput id to parameter name", e);
        }
        return null;
    }

    private static boolean isExpression(String value) {
        return value != null && EXPRESSION_PATTERN.matcher(value.trim()).matches();
    }

    private static boolean isValidUrlFormat(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        if (URL_PLACEHOLDER_PATTERN.matcher(url).find()) {
            return true;
        }

        return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/");
    }

    private static String formatNodeError(String resourceUri, Process process, WorkItemNode node, String errorMessage) {
        String uri = resourceUri != null ? resourceUri : "(unknown)";
        String processId = process != null ? process.getId() : "(unknown)";
        String processName = process != null ? process.getName() : "(unknown)";
        String nodeName = node.getName() != null ? node.getName() : "(unnamed)";
        String nodeId = String.valueOf(node.getId());
        return String.format(NODE_ERROR_TEMPLATE, uri, processId, processName, nodeName, nodeId, errorMessage);
    }
}
