/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.SessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessBaseImpl implements KnowledgeBase {
    
	private Map<String, Process> processes = new HashMap<String, Process>();
	private Map<String, KnowledgePackage> packages = new HashMap<String, KnowledgePackage>();
	private List<KieBaseEventListener> listeners = new ArrayList<KieBaseEventListener>();

    @Override
	public void addEventListener(KieBaseEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeEventListener(KieBaseEventListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public Collection<KieBaseEventListener> getKieBaseEventListeners() {
        return listeners;
    }

//    @Override
//    public void addPackage(InternalKnowledgePackage knowledgePackage) {
//    	packages.put(knowledgePackage.getName(), knowledgePackage);
//    	for (Process process: knowledgePackage.getProcesses()) {
//    		processes.put(process.getId(), process);
//    	}
//    }
//
//    @Override
//    public void addPackages(Collection<InternalKnowledgePackage> knowledgePackages) {
//        for ( InternalKnowledgePackage knowledgePackage : knowledgePackages ) {
//            addPackage(knowledgePackage);
//        }
//    }
    
    @Override
    public void addKnowledgePackages(Collection<KnowledgePackage> kpackages) {
        for ( KnowledgePackage knowledgePackage : kpackages ) {
            addPackage(knowledgePackage);
        }
    }

    public void addPackage(KnowledgePackage knowledgePackage) {
        packages.put(knowledgePackage.getName(), knowledgePackage);
        for (Process process: knowledgePackage.getProcesses()) {
            processes.put(process.getId(), process);
        }
    }
    
    @Override
    public Collection<KnowledgePackage> getKnowledgePackages() {
        return packages.values();
    }

    @Override
    public StatefulKnowledgeSession newStatefulKnowledgeSession() {
    	return newStatefulKnowledgeSession(SessionConfiguration.newInstance(), EnvironmentFactory.newEnvironment());
    }
    
    @Override
    public KieSession newKieSession() {
        return newKieSession(SessionConfiguration.newInstance(), EnvironmentFactory.newEnvironment());
    }
    
    @Override
    public StatefulKnowledgeSession newStatefulKnowledgeSession(KieSessionConfiguration conf, Environment environment) {
        return new StatefulProcessSession(this, conf, environment);
    }  
    
    @Override
    public KieSession newKieSession(KieSessionConfiguration conf, Environment environment) {
        return new StatefulProcessSession(this, conf, environment);
    }  
    
    @Override
    public Collection<StatefulKnowledgeSession> getStatefulKnowledgeSessions() {
        return (Collection<StatefulKnowledgeSession>) unsupported("Stateful sessions are not supported");
    }
    
    @Override
    public Collection<StatefulKnowledgeSession> getKieSessions() {
        return (Collection<StatefulKnowledgeSession>) unsupported("Stateful sessions are not supported");
    }
    
    @Override
    public StatelessKnowledgeSession newStatelessKnowledgeSession() {
        return (StatelessKnowledgeSession) unsupported("Stateless sessions are not supported");
    }
    
    @Override
    public StatelessKnowledgeSession newStatelessKieSession() {
        return (StatelessKnowledgeSession) unsupported("Stateless sessions are not supported");
    }
    
    @Override
    public StatelessKnowledgeSession newStatelessKnowledgeSession(KieSessionConfiguration conf) {        
        return (StatelessKnowledgeSession) unsupported("Stateless sessions are not supported");
    } 

    public StatelessKnowledgeSession newStatelessKieSession(KieSessionConfiguration conf) {        
        return (StatelessKnowledgeSession) unsupported("Stateless sessions are not supported");
    } 

    public void removeKnowledgePackage(String packageName) {
    	packages.remove(packageName);
    }

    @Override
    public void removeRule(String packageName, String ruleName) {
        unsupported(null);
    }
    
    @Override
    public void removeQuery(String packageName, String queryName) {
        unsupported(null);
    }    

    public void removeFunction(String packageName, String ruleName) {
        unsupported(null);
    }

    @Override
    public void removeProcess(String processId) {
    	processes.remove(processId);
    }
    
    @Override
    public FactType getFactType(String packageName, String typeName) {
        return (FactType) unsupported(null);
    }

    @Override
    public KnowledgePackage getKnowledgePackage(String packageName) {
        return packages.get(packageName);
    }

    @Override
    public Process getProcess(String processId) {
    	return processes.get(processId);
    }
    
    @Override
    public Collection<Process> getProcesses() {
    	return processes.values();
    }

    @Override
    public Rule getRule(String packageName, String ruleName) {
        return (Rule) unsupported(null);
    }
    
    @Override
    public Query getQuery(String packageName, String queryName) {
        return (Query) unsupported(null);
    }

    @Override
	public Set<String> getEntryPointIds() {
        return (Set<String>) unsupported("Entry points are not supported");
	}

    @Override
    public Collection<KiePackage> getKiePackages() {
        Collection<KiePackage> kPackages = new ArrayList<KiePackage>(packages.size());
        for( KnowledgePackage pkg : packages.values() ) { 
           kPackages.add(pkg);
        }
        return kPackages;
    }

    @Override
    public KiePackage getKiePackage(String packageName) {
        return getKnowledgePackage(packageName);
    }

    @Override
    public void removeKiePackage(String packageName) {
        removeKnowledgePackage(packageName);
    }

    static Object unsupported(String msg) { 
        String methodName = (new Throwable()).getStackTrace()[1].getMethodName();
        StringBuffer errMsg = new StringBuffer(methodName).append(" is not supported on ").append(ProcessBaseImpl.class.getSimpleName());
        if( msg != null ) { 
           errMsg.append(": ").append(msg);
        }
        throw new UnsupportedOperationException(errMsg.toString());
    }

 
    
}
