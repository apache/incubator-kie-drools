/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.factmodel.traits;

import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.HierarchyEncoder;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleStore;
import org.kie.api.KieBase;
import org.mvel2.asm.Opcodes;

import java.io.Externalizable;
import java.util.HashSet;
import java.util.Set;

public class TraitFactory<T extends Thing<K>, K extends TraitableBean> extends AbstractTraitFactory<T,K> implements Opcodes, Externalizable {

    private transient InternalKnowledgeBase kBase;

    private transient Set<String> runtimeClasses;

    public static void setMode( VirtualPropertyMode newMode, KieBase kBase ) {
        KieComponentFactory rcf = ((InternalKnowledgeBase) kBase).getConfiguration().getComponentFactory();
        setMode( newMode, rcf );
    }

    public static TraitFactory getTraitBuilderForKnowledgeBase( KieBase kb ) {
        return ((InternalKnowledgeBase) kb).getConfiguration().getComponentFactory().getTraitFactory();
    }

    public TraitFactory() {

    }

    protected Class<?> registerAndLoadTypeDefinition( String proxyName, byte[] proxy ) throws ClassNotFoundException {
        registerRuntimeClass( proxyName );
        return kBase.registerAndLoadTypeDefinition( proxyName, proxy );
    }

    private void registerRuntimeClass( String proxyName ) {
        if ( runtimeClasses == null ) {
            runtimeClasses = new HashSet<String>();
        }
        runtimeClasses.add( ClassUtils.convertClassToResourcePath( proxyName ) );
    }

    protected ClassLoader getRootClassLoader() {
        return kBase.getRootClassLoader();
    }

    protected KieComponentFactory getComponentFactory() {
        return kBase.getConfiguration().getComponentFactory();
    }

    protected TraitRegistry getTraitRegistry() {
        return kBase.getTraitRegistry();
    }

    protected HierarchyEncoder getHierarchyEncoder() {
        return getTraitRegistry().getHierarchy();

    }

    protected TripleStore getTripleStore() {
        return kBase.getTripleStore();
    }

    protected TripleFactory getTripleFactory() {
        return getComponentFactory().getTripleFactory();
    }

    protected ClassFieldAccessorStore getClassFieldAccessorStore() {
        InternalKnowledgePackage traitPackage = kBase.getPackagesMap().get( pack );
        if ( traitPackage == null ) {
            traitPackage = new KnowledgePackageImpl( pack );
            traitPackage.setClassFieldAccessorCache( kBase.getClassFieldAccessorCache() );
            kBase.getPackagesMap().put( pack, traitPackage );
        }
        return traitPackage.getClassFieldAccessorStore();
    }


    public InternalKnowledgeBase getKnowledgeBase() {
        return kBase;
    }

    public void setRuleBase( InternalKnowledgeBase kBase ) {
        this.kBase = kBase;
    }

    public static TraitTypeEnum determineTraitType( Object object ) {
        if ( object instanceof TraitProxy ) {
            return TraitTypeEnum.TRAIT;
        } else if ( object instanceof CoreWrapper ) {
            return TraitTypeEnum.WRAPPED_TRAITABLE;
        } else if ( object instanceof TraitableBean ) {
            return TraitTypeEnum.TRAITABLE;
        } else {
            return TraitTypeEnum.LEGACY_TRAITABLE;
        }
    }

    public boolean isRuntimeClass( String resourceName ) {
        return runtimeClasses != null && runtimeClasses.contains( resourceName );
    }
}