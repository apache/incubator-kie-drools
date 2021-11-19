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

package org.drools.traits.core.factmodel;

import java.io.Externalizable;
import java.util.HashSet;
import java.util.Set;

import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.HierarchyEncoder;
import org.kie.api.KieBase;
import org.mvel2.asm.Opcodes;

public class TraitFactoryImpl<T extends Thing<K>, K extends TraitableBean> extends AbstractTraitFactory<T,K> implements Opcodes, Externalizable, TraitFactory {

    private transient InternalKnowledgeBase kBase;

    private transient Set<String> runtimeClasses;

    private transient TraitRegistryImpl traitRegistry;

    private TripleStore tripleStore;

    public static void setMode( VirtualPropertyMode newMode, KieBase kBase ) {
        setMode( newMode, kBase, RuntimeComponentFactory.get() );
    }

    public static TraitFactoryImpl getTraitBuilderForKnowledgeBase( KieBase kb ) {
        return (TraitFactoryImpl) RuntimeComponentFactory.get().getTraitFactory(((InternalKnowledgeBase) kb));
    }

    public TraitFactoryImpl() {
    }

    public TraitFactoryImpl(InternalKnowledgeBase kBase) {
        this.kBase = kBase;
    }

    protected Class<?> registerAndLoadTypeDefinition( String proxyName, byte[] proxy ) throws ClassNotFoundException {
        registerRuntimeClass( proxyName );
        return kBase.registerAndLoadTypeDefinition( proxyName, proxy );
    }

    private void registerRuntimeClass( String proxyName ) {
        if ( runtimeClasses == null ) {
            runtimeClasses = new HashSet<>();
        }
        runtimeClasses.add( ClassUtils.convertClassToResourcePath( proxyName ) );
    }

    protected ClassLoader getRootClassLoader() {
        return kBase.getRootClassLoader();
    }

    public TraitRegistryImpl getTraitRegistry() {
        if (traitRegistry == null) {
            traitRegistry = new TraitRegistryImpl();
        }
        return traitRegistry;
    }

    protected HierarchyEncoder getHierarchyEncoder() {
        return getTraitRegistry().getHierarchy();

    }

    protected TripleStore getTripleStore() {
        if (tripleStore == null) {
            tripleStore = new TripleStore();
        }
        return tripleStore;
    }

    protected TripleFactory getTripleFactory() {
        return TripleFactoryImpl.INSTANCE;
    }

    protected ClassFieldAccessorStore getClassFieldAccessorStore() {
        InternalKnowledgePackage traitPackage = kBase.getPackagesMap().get(PACKAGE);
        if ( traitPackage == null ) {
            traitPackage = new KnowledgePackageImpl(PACKAGE);
            traitPackage.setClassFieldAccessorCache( kBase.getClassFieldAccessorCache() );
            kBase.getPackagesMap().put(PACKAGE, traitPackage );
        }
        return traitPackage.getClassFieldAccessorStore();
    }

    @Override
    public boolean isRuntimeClass( String resourceName ) {
        return runtimeClasses != null && runtimeClasses.contains( resourceName );
    }
}