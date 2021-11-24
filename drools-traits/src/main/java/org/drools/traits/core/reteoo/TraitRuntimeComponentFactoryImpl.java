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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.drools.core.common.NamedEntryPointFactory;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.spi.FactHandleFactory;
import org.drools.kiesession.factory.RuntimeComponentFactoryImpl;
import org.drools.traits.core.common.TraitNamedEntryPointFactory;
import org.drools.traits.core.factmodel.TraitClassBuilderFactory;
import org.drools.traits.core.factmodel.TraitFactoryImpl;

public class TraitRuntimeComponentFactoryImpl extends RuntimeComponentFactoryImpl {

    private final TraitFactHandleFactory traitFactHandleFactory = new TraitFactHandleFactory();

    private final Map<InternalKnowledgeBase, TraitFactoryImpl> traitFactoryCache = new WeakHashMap<>(new IdentityHashMap<>());

    private final TraitClassBuilderFactory traitClassBuilderFactory = new TraitClassBuilderFactory();

    @Override
    public TraitFactoryImpl getTraitFactory(InternalKnowledgeBase knowledgeBase) {
        return traitFactoryCache.computeIfAbsent(knowledgeBase, TraitFactoryImpl::new);
    }

    @Override
    public TraitRegistry getTraitRegistry(InternalKnowledgeBase knowledgeBase) {
        return getTraitFactory(knowledgeBase).getTraitRegistry();
    }

    @Override
    public ClassBuilderFactory getClassBuilderFactory() {
        return traitClassBuilderFactory;
    }

    @Override
    public FactHandleFactory getFactHandleFactoryService() {
        return traitFactHandleFactory;
    }

    @Override
    public NamedEntryPointFactory getNamedEntryPointFactory() {
        return new TraitNamedEntryPointFactory();
    }
}
