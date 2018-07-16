/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;

/**
 * Util class that provides various methods related to KieBase.
 */
public final class KieBaseUtil {

    public static KieBase getDefaultKieBaseFromKieBuilder(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                          final KieBuilder kbuilder) {
        if (kieBaseTestConfiguration.useCanonicalModel()) {
            generateKieModuleForCanonicalModel( kbuilder );
        }
        return getDefaultKieBaseFromKieModule(kbuilder.getKieModule());
    }

    private static KieBase getDefaultKieBaseFromKieModule(final KieModule kmodule) {
        return getDefaultKieBaseFromReleaseId(kmodule.getReleaseId());
    }

    private static KieBase getDefaultKieBaseFromReleaseId(final ReleaseId id) {
        return getKieBaseFromReleaseIdByName(id, null);
    }

    public static KieBase getKieBaseFromClasspathResources(final Class classLoaderFromClass,
            final KieBaseTestConfiguration kieBaseTestConfiguration, final String... resources) {
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromClasspathResources(kieBaseTestConfiguration,
                classLoaderFromClass, true, resources);
        return getDefaultKieBaseFromKieBuilder(kieBaseTestConfiguration, kieBuilder);
    }

    public static KieBase getKieBaseFromResources(final KieBaseTestConfiguration kieBaseTestConfiguration,
            final Resource... resources) {
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, resources);
        if (kieBaseTestConfiguration.useCanonicalModel()) {
            generateKieModuleForCanonicalModel( kieBuilder );
        }
        return getDefaultKieBaseFromKieBuilder(kieBaseTestConfiguration, kieBuilder);
    }

    private static void generateKieModuleForCanonicalModel(KieBuilder kieBuilder) {
        KieServices ks = KieServices.get();
        InternalKieModule kieModule = ( InternalKieModule ) kieBuilder.getKieModule();

        ks.getRepository().addKieModule( kieModule );
    }

    public static KieBase getKieBaseFromDRLResources(final KieBaseTestConfiguration kieBaseTestConfiguration,
            final Resource... resources) {
        generateDRLResourceTargetPath(resources);
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, resources);
        return getDefaultKieBaseFromKieBuilder(kieBaseTestConfiguration, kieBuilder);
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

    public static KieBase getKieBaseFromKieModuleFromResources(final ReleaseId releaseId,
                                                               final KieBaseTestConfiguration kieBaseTestConfiguration, final Resource... resources) {
        final KieModule kieModule = KieUtil.getKieModuleFromResources(releaseId, kieBaseTestConfiguration, resources);
        return getDefaultKieBaseFromReleaseId(kieModule.getReleaseId());
    }

    public static KieBase getKieBaseFromKieModuleFromDrl(final String moduleGroupId,
                                                         final KieBaseTestConfiguration kieBaseTestConfiguration, final String... drls) {
        final List<Resource> resources = KieUtil.getResourcesFromDrls(drls);
        return getKieBaseFromKieModuleFromResources(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, resources.toArray(new Resource[]{}));
    }

    public static KieBase getKieBaseFromClasspathResources(final String moduleGroupId,
                                                           final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                           final String... classpathResources) {
        final List<Resource> resources = KieUtil.getClasspathResources(classpathResources);
        return getKieBaseFromKieModuleFromResources(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, resources.toArray(new Resource[]{}));
    }

    private KieBaseUtil() {
        // Creating instances of util classes should not be possible.
    }

    public static KieModuleModel getKieModuleModelWithAlphaNetworkCompiler() {
        KieModuleModel kproj = KieServices.get().newKieModuleModel();
        kproj.setConfigurationProperty( "drools.alphaNetworkCompiler", "true" );
        return kproj;
    }
}
