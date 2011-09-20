/*
 * Copyright 2010, 2011 JBoss Inc
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

package org.drools.marshalling.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.SessionConfiguration;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.concurrent.CommandExecutor;
import org.drools.concurrent.ExecutorService;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.Marshaller;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.spi.ExecutorServiceFactory;
import org.drools.spi.GlobalResolver;

public class DefaultMarshaller
    implements
    Marshaller {
    KnowledgeBase                  kbase;
    GlobalResolver                 globalResolver;
    RuleBaseConfiguration          ruleBaseConfig;
    MarshallingConfiguration       marshallingConfig;
    ObjectMarshallingStrategyStore strategyStore;

    public DefaultMarshaller(KnowledgeBase kbase,
                             MarshallingConfiguration marshallingConfig) {
        this.kbase = kbase;
        this.ruleBaseConfig = (ruleBaseConfig != null) ? ruleBaseConfig : RuleBaseConfiguration.getDefaultInstance();
        this.marshallingConfig = marshallingConfig;
        this.strategyStore = this.marshallingConfig.getObjectMarshallingStrategyStore();
    }

    
    public StatefulKnowledgeSession unmarshall(final InputStream stream) throws IOException,
                                                                             ClassNotFoundException {
        return unmarshall( stream, null, null );
    }
    /* (non-Javadoc)
     * @see org.drools.marshalling.Marshaller#read(java.io.InputStream, org.drools.common.InternalRuleBase, int, org.drools.concurrent.ExecutorService)
     */
    public StatefulKnowledgeSession unmarshall(final InputStream stream,
                                               KnowledgeSessionConfiguration config,
                                               Environment environment) throws IOException,
                                                                             ClassNotFoundException {
        if ( config == null ) {
            config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        }
        
        if ( environment == null ) {
            environment = KnowledgeBaseFactory.newEnvironment();
        }
        
        MarshallerReaderContext context = new MarshallerReaderContext( stream,
                                                                       (InternalRuleBase) ((KnowledgeBaseImpl) kbase).ruleBase,
                                                                       RuleBaseNodes.getNodeMap( (InternalRuleBase) ((KnowledgeBaseImpl) kbase).ruleBase ),
                                                                       this.strategyStore,
                                                                       this.marshallingConfig.isMarshallProcessInstances(),
                                                                       this.marshallingConfig.isMarshallWorkItems() ,
                                                                       environment);

        int id = ((ReteooRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase).nextWorkingMemoryCounter();
        RuleBaseConfiguration conf = ((ReteooRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase).getConfiguration();
        ExecutorService executor = ExecutorServiceFactory.createExecutorService( conf.getExecutorService() );

        ReteooStatefulSession session = InputMarshaller.readSession( context,
                                                                     id,
                                                                     executor,
                                                                     environment,
                                                                     (SessionConfiguration) config );
        executor.setCommandExecutor( new CommandExecutor( session ) );
        context.close();
        if ( ((SessionConfiguration) config).isKeepReference() ) {
            ((ReteooRuleBase)((KnowledgeBaseImpl)this.kbase).ruleBase).addStatefulSession( session );
        }
        return (StatefulKnowledgeSession) session.getKnowledgeRuntime();

    }

    public void unmarshall(final InputStream stream,
                           final StatefulKnowledgeSession ksession) throws IOException,
                                                                  ClassNotFoundException {    
        MarshallerReaderContext context = new MarshallerReaderContext( stream,
                                                                       (InternalRuleBase) ((KnowledgeBaseImpl) kbase).ruleBase,
                                                                       RuleBaseNodes.getNodeMap( (InternalRuleBase) ((KnowledgeBaseImpl) kbase).ruleBase ),
                                                                       this.strategyStore,
                                                                       this.marshallingConfig.isMarshallProcessInstances(),
                                                                       marshallingConfig.isMarshallWorkItems() , 
                                                                       ksession.getEnvironment());

        InputMarshaller.readSession( (ReteooStatefulSession) ((StatefulKnowledgeSessionImpl)ksession).session,
                                     context );
        context.close();

    }

    public void marshall(final OutputStream stream,
                         final StatefulKnowledgeSession ksession) throws IOException {
        marshall(stream, ksession, ksession.getSessionClock().getCurrentTime() );
    }
    /* (non-Javadoc)
     * @see org.drools.marshalling.Marshaller#write(java.io.OutputStream, org.drools.common.InternalRuleBase, org.drools.StatefulSession)
     */
    public void marshall(final OutputStream stream,
                         final StatefulKnowledgeSession ksession,
                         final long clockTime) throws IOException {
        MarshallerWriteContext context = new MarshallerWriteContext( stream,
                                                                     (InternalRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase(),
                                                                     (InternalWorkingMemory) ((StatefulKnowledgeSessionImpl) ksession).session,
                                                                     RuleBaseNodes.getNodeMap( (InternalRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase() ),
                                                                     this.strategyStore,
                                                                     this.marshallingConfig.isMarshallProcessInstances(),
                                                                     this.marshallingConfig.isMarshallWorkItems(), 
                                                                     ksession.getEnvironment());
        context.clockTime = clockTime;
        OutputMarshaller.writeSession( context );
        context.close();
    }
    
    public MarshallingConfiguration getMarshallingConfiguration() {
    	return marshallingConfig;
    }

}
