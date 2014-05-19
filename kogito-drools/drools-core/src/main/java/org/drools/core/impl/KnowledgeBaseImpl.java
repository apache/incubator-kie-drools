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

import org.kie.internal.io.ResourceTypePackage;
import org.drools.core.event.KieBaseEventSupport;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DroolsObjectInput;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.UpgradableReentrantReadWriteLock;
import org.drools.core.common.WorkingMemoryFactory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSinkPropagator;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.rule.DialectRuntimeRegistry;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.Function;
import org.drools.core.rule.ImportDeclaration;
import org.drools.core.rule.InvalidPatternException;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.WindowDeclaration;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.TripleStore;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.definition.KiePackage;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.kie.internal.weaver.KieWeaverService;
import org.kie.internal.weaver.KieWeavers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static org.drools.core.common.ProjectClassLoader.createProjectClassLoader;
import static org.drools.core.util.BitMaskUtil.isSet;
import static org.drools.core.util.ClassUtils.convertClassToResourcePath;

public class KnowledgeBaseImpl
    implements
    InternalKnowledgeBase,
    Externalizable {

    protected static final transient Logger logger = LoggerFactory.getLogger(KnowledgeBaseImpl.class);

    private static final long serialVersionUID = 510l;

    public  Set<EntryPointNode> addedEntryNodeCache;
    public  Set<EntryPointNode> removedEntryNodeCache;
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    private String              id;

    private final AtomicInteger workingMemoryCounter = new AtomicInteger(0);

    private RuleBaseConfiguration config;

    protected Map<String, InternalKnowledgePackage> pkgs;

    private Map<String, Process> processes;

    private transient ClassLoader rootClassLoader;

    /**
     * The fact handle factory.
     */
    private FactHandleFactory factHandleFactory;

    private transient Map<String, Class<?>> globals;

    private final transient Queue<DialectRuntimeRegistry> reloadPackageCompilationData = new ConcurrentLinkedQueue<DialectRuntimeRegistry>();

    private KieBaseEventSupport eventSupport = new KieBaseEventSupport(this);

    private transient ObjectHashSet statefulSessions;

    // lock for entire rulebase, used for dynamic updates
    private final UpgradableReentrantReadWriteLock lock = new UpgradableReentrantReadWriteLock();

    /**
     * This lock is used when adding to, or reading the <field>statefulSessions</field>
     */
    private final ReentrantLock statefulSessionLock = new ReentrantLock();

    private int additionsSinceLock;
    private int removalsSinceLock;

    private transient Map<String, TypeDeclaration> classTypeDeclaration;

    private List<RuleBasePartitionId> partitionIDs;

    private ClassFieldAccessorCache classFieldAccessorCache;
    /** The root Rete-OO for this <code>RuleBase</code>. */
    private transient Rete rete;
    private ReteooBuilder reteooBuilder;
    private transient Map<Integer, SegmentMemory.Prototype> segmentProtos = new ConcurrentHashMap<Integer, SegmentMemory.Prototype>();

    private KieComponentFactory kieComponentFactory;
    
    // This is just a hack, so spring can find the list of generated classes
    public List<List<String>> jaxbClasses;

    public final Set<KieBaseEventListener> kieBaseListeners = new HashSet<KieBaseEventListener>();

    private transient SessionsCache sessionsCache;

    public KnowledgeBaseImpl() { }

    public KnowledgeBaseImpl(final String id,
                             final RuleBaseConfiguration config) {
        this.config = (config != null) ? config : new RuleBaseConfiguration();
        this.config.makeImmutable();

        if ( this.config.isPhreakEnabled() ) {
            logger.debug("Starting Engine in PHREAK mode");
        } else {
            logger.debug("Starting Engine in RETEOO mode");
        }

        createRulebaseId(id);

        this.rootClassLoader = this.config.getClassLoader();

        this.pkgs = new HashMap<String, InternalKnowledgePackage>();
        this.processes = new HashMap<String, Process>();
        this.globals = new HashMap<String, Class<?>>();
        this.statefulSessions = new ObjectHashSet();

        this.classTypeDeclaration = new HashMap<String, TypeDeclaration>();
        this.partitionIDs = new CopyOnWriteArrayList<RuleBasePartitionId>();

        this.classFieldAccessorCache = new ClassFieldAccessorCache(this.rootClassLoader);
        kieComponentFactory = getConfiguration().getComponentFactory();

        this.factHandleFactory = kieComponentFactory.getFactHandleFactoryService();
        kieComponentFactory.getTraitFactory().setRuleBase(this);
        kieComponentFactory.getTripleStore().setId(id);

        setupRete();
        if (config != null && config.isMBeansEnabled()) {
            DroolsManagementAgent.getInstance().registerKnowledgeBase(this);
        }

        if ( this.config.getSessionCacheOption().isEnabled() ) {
            if ( this.config.isPhreakEnabled() ) {
                sessionsCache = new SessionsCache(this.config.getSessionCacheOption().isAsync());
            } else {
                logger.warn("Session cache can be enabled only in PHREAK mode");
            }
        }
    }

    public int nextWorkingMemoryCounter() {
        return this.workingMemoryCounter.getAndIncrement();
    }

    public int getWorkingMemoryCounter() {
        return this.workingMemoryCounter.get();
    }

    private void createRulebaseId(final String id) {
        if (id != null) {
            this.id = id;
        } else {
            String key = "";
            if (config.isMBeansEnabled()) {
                DroolsManagementAgent agent = DroolsManagementAgent.getInstance();
                key = String.valueOf(agent.getNextKnowledgeBaseId());
            }
            this.id = "default" + key;
        }
    }

    public void addEventListener(KieBaseEventListener listener) {
        if (!kieBaseListeners.contains(listener)) {
            eventSupport.addEventListener(listener);
            kieBaseListeners.add(listener);
        }
    }

    public void removeEventListener(KieBaseEventListener listener) {
        eventSupport.removeEventListener(listener);
        kieBaseListeners.remove(listener);
    }
    
    public Collection<KieBaseEventListener> getKieBaseEventListeners() {
        return Collections.unmodifiableCollection( kieBaseListeners );
    }

    public void addKnowledgePackages(Collection<KnowledgePackage> knowledgePackages) {
        List<InternalKnowledgePackage> list = new ArrayList<InternalKnowledgePackage>();
        for ( KnowledgePackage knowledgePackage : knowledgePackages ) {
            list.add( (InternalKnowledgePackage) knowledgePackage );
        }
        addPackages(list);
    }

    public Collection<KnowledgePackage> getKnowledgePackages() {
        InternalKnowledgePackage[] pkgs = getPackages();
        List<KnowledgePackage> list = new ArrayList<KnowledgePackage>( pkgs.length );
        for ( InternalKnowledgePackage pkg : pkgs ) {
            list.add( pkg );
        }
        return list;
    }

    public StatefulKnowledgeSession newStatefulKnowledgeSession() {
        return newStatefulKnowledgeSession(null, EnvironmentFactory.newEnvironment());
    }
    
    public StatefulKnowledgeSession newStatefulKnowledgeSession(KieSessionConfiguration conf, Environment environment) {
        // NOTE if you update here, you'll also need to update the JPAService
        if ( conf == null ) {
            conf = SessionConfiguration.getDefaultInstance();
        }
        
        if ( environment == null ) {
            environment = EnvironmentFactory.newEnvironment();
        }

        return newStatefulSession((SessionConfiguration) conf, environment);
    }
    
    public Collection<StatefulKnowledgeSession> getStatefulKnowledgeSessions() {
        Collection<StatefulKnowledgeSession> c = new ArrayList<StatefulKnowledgeSession>();
        StatefulKnowledgeSessionImpl[] sss = getStatefulSessions();
        if (sss != null) {
            for (StatefulKnowledgeSession ss : sss) {
                c.add((StatefulKnowledgeSessionImpl) ss);
            }
        }
        return c;
    }
    
    public StatelessKnowledgeSession newStatelessKnowledgeSession() {
        return new StatelessKnowledgeSessionImpl( this, null, null );
    }
    
    public StatelessKnowledgeSession newStatelessKnowledgeSession(KieSessionConfiguration conf) {
        return new StatelessKnowledgeSessionImpl( this, null, conf );
    }

    public void removeKnowledgePackage(String packageName) {
        lock();
        try {
            final InternalKnowledgePackage pkg = this.pkgs.get( packageName );
            if (pkg == null) {
                throw new IllegalArgumentException( "Package name '" + packageName +
                                                    "' does not exist for this Rule Base." );
            }
            this.removalsSinceLock++;

            this.eventSupport.fireBeforePackageRemoved( pkg );

            for (Rule rule : pkg.getRules()) {
                removeRule( pkg, (RuleImpl)rule );
            }

            // getting the list of referenced globals
            final Set<String> referencedGlobals = new HashSet<String>();
            for (InternalKnowledgePackage pkgref : this.pkgs.values()) {
                if (pkgref != pkg) {
                    referencedGlobals.addAll( pkgref.getGlobals().keySet() );
                }
            }
            // removing globals declared inside the package that are not shared
            for (String globalName : pkg.getGlobals().keySet()) {
                if (!referencedGlobals.contains( globalName )) {
                    this.globals.remove( globalName );
                }
            }
            //and now the rule flows
            for ( String processName : new ArrayList<String>(pkg.getRuleFlows().keySet()) ) {
                removeProcess( processName );
            }
            // removing the package itself from the list
            this.pkgs.remove( pkg.getName() );

            pkg.getDialectRuntimeRegistry().onRemove();

            //clear all members of the pkg
            pkg.clear();

            this.eventSupport.fireAfterPackageRemoved( pkg );
        } finally {
            unlock();
        }
    }

    public KnowledgePackage getKnowledgePackage(String packageName) {
        return getPackage(packageName);
    }

    public Rule getRule(String packageName,
                        String ruleName) {
        InternalKnowledgePackage p = getPackage(packageName);
        return p == null ? null : p.getRule( ruleName );
    }
    
    public Query getQuery(String packageName,
                          String queryName) {
        return ( Query ) getPackage(packageName).getRule( queryName );
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


    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Handles the read serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode; which must be restored before any Rules.
     * A custom ObjectInputStream, able to resolve classes against the bytecode in the PackageCompilationData, is used to restore the Rules.
     */
    public void readExternal(final ObjectInput in) throws IOException,
                                                          ClassNotFoundException {
        // PackageCompilationData must be restored before Rules as it has the ClassLoader needed to resolve the generated code references in Rules
        DroolsObjectInput droolsStream;
        boolean isDrools = in instanceof DroolsObjectInputStream;

        boolean wasDrools = in.readBoolean();
        if (wasDrools && !isDrools) {
            throw new IllegalArgumentException("The knowledge base was serialized using a DroolsObjectOutputStream. A DroolsObjectInputStream is required for deserialization.");
        }

        if (isDrools) {
            droolsStream = (DroolsObjectInput) in;
        } else {
            ByteArrayInputStream bytes = new ByteArrayInputStream((byte[]) in.readObject());
            droolsStream = new DroolsObjectInputStream(bytes);
        }

        boolean classLoaderCacheEnabled = droolsStream.readBoolean();
        Map<String, byte[]> store = (Map<String, byte[]>) droolsStream.readObject();

        this.rootClassLoader = createProjectClassLoader(droolsStream.getParentClassLoader(), store);

        droolsStream.setClassLoader(this.rootClassLoader);
        droolsStream.setKnowledgeBase(this);

        this.classFieldAccessorCache = new ClassFieldAccessorCache(this.rootClassLoader);

        this.config = (RuleBaseConfiguration) droolsStream.readObject();
        this.config.setClassLoader(droolsStream.getParentClassLoader());
        kieComponentFactory = getConfiguration().getComponentFactory();

        this.pkgs = (Map<String, InternalKnowledgePackage>) droolsStream.readObject();

        for (InternalKnowledgePackage pkg : this.pkgs.values()) {
            pkg.getDialectRuntimeRegistry().onAdd(this.rootClassLoader);
        }

        // PackageCompilationData must be restored before Rules as it has the ClassLoader needed to resolve the generated code references in Rules
        this.id = (String) droolsStream.readObject();
        this.workingMemoryCounter.set(droolsStream.readInt());

        this.processes = (Map<String, Process>) droolsStream.readObject();
        Class cls = null;
        try {
            cls = droolsStream.getParentClassLoader().loadClass(droolsStream.readUTF());
            this.factHandleFactory = (FactHandleFactory) cls.newInstance();
        } catch (InstantiationException e) {
            DroolsObjectInputStream.newInvalidClassException(cls,
                                                             e);
        } catch (IllegalAccessException e) {
            DroolsObjectInputStream.newInvalidClassException(cls,
                                                             e);
        }

        for (InternalKnowledgePackage pkg : this.pkgs.values()) {
            pkg.getDialectRuntimeRegistry().onBeforeExecute();
            pkg.getClassFieldAccessorStore().setClassFieldAccessorCache(this.classFieldAccessorCache);
            pkg.getClassFieldAccessorStore().wire();
        }

        this.populateTypeDeclarationMaps();

        // read globals
        Map<String, String> globs = (Map<String, String>) droolsStream.readObject();
        populateGlobalsMap(globs);

        this.partitionIDs = (List<RuleBasePartitionId>) droolsStream.readObject();

        this.eventSupport = (KieBaseEventSupport) droolsStream.readObject();
        this.eventSupport.setKnowledgeBase(this);
        this.statefulSessions = new ObjectHashSet();

        this.reteooBuilder = (ReteooBuilder) droolsStream.readObject();
        this.reteooBuilder.setRuleBase(this);
        this.rete = (Rete) droolsStream.readObject();

        if (!isDrools) {
            droolsStream.close();
        }

        this.getConfiguration().getComponentFactory().getTraitFactory().setRuleBase(this);
    }

    /**
     * Handles the write serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode. The generated bytecode must be restored before any Rules.
     */
    public void writeExternal(final ObjectOutput out) throws IOException {
        ObjectOutput droolsStream;
        boolean isDrools = out instanceof DroolsObjectOutputStream;
        ByteArrayOutputStream bytes;

        out.writeBoolean( isDrools );
        if (isDrools) {
            droolsStream = out;
            bytes = null;
        } else {
            bytes = new ByteArrayOutputStream();
            droolsStream = new DroolsObjectOutputStream(bytes);
        }

        // must write this option first in order to properly deserialize later
        droolsStream.writeBoolean(this.config.isClassLoaderCacheEnabled());

        droolsStream.writeObject(((ProjectClassLoader) rootClassLoader).getStore());

        droolsStream.writeObject(this.config);
        droolsStream.writeObject(this.pkgs);

        // Rules must be restored by an ObjectInputStream that can resolve using a given ClassLoader to handle seaprately by storing as
        // a byte[]
        droolsStream.writeObject(this.id);
        droolsStream.writeInt(this.workingMemoryCounter.get());
        droolsStream.writeObject(this.processes);
        droolsStream.writeUTF(this.factHandleFactory.getClass().getName());
        droolsStream.writeObject(buildGlobalMapForSerialization());
        droolsStream.writeObject(this.partitionIDs);

        this.eventSupport.removeEventListener(KieBaseEventListener.class);
        droolsStream.writeObject(this.eventSupport);

        droolsStream.writeObject(this.reteooBuilder);
        droolsStream.writeObject(this.rete);

        if (!isDrools) {
            droolsStream.flush();
            droolsStream.close();
            bytes.close();
            out.writeObject(bytes.toByteArray());
        }
    }


    private Map<String, String> buildGlobalMapForSerialization() {
        Map<String, String> gl = new HashMap<String, String>();
        for (Map.Entry<String, Class<?>> entry : this.globals.entrySet()) {
            gl.put( entry.getKey(),
                    entry.getValue().getName() );
        }
        return gl;
    }

    /**
     * globals class types must be re-wired after serialization
     *
     * @param globs
     * @throws ClassNotFoundException
     */
    private void populateGlobalsMap(Map<String, String> globs) throws ClassNotFoundException {
        this.globals = new HashMap<String, Class<?>>();
        for (Map.Entry<String, String> entry : globs.entrySet()) {
            addGlobal(entry.getKey(),
                      this.rootClassLoader.loadClass(entry.getValue()));
        }
    }

    /**
     * type classes must be re-wired after serialization
     *
     * @throws ClassNotFoundException
     */
    private void populateTypeDeclarationMaps() throws ClassNotFoundException {
        // FIXME: readLock
        this.classTypeDeclaration = new HashMap<String, TypeDeclaration>();
        for (InternalKnowledgePackage pkg : this.pkgs.values()) {
            for (TypeDeclaration type : pkg.getTypeDeclarations().values()) {
                type.setTypeClass(this.rootClassLoader.loadClass(type.getTypeClassName()));
                this.classTypeDeclaration.put(type.getTypeClassName(),
                                              type);
            }
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    public RuleBaseConfiguration getConfig() {
        return config;
    }

    public StatefulKnowledgeSessionImpl newStatefulSession() {
        return newStatefulSession(SessionConfiguration.getDefaultInstance(),
                                  EnvironmentFactory.newEnvironment());
    }

    public void disposeStatefulSession(StatefulKnowledgeSessionImpl statefulSession) {
        statefulSessionLock.lock();

        try {
            if (sessionsCache != null) {
                sessionsCache.store(statefulSession);
            }
            this.statefulSessions.remove(statefulSession);
        } finally {
            statefulSessionLock.unlock();
        }
    }

    public StatefulKnowledgeSessionImpl getCachedSession(SessionConfiguration config, Environment environment) {
        return sessionsCache != null ? sessionsCache.getCachedSession(config) : null;
    }

    public FactHandleFactory getFactHandleFactory() {
        return this.factHandleFactory;
    }

    public FactHandleFactory newFactHandleFactory() {
        return this.factHandleFactory.newInstance();
    }

    public FactHandleFactory newFactHandleFactory(int id,
                                                  long counter) {
        return this.factHandleFactory.newInstance(id,
                                                  counter);
    }

    public Collection<Process> getProcesses() {
        readLock();
        try {
            return this.processes.values();
        } finally {
            readUnlock();
        }
    }

    public InternalKnowledgePackage[] getPackages() {
        readLock();
        try {
            return this.pkgs.values().toArray(new InternalKnowledgePackage[this.pkgs.size()]);
        } finally {
            readUnlock();
        }
    }

    // FIXME: this returns the live map!
    public Map<String, InternalKnowledgePackage> getPackagesMap() {
        return this.pkgs;
    }

    public Map<String, Class<?>> getGlobals() {
        return this.globals;
    }

    public int getAdditionsSinceLock() {
        return additionsSinceLock;
    }

    public int getRemovalsSinceLock() {
        return removalsSinceLock;
    }

    public void lock() {
        // The lock is reentrant, so we need additional magic here to skip
        // notifications for locked if this thread already has locked it.
        boolean firstLock = !this.lock.isWriteLockedByCurrentThread();
        if (firstLock) {
            this.eventSupport.fireBeforeRuleBaseLocked();
        }
        // Always lock to increase the counter
        this.lock.writeLock();
        if ( firstLock ) {
            this.additionsSinceLock = 0;
            this.removalsSinceLock = 0;
            this.eventSupport.fireAfterRuleBaseLocked();
        }
    }

    public void unlock() {
        boolean lastUnlock = this.lock.getWriteHoldCount() == 1;
        if (lastUnlock) {
            this.eventSupport.fireBeforeRuleBaseUnlocked();
        }
        this.lock.writeUnlock();
        if ( lastUnlock ) {
            this.eventSupport.fireAfterRuleBaseUnlocked();
        }
    }

    public void readLock() {
        this.lock.readLock();
    }

    public void readUnlock() {
        this.lock.readUnlock();
    }

    /**
     * Add a <code>Package</code> to the network. Iterates through the
     * <code>Package</code> adding Each individual <code>Rule</code> to the
     * network. Before update network each referenced <code>WorkingMemory</code>
     * is locked.
     *
     * @param newPkgs The package to add.
     */
    public void addPackages( final Collection<InternalKnowledgePackage> newPkgs ) {
        List<InternalKnowledgePackage> clonedPkgs = new ArrayList<InternalKnowledgePackage>();
        for (InternalKnowledgePackage newPkg : newPkgs) {
            clonedPkgs.add(newPkg.deepCloneIfAlreadyInUse(rootClassLoader));
        }

        lock();
        try {
            // we need to merge all byte[] first, so that the root classloader can resolve classes
            for (InternalKnowledgePackage newPkg : clonedPkgs) {
                newPkg.checkValidity();
                this.additionsSinceLock++;
                this.eventSupport.fireBeforePackageAdded( newPkg );

                if ( newPkg.hasTraitRegistry() ) {
                    getTraitRegistry().merge( newPkg.getTraitRegistry() );
                }

                InternalKnowledgePackage pkg = this.pkgs.get( newPkg.getName() );
                if ( pkg == null ) {
                    pkg = new KnowledgePackageImpl( newPkg.getName() );

                    // @TODO we really should have a single root cache
                    pkg.setClassFieldAccessorCache( this.classFieldAccessorCache );
                    pkgs.put( pkg.getName(),
                              pkg );
                }

                // first merge anything related to classloader re-wiring
                pkg.getDialectRuntimeRegistry().merge( newPkg.getDialectRuntimeRegistry(),
                                                       this.rootClassLoader,
                                                       true );

            }

            List<TypeDeclaration> allTypeDeclarations = new ArrayList<TypeDeclaration>();
            // Add all Type Declarations, this has to be done first incase packages cross reference each other during build process.
            for ( InternalKnowledgePackage newPkg : clonedPkgs ) {
                // we have to do this before the merging, as it does some classloader resolving
                if ( newPkg.getTypeDeclarations() != null ) {
                    for ( TypeDeclaration newDecl : newPkg.getTypeDeclarations().values() ) {
                        allTypeDeclarations.add( newDecl );
                    }
                }
            }
            Collections.sort( allTypeDeclarations );

            String lastType = null;
            try {
                // add type declarations according to the global order
                for ( TypeDeclaration newDecl : allTypeDeclarations ) {
                    lastType = newDecl.getTypeClassName();
                    InternalKnowledgePackage newPkg = null;
                    for ( InternalKnowledgePackage kpkg : clonedPkgs ) {
                        if ( kpkg.getTypeDeclarations().containsKey( newDecl.getTypeName() ) ) {
                            newPkg = kpkg;
                            break;
                        }
                    }
                    processTypeDeclaration( newDecl, newPkg );
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException( "unable to resolve Type Declaration class '" + lastType + "'", e );
            }

            for ( InternalKnowledgePackage newPkg : clonedPkgs ) {
                // Add functions
                try {
                    JavaDialectRuntimeData runtime = ((JavaDialectRuntimeData) newPkg.getDialectRuntimeRegistry().getDialectData( "java" ));

                    for ( Function function : newPkg.getFunctions().values() ) {
                        String functionClassName = function.getClassName();
                        byte [] def = runtime.getStore().get(convertClassToResourcePath(functionClassName));
                        registerAndLoadTypeDefinition( functionClassName, def );
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException( "unable to resolve Type Declaration class '" + lastType + "'", e );
                }
            }

            // now iterate again, this time onBeforeExecute will handle any wiring or cloader re-creating that needs to be done as part of the merge
            for (InternalKnowledgePackage newPkg : clonedPkgs) {
                InternalKnowledgePackage pkg = this.pkgs.get( newPkg.getName() );

                // this needs to go here, as functions will set a java dialect to dirty
                if (newPkg.getFunctions() != null) {
                    for (Map.Entry<String, Function> entry : newPkg.getFunctions().entrySet()) {
                        pkg.addFunction( entry.getValue() );
                    }
                }

                pkg.getDialectRuntimeRegistry().onBeforeExecute();

                // with the classloader recreated for all byte[] classes, we should now merge and wire any new accessors
                pkg.getClassFieldAccessorStore().merge( newPkg.getClassFieldAccessorStore() );
            }


            for (InternalKnowledgePackage newPkg : clonedPkgs) {
                InternalKnowledgePackage pkg = this.pkgs.get( newPkg.getName() );

                // now merge the new package into the existing one
                mergePackage( pkg,
                              newPkg );

                // add the window declarations to the kbase
                for( WindowDeclaration window : newPkg.getWindowDeclarations().values() ) {
                    addWindowDeclaration( newPkg,
                                          window );
                }

                // add entry points to the kbase
                for (String id : newPkg.getEntryPointIds()) {
                    addEntryPoint( id );
                }

                // add the rules to the RuleBase
                for ( Rule rule : newPkg.getRules() ) {
                    addRule( newPkg, (RuleImpl)rule );
                }

                // add the flows to the RuleBase
                if ( newPkg.getRuleFlows() != null ) {
                    final Map<String, org.kie.api.definition.process.Process> flows = newPkg.getRuleFlows();
                    for ( org.kie.api.definition.process.Process process : flows.values() ) {
                        // XXX: we could take the lock inside addProcess() out, but OTOH: this is what the VM is supposed to do ...
                        addProcess( process );
                    }
                }

                if ( ! newPkg.getResourceTypePackages().isEmpty() ) {
                    KieWeavers weavers = ServiceRegistryImpl.getInstance().get(KieWeavers.class);
                    for ( ResourceTypePackage rtkKpg : newPkg.getResourceTypePackages().values() ) {
                        ResourceType rt = rtkKpg.getResourceType();
                        KieWeaverService factory = weavers.getWeavers().get( rt );
                        factory.weave( this, newPkg, rtkKpg );
                    }
                }

                this.eventSupport.fireAfterPackageAdded( newPkg );
            }
        } finally {
            unlock();
        }
    }

    protected void processTypeDeclaration( TypeDeclaration newDecl, InternalKnowledgePackage newPkg ) throws ClassNotFoundException {
        JavaDialectRuntimeData runtime = ((JavaDialectRuntimeData) newPkg.getDialectRuntimeRegistry().getDialectData( "java" ));

        TypeDeclaration typeDeclaration = this.classTypeDeclaration.get( newDecl.getTypeClassName() );
        if ( typeDeclaration == null ) {
            String className = newDecl.getTypeClassName();

            byte [] def = runtime.getClassDefinition(convertClassToResourcePath(className));
            Class<?> definedKlass = registerAndLoadTypeDefinition( className, def );

            if ( definedKlass == null && typeDeclaration.isNovel() ) {
                throw new RuntimeException( "Registering null bytes for class " + className );
            }

            if (newDecl.getTypeClassDef() == null) {
                newDecl.setTypeClassDef( new ClassDefinition() );
            }
            newDecl.getTypeClassDef().setDefinedClass( definedKlass );
            newDecl.setTypeClass( definedKlass );

            this.classTypeDeclaration.put( className, newDecl );
            typeDeclaration = newDecl;
        } else {
            Class<?> definedKlass = typeDeclaration.getTypeClass();

            newDecl.getTypeClassDef().setDefinedClass( definedKlass );
            newDecl.setTypeClass( definedKlass );

            mergeTypeDeclarations( typeDeclaration,
                                   newDecl );
        }

        // update existing OTNs
        updateDependentTypes( newPkg,
                              typeDeclaration );
    }

    public Class<?> registerAndLoadTypeDefinition( String className, byte[] def ) throws ClassNotFoundException {
        try {
            return this.rootClassLoader.loadClass( className );
        } catch (ClassNotFoundException e) {
            if (def != null && rootClassLoader instanceof ProjectClassLoader) {
                return ((ProjectClassLoader)rootClassLoader).defineClass(className, def);
            }
            throw e;
        }
    }

    protected void updateDependentTypes( InternalKnowledgePackage newPkg,
                                         TypeDeclaration typeDeclaration ) {
        // update OTNs
        if( this.getConfiguration().getEventProcessingMode().equals( EventProcessingOption.STREAM ) ) {
            // if the expiration for the type was set, then add 1, otherwise return -1
            long exp = typeDeclaration.getExpirationOffset() > -1 ? typeDeclaration.getExpirationOffset() + 1 : -1;

            // if we are running in STREAM mode, update expiration offset
            for( EntryPointNode ep : this.rete.getEntryPointNodes().values() ) {
                for( ObjectTypeNode node : ep.getObjectTypeNodes().values() ) {
                    if( node.isAssignableFrom( typeDeclaration.getObjectType() ) ) {
                        node.setExpirationOffset( Math.max( node.getExpirationOffset(), exp ) );
                    }
                }
            }
        }
    }

    private void mergeTypeDeclarations( TypeDeclaration existingDecl,
                                        TypeDeclaration newDecl ) {

        existingDecl.addRedeclaration(newDecl);

        if ( ! nullSafeEquals( existingDecl.getFormat(),
                               newDecl.getFormat() ) ||
             ! nullSafeEquals( existingDecl.getObjectType(),
                               newDecl.getObjectType() ) ||
             ! nullSafeEquals( existingDecl.getTypeClassName(),
                               newDecl.getTypeClassName() ) ||
             ! nullSafeEquals( existingDecl.getTypeName(),
                               newDecl.getTypeName() ) ) {

            throw new RuntimeException( "Unable to merge Type Declaration for class '" + existingDecl.getTypeName() + "'" );

        }

        existingDecl.setDurationAttribute( mergeLeft( existingDecl.getTypeName(),
                                                      "Unable to merge @duration attribute for type declaration of class:",
                                                      existingDecl.getDurationAttribute(),
                                                      newDecl.getDurationAttribute(),
                                                      true,
                                                      false ) );

        existingDecl.setDynamic( mergeLeft( existingDecl.getTypeName(),
                                            "Unable to merge @propertyChangeSupport  (a.k.a. dynamic) attribute for type declaration of class:",
                                            existingDecl.isDynamic(),
                                            newDecl.isDynamic(),
                                            true,
                                            false ) );

        existingDecl.setPropertyReactive( mergeLeft(existingDecl.getTypeName(),
                                                    "Unable to merge @propertyReactive attribute for type declaration of class:",
                                                    existingDecl.isPropertyReactive(),
                                                    newDecl.isPropertyReactive(),
                                                    true,
                                                    false) );

        existingDecl.setExpirationOffset( Math.max( existingDecl.getExpirationOffset(),
                                                    newDecl.getExpirationOffset() ) );

        if ( newDecl.getNature().equals( TypeDeclaration.Nature.DEFINITION ) && newDecl.isNovel() ) {
            // At this point, the definitions must be equivalent.
            // So the only illegal case is a novel definition of an already existing type
            existingDecl.setNovel( mergeLeft( existingDecl.getTypeName(),
                                              "Unable to merge @novel attribute for type declaration of class:",
                                              existingDecl.isNovel(),
                                              newDecl.isNovel(),
                                              true,
                                              false ) );
        }

        if ( newDecl.getNature().equals( TypeDeclaration.Nature.DEFINITION ) || existingDecl.getResource() == null ) {
            existingDecl.setResource( mergeLeft( existingDecl.getTypeName(),
                                                 "Unable to merge resource attribute for type declaration of class:",
                                                 existingDecl.getResource(),
                                                 newDecl.getResource(),
                                                 true,
                                                 true ) );
        }

        existingDecl.setRole( mergeLeft( existingDecl.getTypeName(),
                                         "Unable to merge @role attribute for type declaration of class:",
                                         isSet(existingDecl.getSetMask(), TypeDeclaration.ROLE_BIT)
                                         && newDecl.getRole() != TypeDeclaration.Role.FACT
                                         ? existingDecl.getRole() : null,
                                         newDecl.getRole(),
                                         true,
                                         false ) );

        existingDecl.setTimestampAttribute( mergeLeft( existingDecl.getTypeName(),
                                                       "Unable to merge @timestamp attribute for type declaration of class:",
                                                       existingDecl.getTimestampAttribute(),
                                                       newDecl.getTimestampAttribute(),
                                                       true,
                                                       false ) );

        existingDecl.setTypesafe( mergeLeft(existingDecl.getTypeName(),
                                            "Unable to merge @typesafe attribute for type declaration of class:",
                                            existingDecl.isTypesafe(),
                                            newDecl.isTypesafe(),
                                            true,
                                            false ) );
    }

    private <T> T mergeLeft( String typeClass,
                             String errorMsg,
                             T leftVal,
                             T rightVal,
                             boolean errorOnDiff,
                             boolean override ) {
        T newValue = leftVal;
        if ( ! nullSafeEquals( leftVal,
                               rightVal ) ) {
            if ( leftVal == null && rightVal != null ) {
                newValue = rightVal;
            } else if ( leftVal != null && rightVal != null ) {
                if ( override ) {
                    newValue = rightVal;
                } else {
                    if ( errorOnDiff ) {
                        throw new RuntimeException( errorMsg + " '" + typeClass + "'" );
                    } else {
                        // do nothing, just use the left value
                    }
                }
            }
        }
        return newValue;
    }

    private boolean nullSafeEquals( Object o1,
                                    Object o2 ) {
        return ( o1 == null ) ? o2 == null : o1.equals( o2 );
    }

    /**
     * Merge a new package with an existing package.
     * Most of the work is done by the concrete implementations,
     * but this class does some work (including combining imports, compilation data, globals,
     * and the actual Rule objects into the package).
     */
    private void mergePackage( InternalKnowledgePackage pkg,
                               InternalKnowledgePackage newPkg ) {
        // Merge imports
        final Map<String, ImportDeclaration> imports = pkg.getImports();
        imports.putAll(newPkg.getImports());

        String lastIdent = null;
        String lastType = null;
        try {
            // merge globals
            if (newPkg.getGlobals() != null && newPkg.getGlobals() != Collections.EMPTY_MAP) {
                Map<String, String> globals = pkg.getGlobals();
                // Add globals
                for (final Map.Entry<String, String> entry : newPkg.getGlobals().entrySet()) {
                    final String identifier = entry.getKey();
                    final String type = entry.getValue();
                    lastIdent = identifier;
                    lastType = type;
                    if (globals.containsKey( identifier ) && !globals.get( identifier ).equals( type )) {
                        throw new RuntimeException(pkg.getName() + " cannot be integrated");
                    } else {
                        pkg.addGlobal( identifier,
                                       this.rootClassLoader.loadClass( type ) );
                        // this isn't a package merge, it's adding to the rulebase, but I've put it here for convienience
                        addGlobal(identifier,
                                  this.rootClassLoader.loadClass(type));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( "Unable to resolve class '" + lastType +
                                        "' for global '" + lastIdent + "'" );
        }

        // merge entry point declarations
        if (newPkg.getEntryPointIds() != null) {
            for (String ep : newPkg.getEntryPointIds()) {
                pkg.addEntryPointId( ep );

            }
        }

        // merge the type declarations
        if (newPkg.getTypeDeclarations() != null) {
            // add type declarations
            for (TypeDeclaration type : newPkg.getTypeDeclarations().values()) {
                // @TODO should we allow overrides? only if the class is not in use.
                if (!pkg.getTypeDeclarations().containsKey( type.getTypeName() )) {
                    // add to package list of type declarations
                    pkg.addTypeDeclaration( type );
                }
            }
        }

        // merge window declarations
        if ( newPkg.getWindowDeclarations() != null ) {
            // add window declarations
            for ( WindowDeclaration window : newPkg.getWindowDeclarations().values() ) {
                if ( !pkg.getWindowDeclarations().containsKey( window.getName() ) ||
                     pkg.getWindowDeclarations().get( window.getName() ).equals( window ) ) {
                    pkg.addWindowDeclaration( window );
                } else {
                    throw new RuntimeException( "Unable to merge two conflicting window declarations for window named: "+window.getName() );
                }
            }
        }

        //Merge rules into the RuleBase package
        //as this is needed for individual rule removal later on
        for (Rule newRule : newPkg.getRules()) {
            // remove the rule if it already exists
            if (pkg.getRule(newRule.getName()) != null) {
                removeRule( pkg, pkg.getRule(newRule.getName()) );
            }

            pkg.addRule((RuleImpl)newRule);
        }

        //Merge The Rule Flows
        if (newPkg.getRuleFlows() != null) {
            for (Process flow : newPkg.getRuleFlows().values()) {
                pkg.addProcess(flow);
            }
        }

        if ( ! newPkg.getResourceTypePackages().isEmpty() ) {
            for ( ResourceTypePackage rtkKpg : newPkg.getResourceTypePackages().values() ) {
                ResourceType rt = rtkKpg.getResourceType();
                KieWeavers weavers = ServiceRegistryImpl.getInstance().get(KieWeavers.class);

                KieWeaverService weaver = weavers.getWeavers().get(rt);
                weaver.merge( this, pkg, rtkKpg );
            }
        }
    }

    public void addGlobal(String identifier, Class clazz) {
        this.globals.put( identifier, clazz );
    }

    protected void setupRete() {
        this.rete = new Rete( this );
        this.reteooBuilder = new ReteooBuilder( this );

        // always add the default entry point
        EntryPointNode epn = kieComponentFactory.getNodeFactoryService().buildEntryPointNode( this.reteooBuilder.getIdGenerator().getNextId(),
                                                                                              RuleBasePartitionId.MAIN_PARTITION,
                                                                                              this.getConfiguration().isMultithreadEvaluation(),
                                                                                              this.rete,
                                                                                              EntryPointId.DEFAULT );
        epn.attach();
    }

    public void registerAddedEntryNodeCache(EntryPointNode node) {
        if (addedEntryNodeCache == null) addedEntryNodeCache = new HashSet<EntryPointNode>();
        addedEntryNodeCache.add(node);
    }

    public Set<EntryPointNode> getAddedEntryNodeCache() {
        return addedEntryNodeCache;
    }

    public void registeRremovedEntryNodeCache(EntryPointNode node) {
        if (removedEntryNodeCache == null) removedEntryNodeCache = new HashSet<EntryPointNode>();
        removedEntryNodeCache.add(node);
    }

    public Set<EntryPointNode> getRemovedEntryNodeCache() {
        return removedEntryNodeCache;
    }

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
     */
    public void assertObject(final FactHandle handle,
                             final Object object,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
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
     */
    public void retractObject(final FactHandle handle,
                              final PropagationContext context,
                              final StatefulKnowledgeSessionImpl workingMemory) {
        getRete().retractObject((InternalFactHandle) handle,
                                context,
                                workingMemory);
    }

    public StatefulKnowledgeSessionImpl newStatefulSession(boolean keepReference) {
        SessionConfiguration config = new SessionConfiguration();
        config.setKeepReference( keepReference );

        return newStatefulSession( config,
                                   EnvironmentFactory.newEnvironment() );
    }

    public StatefulKnowledgeSessionImpl newStatefulSession(java.io.InputStream stream) {
        return newStatefulSession( stream,
                                   true );
    }

    public StatefulKnowledgeSessionImpl newStatefulSession(java.io.InputStream stream,
                                                           boolean keepReference) {
        return newStatefulSession( stream,
                                   keepReference,
                                   SessionConfiguration.getDefaultInstance() );
    }

    public StatefulKnowledgeSessionImpl newStatefulSession(java.io.InputStream stream,
                                                           boolean keepReference,
                                                           SessionConfiguration conf) {
        StatefulKnowledgeSessionImpl session = null;
        try {
            readLock();
            try {
                // first unwrap the byte[]
                ObjectInputStream ois = new ObjectInputStream( stream );

                // standard serialisation would have written the statateful session instance info to the stream first
                // so we read it, but we don't need it, so just ignore.
                StatefulKnowledgeSessionImpl rsession = (StatefulKnowledgeSessionImpl) ois.readObject();

                // now unmarshall that byte[]
                ByteArrayInputStream bais = new ByteArrayInputStream( rsession.bytes );
                Marshaller marshaller = MarshallerFactory.newMarshaller(this, new ObjectMarshallingStrategy[]{MarshallerFactory.newSerializeMarshallingStrategy()});

                Environment environment = EnvironmentFactory.newEnvironment();
                KieSession ksession = marshaller.unmarshall( bais,
                                                             conf,
                                                             environment );
                session = (StatefulKnowledgeSessionImpl) ksession;

                if ( keepReference ) {
                    addStatefulSession(session);
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

    public StatefulKnowledgeSessionImpl newStatefulSession(SessionConfiguration sessionConfig,
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

    StatefulKnowledgeSessionImpl newStatefulSession(int id,
                                                    SessionConfiguration sessionConfig,
                                                    Environment environment) {
        if ( this.getConfiguration().isSequential() ) {
            throw new RuntimeException( "Cannot have a stateful rule session, with sequential configuration set to true" );
        }

        readLock();
        try {
            WorkingMemoryFactory wmFactory = kieComponentFactory.getWorkingMemoryFactory();
            StatefulKnowledgeSessionImpl session = ( StatefulKnowledgeSessionImpl ) wmFactory.createWorkingMemory( id, this,
                                                                                                                   sessionConfig, environment );
            if ( sessionConfig.isKeepReference() ) {
                addStatefulSession(session);
            }

            return session;
        } finally {
            readUnlock();
        }
    }

    public int getNodeCount() {
        // may start in 0
        return this.reteooBuilder.getIdGenerator().getLastId() + 1;
    }

    public void addPackages(InternalKnowledgePackage[] pkgs) {
        addPackages( Arrays.asList(pkgs) );
    }

    public void addPackage(final InternalKnowledgePackage newPkg) {
        addPackages( Collections.singleton(newPkg) );
    }

    public void registerSegmentPrototype(LeftTupleSource tupleSource, SegmentMemory smem) {
        segmentProtos.put(tupleSource.getId(), smem.asPrototype());
    }

    public void invalidateSegmentPrototype(LeftTupleSource tupleSource) {
        segmentProtos.remove(tupleSource.getId());
        LeftTupleSinkPropagator sinkProp = tupleSource.getSinkPropagator();
        for (LeftTupleSinkNode sink = (LeftTupleSinkNode) sinkProp.getFirstLeftTupleSink(); sink != null; sink = sink.getNextLeftTupleSinkNode()) {
            if (sink instanceof LeftTupleSource) {
                invalidateSegmentPrototype((LeftTupleSource)sink);
            }
        }
    }

    public SegmentMemory createSegmentFromPrototype(InternalWorkingMemory wm, LeftTupleSource tupleSource) {
        SegmentMemory.Prototype proto = segmentProtos.get(tupleSource.getId());
        if (proto == null) {
            return null;
        }
        return proto.newSegmentMemory(wm);
    }

    public SegmentMemory.Prototype getSegmentPrototype(SegmentMemory segment) {
        return segmentProtos.get(segment.getRootNode().getId());
    }

    private static class TypeDeclarationCandidate {

        public TypeDeclaration candidate = null;
        public int             score     = Integer.MAX_VALUE;
    }

    public TypeDeclaration getTypeDeclaration( Class<?> clazz ) {
        TypeDeclaration typeDeclaration = this.classTypeDeclaration.get( clazz.getName() );
        if (typeDeclaration == null) {
            // check super classes and keep a score of how up in the hierarchy is there a declaration
            TypeDeclarationCandidate candidate = checkSuperClasses( clazz );
            // now check interfaces
            candidate = checkInterfaces( clazz,
                                         candidate,
                                         1 );
            if (candidate != null) {
                typeDeclaration = candidate.candidate;
            }
        }
        return typeDeclaration;
    }

    private TypeDeclarationCandidate checkSuperClasses( Class<?> clazz ) {

        TypeDeclaration typeDeclaration = null;
        Class<?> current = clazz.getSuperclass();
        int score = 0;
        while ( typeDeclaration == null && current != null ) {
            score++;
            typeDeclaration = this.classTypeDeclaration.get( current.getName() );
            current = current.getSuperclass();
        }
        TypeDeclarationCandidate candidate = null;
        if ( typeDeclaration != null ) {
            candidate = new TypeDeclarationCandidate();
            candidate.candidate = typeDeclaration;
            candidate.score = score;
        }
        return candidate;
    }

    private TypeDeclarationCandidate checkInterfaces( Class<?> clazz,
                                                      TypeDeclarationCandidate baseline,
                                                      int level ) {
        TypeDeclarationCandidate candidate = null;
        if (baseline == null || level < baseline.score) {
            // search
            TypeDeclaration typeDeclaration;
            for (Class<?> ifc : clazz.getInterfaces()) {
                typeDeclaration = this.classTypeDeclaration.get( ifc.getName() );
                if (typeDeclaration != null) {
                    candidate = new TypeDeclarationCandidate();
                    candidate.candidate = typeDeclaration;
                    candidate.score = level;
                    break;
                } else {
                    candidate = checkInterfaces( ifc,
                                                 baseline,
                                                 level + 1 );
                }
            }
        } else {
            candidate = baseline;
        }
        return candidate;
    }

    public Collection<TypeDeclaration> getTypeDeclarations() {
        return this.classTypeDeclaration.values();
    }

    public void addRule( final InternalKnowledgePackage pkg,
                         final RuleImpl rule ) throws InvalidPatternException {
        lock();
        try {
            this.eventSupport.fireBeforeRuleAdded( pkg,
                                                   rule );
            //        if ( !rule.isValid() ) {
            //            throw new IllegalArgumentException( "The rule called " + rule.getName() + " is not valid. Check for compile errors reported." );
            //        }
            addRule( rule );
            this.eventSupport.fireAfterRuleAdded(pkg,
                                                 rule);
        } finally {
            unlock();
        }
    }

    protected void addEntryPoint( final Package pkg,
                                  final String id ) throws InvalidPatternException {
        lock();
        try {
            addEntryPoint(id);
        } finally {
            unlock();
        }
    }

    protected void addRule(final RuleImpl rule) throws InvalidPatternException {
        // This adds the rule. ReteBuilder has a reference to the WorkingMemories and will propagate any existing facts.
        this.reteooBuilder.addRule(rule);
    }

    protected void addEntryPoint(final String id) throws InvalidPatternException {
        // This adds the entry point. ReteBuilder has a reference to the WorkingMemories and will propagate any existing facts.
        this.reteooBuilder.addEntryPoint(id);
    }

    public void addWindowDeclaration( final InternalKnowledgePackage pkg,
                                      final WindowDeclaration window ) throws InvalidPatternException {
        lock();
        try {
            addWindowDeclaration( window );
        } finally {
            unlock();
        }
    }

    protected void addWindowDeclaration(final WindowDeclaration window) throws InvalidPatternException {
        // This adds the named window. ReteBuilder has a reference to the WorkingMemories and will propagate any existing facts.
        this.reteooBuilder.addNamedWindow(window);
    }

    public void removeQuery( final String packageName,
                             final String ruleName ) {
        removeRule(packageName,
                   ruleName);
    }

    public void removeRule( final String packageName,
                            final String ruleName ) {
        lock();
        try {
            final InternalKnowledgePackage pkg = this.pkgs.get( packageName );
            if (pkg == null) {
                throw new IllegalArgumentException( "Package name '" + packageName +
                                                    "' does not exist for this Rule Base." );
            }

            RuleImpl rule = pkg.getRule( ruleName );
            if (rule == null) {
                throw new IllegalArgumentException( "Rule name '" + ruleName +
                                                    "' does not exist in the Package '" +
                                                    packageName +
                                                    "'." );
            }

            this.removalsSinceLock++;

            removeRule( pkg,
                        rule );
            pkg.removeRule( rule );
            addReloadDialectDatas( pkg.getDialectRuntimeRegistry() );
        } finally {
            unlock();
        }
    }

    /**
     * Notify listeners and sub-classes about imminent removal of a rule from a package.
     *
     * @param pkg
     * @param rule
     */
    // FIXME: removeTerminalNode(String, String) and removeTerminalNode(Package, Rule) do totally different things!
    public void removeRule( final InternalKnowledgePackage pkg,
                            final RuleImpl rule ) {
        lock();
        try {
            this.eventSupport.fireBeforeRuleRemoved(pkg,
                                                    rule);
            removeRule(rule);
            this.eventSupport.fireAfterRuleRemoved(pkg,
                                                   rule);
        } finally {
            unlock();
        }
    }

    protected void removeRule(final RuleImpl rule) {
        this.reteooBuilder.removeRule(rule);
    }

    public void removeFunction( final String packageName,
                                final String functionName ) {
        lock();
        try {
            final InternalKnowledgePackage pkg = this.pkgs.get( packageName );
            if (pkg == null) {
                throw new IllegalArgumentException( "Package name '" + packageName +
                                                    "' does not exist for this Rule Base." );
            }

            Function function = pkg.getFunctions().get(functionName);
            if (function == null) {
                throw new IllegalArgumentException( "function name '" + packageName +
                                                    "' does not exist in the Package '" +
                                                    packageName +
                                                    "'." );
            }

            removeFunction( pkg,
                            functionName );
            pkg.removeFunction( functionName );
            if (rootClassLoader instanceof ProjectClassLoader) {
                ((ProjectClassLoader)rootClassLoader).undefineClass(function.getClassName());
            }

            addReloadDialectDatas( pkg.getDialectRuntimeRegistry() );
        } finally {
            unlock();
        }
    }

    /**
     * Handle function removal.
     *
     * This method is intended for sub-classes, and called after the
     *  {@link KieBaseEventListener#beforeRuleRemoved(org.drools.core.event.BeforeRuleRemovedEvent) before-rule-removed}
     * event is fired, and before the function is physically removed from the package.
     *
     * This method is called with the rulebase lock held.
     * @param functionName
     */
    protected/* abstract */void removeFunction( String functionName ) {
        // Nothing in default.
    }

    /**
     * Notify listeners and sub-classes about imminent removal of a function from a package.
     *
     * This method is called with the rulebase lock held.
     * @param pkg
     * @param functionName
     */
    private void removeFunction( final InternalKnowledgePackage pkg,
                                 final String functionName ) {
        this.eventSupport.fireBeforeFunctionRemoved( pkg,
                                                     functionName );
        removeFunction( functionName );
        this.eventSupport.fireAfterFunctionRemoved( pkg,
                                                    functionName );
    }

    public void addProcess( final Process process ) {
        // XXX: could use a synchronized(processes) here.
        this.eventSupport.fireBeforeProcessAdded(process);
        lock();
        try {
            this.processes.put( process.getId(),
                                process );
        } finally {
            unlock();
        }
        this.eventSupport.fireAfterProcessAdded(process);
    }

    public void removeProcess( final String id ) {
        Process process = this.processes.get( id );
        if (process == null) {
            throw new IllegalArgumentException( "Process '" + id + "' does not exist for this Rule Base." );
        }
        this.eventSupport.fireBeforeProcessRemoved(process);
        lock();
        try {
            this.processes.remove( id );
            this.pkgs.get(process.getPackageName()).removeRuleFlow(id);
        } finally {
            unlock();
        }
        this.eventSupport.fireAfterProcessRemoved(process);
    }

    public Process getProcess( final String id ) {
        readLock();
        try {
            return this.processes.get( id );
        } finally {
            readUnlock();
        }
    }

    public void addStatefulSession( StatefulKnowledgeSessionImpl wm ) {
        statefulSessionLock.lock();
        try {
            this.statefulSessions.add( wm );
        } finally {
            statefulSessionLock.unlock();
        }

    }

    public InternalKnowledgePackage getPackage( final String name ) {
        return this.pkgs.get( name );
    }

    public StatefulKnowledgeSessionImpl[] getStatefulSessions() {
        statefulSessionLock.lock();
        try {
            final StatefulKnowledgeSessionImpl[] copyOfSessions = new StatefulKnowledgeSessionImpl[this.statefulSessions.size()];
            this.statefulSessions.toArray( copyOfSessions );
            return copyOfSessions;
        } finally {
            statefulSessionLock.unlock();
        }
    }

    public InternalWorkingMemory[] getWorkingMemories() {
        statefulSessionLock.lock();
        try {
            final InternalWorkingMemory[] copyOfMemories = new InternalWorkingMemory[this.statefulSessions.size()];
            this.statefulSessions.toArray( copyOfMemories );
            return copyOfMemories;
        } finally {
            statefulSessionLock.unlock();
        }
    }

    public RuleBaseConfiguration getConfiguration() {
        if ( this.config == null ) {
            this.config = new RuleBaseConfiguration();
        }
        return this.config;
    }

    public ClassLoader getRootClassLoader() {
        return this.rootClassLoader;
    }

    public void executeQueuedActions() {
        DialectRuntimeRegistry registry;
        while (( registry = reloadPackageCompilationData.poll() ) != null) {
            registry.onBeforeExecute();
        }
    }

    public RuleBasePartitionId createNewPartitionId() {
        RuleBasePartitionId p;
        synchronized (this.partitionIDs) {
            p = new RuleBasePartitionId( "P-" + this.partitionIDs.size() );
            this.partitionIDs.add( p );
        }
        return p;
    }

    public List<RuleBasePartitionId> getPartitionIds() {
        // this returns an unmodifiable CopyOnWriteArrayList, so should be safe for concurrency
        return Collections.unmodifiableList( this.partitionIDs );
    }

    public boolean isEvent( Class<?> clazz ) {
        readLock();
        try {
            for (InternalKnowledgePackage pkg : this.pkgs.values()) {
                if (pkg.isEvent( clazz )) {
                    return true;
                }
            }
            return false;
        } finally {
            readUnlock();
        }
    }

    public FactType getFactType(String packageName,
                                String typeName) {
        return getFactType(packageName + "." + typeName);
    }

    public FactType getFactType( final String name ) {
        readLock();
        try {
            for (InternalKnowledgePackage pkg : this.pkgs.values()) {
                FactType type = pkg.getFactType( name );
                if (type != null) {
                    return type;
                }
            }
            return null;
        } finally {
            readUnlock();
        }
    }

    private void addReloadDialectDatas( DialectRuntimeRegistry registry ) {
        this.reloadPackageCompilationData.offer( registry );
    }

    public ClassFieldAccessorCache getClassFieldAccessorCache() {
        return this.classFieldAccessorCache;
    }

    public Set<String> getEntryPointIds() {
        Set<String> entryPointIds = new HashSet<String>();
        for (InternalKnowledgePackage pkg : this.pkgs.values()) {
            entryPointIds.addAll( pkg.getEntryPointIds() );
        }
        return entryPointIds;
    }

    public TripleStore getTripleStore() {
        return this.getConfiguration().getComponentFactory().getTripleStore();
    }

    public TraitRegistry getTraitRegistry() {
        return this.getConfiguration().getComponentFactory().getTraitRegistry();
    }


    public void removeObjectsGeneratedFromResource(Resource resource) {
        for (InternalKnowledgePackage pkg : pkgs.values()) {
            List<RuleImpl> rulesToBeRemoved = pkg.removeRulesGeneratedFromResource(resource);
            for (RuleImpl rule : rulesToBeRemoved) {
                removeRule(rule);
            }
            List<Function> functionsToBeRemoved = pkg.removeFunctionsGeneratedFromResource(resource);
            for (Function function : functionsToBeRemoved) {
                removeFunction(function.getName());
            }
        }
    }
}
