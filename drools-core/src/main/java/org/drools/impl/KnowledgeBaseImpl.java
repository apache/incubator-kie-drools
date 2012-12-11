/*
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

package org.drools.impl;

import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.definitions.rule.impl.RuleImpl;
import org.drools.event.AfterFunctionRemovedEvent;
import org.drools.event.AfterPackageAddedEvent;
import org.drools.event.AfterPackageRemovedEvent;
import org.drools.event.AfterProcessAddedEvent;
import org.drools.event.AfterProcessRemovedEvent;
import org.drools.event.AfterRuleAddedEvent;
import org.drools.event.AfterRuleBaseLockedEvent;
import org.drools.event.AfterRuleBaseUnlockedEvent;
import org.drools.event.AfterRuleRemovedEvent;
import org.drools.event.BeforeFunctionRemovedEvent;
import org.drools.event.BeforePackageAddedEvent;
import org.drools.event.BeforePackageRemovedEvent;
import org.drools.event.BeforeProcessAddedEvent;
import org.drools.event.BeforeProcessRemovedEvent;
import org.drools.event.BeforeRuleAddedEvent;
import org.drools.event.BeforeRuleBaseLockedEvent;
import org.drools.event.BeforeRuleBaseUnlockedEvent;
import org.drools.event.BeforeRuleRemovedEvent;
import org.drools.event.knowlegebase.impl.AfterFunctionRemovedEventImpl;
import org.drools.event.knowlegebase.impl.AfterKiePackageAddedEventImpl;
import org.drools.event.knowlegebase.impl.AfterKiePackageRemovedEventImpl;
import org.drools.event.knowlegebase.impl.AfterKnowledgeBaseLockedEventImpl;
import org.drools.event.knowlegebase.impl.AfterKnowledgeBaseUnlockedEventImpl;
import org.drools.event.knowlegebase.impl.AfterProcessAddedEventImpl;
import org.drools.event.knowlegebase.impl.AfterProcessRemovedEventImpl;
import org.drools.event.knowlegebase.impl.AfterRuleAddedEventImpl;
import org.drools.event.knowlegebase.impl.AfterRuleRemovedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeFunctionRemovedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeKiePackageAddedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeKiePackageRemovedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeKnowledgeBaseLockedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeKnowledgeBaseUnlockedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeProcessAddedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeProcessRemovedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeRuleAddedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeRuleRemovedEventImpl;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.rule.Package;
import org.kie.KnowledgeBase;
import org.kie.definition.KiePackage;
import org.kie.definition.KnowledgePackage;
import org.kie.definition.process.Process;
import org.kie.definition.rule.Query;
import org.kie.definition.rule.Rule;
import org.kie.definition.type.FactType;
import org.kie.event.kiebase.KieBaseEventListener;
import org.kie.runtime.Environment;
import org.kie.runtime.KieSession;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKieSession;
import org.kie.runtime.StatelessKnowledgeSession;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnowledgeBaseImpl
    implements
    InternalKnowledgeBase,
    Externalizable {
    public RuleBase                                                          ruleBase;
    
    // This is just a hack, so spring can find the list of generated classes
    public List<List<String>> jaxbClasses;

    public Map<KieBaseEventListener, KnowledgeBaseEventListenerWrapper> mappedKnowledgeBaseListeners;

    public KnowledgeBaseImpl() {

    }

    public KnowledgeBaseImpl(RuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.mappedKnowledgeBaseListeners = new HashMap<KieBaseEventListener, KnowledgeBaseEventListenerWrapper>();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        this.ruleBase.writeExternal( out );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        ruleBase = new ReteooRuleBase();
        ruleBase.readExternal( in );
    }
    
    public RuleBase getRuleBase() {
        return ruleBase;
    }

    public void addEventListener(KieBaseEventListener listener) {
        if (!mappedKnowledgeBaseListeners.containsKey(listener)) {
            KnowledgeBaseEventListenerWrapper wrapper = new KnowledgeBaseEventListenerWrapper( this, listener );
            mappedKnowledgeBaseListeners.put( listener, wrapper );
            ruleBase.addEventListener( wrapper );
        }
    }

    public void removeEventListener(KieBaseEventListener listener) {
        KnowledgeBaseEventListenerWrapper wrapper = this.mappedKnowledgeBaseListeners.remove( listener );
        this.ruleBase.removeEventListener( wrapper );
    }
    
    public Collection<KieBaseEventListener> getKnowledgeBaseEventListeners() {
        return Collections.unmodifiableCollection( this.mappedKnowledgeBaseListeners.keySet() );
    }

    public void addKnowledgePackage(KnowledgePackage knowledgePackage) {
        ruleBase.addPackage( ((KnowledgePackageImp) knowledgePackage).pkg );
    }

    public void addKnowledgePackages(Collection<KnowledgePackage> knowledgePackages) {
        List<Package> list = new ArrayList<Package>();
        for ( KnowledgePackage knowledgePackage : knowledgePackages ) {
            list.add( ((KnowledgePackageImp) knowledgePackage).pkg  );
        }
        ((ReteooRuleBase)ruleBase).addPackages( list);
    }

    public Collection<KnowledgePackage> getKnowledgePackages() {
        Package[] pkgs = ruleBase.getPackages();
        List<KnowledgePackage> list = new ArrayList<KnowledgePackage>( pkgs.length );
        for ( Package pkg : pkgs ) {
            list.add( new KnowledgePackageImp( pkg ) );
        }
        return list;
    }

    public StatefulKnowledgeSession newStatefulKnowledgeSession() {
        return newStatefulKnowledgeSession(null, EnvironmentFactory.newEnvironment() );
    }
    
    public StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeSessionConfiguration conf, Environment environment) {
        // NOTE if you update here, you'll also need to update the JPAService
        if ( conf == null ) {
            conf = SessionConfiguration.getDefaultInstance();
        }
        
        if ( environment == null ) {
            environment = EnvironmentFactory.newEnvironment();
        }
        
        ReteooStatefulSession session = (ReteooStatefulSession) this.ruleBase.newStatefulSession( (SessionConfiguration) conf, environment );
        return (StatefulKnowledgeSession) session.getKnowledgeRuntime();
    }
    
    public Collection<StatefulKnowledgeSession> getStatefulKnowledgeSessions()
    {
        Collection<StatefulKnowledgeSession> c = new ArrayList<StatefulKnowledgeSession>();
        StatefulSession[] sss = this.ruleBase.getStatefulSessions();
        if (sss != null) {
            for (StatefulSession ss : sss) {
                if (ss instanceof ReteooStatefulSession) {
                    c.add(new StatefulKnowledgeSessionImpl((ReteooStatefulSession)ss, this));
                }
            }
        }
        return c;
    }
    
    public StatelessKnowledgeSession newStatelessKnowledgeSession() {
        return new StatelessKnowledgeSessionImpl( (InternalRuleBase) this.ruleBase, null, null );
    }
    
    public StatelessKnowledgeSession newStatelessKnowledgeSession(KnowledgeSessionConfiguration conf) {
        return new StatelessKnowledgeSessionImpl( (InternalRuleBase) this.ruleBase, null, conf );
    }

    public void removeKnowledgePackage(String packageName) {
        this.ruleBase.removePackage( packageName );
    }

    public void removeRule(String packageName,
                           String ruleName) {
        this.ruleBase.removeRule( packageName,
                                  ruleName );
    }
    
    public void removeQuery(String packageName,
                            String queryName) {
        this.ruleBase.removeQuery( packageName,
                                   queryName );
    }

    public void removeFunction(String packageName,
                           String ruleName) {
        this.ruleBase.removeFunction( packageName,
                                  ruleName );
    }

    public void removeProcess(String processId) {
        this.ruleBase.removeProcess( processId );
    }
    
    public FactType getFactType(String packageName,
                                String typeName) {
        return this.ruleBase.getFactType(packageName + "." + typeName);
    }

    public KnowledgePackage getKnowledgePackage(String packageName) {
        Package pkg = this.ruleBase.getPackage( packageName );
        if ( pkg != null ) {
            return new KnowledgePackageImp( pkg );
        } else {
            return null;
        }
    }

    public Process getProcess(String processId) {
        return ((InternalRuleBase) this.ruleBase).getProcess(processId);
    }

    public Collection<Process> getProcesses() {
        return Arrays.asList(((InternalRuleBase) this.ruleBase).getProcesses());
    }

    public Rule getRule(String packageName,
                        String ruleName) {
        Package p = this.ruleBase.getPackage( packageName );
        return p == null ? null : p.getRule( ruleName );
    }
    
    public Query getQuery(String packageName,
                          String queryName) {
        return ( Query ) this.ruleBase.getPackage( packageName ).getRule( queryName );
    }
    
    public Set<String> getEntryPointIds() {
        return this.ruleBase.getEntryPointIds();
    }

    public static class KnowledgeBaseEventListenerWrapper
        implements
        org.drools.event.RuleBaseEventListener {
        private KieBaseEventListener listener;
        private KnowledgeBase              kbase;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub

        }

        public KnowledgeBaseEventListenerWrapper(KnowledgeBase kbase,
                                                 KieBaseEventListener listener) {
            this.listener = listener;
        }

        public void afterFunctionRemoved(AfterFunctionRemovedEvent event) {
            this.listener.afterFunctionRemoved( new AfterFunctionRemovedEventImpl( this.kbase,
                                                                                   event.getFunction() ) );
        }

        public void afterPackageAdded(AfterPackageAddedEvent event) {
            this.listener.afterKiePackageAdded(new AfterKiePackageAddedEventImpl(this.kbase,
                    new KnowledgePackageImp(event.getPackage())));
        }

        public void afterPackageRemoved(AfterPackageRemovedEvent event) {
            this.listener.afterKiePackageRemoved(new AfterKiePackageRemovedEventImpl(this.kbase,
                    new KnowledgePackageImp(event.getPackage())));
        }

        public void afterRuleAdded(AfterRuleAddedEvent event) {
            this.listener.afterRuleAdded( new AfterRuleAddedEventImpl( this.kbase,
                                                                       new RuleImpl( event.getRule() ) ) );
        }

        public void afterRuleBaseLocked(AfterRuleBaseLockedEvent event) {
            this.listener.afterKieBaseLocked(new AfterKnowledgeBaseLockedEventImpl(this.kbase));
        }

        public void afterRuleBaseUnlocked(AfterRuleBaseUnlockedEvent event) {
            this.listener.afterKieBaseUnlocked(new AfterKnowledgeBaseUnlockedEventImpl(this.kbase));
        }

        public void afterRuleRemoved(AfterRuleRemovedEvent event) {
            this.listener.afterRuleRemoved( new AfterRuleRemovedEventImpl( this.kbase,
                                                                           new RuleImpl( event.getRule() ) ) );
        }

        public void beforeFunctionRemoved(BeforeFunctionRemovedEvent event) {
            this.listener.beforeFunctionRemoved( new BeforeFunctionRemovedEventImpl( this.kbase,
                                                                                     event.getFunction() ) );
        }

        public void beforePackageAdded(BeforePackageAddedEvent event) {
            this.listener.beforeKiePackageAdded(new BeforeKiePackageAddedEventImpl(this.kbase,
                    new KnowledgePackageImp(event.getPackage())));
        }

        public void beforePackageRemoved(BeforePackageRemovedEvent event) {
            this.listener.beforeKiePackageRemoved(new BeforeKiePackageRemovedEventImpl(this.kbase,
                    new KnowledgePackageImp(event.getPackage())));
        }

        public void beforeRuleAdded(BeforeRuleAddedEvent event) {
            this.listener.beforeRuleAdded( new BeforeRuleAddedEventImpl( this.kbase,
                                                                         new RuleImpl( event.getRule() ) ) );
        }

        public void beforeRuleBaseLocked(BeforeRuleBaseLockedEvent event) {
            this.listener.beforeKieBaseLocked(new BeforeKnowledgeBaseLockedEventImpl(this.kbase));
        }

        public void beforeRuleBaseUnlocked(BeforeRuleBaseUnlockedEvent event) {
            this.listener.beforeKieBaseUnlocked(new BeforeKnowledgeBaseUnlockedEventImpl(this.kbase));
        }

        public void beforeRuleRemoved(BeforeRuleRemovedEvent event) {
            this.listener.beforeRuleRemoved( new BeforeRuleRemovedEventImpl( this.kbase,
                                                                             new RuleImpl( event.getRule() ) ) );
        }

		public void beforeProcessAdded(BeforeProcessAddedEvent event) {
			this.listener.beforeProcessAdded(new BeforeProcessAddedEventImpl( this.kbase,
                                                                              event.getProcess() ));
		}

		public void afterProcessAdded(AfterProcessAddedEvent event) {
			this.listener.afterProcessAdded(new AfterProcessAddedEventImpl( this.kbase,
                                                                            event.getProcess() ));
		}

		public void beforeProcessRemoved(BeforeProcessRemovedEvent event) {
			this.listener.beforeProcessRemoved(new BeforeProcessRemovedEventImpl( this.kbase,
                                                                                  event.getProcess() ));
		}

		public void afterProcessRemoved(AfterProcessRemovedEvent event) {
			this.listener.afterProcessRemoved(new AfterProcessRemovedEventImpl( this.kbase,
                                                                                event.getProcess() ));
		}
    }

    public KieSession newKieSession(KnowledgeSessionConfiguration conf,
                                    Environment environment) {
        return newStatefulKnowledgeSession( conf, environment );
    }

    public KieSession newKieSession() {
        return newStatefulKnowledgeSession();
    }

    public Collection<? extends KieSession> getKieSessions() {
        return getStatefulKnowledgeSessions();
    }

    public StatelessKieSession newStatelessKieSession(KnowledgeSessionConfiguration conf) {
        return newStatelessKnowledgeSession( conf );
    }

    public StatelessKieSession newStatelessKieSession() {
        return newStatelessKnowledgeSession();
    }

    public Collection<KiePackage> getKiePackages() {
        return getKiePackages();
    }

    public KiePackage getKiePackage(String packageName) {
        return getKnowledgePackage(packageName);
    }

    public void removeKiePackage(String packageName) {
        removeKnowledgePackage(packageName);
    }
}
