/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.bpmn2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Additional BusinesRuleTask support that allows to decouple rules from processes - as default BusinessRuleTask
 * uses exact same working memory (kie session) as process which essentially means same kbase.
 * To allow better separation and maintainability BusinessRuleTaskHandler is provided that supports:
 * <ul>
 * <li>DRL stateful</li>
 * <li>DRL stateless</li>
 * <li>DMN</li>
 * </ul>
 * Type of runtime is selected by Language data input and if not given defaults to DRL stateless.
 * <p>
 * Session type can be given by KieSessionType data input and session name can be given as KieSessionName property -these apply to DRL only.
 * <p>
 * DMN support following data inputs:
 * <ul>
 * <li>Namespace - DMN namespace to be used - mandatory</li>
 * <li>Model - DMN model to be used - mandatory</li>
 * <li>Decision - DMN decision name to be used - optional</li>
 * </ul>
 * <p>
 * Results returned will be then put back into the data outputs. <br/>
 * <br/>
 * DRL handling is based on same names for data input and output as that is then used as correlation.<br/>
 * DMN handling receives all data from DMNResult.<br/>
 */
public class BusinessRuleTaskHandler extends AbstractLogOrThrowWorkItemHandler implements Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(BusinessRuleTaskHandler.class);

    protected static final String STATELESS_TYPE = "stateless";
    protected static final String STATEFULL_TYPE = "statefull";

    protected static final String DRL_LANG = "DRL";
    protected static final String DMN_LANG = "DMN";

    private KieServices kieServices = KieServices.get();
    private KieCommands commandsFactory = kieServices.getCommands();
    private KieContainer kieContainer;
    private KieScanner kieScanner;

    public BusinessRuleTaskHandler(String groupId,
                                   String artifactId,
                                   String version) {
        this(groupId,
             artifactId,
             version,
             -1);
    }

    public BusinessRuleTaskHandler(String groupId,
                                   String artifactId,
                                   String version,
                                   long scannerInterval) {
        logger.debug("About to create KieContainer for {}, {}, {} with scanner interval {}",
                     groupId,
                     artifactId,
                     version,
                     scannerInterval);
        kieContainer = kieServices.newKieContainer(kieServices.newReleaseId(groupId,
                                                                            artifactId,
                                                                            version));

        if (scannerInterval > 0) {
            kieScanner = kieServices.newKieScanner(kieContainer);
            kieScanner.start(scannerInterval);
            logger.debug("Scanner started for {} with poll interval set to {}",
                         kieContainer,
                         scannerInterval);
        }
    }

    public void executeWorkItem(WorkItem workItem,
                                final WorkItemManager manager) {

        Map<String, Object> parameters = new HashMap<>(workItem.getParameters());
        String language = (String) parameters.remove("Language");
        if (language == null) {
            language = DRL_LANG;
        }
        String kieSessionName = (String) parameters.remove("KieSessionName");
        String kieSessionType = (String) parameters.remove("KieSessionType");
        if (kieSessionType == null) {
            kieSessionType = STATELESS_TYPE;
        }

        Map<String, Object> results = new HashMap<>();
        try {
            logger.debug("Facts to be inserted into working memory {}",
                         parameters);
            if (DRL_LANG.equalsIgnoreCase(language)) {
                if (STATEFULL_TYPE.equalsIgnoreCase(kieSessionType)) {
                    handleStatefull(workItem,
                                    kieSessionName,
                                    parameters,
                                    results);
                } else {
                    handleStateless(workItem,
                                    kieSessionName,
                                    parameters,
                                    results);
                }
            } else if (DMN_LANG.equalsIgnoreCase(language)) {
                handleDMN(workItem,
                          parameters,
                          results);
            } else {
                throw new IllegalArgumentException("Not supported language type " + language);
            }
            logger.debug("Facts retrieved from working memory {}",
                         results);
            manager.completeWorkItem(workItem.getId(),
                                     results);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // no-op
    }

    @Override
    public void close() {
        if (kieScanner != null) {
            kieScanner.shutdown();
            logger.debug("Scanner shutdown for kie container {}",
                         kieContainer);
        }
        kieContainer.dispose();
    }

    protected void handleStatefull(WorkItem workItem,
                                   String kieSessionName,
                                   Map<String, Object> parameters,
                                   Map<String, Object> results) {
        logger.debug("Evalating rules in statefull session with name {}",
                     kieSessionName);
        Map<String, FactHandle> factHandles = new HashMap<String, FactHandle>();
        KieSession kieSession = kieContainer.newKieSession(kieSessionName);
        for (Entry<String, Object> entry : parameters.entrySet()) {
            String inputKey = workItem.getId() + "_" + entry.getKey();

            factHandles.put(inputKey,
                            kieSession.insert(entry.getValue()));
        }
        int fired = kieSession.fireAllRules();
        logger.debug("{} rules fired",
                     fired);
        for (Entry<String, FactHandle> entry : factHandles.entrySet()) {

            Object object = kieSession.getObject(entry.getValue());
            String key = entry.getKey().replaceAll(workItem.getId() + "_",
                                                   "");
            results.put(key,
                        object);

            kieSession.delete(entry.getValue());
        }
        factHandles.clear();
    }

    protected void handleStateless(WorkItem workItem,
                                   String kieSessionName,
                                   Map<String, Object> parameters,
                                   Map<String, Object> results) {
        logger.debug("Evalating rules in stateless session with name {}",
                     kieSessionName);
        StatelessKieSession kieSession = kieContainer.newStatelessKieSession(kieSessionName);
        List<Command<?>> commands = new ArrayList<Command<?>>();

        for (Entry<String, Object> entry : parameters.entrySet()) {
            String inputKey = workItem.getId() + "_" + entry.getKey();

            commands.add(commandsFactory.newInsert(entry.getValue(),
                                                   inputKey,
                                                   true,
                                                   null));
        }
        commands.add(commandsFactory.newFireAllRules("Fired"));
        BatchExecutionCommand executionCommand = commandsFactory.newBatchExecution(commands);
        ExecutionResults executionResults = kieSession.execute(executionCommand);
        logger.debug("{} rules fired",
                     executionResults.getValue("Fired"));

        for (Entry<String, Object> entry : parameters.entrySet()) {
            String inputKey = workItem.getId() + "_" + entry.getKey();
            String key = entry.getKey().replaceAll(workItem.getId() + "_",
                                                   "");
            results.put(key,
                        executionResults.getValue(inputKey));
        }
    }

    protected void handleDMN(WorkItem workItem,
                             Map<String, Object> parameters,
                             Map<String, Object> results) {
        String namespace = (String) parameters.remove("Namespace");
        String model = (String) parameters.remove("Model");
        String decision = (String) parameters.remove("Decision");

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        DMNModel dmnModel = runtime.getModel(namespace,
                                             model);
        if (dmnModel == null) {
            throw new IllegalArgumentException("DMN model '" + model + "' not found with namespace '" + namespace + "'");
        }
        DMNResult dmnResult = null;
        DMNContext context = runtime.newContext();

        for (Entry<String, Object> entry : parameters.entrySet()) {
            context.set(entry.getKey(),
                        entry.getValue());
        }

        if (decision != null && !decision.isEmpty()) {
            dmnResult = runtime.evaluateDecisionByName(dmnModel,
                                                       decision,
                                                       context);
        } else {
            dmnResult = runtime.evaluateAll(dmnModel,
                                            context);
        }

        if (dmnResult.hasErrors()) {
            String errors = dmnResult.getMessages(Severity.ERROR).stream()
                    .map(message -> message.toString())
                    .collect(Collectors.joining(", "));

            throw new RuntimeException("DMN result errors:: " + errors);
        }

        results.putAll(dmnResult.getContext().getAll());
    }

    public KieContainer getKieContainer() {
        return this.kieContainer;
    }
}
