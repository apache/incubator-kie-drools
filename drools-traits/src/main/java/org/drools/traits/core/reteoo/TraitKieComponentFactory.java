/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.traits.core.reteoo;

import org.drools.core.base.TraitHelper;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.NamedEntryPointFactory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.spi.FactHandleFactory;
import org.drools.traits.core.base.TraitHelperImpl;
import org.drools.traits.core.common.TraitNamedEntryPointFactory;
import org.drools.traits.core.definitions.impl.TraitKnowledgePackageImpl;
import org.drools.traits.core.factmodel.TraitClassBuilderFactory;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.traits.core.factmodel.traits.TraitFactoryImpl;
import org.drools.traits.core.factmodel.traits.TraitProxyImpl;
import org.drools.traits.core.factmodel.traits.TraitRegistryImpl;

public class TraitKieComponentFactory extends KieComponentFactory {

    private NodeFactory nodeFactory = TraitPhreakNodeFactory.getInstance();

    @Override
    public NodeFactory getNodeFactoryService() {
        return nodeFactory;
    }

    public void setNodeFactoryProvider(NodeFactory provider) {
        nodeFactory = provider;
    }

    private Class<?> baseTraitProxyClass = TraitProxyImpl.class;

    @Override
    public Class<?> getBaseTraitProxyClass() {
        return baseTraitProxyClass;
    }

    private TraitFactory traitFactory;

    @Override
    public TraitFactory initTraitFactory(InternalKnowledgeBase knowledgeBase) {
        if(traitFactory == null) {
            traitFactory = new TraitFactoryImpl<>(knowledgeBase);
        }
        return traitFactory;
    }

    @Override
    public TraitFactory getTraitFactory() {
        return traitFactory;
    }

    public void setTraitFactory(TraitFactory tf) {
        traitFactory = tf;
    }

    private TraitRegistry traitRegistry;

    @Override
    public TraitRegistry getTraitRegistry() {
        if(traitRegistry == null) {
            traitRegistry = new TraitRegistryImpl();
        }
        return traitRegistry;
    }

    private TraitClassBuilderFactory traitClassBuilderFactory = new TraitClassBuilderFactory();

    @Override
    public ClassBuilderFactory getClassBuilderFactory() {
        return traitClassBuilderFactory;
    }

    @Override
    public FactHandleFactory getFactHandleFactoryService() {
        return new TraitFactHandleFactory();
    }

    @Override
    public NamedEntryPointFactory getNamedEntryPointFactory() {
        return new TraitNamedEntryPointFactory();
    }

    @Override
    public TraitHelper createTraitHelper(InternalWorkingMemoryActions workingMemory, InternalWorkingMemoryEntryPoint nep) {
        return new TraitHelperImpl(workingMemory, nep);
    }

    @Override
    public InternalKnowledgePackage createKnowledgePackage(String name) {
        return new TraitKnowledgePackageImpl(name);
    }
}
