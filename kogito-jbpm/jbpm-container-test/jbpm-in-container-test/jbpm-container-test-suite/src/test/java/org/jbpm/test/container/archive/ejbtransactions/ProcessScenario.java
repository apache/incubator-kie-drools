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

package org.jbpm.test.container.archive.ejbtransactions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.KieSession;

/**
 * Represents scenario of starting and testing process instance.
 */
public abstract class ProcessScenario {
    protected List<Exception> exceptions = new ArrayList<Exception>();

    protected List<ProcessEventListener> listeners = new ArrayList<ProcessEventListener>();

    protected KieBase kbase;

    protected String processId;

    protected Map<String, Object> params;

    /**
     * Constructor with mandatory parameters:
     * 
     * @param kbase
     *            KnowledgeBase instance containing the process definition
     * @param processId
     *            ID of process to be tested
     * @param params
     *            Parameters which will be passed to process instance.
     */
    public ProcessScenario(KieBase kbase, String processId, Map<String, Object> params) {
        this.kbase = kbase;
        this.processId = processId;
        this.params = params;
    }

    public ProcessScenario(KieBase kbase, String processId, Map<String, Object> params, ProcessEventListener l) {
        this(kbase, processId, params);
        listeners.add(l);
    }

    /**
     * Enables adding of listeners to monitor the sequence flow.
     */
    public void addProcessEventListener(ProcessEventListener listener) {
        listeners.add(listener);
    }

    protected abstract void
        runScenario(String processId, Map<String, Object> params, KieSession ksession);

    /**
     * Starts the process instance in specified knowledge session. Information
     * about the process to run has to be specified in constructor.
     */
    public void runProcess(KieSession ksession) {
        for (ProcessEventListener l : listeners) {
            ksession.addEventListener(l);
        }

        try {
            runScenario(processId, params, ksession);
        } catch (Exception ex) {
            exceptions.add(ex);
        }
    }

    /**
     * Information whether any exception raised during process execution.
     */
    public boolean hasErrors() {
        return !exceptions.isEmpty();
    }

    public List<Exception> getErrors() {
        return exceptions;
    }

    public List<ProcessEventListener> getListeners() {
        return listeners;
    }

    /**
     * Useful when only one listener was registered.
     * 
     * @return the first registered listener.
     */
    public ProcessEventListener getFirstListener() {
        return listeners.isEmpty() ? null : listeners.get(0);
    }

    public KieBase getKbase() {
        return this.kbase;
    }
}
