/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.internal.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.io.impl.ClassPathResource;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.assembler.DMNResource;
import org.kie.dmn.core.assembler.DMNResourceDependenciesSorter;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.impl.DMNRuntimeKB;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.api.Definitions;
import org.kie.internal.io.ResourceWithConfigurationImpl;

/**
 * Internal Utility class.
 */
public class DMNRuntimeBuilder {

    private final DMNCompilerConfigurationImpl cc;
    private final List<DMNProfile> dmnProfiles = new ArrayList<>();

    private DMNRuntimeBuilder() {
        this.cc = new DMNCompilerConfigurationImpl();
    }

    public static DMNRuntimeBuilder fromDefaults() {
        DMNRuntimeBuilder dmnRuntimeBuilder = new DMNRuntimeBuilder();
        dmnRuntimeBuilder.addProfile(new ExtendedDMNProfile());
        return dmnRuntimeBuilder;
    }

    public DMNRuntimeBuilder addProfile(DMNProfile dmnProfile) {
        dmnProfiles.add(dmnProfile);
        cc.addExtensions(dmnProfile.getExtensionRegisters());
        cc.addDRGElementCompilers(dmnProfile.getDRGElementCompilers());
        cc.addFEELProfile(dmnProfile);
        return this;
    }

    public DMNRuntimeBuilder setOption(RuntimeTypeCheckOption option) {
        cc.setProperty(option.getPropertyName(), "" + option.isRuntimeTypeCheck());
        return this;
    }

    public static DMNRuntimeBuilderConfigured usingStrict() {
        DMNRuntimeBuilder dmnRuntimeBuilder = new DMNRuntimeBuilder();
        dmnRuntimeBuilder.setRootClassLoader(null);
        dmnRuntimeBuilder.setOption(new RuntimeTypeCheckOption(true));
        return dmnRuntimeBuilder.buildConfiguration();
    }

    public DMNRuntimeBuilder setRootClassLoader(ClassLoader classLoader) {
        cc.setRootClassLoader(classLoader);
        return this;
    }

    public DMNRuntimeBuilderConfigured buildConfiguration() {
        DMNCompilerImpl dmnCompiler = new DMNCompilerImpl(cc);
        return new DMNRuntimeBuilderConfigured(dmnCompiler);
    }

    public static class DMNRuntimeBuilderConfigured {

        private final DMNCompilerImpl dmnCompiler;

        private DMNRuntimeBuilderConfigured(DMNCompilerImpl dmnCompiler) {
            this.dmnCompiler = dmnCompiler;
        }

        public Either<Exception, DMNRuntime> fromClasspathResource(final String resourceName, final Class<?> testClass) {
            return fromResources(Arrays.asList(new ClassPathResource(resourceName, testClass)));
        }

        public Either<Exception, DMNRuntime> fromClasspathResources(final String resourceName, final Class<?> testClass, final String... additionalResources) {
            List<Resource> resources = new ArrayList<>();
            resources.add(new ClassPathResource(resourceName, testClass));
            for (String ar : additionalResources) {
                resources.add(new ClassPathResource(ar, testClass));
            }
            return fromResources(resources);
        }

        public Either<Exception, DMNRuntime> fromResources(Collection<? extends Resource> resources) {
            List<DMNResource> dmnResources = new ArrayList<>();
            for (Resource r : resources) {
                Definitions definitions;
                try {
                    definitions = dmnCompiler.getMarshaller().unmarshal(r.getReader());
                } catch (IOException e) {
                    return Either.ofLeft(e);
                }
                ResourceConfigurationImpl rc = new ResourceConfigurationImpl();
                rc.setResourceType(ResourceType.DMN);
                DMNResource dmnResource = new DMNResource(definitions, new ResourceWithConfigurationImpl(r, rc, b -> {
                }, a -> {
                }));
                dmnResources.add(dmnResource);
            }
            DMNAssemblerService.enrichDMNResourcesWithImportsDependencies(dmnResources, Collections.emptyList());
            List<DMNResource> sortedDmnResources = DMNResourceDependenciesSorter.sort(dmnResources);

            List<DMNModel> dmnModels = new ArrayList<>();
            for (DMNResource dmnRes : sortedDmnResources) {
                DMNModel dmnModel = dmnCompiler.compile(dmnRes.getDefinitions(), dmnRes.getResAndConfig().getResource(), dmnModels);
                dmnModels.add(dmnModel);
            }
            return Either.ofRight(new DMNRuntimeImpl(new DMNRuntimeKBStatic(dmnModels)));
        }
    }


    private static class DMNRuntimeKBStatic implements DMNRuntimeKB {

        private final List<DMNModel> models;

        public DMNRuntimeKBStatic(Collection<? extends DMNModel> models) {
            this.models = Collections.unmodifiableList(new ArrayList<>(models));
        }

        @Override
        public List<DMNModel> getModels() {
            return models;
        }

        @Override
        public DMNModel getModel(String namespace, String modelName) {
            return models.stream().filter(m -> m.getNamespace().equals(namespace) && m.getName().equals(modelName)).findFirst().orElse(null);
        }

        @Override
        public DMNModel getModelById(String namespace, String modelId) {
            return models.stream().filter(m -> m.getNamespace().equals(namespace) && m.getDefinitions().getId().equals(modelId)).findFirst().orElse(null);
        }

        @Override
        public List<DMNProfile> getProfiles() {
            return Collections.emptyList();
        }

        @Override
        public List<DMNRuntimeEventListener> getListeners() {
            return Collections.emptyList();
        }

        @Override
        public ClassLoader getRootClassLoader() {
            return null;
        }

        @Override
        public InternalKnowledgeBase getInternalKnowledgeBase() {
            throw new UnsupportedOperationException();
        }

    }
}
