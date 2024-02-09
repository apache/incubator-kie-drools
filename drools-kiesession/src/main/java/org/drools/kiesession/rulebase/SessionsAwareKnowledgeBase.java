/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.kiesession.rulebase;

import org.drools.base.RuleBase;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.InvalidPatternException;
import org.drools.base.rule.TypeDeclaration;
import org.drools.base.ruleunit.RuleUnitDescriptionRegistry;
import org.drools.core.KieBaseConfigurationImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.InternalKieContainer;
import org.drools.core.impl.KieBaseUpdate;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.SegmentMemory.SegmentPrototype;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.KieSessionsPool;
import org.kie.api.runtime.StatelessKieSession;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionsAwareKnowledgeBase implements InternalKnowledgeBase {

    private final KnowledgeBaseImpl delegate;

    private final transient Set<InternalWorkingMemory> statefulSessions = ConcurrentHashMap.newKeySet();

    private KieSessionsPool sessionPool;

    private final AtomicInteger workingMemoryCounter = new AtomicInteger(0);

    private InternalKieContainer kieContainer;

    private final Queue<Runnable> kbaseModificationsQueue = new ConcurrentLinkedQueue<>();

    private final AtomicInteger sessionDeactivationsCounter = new AtomicInteger();
    private final AtomicBoolean flushingUpdates = new AtomicBoolean( false );

    private final AtomicBoolean mbeanRegistered = new AtomicBoolean(false);

    private final KieBaseEventSupport eventSupport = new KieBaseEventSupport(this);
    public final Set<KieBaseEventListener> kieBaseListeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public SessionsAwareKnowledgeBase() {
        this(RuleBaseFactory.newRuleBase());
    }

    public SessionsAwareKnowledgeBase(KieBaseConfiguration kbaseConfiguration) {
        this(RuleBaseFactory.newRuleBase(kbaseConfiguration));
    }

    public SessionsAwareKnowledgeBase(RuleBase delegate) {
        this.delegate = (KnowledgeBaseImpl) delegate;

        if (this.delegate.getRuleBaseConfiguration().getSessionPoolSize() > 0) {
            sessionPool = newKieSessionsPool( this.delegate.getRuleBaseConfiguration().getSessionPoolSize() );
        }
    }

    public KnowledgeBaseImpl getDelegate() {
        return delegate;
    }

    @Override
    public void setKieContainer( InternalKieContainer kieContainer ) {
        this.kieContainer = kieContainer;
    }

    @Override
    public InternalKieContainer getKieContainer() {
        return this.kieContainer;
    }

    @Override
    public KieSessionsPool getSessionPool() {
        return sessionPool;
    }

    @Override
    public Collection<? extends KieSession> getKieSessions() {
        return (Collection<? extends KieSession>) (Object) Collections.unmodifiableSet(statefulSessions);
    }

    @Override
    public StatelessKieSession newStatelessKieSession(KieSessionConfiguration conf) {
        return RuntimeComponentFactory.get().createStatelessSession( this, conf );
    }

    @Override
    public StatelessKieSession newStatelessKieSession() {
        return RuntimeComponentFactory.get().createStatelessSession( this, null );
    }

    @Override
    public KieSessionsPool newKieSessionsPool( int initialSize) {
        return RuntimeComponentFactory.get().createSessionsPool(this, initialSize);
    }

    @Override
    public KieSession newKieSession() {
        return newKieSession(getSessionConfiguration(), EnvironmentFactory.newEnvironment());
    }

    @Override
    public KieSession newKieSession(KieSessionConfiguration conf, Environment environment) {
        return newKieSession(conf, environment, false);
    }

    @Override
    public KieSession newKieSession(KieSessionConfiguration conf, Environment environment, boolean fromPool) {
        // NOTE if you update here, you'll also need to update the JPAService
        if ( conf == null ) {
            conf = getSessionConfiguration();
        }

        SessionConfiguration sessionConfig = conf.as(SessionConfiguration.KEY);

        if ( environment == null ) {
            environment = EnvironmentFactory.newEnvironment();
        }

        if ( this.getRuleBaseConfiguration().isSequential() ) {
            throw new RuntimeException( "Cannot have a stateful rule session, with sequential configuration set to true" );
        }

        readLock();
        try {
            return (KieSession) RuntimeComponentFactory.get().createStatefulSession(this, environment, sessionConfig, fromPool );
        } finally {
            readUnlock();
        }
    }

    @Override
    public void addStatefulSession( InternalWorkingMemory wm ) {
        this.statefulSessions.add( wm );
    }

    @Override
    public void disposeStatefulSession(InternalWorkingMemory statefulSession) {
        this.statefulSessions.remove(statefulSession);
        if (kieContainer != null) {
            kieContainer.disposeSession( (KieSession) statefulSession );
        }
    }

    @Override
    public int nextWorkingMemoryCounter() {
        return this.workingMemoryCounter.getAndIncrement();
    }

    @Override
    public int getWorkingMemoryCounter() {
        return this.workingMemoryCounter.get();
    }

    @Override
    public Collection<InternalWorkingMemory> getWorkingMemories() {
        return Collections.unmodifiableSet( statefulSessions );
    }

    @Override
    public void addPackages( Collection<? extends KiePackage> newPkgs ) {
        final List<InternalKnowledgePackage> clonedPkgs = new ArrayList<>();
        for (KiePackage newPkg : newPkgs) {
            clonedPkgs.add(((InternalKnowledgePackage)newPkg).deepCloneIfAlreadyInUse(delegate.getRootClassLoader()));
        }

        clonedPkgs.sort(Comparator.comparing( (InternalKnowledgePackage p) -> p.getRules().size() ).reversed().thenComparing( InternalKnowledgePackage::getName ));
        enqueueModification( () -> internalAddPackages(clonedPkgs));
    }

    public void removeGlobal(String identifier) {
        delegate.removeGlobal( identifier );
        for ( InternalWorkingMemory wm : getWorkingMemories() ) {
            wm.removeGlobal(identifier);
        }
    }

    @Override
    public Future<KiePackage> addPackage(final KiePackage newPkg ) {
        InternalKnowledgePackage clonedPkg = ((InternalKnowledgePackage)newPkg).deepCloneIfAlreadyInUse(delegate.getRootClassLoader());
        CompletableFuture<KiePackage> result = new CompletableFuture<>();
        enqueueModification( () -> {
            internalAddPackages(Collections.singletonList(clonedPkg));
            result.complete( getPackage(newPkg.getName()) );
        } );
        return result;
    }

    private void internalAddPackages(List<InternalKnowledgePackage> clonedPkgs) {
        for ( InternalWorkingMemory wm : getWorkingMemories() ) {
            wm.flushPropagations();
        }

        for (InternalKnowledgePackage newPkg : clonedPkgs) {
            this.eventSupport.fireBeforePackageAdded(newPkg);
            for (Rule rule : newPkg.getRules()) {
                this.eventSupport.fireBeforeRuleAdded( (RuleImpl) rule );
            }
        }

        delegate.kBaseInternal_addPackages(clonedPkgs, statefulSessions);

        for (InternalKnowledgePackage newPkg : clonedPkgs) {
            for (Rule rule : newPkg.getRules()) {
                this.eventSupport.fireAfterRuleAdded( (RuleImpl) rule );
            }
            this.eventSupport.fireAfterPackageAdded(newPkg);
        }
    }

    @Override
    public void removeKiePackage(String packageName) {
        enqueueModification( () -> {
            final InternalKnowledgePackage pkg = getPackage( packageName );
            if (pkg == null) {
                throw new IllegalArgumentException( "Package name '" + packageName + "' does not exist for this Rule Base." );
            }
            this.eventSupport.fireBeforePackageRemoved( pkg );
            removeRules( (Collection<RuleImpl>) (Object) pkg.getRules() );
            delegate.kBaseInternal_removePackage(pkg, statefulSessions);
            this.eventSupport.fireAfterPackageRemoved( pkg );
        } );
    }

    @Override
    public void enqueueModification(Runnable modification) {
        if ( tryLockAndDeactivate() ) {
            try {
                modification.run();
            } finally {
                unlockAndActivate();
            }
        } else {
            kbaseModificationsQueue.offer(modification);
        }
    }

    public boolean flushModifications() {
        if (!flushingUpdates.compareAndSet( false, true )) {
            return false;
        }

        if (kbaseModificationsQueue.isEmpty()) {
            flushingUpdates.set( false );
            return false;
        }

        try {
            lockAndDeactivate();
            while (!kbaseModificationsQueue.isEmpty()) {
                kbaseModificationsQueue.poll().run();
            }
        } finally {
            flushingUpdates.set( false );
            unlockAndActivate();
        }
        return true;
    }

    private void lockAndDeactivate() {
        lock();
        deactivateAllSessions();
    }

    private void unlockAndActivate() {
        activateAllSessions();
        unlock();
    }

    public void lock() {
        // The lock is reentrant, so we need additional magic here to skip
        // notifications for locked if this thread already has locked it.
        boolean firstLock = !delegate.kBaseInternal_getLock().isWriteLockedByCurrentThread();
        if (firstLock) {
            this.eventSupport.fireBeforeRuleBaseLocked();
        }
        // Always lock to increase the counter
        delegate.kBaseInternal_lock();
        if ( firstLock ) {
            this.eventSupport.fireAfterRuleBaseLocked();
        }
    }

    public void unlock() {
        boolean lastUnlock = delegate.kBaseInternal_getLock().getWriteHoldCount() == 1;
        if (lastUnlock) {
            this.eventSupport.fireBeforeRuleBaseUnlocked();
        }
        delegate.kBaseInternal_unlock();
        if ( lastUnlock ) {
            this.eventSupport.fireAfterRuleBaseUnlocked();
        }
    }


    private boolean tryDeactivateAllSessions() {
        Collection<InternalWorkingMemory> wms = getWorkingMemories();
        if (wms.isEmpty()) {
            return true;
        }
        List<InternalWorkingMemory> deactivatedWMs = new ArrayList<>();
        for ( InternalWorkingMemory wm : wms ) {
            if (wm.tryDeactivate()) {
                deactivatedWMs.add(wm);
            } else {
                for (InternalWorkingMemory deactivatedWM : deactivatedWMs) {
                    deactivatedWM.activate();
                }
                return false;
            }
        }
        return true;
    }

    private boolean tryLockAndDeactivate() {
        if ( sessionDeactivationsCounter.incrementAndGet() > 1 ) {
            delegate.kBaseInternal_writeLock();
            return true;
        }

        boolean locked = delegate.kBaseInternal_tryWriteLock();
        if ( locked && !tryDeactivateAllSessions() ) {
            delegate.kBaseInternal_writeUnlock();
            locked = false;
        }

        if (!locked) {
            sessionDeactivationsCounter.decrementAndGet();
        }

        return locked;
    }

    private void deactivateAllSessions() {
        if ( sessionDeactivationsCounter.incrementAndGet() < 2 ) {
            for ( InternalWorkingMemory wm : getWorkingMemories() ) {
                wm.deactivate();
            }
        }
    }

    private void activateAllSessions() {
        if ( sessionDeactivationsCounter.decrementAndGet() == 0 ) {
            for ( InternalWorkingMemory wm : getWorkingMemories() ) {
                wm.activate();
            }
        }
    }

    @Override
    public void initMBeans() {
        if (getConfiguration() != null && getKieBaseConfiguration().isMBeansEnabled() && mbeanRegistered.compareAndSet(false, true)) {
            // no further synch enforced at this point, even if other threads might not immediately see (yet) the MBean registered on JMX.
            DroolsManagementAgent.getInstance().registerKnowledgeBase(this);
        }
    }

    @Override
    public void addEventListener(KieBaseEventListener listener) {
        synchronized (kieBaseListeners) {
            if ( !kieBaseListeners.contains( listener ) ) {
                eventSupport.addEventListener( listener );
                kieBaseListeners.add( listener );
            }
        }
    }

    @Override
    public void removeEventListener(KieBaseEventListener listener) {
        synchronized (kieBaseListeners) {
            eventSupport.removeEventListener( listener );
            kieBaseListeners.remove( listener );
        }
    }

    @Override
    public Collection<KieBaseEventListener> getKieBaseEventListeners() {
        return Collections.unmodifiableCollection( kieBaseListeners );
    }

    @Override
    public SessionConfiguration getSessionConfiguration() {
        return delegate.getSessionConfiguration();
    }

    @Override
    public void registerAddedEntryNodeCache(EntryPointNode node) {
        delegate.registerAddedEntryNodeCache(node);
    }

    @Override
    public Set<EntryPointNode> getAddedEntryNodeCache() {
        return delegate.getAddedEntryNodeCache();
    }

    @Override
    public void registeRremovedEntryNodeCache(EntryPointNode node) {
        delegate.registeRremovedEntryNodeCache(node);
    }

    @Override
    public Set<EntryPointNode> getRemovedEntryNodeCache() {
        return delegate.getRemovedEntryNodeCache();
    }

    @Override
    public Rete getRete() {
        return delegate.getRete();
    }

    @Override
    public ReteooBuilder getReteooBuilder() {
        return delegate.getReteooBuilder();
    }

    @Override
    public int getNodeCount() {
        return delegate.getNodeCount();
    }

    @Override
    public int getMemoryCount() {
        return delegate.getMemoryCount();
    }

    @Override
    public void invalidateSegmentPrototype(LeftTupleNode rootNode) {
        delegate.invalidateSegmentPrototype(rootNode);
    }

    @Override
    public SegmentMemory createSegmentFromPrototype(ReteEvaluator reteEvaluator, LeftTupleSource tupleSource) {
        return delegate.createSegmentFromPrototype(reteEvaluator, tupleSource);
    }

    @Override
    public SegmentMemory createSegmentFromPrototype(ReteEvaluator reteEvaluator, SegmentPrototype smem) {
        return delegate.createSegmentFromPrototype(reteEvaluator, smem);
    }

    public boolean hasSegmentPrototypes() {
        return delegate.hasSegmentPrototypes();
    }

    @Override
    public SegmentPrototype getSegmentPrototype(LeftTupleNode node) {
        return delegate.getSegmentPrototype(node);
    }

    @Override
    public SegmentPrototype getSegmentPrototype(SegmentMemory segment) {
        return delegate.getSegmentPrototype(segment);
    }

    @Override
    public TypeDeclaration getExactTypeDeclaration(Class<?> clazz) {
        return delegate.getExactTypeDeclaration(clazz);
    }

    @Override
    public TypeDeclaration getOrCreateExactTypeDeclaration(Class<?> clazz) {
        return delegate.getOrCreateExactTypeDeclaration(clazz);
    }

    @Override
    public TypeDeclaration getTypeDeclaration(Class<?> clazz) {
        return delegate.getTypeDeclaration(clazz);
    }

    @Override
    public Collection<TypeDeclaration> getTypeDeclarations() {
        return delegate.getTypeDeclarations();
    }

    @Override
    public void beforeIncrementalUpdate(KieBaseUpdate kieBaseUpdate) {
        delegate.beforeIncrementalUpdate(kieBaseUpdate);
    }

    @Override
    public void afterIncrementalUpdate(KieBaseUpdate kieBaseUpdate) {
        delegate.afterIncrementalUpdate(kieBaseUpdate);
    }

    @Override
    public void processAllTypesDeclaration(Collection<InternalKnowledgePackage> pkgs) {
        delegate.processAllTypesDeclaration(pkgs);
    }

    @Override
    public boolean hasMultipleAgendaGroups() {
        return delegate.hasMultipleAgendaGroups();
    }

    @Override
    public void registerTypeDeclaration(TypeDeclaration newDecl, InternalKnowledgePackage newPkg) {
        delegate.registerTypeDeclaration(newDecl, newPkg);
    }

    @Override
    public Class<?> registerAndLoadTypeDefinition(String className, byte[] def) throws ClassNotFoundException {
        return delegate.registerAndLoadTypeDefinition(className, def);
    }

    @Override
    public void addGlobal(String identifier, Type type) {
        delegate.addGlobal(identifier, type);
    }

    @Override
    public Rule getRule(String packageName, String ruleName) {
        return delegate.getRule(packageName, ruleName);
    }

    @Override
    public Query getQuery(String packageName, String queryName) {
        return delegate.getQuery(packageName, queryName);
    }

    @Override
    public Collection<KiePackage> getKiePackages() {
        return delegate.getKiePackages();
    }

    @Override
    public KiePackage getKiePackage(String packageName) {
        return delegate.getKiePackage(packageName);
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public FactHandleFactory newFactHandleFactory() {
        return delegate.newFactHandleFactory();
    }

    @Override
    public FactHandleFactory newFactHandleFactory(long id, long counter) {
        return delegate.newFactHandleFactory(id, counter);
    }

    @Override
    public Collection<Process> getProcesses() {
        return delegate.getProcesses();
    }

    @Override
    public InternalKnowledgePackage[] getPackages() {
        return delegate.getPackages();
    }

    @Override
    public Map<String, InternalKnowledgePackage> getPackagesMap() {
        return delegate.getPackagesMap();
    }

    @Override
    public Map<String, Type> getGlobals() {
        return delegate.getGlobals();
    }

    @Override
    public void readLock() {
        delegate.readLock();
    }

    @Override
    public void readUnlock() {
        delegate.readUnlock();
    }

    @Override
    public void addRules(Collection<RuleImpl> rules ) throws InvalidPatternException {
        enqueueModification( () -> {
            for (RuleImpl rule : rules) {
                this.eventSupport.fireBeforeRuleAdded(rule);
            }
            delegate.kBaseInternal_addRules( rules, statefulSessions );
            for (RuleImpl rule : rules) {
                this.eventSupport.fireAfterRuleAdded(rule);
            }
        } );
    }

    @Override
    public void removeQuery(String packageName, String ruleName) {
        delegate.removeQuery(packageName, ruleName);
    }

    @Override
    public void removeRule( final String packageName, final String ruleName ) {
        enqueueModification( () -> {
            final InternalKnowledgePackage pkg = getPackage(packageName);
            if (pkg == null) {
                throw new IllegalArgumentException( "Package name '" + packageName +
                        "' does not exist for this Rule Base." );
            }

            RuleImpl rule = pkg.getRule(ruleName);
            if (rule == null) {
                throw new IllegalArgumentException( "Rule name '" + ruleName +
                        "' does not exist in the Package '" + packageName + "'." );
            }
            this.eventSupport.fireBeforeRuleRemoved(rule);
            delegate.kBaseInternal_removeRule(pkg, rule, statefulSessions);
            this.eventSupport.fireAfterRuleRemoved(rule);
        } );
    }

    @Override
    public void removeRules( Collection<RuleImpl> rules ) {
        enqueueModification( () -> {
            for (RuleImpl rule : rules) {
                this.eventSupport.fireBeforeRuleRemoved(rule);
            }
            delegate.kBaseInternal_removeRules( rules, statefulSessions );
            for (RuleImpl rule : rules) {
                this.eventSupport.fireAfterRuleRemoved(rule);
            }
        } );
    }

    @Override
    public void removeFunction( final String packageName, final String functionName ) {
        enqueueModification( () -> {
            final InternalKnowledgePackage pkg = this.getPackage( packageName );
            if (pkg == null) {
                throw new IllegalArgumentException( "Package name '" + packageName +
                        "' does not exist for this Rule Base." );
            }
            this.eventSupport.fireBeforeFunctionRemoved( pkg, functionName );
            delegate.kBaseInternal_removeFunction( pkg, functionName );
            this.eventSupport.fireAfterFunctionRemoved( pkg, functionName );
        });
    }

    @Override
    public void addProcess( final Process process ) {
        // XXX: could use a synchronized(processes) here.
        lock();
        try {
            this.eventSupport.fireBeforeProcessAdded(process);
            delegate.kBaseInternal_addProcess( process );
            this.eventSupport.fireAfterProcessAdded(process);
        } finally {
            unlock();
        }
    }


    @Override
    public void removeProcess( final String id ) {
        enqueueModification( () -> {
            Process process = getProcess( id );
            if ( process == null ) {
                throw new IllegalArgumentException( "Process '" + id + "' does not exist for this Rule Base." );
            }
            this.eventSupport.fireBeforeProcessRemoved( process );
            delegate.kBaseInternal_removeProcess( id, process );
            this.eventSupport.fireAfterProcessRemoved( process );
        } );
    }

    @Override
    public Process getProcess(String id) {
        return delegate.getProcess(id);
    }

    @Override
    public InternalKnowledgePackage getPackage(String name) {
        return delegate.getPackage(name);
    }

    @Override public KieBaseConfigurationImpl getKieBaseConfiguration() {
        return delegate.getKieBaseConfiguration();
    }

    @Override public KieBaseConfiguration getConfiguration() {
        return delegate.getConfiguration();
    }

    @Override
    public RuleBaseConfiguration getRuleBaseConfiguration() {
        return delegate.getRuleBaseConfiguration();
    }

    @Override
    public ClassLoader getRootClassLoader() {
        return delegate.getRootClassLoader();
    }

    @Override
    public void executeQueuedActions() {
        delegate.executeQueuedActions();
    }

    @Override
    public RuleBasePartitionId createNewPartitionId() {
        return delegate.createNewPartitionId();
    }

    @Override
    public boolean isPartitioned() {
        return delegate.isPartitioned();
    }

    @Override
    public int getParallelEvaluationSlotsCount() {
        return delegate.getParallelEvaluationSlotsCount();
    }

    @Override
    public FactType getFactType(String packageName, String typeName) {
        return delegate.getFactType(packageName, typeName);
    }

    @Override
    public ClassFieldAccessorCache getClassFieldAccessorCache() {
        return delegate.getClassFieldAccessorCache();
    }

    @Override
    public Set<String> getEntryPointIds() {
        return delegate.getEntryPointIds();
    }

    @Override
    public boolean removeObjectsGeneratedFromResource(Resource resource, Collection<InternalWorkingMemory> workingMemories) {
        return delegate.removeObjectsGeneratedFromResource(resource, workingMemories);
    }

    @Override
    public ReleaseId getResolvedReleaseId() {
        return delegate.getResolvedReleaseId();
    }

    @Override
    public void setResolvedReleaseId(ReleaseId currentReleaseId) {
        delegate.setResolvedReleaseId(currentReleaseId);
    }

    @Override
    public String getContainerId() {
        return delegate.getContainerId();
    }

    @Override
    public void setContainerId(String containerId) {
        delegate.setContainerId(containerId);
    }

    @Override
    public RuleUnitDescriptionRegistry getRuleUnitDescriptionRegistry() {
        return delegate.getRuleUnitDescriptionRegistry();
    }

    @Override
    public boolean hasUnits() {
        return delegate.hasUnits();
    }

    @Override
    public List<AsyncReceiveNode> getReceiveNodes() {
        return delegate.getReceiveNodes();
    }

    @Override
    public void addReceiveNode(AsyncReceiveNode node) {
        delegate.addReceiveNode(node);
    }

    @Override
    public void registerSegmentPrototype(LeftTupleNode tupleSource, SegmentPrototype smem) {
        delegate.registerSegmentPrototype(tupleSource, smem);
    }
}
