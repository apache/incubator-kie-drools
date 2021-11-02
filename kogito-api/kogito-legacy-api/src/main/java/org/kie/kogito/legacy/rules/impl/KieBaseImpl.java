/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.legacy.rules.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.impl.InternalKieContainer;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.AsyncReceiveNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.rule.InvalidPatternException;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.ruleunit.RuleUnitDescriptionRegistry;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.util.TripleStore;
import org.kie.api.KieBase;
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

public class KieBaseImpl implements InternalKnowledgeBase {

    private final InternalKnowledgeBase delegate;

    public KieBaseImpl(KieBase delegate) {
        this.delegate = (InternalKnowledgeBase) delegate;
    }

    @Override
    public Collection<KiePackage> getKiePackages() {
        return delegate.getKiePackages();
    }

    @Override
    public KiePackage getKiePackage(String s) {
        return delegate.getKiePackage(s);
    }

    @Override
    public void removeKiePackage(String s) {
        delegate.removeKiePackage(s);
    }

    @Override
    public Rule getRule(String s, String s1) {
        return delegate.getRule(s, s1);
    }

    @Override
    public void removeRule(String s, String s1) {
        delegate.removeRule(s, s1);
    }

    @Override
    public Query getQuery(String s, String s1) {
        return delegate.getQuery(s, s1);
    }

    @Override
    public void removeQuery(String s, String s1) {
        delegate.removeQuery(s, s1);
    }

    @Override
    public void removeFunction(String s, String s1) {
        delegate.removeFunction(s, s1);
    }

    @Override
    public FactType getFactType(String s, String s1) {
        return delegate.getFactType(s, s1);
    }

    @Override
    public KieSession newKieSession(KieSessionConfiguration kieSessionConfiguration, Environment environment) {
        return new KieSessionImpl(delegate.newKieSession(kieSessionConfiguration, environment));
    }

    @Override
    public KieSession newKieSession() {
        return new KieSessionImpl(delegate.newKieSession());
    }

    @Override
    public KieSessionsPool newKieSessionsPool(int i) {
        return delegate.newKieSessionsPool(i);
    }

    @Override
    public Collection<? extends KieSession> getKieSessions() {
        return delegate.getKieSessions();
    }

    @Override
    public StatelessKieSession newStatelessKieSession(KieSessionConfiguration kieSessionConfiguration) {
        return delegate.newStatelessKieSession(kieSessionConfiguration);
    }

    @Override
    public StatelessKieSession newStatelessKieSession() {
        return delegate.newStatelessKieSession();
    }

    @Override
    public Set<String> getEntryPointIds() {
        return delegate.getEntryPointIds();
    }

    @Override
    public void addEventListener(KieBaseEventListener kieBaseEventListener) {
        delegate.addEventListener(kieBaseEventListener);
    }

    @Override
    public void removeEventListener(KieBaseEventListener kieBaseEventListener) {
        delegate.removeEventListener(kieBaseEventListener);
    }

    @Override
    public Collection<KieBaseEventListener> getKieBaseEventListeners() {
        return delegate.getKieBaseEventListeners();
    }

    @Override
    public Process getProcess(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StatefulKnowledgeSessionImpl createSession(long l, FactHandleFactory factHandleFactory, long l1, SessionConfiguration sessionConfiguration, InternalAgenda internalAgenda,
            Environment environment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public RuleBasePartitionId createNewPartitionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RuleBaseConfiguration getConfiguration() {
        return delegate.getConfiguration();
    }

    @Override
    public void readLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readUnlock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enqueueModification(Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean flushModifications() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nextWorkingMemoryCounter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getWorkingMemoryCounter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FactHandleFactory newFactHandleFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FactHandleFactory newFactHandleFactory(long l, long l1) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Class<?>> getGlobals() {
        return delegate.getGlobals();
    }

    @Override
    public int getNodeCount() {
        return delegate.getNodeCount();
    }

    @Override
    public int getMemoryCount(String s) {
        return delegate.getMemoryCount(s);
    }

    @Override
    public void executeQueuedActions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReteooBuilder getReteooBuilder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerAddedEntryNodeCache(EntryPointNode entryPointNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<EntryPointNode> getAddedEntryNodeCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registeRremovedEntryNodeCache(EntryPointNode entryPointNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<EntryPointNode> getRemovedEntryNodeCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rete getRete() {
        return delegate.getRete();
    }

    @Override
    public ClassLoader getRootClassLoader() {
        return delegate.getRootClassLoader();
    }

    @Override
    public void disposeStatefulSession(StatefulKnowledgeSessionImpl statefulKnowledgeSession) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TripleStore getTripleStore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TraitRegistry getTraitRegistry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> registerAndLoadTypeDefinition(String s, byte[] bytes) throws ClassNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InternalKnowledgePackage getPackage(String s) {
        return delegate.getPackage(s);
    }

    @Override
    public Future<KiePackage> addPackage(KiePackage kiePackage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addPackages(Collection<? extends KiePackage> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, InternalKnowledgePackage> getPackagesMap() {
        return delegate.getPackagesMap();
    }

    @Override
    public ClassFieldAccessorCache getClassFieldAccessorCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<InternalWorkingMemory> getWorkingMemories() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSegmentPrototypes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidateSegmentPrototype(LeftTupleNode leftTupleNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SegmentMemory createSegmentFromPrototype(ReteEvaluator reteEvaluator, LeftTupleSource leftTupleSource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SegmentMemory.Prototype getSegmentPrototype(SegmentMemory segmentMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void processAllTypesDeclaration(Collection<InternalKnowledgePackage> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRules(Collection<RuleImpl> collection) throws InvalidPatternException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeRules(Collection<RuleImpl> collection) throws InvalidPatternException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addProcess(Process process) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeProcess(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addGlobal(String s, Class aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeGlobal(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeObjectsGeneratedFromResource(Resource resource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeDeclaration getTypeDeclaration(Class<?> aClass) {
        return delegate.getTypeDeclaration(aClass);
    }

    @Override
    public TypeDeclaration getExactTypeDeclaration(Class<?> aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeDeclaration getOrCreateExactTypeDeclaration(Class<?> aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<TypeDeclaration> getTypeDeclarations() {
        return delegate.getTypeDeclarations();
    }

    @Override
    public void registerTypeDeclaration(TypeDeclaration typeDeclaration, InternalKnowledgePackage internalKnowledgePackage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReleaseId getResolvedReleaseId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setResolvedReleaseId(ReleaseId releaseId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContainerId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContainerId(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setKieContainer(InternalKieContainer internalKieContainer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initMBeans() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RuleUnitDescriptionRegistry getRuleUnitDescriptionRegistry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasUnits() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SessionConfiguration getSessionConfiguration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AsyncReceiveNode> getReceiveNodes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addReceiveNode(AsyncReceiveNode asyncReceiveNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasMultipleAgendaGroups() {
        throw new UnsupportedOperationException();
    }

    public Collection<Process> getProcesses() {
        throw new UnsupportedOperationException();
    }
}
