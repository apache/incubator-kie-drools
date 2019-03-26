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

/**
 *
 */
package org.jbpm.compiler.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.jbpm.workflow.core.Node;
import org.kie.api.definition.process.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessBuildData {

    private static final Logger logger = LoggerFactory.getLogger(ProcessBuildData.class);

    private static List<ProcessDataEventListenerProvider> providers = collectProviders();

    private List<Process> processes = new ArrayList<Process>();
    private Map<Long, Node> nodes = new HashMap<Long, Node>();
    private Map<String, Object> metaData = new HashMap<String, Object>();

    private List<ProcessDataEventListener> listeners = new ArrayList<ProcessDataEventListener>();

    public ProcessBuildData() {
        if (providers != null) {
            for (ProcessDataEventListenerProvider provider : providers) {
                listeners.add(provider.newInstance());
            }
        }
    }

    public List<Process> getProcesses() {
        for (Process process : processes) {
            onComplete(process);
        }

        return processes;
    }

    public void addProcess(Process process) {
        onProcess(process);
        this.processes.add(process);
    }

    public void setProcesses(List<Process> process) {
        this.processes = process;
    }

    public Map<Long, Node> getNodes() {
        return nodes;
    }
    public boolean addNode(Node node) {
        onNode(node);
        return( this.nodes.put( node.getId(), node ) != null );
    }

    public Node getNode(Long id) {
        return this.nodes.get( id );
    }

    public Object getMetaData(String name) {
        return metaData.get(name);
    }

    public void setMetaData(String name, Object data) {
        onMetaData(name, data);
        this.metaData.put(name, data);
    }

    // listener support

    protected void onNode(Node node) {
        for (ProcessDataEventListener listener : listeners) {
            listener.onNodeAdded(node);
        }
    }

    protected void onProcess(Process process) {
        for (ProcessDataEventListener listener : listeners) {
            listener.onProcessAdded(process);
        }
    }

    protected void onMetaData(String name, Object data) {
        for (ProcessDataEventListener listener : listeners) {
            listener.onMetaDataAdded(name, data);
        }
    }

    protected void onComplete(Process process) {
        for (ProcessDataEventListener listener : listeners) {
            listener.onComplete(process);
        }
    }

    public void onBuildComplete(Process process) {
        for (ProcessDataEventListener listener : listeners) {
            listener.onBuildComplete(process);
        }
    }

    private static List<ProcessDataEventListenerProvider> collectProviders() {
        ServiceLoader<ProcessDataEventListenerProvider> availableProviders = ServiceLoader.load(ProcessDataEventListenerProvider.class);
        List<ProcessDataEventListenerProvider> collected = new ArrayList<ProcessDataEventListenerProvider>();
        try {
            for (ProcessDataEventListenerProvider provider : availableProviders) {
                collected.add(provider);
            }
        } catch (Throwable e) {
            logger.debug("Unable to collect process data event listeners due to {}", e.getMessage());
        }
        return collected;
    }
}
