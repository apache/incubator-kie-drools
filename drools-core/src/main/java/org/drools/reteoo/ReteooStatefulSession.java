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

package org.drools.reteoo;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.FactHandle;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalRuleBase;
import org.drools.concurrent.AssertObject;
import org.drools.concurrent.AssertObjects;
import org.drools.concurrent.ExecutorService;
import org.drools.concurrent.FireAllRules;
import org.drools.concurrent.Future;
import org.drools.concurrent.RetractObject;
import org.drools.concurrent.UpdateObject;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.runtime.Environment;
import org.drools.spi.AgendaFilter;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.RuleBaseUpdateListener;
import org.drools.spi.RuleBaseUpdateListenerFactory;

public class ReteooStatefulSession extends ReteooWorkingMemory
    implements
    StatefulSession,
    Externalizable { 

    private static final long         serialVersionUID = -5360554247241558374L;
    private transient ExecutorService executor;

    private transient List            ruleBaseListeners;
    
    public ReteooStatefulSession() {
        super();
    }
    
    public ReteooStatefulSession(final int id,
                                 final InternalRuleBase ruleBase,
                                 final ExecutorService executorService) {
        this( id,
              ruleBase,
              executorService,
              new SessionConfiguration(),
              EnvironmentFactory.newEnvironment() );
    }

    public ReteooStatefulSession(final int id,
                                 final InternalRuleBase ruleBase,
                                 final ExecutorService executorService,
                                 final SessionConfiguration config,
                                 final Environment environment) {
        super( id,
               ruleBase,
               config,
               environment );
        this.executor = executorService;
    }

    public ReteooStatefulSession(final int id,
                                 final InternalRuleBase ruleBase,
                                 final ExecutorService executorService,
                                 final FactHandleFactory handleFactory,
                                 final InitialFactHandle initialFactHandle,
                                 final long propagationContext,
                                 final SessionConfiguration config,
                                 final InternalAgenda agenda,
                                 final Environment environment) {
        super( id,
               ruleBase,
               handleFactory,
               initialFactHandle,
               propagationContext,
               config,
               agenda,
               environment );
        this.executor = executorService;
    }
    
    public byte[] bytes;
    
    public void writeExternal(ObjectOutput out) throws IOException {
        // all we do is create marshall to a byte[] and write to the stream
        StatefulKnowledgeSessionImpl ksession = new StatefulKnowledgeSessionImpl( this );
        Marshaller marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase() );
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        marshaller.marshall( stream, ksession );
        stream.close();
        
        byte[] bytes = stream.toByteArray();
        out.writeInt( bytes.length );
        out.write( bytes );  
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        bytes = new byte[ in.readInt() ];
        in.readFully( bytes );
    }

    public Future asyncInsert(final Object object) {
        final AssertObject assertObject = new AssertObject( object );
        this.executor.submit( assertObject );
        return assertObject;
    }

    public Future asyncRetract(final FactHandle factHandle) {
        return this.executor.submit( new RetractObject( factHandle ) );
    }

    public Future asyncUpdate(final FactHandle factHandle,
                              final Object object) {
        return this.executor.submit( new UpdateObject( (org.drools.FactHandle)factHandle,
                                                       object ) );
    }

    public Future asyncInsert(final Object[] array) {
        final AssertObjects assertObjects = new AssertObjects( array );
        this.executor.submit( assertObjects );
        return assertObjects;
    }

    public Future asyncInsert(final Collection collection) {
        final AssertObjects assertObjects = new AssertObjects( collection );
        this.executor.submit( assertObjects );
        return assertObjects;
    }
    
    public Future asyncInsert(final Iterable<?> iterable) {
        final AssertObjects assertObjects = new AssertObjects( iterable );
        this.executor.submit( assertObjects );
        return assertObjects;
    }    

    public Future asyncFireAllRules(final AgendaFilter agendaFilter) {
        final FireAllRules fireAllRules = new FireAllRules( agendaFilter );
        this.executor.submit( fireAllRules );
        return fireAllRules;
    }

    public Future asyncFireAllRules() {
        final FireAllRules fireAllRules = new FireAllRules( null );
        this.executor.submit( fireAllRules );
        return fireAllRules;
    }

    public void dispose() {
        this.ruleBase.disposeStatefulSession( this );
    	super.dispose();
        this.executor.shutDown();
    }

    @Override
    protected void finalize() throws Throwable {
    	dispose();
    }

    public List getRuleBaseUpdateListeners() {
        if ( this.ruleBaseListeners == null || this.ruleBaseListeners == Collections.EMPTY_LIST ) {
            String listenerName = this.ruleBase.getConfiguration().getRuleBaseUpdateHandler();
            if ( listenerName != null && listenerName.length() > 0 ) {
                RuleBaseUpdateListener listener = RuleBaseUpdateListenerFactory.createListener( listenerName,
                                                                                                this );
                this.ruleBaseListeners = Collections.singletonList( listener );
            } else {
                this.ruleBaseListeners = Collections.EMPTY_LIST;
            }
        }
        return this.ruleBaseListeners;
    }

    //    public StatefulSession getEntryPoint(String id) {
    //        EntryPoint ep = new EntryPoint( id );
    //        return new EntryPointInterfaceImpl( ep,
    //                                            this );
    //    }

    public ExecutorService getExecutorService() {
        return executor;
    }

    public void setExecutorService(ExecutorService executor) {
        this.executor = executor;
    }
}
