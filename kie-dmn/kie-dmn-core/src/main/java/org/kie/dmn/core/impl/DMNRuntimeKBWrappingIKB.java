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
package org.kie.dmn.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.ResourceTypePackageRegistry;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNPackage;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.util.ClassLoaderUtil;
import org.kie.internal.utils.ChainedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNRuntimeKBWrappingIKB implements DMNRuntimeKB {
    private static final Logger logger = LoggerFactory.getLogger( DMNRuntimeKBWrappingIKB.class );

    private final InternalKnowledgeBase knowledgeBase;

    public DMNRuntimeKBWrappingIKB(InternalKnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    @Override
    public List<DMNRuntimeEventListener> getListeners() {
        if (knowledgeBase != null && knowledgeBase.getKieContainer() instanceof KieContainerImpl) {
            KieBaseModelImpl kieBaseModel = (KieBaseModelImpl) ((KieContainerImpl) knowledgeBase.getKieContainer()).getKieProject().getKieBaseModel(knowledgeBase.getId());
            ChainedProperties cp = ChainedProperties.getChainedProperties(getRootClassLoader());
            Properties kmoduleProperties = new Properties();
            kmoduleProperties.putAll(kieBaseModel.getKModule().getConfigurationProperties());
            cp.addProperties(kmoduleProperties);

            Map<String, String> listenersMap = new HashMap<>();
            cp.mapStartsWith(listenersMap, DMNAssemblerService.DMN_RUNTIME_LISTENER_PREFIX, true);
            logger.debug("{}", listenersMap);

            return listenersMap.values().stream()
                               .map(this::loadEventListener)
                               .flatMap(o -> o.stream())
                               .collect(Collectors.toList());
        } else {
            logger.warn("No DMNRuntime Listener can be provided, as created without a reference to KnowledgeBase");
        }
        return Collections.emptyList();
    }

    private Optional<DMNRuntimeEventListener> loadEventListener(String classString) {
        if (ClassLoaderUtil.CAN_PLATFORM_CLASSLOAD) {
            try {
                DMNRuntimeEventListener runtimeListenerInstance = (DMNRuntimeEventListener) knowledgeBase.getRootClassLoader().loadClass(classString).newInstance();
                return Optional.of(runtimeListenerInstance);
            } catch (Exception e) {
                logger.error("Cannot perform classloading of runtime listener: {}", classString, e);
                return Optional.empty();
            }
        } else {
            logger.error("This platform does not support classloading of runtime listener: {}", classString);
            return Optional.empty();
        }
    }

    @Override
    public List<DMNModel> getModels() {
        List<DMNModel> models = new ArrayList<>(  );
        knowledgeBase.getKiePackages().forEach( kpkg -> {
            DMNPackage dmnPkg = (DMNPackage) ((InternalKnowledgePackage) kpkg).getResourceTypePackages().get( ResourceType.DMN );
            if( dmnPkg != null ) {
                models.addAll(dmnPkg.getAllModels().values());
            }
        } );
        return models;
    }

    @Override
    public DMNModel getModel(String namespace, String modelName) {
        Objects.requireNonNull(namespace, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "namespace"));
        Objects.requireNonNull(modelName, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "modelName"));
        InternalKnowledgePackage kpkg = (InternalKnowledgePackage) knowledgeBase.getKiePackage( namespace );
        if( kpkg == null ) {
            return null;
        }
        ResourceTypePackageRegistry map = kpkg.getResourceTypePackages();
        DMNPackage dmnpkg = (DMNPackage) map.get( ResourceType.DMN );
        return dmnpkg != null ? dmnpkg.getModel( modelName ) : null;
    }

    @Override
    public DMNModel getModelById(String namespace, String modelId) {
        Objects.requireNonNull(namespace, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "namespace"));
        Objects.requireNonNull(modelId, () -> MsgUtil.createMessage(Msg.PARAM_CANNOT_BE_NULL, "modelId"));
        InternalKnowledgePackage kpkg = (InternalKnowledgePackage) knowledgeBase.getKiePackage( namespace );
        if( kpkg == null ) {
            return null;
        }
        ResourceTypePackageRegistry map = kpkg.getResourceTypePackages();
        DMNPackage dmnpkg = (DMNPackage) map.get( ResourceType.DMN );
        return dmnpkg != null ? dmnpkg.getModelById( modelId ) : null;
    }

    @Override
    public List<DMNProfile> getProfiles() {
        // need list to preserve ordering
        List<DMNProfile> profiles = new ArrayList<>();
        knowledgeBase.getKiePackages().forEach(kpkg -> {
            DMNPackageImpl dmnPkg = (DMNPackageImpl) ((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.DMN);
            if (dmnPkg != null) {
                for (DMNProfile p : dmnPkg.getProfiles()) {
                    if (!profiles.contains(p)) {
                        profiles.add(p);
                    }
                }
            }
        });
        return profiles;
    }

    @Override
    public ClassLoader getRootClassLoader() {
        return knowledgeBase.getRootClassLoader();
    }

    @Override
    public InternalKnowledgeBase getInternalKnowledgeBase() {
        return this.knowledgeBase;
    }
}
