/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.rule.InvalidPatternException;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.ruleunit.RuleUnitRegistry;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.TripleStore;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;

public interface InternalKnowledgeBase extends KnowledgeBase {

    String getId();

    RuleBasePartitionId createNewPartitionId();

    RuleBaseConfiguration getConfiguration();

    void readLock();
    void readUnlock();

    void lock();
    void unlock();

    void enqueueModification(Runnable modification);
    boolean flushModifications();

    int nextWorkingMemoryCounter();

    int getWorkingMemoryCounter();

    FactHandleFactory newFactHandleFactory();

    FactHandleFactory newFactHandleFactory(int id, long counter) throws IOException;

    Map<String, Class<?>> getGlobals();

    int getNodeCount();
    int getMemoryCount(String unitName);

    void executeQueuedActions();

    ReteooBuilder getReteooBuilder();

    void registerAddedEntryNodeCache(EntryPointNode node);
    Set<EntryPointNode> getAddedEntryNodeCache();

    void registeRremovedEntryNodeCache(EntryPointNode node);
    Set<EntryPointNode> getRemovedEntryNodeCache();

    Rete getRete();

    ClassLoader getRootClassLoader();

    void assertObject(FactHandle handle,
                      Object object,
                      PropagationContext context,
                      InternalWorkingMemory workingMemory);

    void retractObject(FactHandle handle,
                       PropagationContext context,
                       StatefulKnowledgeSessionImpl workingMemory);

    void disposeStatefulSession(StatefulKnowledgeSessionImpl statefulSession);

    StatefulKnowledgeSessionImpl getCachedSession(SessionConfiguration config, Environment environment);

    TripleStore getTripleStore();

    TraitRegistry getTraitRegistry();

    Class<?> registerAndLoadTypeDefinition( String className, byte[] def ) throws ClassNotFoundException;

    InternalKnowledgePackage getPackage(String name);
    void addPackages(InternalKnowledgePackage[] pkgs );
    void addPackage(InternalKnowledgePackage pkg);
    void addPackages( final Collection<InternalKnowledgePackage> newPkgs );
    Map<String, InternalKnowledgePackage> getPackagesMap();

    ClassFieldAccessorCache getClassFieldAccessorCache();

    InternalWorkingMemory[] getWorkingMemories();

    boolean hasSegmentPrototypes();
    void invalidateSegmentPrototype(LeftTupleNode rootNode);
    SegmentMemory createSegmentFromPrototype(InternalWorkingMemory wm, LeftTupleSource tupleSource);
    SegmentMemory.Prototype getSegmentPrototype(SegmentMemory segment);

    void addRule( InternalKnowledgePackage pkg, RuleImpl rule ) throws InvalidPatternException;
    void removeRule( InternalKnowledgePackage pkg, RuleImpl rule ) throws InvalidPatternException;
    void removeRules( InternalKnowledgePackage pkg, List<RuleImpl> rules ) throws InvalidPatternException;

    void addProcess( Process process );
    void removeProcess( final String id );

    void addGlobal(String identifier, Class clazz);
    void removeGlobal(String identifier);

    boolean removeObjectsGeneratedFromResource(Resource resource);

    TypeDeclaration getTypeDeclaration( Class<?> clazz );
    TypeDeclaration getExactTypeDeclaration( Class<?> clazz );
    TypeDeclaration getOrCreateExactTypeDeclaration( Class<?> clazz );
    Collection<TypeDeclaration> getTypeDeclarations();
    void registerTypeDeclaration( TypeDeclaration newDecl, InternalKnowledgePackage newPkg );

	ReleaseId getResolvedReleaseId();
	void setResolvedReleaseId(ReleaseId currentReleaseId);
	String getContainerId();
	void setContainerId(String containerId);
	void initMBeans();

    RuleUnitRegistry getRuleUnitRegistry();
    boolean hasUnits();
}
