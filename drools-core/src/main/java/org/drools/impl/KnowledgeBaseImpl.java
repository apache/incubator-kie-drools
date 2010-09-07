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

package org.drools.impl;

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

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.command.CommandService;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.common.InternalRuleBase;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.definition.rule.Query;
import org.drools.definition.rule.Rule;
import org.drools.definition.type.FactType;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.definitions.rule.impl.RuleImpl;
import org.drools.event.AfterFunctionRemovedEvent;
import org.drools.event.AfterPackageAddedEvent;
import org.drools.event.AfterPackageRemovedEvent;
import org.drools.event.AfterRuleAddedEvent;
import org.drools.event.AfterRuleBaseLockedEvent;
import org.drools.event.AfterRuleBaseUnlockedEvent;
import org.drools.event.AfterRuleRemovedEvent;
import org.drools.event.BeforeFunctionRemovedEvent;
import org.drools.event.BeforePackageAddedEvent;
import org.drools.event.BeforePackageRemovedEvent;
import org.drools.event.BeforeRuleAddedEvent;
import org.drools.event.BeforeRuleBaseLockedEvent;
import org.drools.event.BeforeRuleBaseUnlockedEvent;
import org.drools.event.BeforeRuleRemovedEvent;
import org.drools.event.knowledgebase.KnowledgeBaseEventListener;
import org.drools.event.knowlegebase.impl.AfterFunctionRemovedEventImpl;
import org.drools.event.knowlegebase.impl.AfterKnowledgeBaseLockedEventImpl;
import org.drools.event.knowlegebase.impl.AfterKnowledgeBaseUnlockedEventImpl;
import org.drools.event.knowlegebase.impl.AfterKnowledgePackageAddedEventImpl;
import org.drools.event.knowlegebase.impl.AfterKnowledgePackageRemovedEventImpl;
import org.drools.event.knowlegebase.impl.AfterRuleAddedEventImpl;
import org.drools.event.knowlegebase.impl.AfterRuleRemovedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeFunctionRemovedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeKnowledgeBaseLockedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeKnowledgeBaseUnlockedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeKnowledgePackageAddedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeKnowledgePackageRemovedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeRuleAddedEventImpl;
import org.drools.event.knowlegebase.impl.BeforeRuleRemovedEventImpl;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.rule.Package;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

