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
package org.drools.testcoverage.common.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;

/**
 * Util class that provides various methods related to KieBase.
 */
public final class KieBaseUtil {

    public static KieBase getDefaultKieBaseFromKieBuilder(final KieBuilder kbuilder) {
        return getDefaultKieBaseFromKieModule(kbuilder.getKieModule());
    }

    private static KieBase getDefaultKieBaseFromKieModule(final KieModule kmodule) {
        return getDefaultKieBaseFromReleaseId(kmodule.getReleaseId());
    }

    public static KieBase getDefaultKieBaseFromReleaseId(final ReleaseId id) {
        return getKieBaseFromReleaseIdByName(id, null);
    }

    public static KieBase getKieBaseFromClasspathResources(final Class classLoaderFromClass,
            final KieBaseTestConfiguration kieBaseTestConfiguration, final String... resources) {
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromClasspathResources(kieBaseTestConfiguration,
                classLoaderFromClass, true, resources);
        return getDefaultKieBaseFromKieBuilder(kieBuilder);
    }

    public static KieBase getKieBaseFromResources(final KieBaseTestConfiguration kieBaseTestConfiguration,
            final Resource... resources) {
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, resources);
        return getDefaultKieBaseFromKieBuilder(kieBuilder);
    }

    public static KieBase getKieBaseFromDRLResources(final KieBaseTestConfiguration kieBaseTestConfiguration,
            final Resource... resources) {
        generateDRLResourceTargetPath(resources);
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, resources);
        return getDefaultKieBaseFromKieBuilder(kieBuilder);
    }

    private static void generateDRLResourceTargetPath(final Resource[] resources) {
        for (int index = 0; index < resources.length; index++) {
            resources[index].setTargetPath(String.format("rule-%d.drl", index));
        }
    }

    private static KieBase getKieBaseFromReleaseIdByName(final ReleaseId id, final String name) {
        final KieContainer container = KieServices.Factory.get().newKieContainer(id);
        if (name == null) {
            return container.getKieBase();
        } else {
            return container.getKieBase(name);
        }
    }

    public static KieBase getKieBaseFromKieModuleFromResources(final String moduleGroupId,
                                                               final KieBaseTestConfiguration kieBaseTestConfiguration, final Resource... resources) {
        return getKieBaseFromKieModuleFromResources(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, resources);
    }

    public static KieBase getKieBaseFromKieModuleFromResources(final String moduleGroupId,
                                                               final KieBaseTestConfiguration kieBaseTestConfiguration, final Map<String, String> kieModuleConfigurationProperties, final Resource... resources) {
        return getKieBaseFromKieModuleFromResources(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, kieModuleConfigurationProperties, resources);
    }

    public static KieBase getKieBaseFromKieModuleFromResources(final ReleaseId releaseId,
                                                               final KieBaseTestConfiguration kieBaseTestConfiguration, final Resource... resources) {
        final KieModule kieModule = KieUtil.getKieModuleFromResources(releaseId, kieBaseTestConfiguration, resources);
        return getDefaultKieBaseFromReleaseId(kieModule.getReleaseId());
    }

    public static KieBase getKieBaseFromKieModuleFromResources(final ReleaseId releaseId,
                                                               final KieBaseTestConfiguration kieBaseTestConfiguration, final Map<String, String> kieModuleConfigurationProperties, final Resource... resources) {
        final KieModule kieModule = KieUtil.getKieModuleFromResources(releaseId, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_REALTIME, kieModuleConfigurationProperties, resources);
        return getDefaultKieBaseFromReleaseId(kieModule.getReleaseId());
    }

    public static KieBase getKieBaseFromKieModuleFromDrl(final String moduleGroupId,
                                                         final KieBaseTestConfiguration kieBaseTestConfiguration, final String... drls) {
        final List<Resource> resources = KieUtil.getResourcesFromDrls(drls);
        return getKieBaseFromKieModuleFromResources(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, resources.toArray(new Resource[]{}));
    }

    public static KieBase getKieBaseFromKieModuleFromDrl(final String moduleGroupId,
                                                         final KieBaseTestConfiguration kieBaseTestConfiguration, final Map<String, String> kieModuleConfigurationProperties, final String... drls) {
        final List<Resource> resources = KieUtil.getResourcesFromDrls(drls);
        return getKieBaseFromKieModuleFromResources(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, kieModuleConfigurationProperties, resources.toArray(new Resource[]{}));
    }

    public static KieBase getKieBaseFromClasspathResources(final String moduleGroupId,
                                                           final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                           final String... classpathResources) {
        final List<Resource> resources = KieUtil.getClasspathResources(classpathResources);
        return getKieBaseFromKieModuleFromResources(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, resources.toArray(new Resource[]{}));
    }

    public static KieBase getKieBaseFromClasspathResources(final String moduleGroupId,
                                                           final Class<?> classLoaderFromClass,
                                                           final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                           final String... classpathResources) {
        final List<Resource> resources = KieUtil.getClasspathResources(classLoaderFromClass, classpathResources);
        return getKieBaseFromKieModuleFromResources(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, resources.toArray(new Resource[]{}));
    }

    public static KieBase getKieBaseFromClasspathResourcesWithClassLoaderForKieBuilder(final String moduleGroupId,
                                                           final Class<?> classLoaderFromClass,
                                                           final ClassLoader classLoaderForKieBuilder,
                                                           final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                           final String... classpathResources) {
        final List<Resource> resources = KieUtil.getClasspathResources(classLoaderFromClass, classpathResources);
        return getKieBaseFromResourcesWithClassLoaderForKieBuilder(moduleGroupId, classLoaderForKieBuilder, kieBaseTestConfiguration, new HashMap<>(), resources);
    }

    public static KieBase getKieBaseFromClasspathResourcesWithClassLoaderForKieBuilder(final String moduleGroupId,
                                                                                       final Class<?> classLoaderFromClass,
                                                                                       final ClassLoader classLoaderForKieBuilder,
                                                                                       final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                                                       final Map<String, String> kieModuleConfigurationProperties,
                                                                                       final String... classpathResources) {
        final List<Resource> resources = KieUtil.getClasspathResources(classLoaderFromClass, classpathResources);
        return getKieBaseFromResourcesWithClassLoaderForKieBuilder(moduleGroupId, classLoaderForKieBuilder, kieBaseTestConfiguration, kieModuleConfigurationProperties, resources);
    }

    public static KieBase getKieBaseFromDrlWithClassLoaderForKieBuilder(final String moduleGroupId,
                                                                        final ClassLoader classLoaderForKieBuilder,
                                                                        final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                                        final String... drls) {
        final List<Resource> resources = KieUtil.getResourcesFromDrls(drls);
        return getKieBaseFromResourcesWithClassLoaderForKieBuilder(moduleGroupId, classLoaderForKieBuilder, kieBaseTestConfiguration, new HashMap<>(), resources);
    }

    private static KieBase getKieBaseFromResourcesWithClassLoaderForKieBuilder(final String moduleGroupId,
                                                                               final ClassLoader classLoaderForKieBuilder,
                                                                               final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                                               final Map<String, String> kieModuleConfigurationProperties,
                                                                               final List<Resource> resources) {
        KieModuleModel kieModuleModel = KieUtil.getKieModuleModel(kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_REALTIME, kieModuleConfigurationProperties);
        KieFileSystem kfs = KieUtil.getKieFileSystemWithKieModule(kieModuleModel, KieUtil.generateReleaseId(moduleGroupId), resources.toArray(new Resource[]{}));
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false, classLoaderForKieBuilder);
        final KieModule kieModule = kieBuilder.getKieModule();
        KieServices.Factory.get().getRepository().addKieModule(kieModule);
        return getDefaultKieBaseFromReleaseId(kieModule.getReleaseId());
    }

    public static KieBase newKieBaseFromKieModuleWithAdditionalOptions(final KieModule kieModule,
                                                         final KieBaseTestConfiguration kieBaseTestConfiguration, final KieBaseOption... options) {
        final KieContainer kieContainer = KieServices.get().newKieContainer(kieModule.getReleaseId());
        final KieBaseConfiguration kieBaseConfiguration = kieBaseTestConfiguration.getKieBaseConfiguration();
        Arrays.stream(options).forEach(kieBaseConfiguration::setOption);
        return kieContainer.newKieBase(kieBaseConfiguration);
    }

    public static KieBase newKieBaseFromReleaseId(final ReleaseId id, final KieBaseConfiguration kbaseConf) {
        final KieContainer container = KieServices.Factory.get().newKieContainer(id);
        return container.newKieBase(kbaseConf);
    }

    private KieBaseUtil() {
        // Creating instances of util classes should not be possible.
    }
}
