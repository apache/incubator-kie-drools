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

package org.drools.core.reteoo;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

import org.drools.core.SessionConfiguration;
import org.drools.core.StatefulSession;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.RuleBaseUpdateListener;
import org.drools.core.spi.RuleBaseUpdateListenerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.marshalling.Marshaller;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;

public class ReteooStatefulSession extends ReteooWorkingMemory
    implements
    StatefulSession,
    Externalizable {

    private static final long         serialVersionUID = 510l;

    private transient List            ruleBaseListeners;
    
    public ReteooStatefulSession() {
        super();
    }
    
    public ReteooStatefulSession(final int id,
                                 final InternalRuleBase ruleBase) {
        this( id,
              ruleBase,
              SessionConfiguration.getDefaultInstance(),
              EnvironmentFactory.newEnvironment() );
    }

    public ReteooStatefulSession(final int id,
                                 final InternalRuleBase ruleBase,
                                 final SessionConfiguration config,
                                 final Environment environment) {
        super( id,
               ruleBase,
               config,
               environment );
    }

    public ReteooStatefulSession(final int id,
                                 final InternalRuleBase ruleBase,
                                 final FactHandleFactory handleFactory,
                                 final InternalFactHandle initialFactHandle,
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
    }
    
    public byte[] bytes;
    
    public void writeExternal(ObjectOutput out) throws IOException {
        // all we do is create marshall to a byte[] and write to the stream
        StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) getKnowledgeRuntime();
        
        Marshaller marshaller = MarshallerFactory.newMarshaller( ksession.getKieBase(), new ObjectMarshallingStrategy[] { MarshallerFactory.newSerializeMarshallingStrategy() }   );
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        marshaller.marshall( stream, (StatefulKnowledgeSession) getKnowledgeRuntime() );
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

    public void dispose() {
        this.ruleBase.disposeStatefulSession( this );
        super.dispose();
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

}
