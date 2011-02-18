/*
 * Copyright 2005 JBoss Inc
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.common.AbstractRuleBase;
import org.drools.common.DefaultFactHandle;
import org.drools.common.DroolsObjectInput;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.concurrent.CommandExecutor;
import org.drools.concurrent.ExecutorService;
import org.drools.event.RuleBaseEventListener;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.management.DroolsManagementAgent;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteAssertAction;
import org.drools.rule.EntryPoint;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.spi.ExecutorServiceFactory;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;

/**
 * Implementation of <code>RuleBase</code>.
 *
 *
 * @version $Id: RuleBaseImpl.java,v 1.5 2005/08/14 22:44:12 mproctor Exp $
 */
public class ReteooRuleBase extends AbstractRuleBase {
    /**
     * DO NOT CHANGE BELLOW SERIAL_VERSION_ID UNLESS YOU ARE CHANGING DROOLS VERSION
     * SERIAL_VERSION_ID=320 stands for version 3.2.0
     */
    private static final long serialVersionUID = 510l;

    /** The root Rete-OO for this <code>RuleBase</code>. */
    private transient Rete    rete;

    private ReteooBuilder     reteooBuilder;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Default constructor - for Externalizable. This should never be used by a user, as it
     * will result in an invalid state for the instance.
     */
    public ReteooRuleBase() {

    }

    /**
     * Construct.
     *
     * @param id
     *            The rete network.
     */
    public ReteooRuleBase(final String id) {
        this( id,
              null,
              new ReteooFactHandleFactory() );

    }

    /**
     * @param factHandleFactory
     */
    public ReteooRuleBase(final String id,
                          final FactHandleFactory factHandleFactory) {
        this( id,
              null,
              factHandleFactory );
    }

    public ReteooRuleBase(final String id,
                          final RuleBaseConfiguration config) {
        this( id,
              config,
              new ReteooFactHandleFactory() );
    }

    /**
     * @param config
     */
    public ReteooRuleBase(final RuleBaseConfiguration config) {
        this( null,
              config,
              new ReteooFactHandleFactory() );
    }

    /**
     * Construct.
     *
     * @param id
     *            The rete network.
     */
    public ReteooRuleBase(final String id,
                          final RuleBaseConfiguration config,
                          final FactHandleFactory factHandleFactory) {
        super( id,
               config,
               factHandleFactory );
        setupRete();
        if( config != null && config.isMBeansEnabled() ) {
            DroolsManagementAgent.getInstance().registerKnowledgeBase( this );
        }
    }

    private void setupRete() {
        this.rete = new Rete( this );
        this.reteooBuilder = new ReteooBuilder( this );

        // always add the default entry point
        EntryPointNode epn = new EntryPointNode( this.reteooBuilder.getIdGenerator().getNextId(),
                                                 RuleBasePartitionId.MAIN_PARTITION,
                                                 this.getConfig().isMultithreadEvaluation(),
                                                 this.rete,
                                                 EntryPoint.DEFAULT );
        epn.attach();
    }

    /**
     * Handles the write serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode. The generated bytecode must be restored before any Rules.
     *
     */
    public void writeExternal(final ObjectOutput stream) throws IOException {
        DroolsObjectOutputStream droolsStream = null;
        boolean isDrools = stream instanceof DroolsObjectOutputStream;
        ByteArrayOutputStream bytes = null;
        
        stream.writeBoolean( isDrools );
        if ( isDrools ) {
            droolsStream = (DroolsObjectOutputStream) stream;
        } else {
            bytes = new ByteArrayOutputStream();
            droolsStream = new DroolsObjectOutputStream( bytes );
        }
        
        super.writeExternal( droolsStream );
        droolsStream.writeObject( this.reteooBuilder );
        droolsStream.writeObject( this.rete );
        
        if ( !isDrools ) {
            droolsStream.flush();
            droolsStream.close();
            bytes.close();
            stream.writeObject( bytes.toByteArray() );
        }
    }