public class KnowledgeBaseImpl
    implements
    InternalKnowledgeBase,
    Externalizable {
    public RuleBase                                                          ruleBase;
    
    // This is just a hack, so spring can find the list of generated classes
    public List<List<String>> jaxbClasses;

    public Map<KnowledgeBaseEventListener, KnowledgeBaseEventListenerWrapper> mappedKnowledgeBaseListeners;

    public KnowledgeBaseImpl() {

    }

    public KnowledgeBaseImpl(RuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.mappedKnowledgeBaseListeners = new HashMap<KnowledgeBaseEventListener, KnowledgeBaseEventListenerWrapper>();
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

    public void addEventListener(KnowledgeBaseEventListener listener) {
        KnowledgeBaseEventListenerWrapper wrapper = new KnowledgeBaseEventListenerWrapper( this,
                                                                                           listener );
        this.mappedKnowledgeBaseListeners.put( listener,
                                               wrapper );
        this.ruleBase.addEventListener( wrapper );

    }

    public void removeEventListener(KnowledgeBaseEventListener listener) {
        KnowledgeBaseEventListenerWrapper wrapper = this.mappedKnowledgeBaseListeners.remove( listener );
        this.ruleBase.removeEventListener( wrapper );
    }
    
    public Collection<KnowledgeBaseEventListener> getKnowledgeBaseEventListeners() {
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
    	return newStatefulKnowledgeSession(new SessionConfiguration(), EnvironmentFactory.newEnvironment() );
    }
    
    public StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeSessionConfiguration conf, Environment environment) {
        // NOTE if you update here, you'll also need to update the JPAService
        if ( conf == null ) {
            conf = new SessionConfiguration();
        }
        
        if ( environment == null ) {
            environment = EnvironmentFactory.newEnvironment();
        }
        
    	CommandService commandService = ((SessionConfiguration) conf).getCommandService(this, environment);
    	if (commandService != null) {
			return new CommandBasedStatefulKnowledgeSession(commandService);
    	} else {
    		ReteooStatefulSession session = (ReteooStatefulSession) this.ruleBase.newStatefulSession( (SessionConfiguration) conf, 
    		                                                                                          environment );
    		return new StatefulKnowledgeSessionImpl( session, this );
    	}
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
        return this.ruleBase.getFactType( packageName + "." + typeName );
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
        return this.ruleBase.getPackage( packageName ).getRule( ruleName );
    }
    
    public Query getQuery(String packageName,
                          String queryName) {
        return this.ruleBase.getPackage( packageName ).getRule( queryName );
    }
    

    public static class KnowledgeBaseEventListenerWrapper
        implements
        org.drools.event.RuleBaseEventListener {
        private KnowledgeBaseEventListener listener;
        private KnowledgeBase              kbase;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub

        }

        public KnowledgeBaseEventListenerWrapper(KnowledgeBase kbase,
                                                 KnowledgeBaseEventListener listener) {
            this.listener = listener;
        }

        public void afterFunctionRemoved(AfterFunctionRemovedEvent event) {
            this.listener.afterFunctionRemoved( new AfterFunctionRemovedEventImpl( this.kbase,
                                                                                   event.getFunction() ) );
        }

        public void afterPackageAdded(AfterPackageAddedEvent event) {
            this.listener.afterKnowledgePackageAdded( new AfterKnowledgePackageAddedEventImpl( this.kbase,
                                                                                               new KnowledgePackageImp( event.getPackage() ) ) );
        }

        public void afterPackageRemoved(AfterPackageRemovedEvent event) {
            this.listener.afterKnowledgePackageRemoved( new AfterKnowledgePackageRemovedEventImpl( this.kbase,
                                                                                                   new KnowledgePackageImp( event.getPackage() ) ) );
        }

        public void afterRuleAdded(AfterRuleAddedEvent event) {
            this.listener.afterRuleAdded( new AfterRuleAddedEventImpl( this.kbase,
                                                                       new RuleImpl( event.getRule() ) ) );
        }

        public void afterRuleBaseLocked(AfterRuleBaseLockedEvent event) {
            this.listener.afterKnowledgeBaseLocked( new AfterKnowledgeBaseLockedEventImpl( this.kbase ) );
        }

        public void afterRuleBaseUnlocked(AfterRuleBaseUnlockedEvent event) {
            this.listener.afterKnowledgeBaseUnlocked( new AfterKnowledgeBaseUnlockedEventImpl( this.kbase ) );
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
            this.listener.beforeKnowledgePackageAdded( new BeforeKnowledgePackageAddedEventImpl( this.kbase,
                                                                                                 new KnowledgePackageImp( event.getPackage() ) ) );
        }

        public void beforePackageRemoved(BeforePackageRemovedEvent event) {
            this.listener.beforeKnowledgePackageRemoved( new BeforeKnowledgePackageRemovedEventImpl( this.kbase,
                                                                                                     new KnowledgePackageImp( event.getPackage() ) ) );
        }

        public void beforeRuleAdded(BeforeRuleAddedEvent event) {
            this.listener.beforeRuleAdded( new BeforeRuleAddedEventImpl( this.kbase,
                                                                         new RuleImpl( event.getRule() ) ) );
        }

        public void beforeRuleBaseLocked(BeforeRuleBaseLockedEvent event) {
            this.listener.beforeKnowledgeBaseLocked( new BeforeKnowledgeBaseLockedEventImpl( this.kbase ) );
        }

        public void beforeRuleBaseUnlocked(BeforeRuleBaseUnlockedEvent event) {
            this.listener.beforeKnowledgeBaseUnlocked( new BeforeKnowledgeBaseUnlockedEventImpl( this.kbase ) );
        }

        public void beforeRuleRemoved(BeforeRuleRemovedEvent event) {
            this.listener.beforeRuleRemoved( new BeforeRuleRemovedEventImpl( this.kbase,
                                                                             new RuleImpl( event.getRule() ) ) );
        }
    }

}
