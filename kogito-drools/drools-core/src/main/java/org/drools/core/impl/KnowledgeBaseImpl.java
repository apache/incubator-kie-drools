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

package org.drools.core.impl;

import org.drools.core.RuleBase;
import org.drools.core.SessionConfiguration;
import org.drools.core.StatefulSession;
import org.drools.core.common.AbstractWorkingMemory;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.definitions.impl.KnowledgePackageImp;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.AfterFunctionRemovedEvent;
import org.drools.core.event.AfterPackageAddedEvent;
import org.drools.core.event.AfterPackageRemovedEvent;
import org.drools.core.event.AfterProcessAddedEvent;
import org.drools.core.event.AfterProcessRemovedEvent;
import org.drools.core.event.AfterRuleAddedEvent;
import org.drools.core.event.AfterRuleBaseLockedEvent;
import org.drools.core.event.AfterRuleBaseUnlockedEvent;
import org.drools.core.event.AfterRuleRemovedEvent;
import org.drools.core.event.BeforeFunctionRemovedEvent;
import org.drools.core.event.BeforePackageAddedEvent;
import org.drools.core.event.BeforePackageRemovedEvent;
import org.drools.core.event.BeforeProcessAddedEvent;
import org.drools.core.event.BeforeProcessRemovedEvent;
import org.drools.core.event.BeforeRuleAddedEvent;
import org.drools.core.event.BeforeRuleBaseLockedEvent;
import org.drools.core.event.BeforeRuleBaseUnlockedEvent;
import org.drools.core.event.BeforeRuleRemovedEvent;
import org.drools.core.event.knowlegebase.impl.AfterFunctionRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterKiePackageAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterKiePackageRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterKnowledgeBaseLockedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterKnowledgeBaseUnlockedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterProcessAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterProcessRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterRuleAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterRuleRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeFunctionRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeKiePackageAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeKiePackageRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeKnowledgeBaseLockedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeKnowledgeBaseUnlockedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeProcessAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeProcessRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeRuleAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeRuleRemovedEventImpl;
import org.drools.core.reteoo.ReteooRuleBase;
import org.drools.core.rule.Package;
import org.kie.internal.KnowledgeBase;
import org.kie.api.definition.KiePackage;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;

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
        this( null );
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
    
    public Collection<KieBaseEventListener> getKieBaseEventListeners() {
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
    
    public StatefulKnowledgeSession newStatefulKnowledgeSession(KieSessionConfiguration conf, Environment environment) {
        // NOTE if you update here, you'll also need to update the JPAService
        if ( conf == null ) {
            conf = SessionConfiguration.getDefaultInstance();
        }
        
        if ( environment == null ) {
            environment = EnvironmentFactory.newEnvironment();
        }
        
        AbstractWorkingMemory session = (AbstractWorkingMemory) this.ruleBase.newStatefulSession( (SessionConfiguration) conf, environment );
        return (StatefulKnowledgeSession) session.getKnowledgeRuntime();
    }
    
    public Collection<StatefulKnowledgeSession> getStatefulKnowledgeSessions()
    {
        Collection<StatefulKnowledgeSession> c = new ArrayList<StatefulKnowledgeSession>();
        StatefulSession[] sss = this.ruleBase.getStatefulSessions();
        if (sss != null) {
            for (StatefulSession ss : sss) {
                if (ss instanceof AbstractWorkingMemory) {
                    InternalKnowledgeRuntime kruntime = ((AbstractWorkingMemory) ss).getKnowledgeRuntime();
                    if( kruntime instanceof StatefulKnowledgeSession ) {
                        c.add((StatefulKnowledgeSessionImpl) kruntime);
                    }
                }
            }
        }
        return c;
    }
    
    public StatelessKnowledgeSession newStatelessKnowledgeSession() {
        return new StatelessKnowledgeSessionImpl( (InternalRuleBase) this.ruleBase, null, null );
    }
    
    public StatelessKnowledgeSession newStatelessKnowledgeSession(KieSessionConfiguration conf) {
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
        org.drools.core.event.RuleBaseEventListener {
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

    public KieSession newKieSession(KieSessionConfiguration conf,
                                    Environment environment) {
        return newStatefulKnowledgeSession( conf, environment );
    }

    public KieSession newKieSession() {
        return newStatefulKnowledgeSession();
    }

    public Collection<? extends KieSession> getKieSessions() {
        return getStatefulKnowledgeSessions();
    }

    public StatelessKieSession newStatelessKieSession(KieSessionConfiguration conf) {
        return newStatelessKnowledgeSession( conf );
    }

    public StatelessKieSession newStatelessKieSession() {
        return newStatelessKnowledgeSession();
    }

    public Collection<KiePackage> getKiePackages() {
        Object o = getKnowledgePackages();
        return (Collection<KiePackage>) o;
    }

    public KiePackage getKiePackage(String packageName) {
        return getKnowledgePackage(packageName);
    }

    public void removeKiePackage(String packageName) {
        removeKnowledgePackage(packageName);
    }
}
