/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.kie.definition.KnowledgePackage;
import org.kie.definition.process.Process;
import org.kie.definition.rule.Query;
import org.kie.definition.rule.Rule;
import org.kie.definition.type.FactType;
import org.kie.event.knowledgebase.KnowledgeBaseEventListener;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.InternalKnowledgeBase;
import org.kie.runtime.Environment;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;

public class ProcessBaseImpl implements InternalKnowledgeBase {
    
	private Map<String, Process> processes = new HashMap<String, Process>();
	private Map<String, KnowledgePackage> packages = new HashMap<String, KnowledgePackage>();
	private List<KnowledgeBaseEventListener> listeners = new ArrayList<KnowledgeBaseEventListener>();

	public void addEventListener(KnowledgeBaseEventListener listener) {
        listeners.add(listener);
    }

    public void removeEventListener(KnowledgeBaseEventListener listener) {
        listeners.remove(listener);
    }
    
    public Collection<KnowledgeBaseEventListener> getKnowledgeBaseEventListeners() {
        return listeners;
    }

    public void addKnowledgePackage(KnowledgePackage knowledgePackage) {
    	packages.put(knowledgePackage.getName(), knowledgePackage);
    	for (Process process: knowledgePackage.getProcesses()) {
    		processes.put(process.getId(), process);
    	}
    }

    public void addKnowledgePackages(Collection<KnowledgePackage> knowledgePackages) {
        for ( KnowledgePackage knowledgePackage : knowledgePackages ) {
            addKnowledgePackage(knowledgePackage);
        }
    }

    public Collection<KnowledgePackage> getKnowledgePackages() {
        return packages.values();
    }

    public StatefulKnowledgeSession newStatefulKnowledgeSession() {
    	return newStatefulKnowledgeSession(new SessionConfiguration(), EnvironmentFactory.newEnvironment());
    }
    
    public StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeSessionConfiguration conf, Environment environment) {
        return new StatefulProcessSession(this, conf, environment);
    }  
    
    public Collection<StatefulKnowledgeSession> getStatefulKnowledgeSessions() {
        throw new UnsupportedOperationException("Getting stateful sessions not supported");
    }
    
    public StatelessKnowledgeSession newStatelessKnowledgeSession() {
        throw new UnsupportedOperationException("Stateless sessions not supported");
    }
    
    public StatelessKnowledgeSession newStatelessKnowledgeSession(KnowledgeSessionConfiguration conf) {        
        throw new UnsupportedOperationException("Stateless sessions not supported");
    } 

    public void removeKnowledgePackage(String packageName) {
    	packages.remove(packageName);
    }

    public void removeRule(String packageName, String ruleName) {
        throw new UnsupportedOperationException();
    }
    
    public void removeQuery(String packageName, String queryName) {
        throw new UnsupportedOperationException();
    }    

    public void removeFunction(String packageName, String ruleName) {
        throw new UnsupportedOperationException();
    }

    public void removeProcess(String processId) {
    	processes.remove(processId);
    }
    
    public FactType getFactType(String packageName, String typeName) {
        throw new UnsupportedOperationException();
    }

    public KnowledgePackage getKnowledgePackage(String packageName) {
        return packages.get(packageName);
    }

    public Process getProcess(String processId) {
    	return processes.get(processId);
    }
    
    public Collection<Process> getProcesses() {
    	return processes.values();
    }

    public Rule getRule(String packageName, String ruleName) {
        throw new UnsupportedOperationException();
    }
    
    public Query getQuery(String packageName, String queryName) {
        throw new UnsupportedOperationException();
    }

	public RuleBase getRuleBase() {
		return null;
	}

	public Set<String> getEntryPointIds() {
		throw new UnsupportedOperationException("Entry points not supported");
	}
    
}