    /**
     * Handles the read serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode; which must be restored before any Rules.
     * A custom ObjectInputStream, able to resolve classes against the bytecode in the PackageCompilationData, is used to restore the Rules.
     *
     */
    public void readExternal(final ObjectInput stream) throws IOException,
                                                      ClassNotFoundException {
        DroolsObjectInput droolsStream = null;
        boolean isDrools = stream instanceof DroolsObjectInputStream;
        ByteArrayInputStream bytes = null;

        boolean wasDrools = stream.readBoolean();
        if( wasDrools && !isDrools) {
            throw new IllegalArgumentException("The knowledge base was serialized using a DroolsObjectOutputStream. A DroolsObjectInputStream is required for deserialization.");
        }
        
        if ( wasDrools ) {
            droolsStream = (DroolsObjectInput) stream;
        } else {
            bytes = new ByteArrayInputStream( (byte[]) stream.readObject() );
            droolsStream = new DroolsObjectInputStream( bytes );
        }
        
        super.readExternal( droolsStream );
        this.reteooBuilder = (ReteooBuilder) droolsStream.readObject();
        this.reteooBuilder.setRuleBase( this );
        this.rete = (Rete) droolsStream.readObject();
        
        if( !wasDrools ) {
            droolsStream.close();
        }
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the Rete-OO network for this <code>RuleBase</code>.
     *
     * @return The RETE-OO network.
     */
    public Rete getRete() {
        return this.rete;
    }

    public ReteooBuilder getReteooBuilder() {
        return this.reteooBuilder;
    }

    /**
     * Assert a fact object.
     *
     * @param handle
     *            The handle.
     * @param object
     *            The fact.
     * @param workingMemory
     *            The working-memory.
     *
     * @throws FactException
     *             If an error occurs while performing the assertion.
     */
    public void assertObject(final FactHandle handle,
                             final Object object,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) throws FactException {
        getRete().assertObject( (DefaultFactHandle) handle,
                                context,
                                workingMemory );
    }

    /**
     * Retract a fact object.
     *
     * @param handle
     *            The handle.
     * @param workingMemory
     *            The working-memory.
     *
     * @throws FactException
     *             If an error occurs while performing the retraction.
     */
    public void retractObject(final FactHandle handle,
                              final PropagationContext context,
                              final ReteooWorkingMemory workingMemory) throws FactException {
        getRete().retractObject( (InternalFactHandle) handle,
                                 context,
                                 workingMemory );
    }

    public StatefulSession newStatefulSession(boolean keepReference) {
        SessionConfiguration config = new SessionConfiguration();
        config.setKeepReference( keepReference );

        return newStatefulSession( config,
                                   EnvironmentFactory.newEnvironment() );
    }

    public StatefulSession newStatefulSession(java.io.InputStream stream) {
        return newStatefulSession( stream,
                                   true );
    }

    public StatefulSession newStatefulSession(java.io.InputStream stream,
                                              boolean keepReference) {
        StatefulSession session = null;
        try {
            readLock();
            try {
                // first unwrap the byte[]
                ObjectInputStream ois = new ObjectInputStream( stream );
    
                // standard serialisation would have written the statateful session instance info to the stream first
                // so we read it, but we don't need it, so just ignore.
                ReteooStatefulSession rsession = (ReteooStatefulSession) ois.readObject();
    
                // now unmarshall that byte[]
                ByteArrayInputStream bais = new ByteArrayInputStream( rsession.bytes );
                Marshaller marshaller = MarshallerFactory.newMarshaller( new KnowledgeBaseImpl( this ) );
                StatefulKnowledgeSession ksession = marshaller.unmarshall( bais,
                                                                           SessionConfiguration.getDefaultInstance(),
                                                                           EnvironmentFactory.newEnvironment() );
                session = (StatefulSession) ((StatefulKnowledgeSessionImpl) ksession).session;
    
                if ( keepReference ) {
                    super.addStatefulSession( session );
                    for (Object listener : session.getRuleBaseUpdateListeners()) {
                        addEventListener((RuleBaseEventListener) listener);
                    }
                }
    
                bais.close();
            } finally {
                readUnlock();
            }

        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to unmarshall session",
                                        e );
        } finally {
            try {
                stream.close();
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to close stream", e );
            }
        }
        return session;
    }

    public StatefulSession newStatefulSession(SessionConfiguration sessionConfig,
                                              Environment environment) {
        if ( sessionConfig == null ) {
            sessionConfig = SessionConfiguration.getDefaultInstance();
        }
        if ( environment == null ) {
            environment = EnvironmentFactory.newEnvironment();
        }
        return newStatefulSession( nextWorkingMemoryCounter(),
                                   sessionConfig,
                                   environment );
    }

    StatefulSession newStatefulSession(int id,
                                              final SessionConfiguration sessionConfig,
                                              final Environment environment) {
        if ( this.getConfig().isSequential() ) {
            throw new RuntimeException( "Cannot have a stateful rule session, with sequential configuration set to true" );
        }

        ExecutorService executor = ExecutorServiceFactory.createExecutorService( this.getConfig().getExecutorService() );
        readLock();
        try {
            ReteooStatefulSession session = new ReteooStatefulSession( id,
                                                                       this,
                                                                       executor,
                                                                       sessionConfig,
                                                                       environment );
            session.setKnowledgeRuntime(new StatefulKnowledgeSessionImpl(session));

            executor.setCommandExecutor( new CommandExecutor( session ) );

            if ( sessionConfig.isKeepReference() ) {
                super.addStatefulSession( session );
                for (Object listener : session.getRuleBaseUpdateListeners()) {
                    addEventListener((RuleBaseEventListener) listener);
                }
            }

            session.startPartitionManagers();

            session.queueWorkingMemoryAction( new WorkingMemoryReteAssertAction( session.getInitialFactHandle(),
                                                                                 false,
                                                                                 true,
                                                                                 null,
                                                                                 null ) );
            return session;
        } finally {
            readUnlock();
        }
    }

    public StatelessSession newStatelessSession() {

        //orders the rules
        if ( this.getConfig().isSequential() ) {
            this.reteooBuilder.order();
        }

        synchronized ( this.pkgs ) {
            return new ReteooStatelessSession( this );
        }
    }

    protected void addRule(final Rule rule) throws InvalidPatternException {
        // This adds the rule. ReteBuilder has a reference to the WorkingMemories and will propagate any existing facts.
        this.reteooBuilder.addRule( rule );
    }

    protected void removeRule(final Rule rule) {
        this.reteooBuilder.removeRule( rule );
    }

    public int getNodeCount() {
        // may start in 0
        return this.reteooBuilder.getIdGenerator().getLastId() + 1;
    }

    public void addPackages(Package[] pkgs) {
        addPackages( Arrays.asList( pkgs ) );
    }

    public void addPackages(Collection<Package> pkgs) {
        super.addPackages( pkgs );
        if ( this.getConfig().isSequential() ) {
            this.reteooBuilder.setOrdered( false );
        }
    }

    public void addPackage(final Package newPkg) {
        addPackages( Collections.singleton( newPkg ) );
    }
}
