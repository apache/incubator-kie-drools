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

package org.drools.traits.core.factmodel.traits;

import java.io.Externalizable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.protobuf.ByteString;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.reteoo.KieComponentFactory;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.HierarchyEncoder;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleStore;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.kie.api.KieBase;
import org.mvel2.asm.Opcodes;

public class TraitFactoryImpl<T extends Thing<K>, K extends TraitableBean> extends AbstractTraitFactory<T,K> implements Opcodes, Externalizable,
                                                                                                                        TraitFactory {

    private transient InternalKnowledgeBase kBase;

    private transient Set<String> runtimeClasses;

    public static void setMode( VirtualPropertyMode newMode, KieBase kBase ) {
        KieComponentFactory rcf = ((InternalKnowledgeBase) kBase).getConfiguration().getComponentFactory();
        setMode( newMode, rcf );
    }

    public static TraitFactoryImpl getTraitBuilderForKnowledgeBase( KieBase kb ) {
        return (TraitFactoryImpl) ((InternalKnowledgeBase) kb).getConfiguration().getComponentFactory().getTraitFactory();
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

    protected KieComponentFactory getComponentFactory() {
        return kBase.getConfiguration().getComponentFactory();
    }

    protected TraitRegistryImpl getTraitRegistry() {
        return (TraitRegistryImpl) kBase.getTraitRegistry();
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
        InternalKnowledgePackage traitPackage = kBase.getPackagesMap().get(PACKAGE);
        if ( traitPackage == null ) {
            traitPackage = new KnowledgePackageImpl(PACKAGE);
            traitPackage.setClassFieldAccessorCache( kBase.getClassFieldAccessorCache() );
            kBase.getPackagesMap().put(PACKAGE, traitPackage );
        }
        return traitPackage.getClassFieldAccessorStore();
    }

    public void setRuleBase( InternalKnowledgeBase kBase ) {
        this.kBase = kBase;
    }

    public boolean isRuntimeClass( String resourceName ) {
        return runtimeClasses != null && runtimeClasses.contains( resourceName );
    }

    @Override
    public void writeRuntimeDefinedClasses(MarshallerWriteContext context, ProtobufMessages.Header.Builder _header) {
        if (context.kBase == null) {
            return;
        }

        ProjectClassLoader pcl = (ProjectClassLoader) (context.kBase).getRootClassLoader();
        if (pcl.getStore() == null || pcl.getStore().isEmpty()) {
            return;
        }

        List<String> runtimeClassNames = new ArrayList(pcl.getStore().keySet());
        Collections.sort(runtimeClassNames);
        ProtobufMessages.RuntimeClassDef.Builder _classDef = ProtobufMessages.RuntimeClassDef.newBuilder();
        for (String resourceName : runtimeClassNames) {
            if (isRuntimeClass(resourceName)) {
                _classDef.clear();
                _classDef.setClassFqName(resourceName);
                _classDef.setClassDef(ByteString.copyFrom(pcl.getStore().get(resourceName)));
                _header.addRuntimeClassDefinitions(_classDef.build());
            }
        }
    }
}