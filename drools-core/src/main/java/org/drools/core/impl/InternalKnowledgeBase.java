package org.drools.core.impl;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.rule.InvalidPatternException;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.TripleStore;
import org.kie.api.io.Resource;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface InternalKnowledgeBase extends KnowledgeBase {

    String getId();

    RuleBasePartitionId createNewPartitionId();

    RuleBaseConfiguration getConfiguration();

    void readLock();
    void readUnlock();

    void lock();
    void unlock();

    int nextWorkingMemoryCounter();

    int getWorkingMemoryCounter();

    FactHandleFactory newFactHandleFactory();

    FactHandleFactory newFactHandleFactory(int id, long counter) throws IOException;

    Map<String, Class<?>> getGlobals();

    int getNodeCount();

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

    void invalidateSegmentPrototype(LeftTupleSource tupleSource);

    void addRule( InternalKnowledgePackage pkg, RuleImpl rule ) throws InvalidPatternException;
    void removeRule( InternalKnowledgePackage pkg, RuleImpl rule ) throws InvalidPatternException;

    void addGlobal(String identifier, Class clazz);

    void removeObjectsGeneratedFromResource(Resource resource);

    TypeDeclaration getTypeDeclaration( Class<?> clazz );
    Collection<TypeDeclaration> getTypeDeclarations();
}
