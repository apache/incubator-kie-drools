/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.assertj.core.api.Assertions;
import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class that provides various methods related to KieBase.
 */
public final class KieBaseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieBaseUtil.class);

    public static Resource getDecisionTableResourceFromClasspath(final String resourceName,
                                                                 final Class classLoaderFromClass,
                                                                 final DecisionTableInputType type) {
        final Resource dtable =
                KieServices.Factory.get().getResources().newClassPathResource(resourceName, classLoaderFromClass);
        final DecisionTableConfiguration resourceConfig = new DecisionTableConfigurationImpl();
        resourceConfig.setInputType(type);
        dtable.setConfiguration(resourceConfig);
        return dtable;
    }

    public static KieBase getDefaultKieBaseFromKieBuilder(final KieBuilder kbuilder) {
        return getDefaultKieBaseFromKieModule(kbuilder.getKieModule());
    }

    public static KieBase getDefaultKieBaseFromKieModule(final KieModule kmodule) {
        return getDefaultKieBaseFromReleaseId(kmodule.getReleaseId());
    }

    public static KieBase getDefaultKieBaseFromReleaseId(final ReleaseId id) {
        return getKieBaseFromReleaseIdByName(id, null);
    }

    public static KieBase getKieBaseFromClasspathResources(final Class classLoaderFromClass,
                                                           final boolean failIfBuildError, final String... resources) {
        final KieBuilder kieBuilder = getKieBuilderFromClasspathResources(classLoaderFromClass, failIfBuildError, resources);
        return getDefaultKieBaseFromKieBuilder(kieBuilder);
    }

    public static KieBase getKieBaseFromResources(final boolean failIfBuildError, final Resource... resources) {
        final KieBuilder kieBuilder = getKieBuilderFromResources(failIfBuildError, resources);
        return getDefaultKieBaseFromKieBuilder(kieBuilder);
    }

    public static KieBase getKieBaseFromKieBuilderByName(final KieBuilder kbuilder, final String name) {
        return getKieBaseFromKieModuleByName(kbuilder.getKieModule(), name);
    }

    public static KieBase getKieBaseFromKieModuleByName(final KieModule kmodule, final String name) {
        return getKieBaseFromReleaseIdByName(kmodule.getReleaseId(), name);
    }

    public static KieBase getKieBaseFromReleaseIdByName(final ReleaseId id, final String name) {
        final KieContainer container = KieServices.Factory.get().newKieContainer(id);
        if (name == null) {
            return container.getKieBase();
        } else {
            return container.getKieBase(name);
        }
    }

    public static KieFileSystem writeKieModuleWithResourceToFileSystem(final KieModuleModel kieModuleModel,
            final ReleaseId releaseId, final Resource resource) {
        final KieFileSystem fileSystem = KieServices.Factory.get().newKieFileSystem();
        fileSystem.generateAndWritePomXML(releaseId);
        fileSystem.write(resource);
        fileSystem.writeKModuleXML(kieModuleModel.toXML());
        return fileSystem;
    }

    public static void addKieModuleWithResourceToRepository(final ReleaseId releaseId, final Resource resource) {
        final KieServices kieServices = KieServices.Factory.get();

        final KieModuleModel module = kieServices.newKieModuleModel();
        final KieBaseModel base = module.newKieBaseModel();
        base.setDefault(true);
        final KieFileSystem fileSystem = writeKieModuleWithResourceToFileSystem(module, releaseId, resource);
        final KieBuilder builder = getKieBuilderFromKieFileSystem(fileSystem, true);
        kieServices.getRepository().addKieModule(builder.getKieModule());
    }

    public static KieBuilder getKieBuilderFromKieFileSystem(final KieFileSystem fileSystem, final boolean failIfBuildError) {
        return getKieBuilderFromFileSystemWithResources(fileSystem, failIfBuildError);
    }

    public static KieBuilder getKieBuilderFromClasspathResources(final Class classLoaderFromClass,
                                                                 final boolean failIfBuildError,
                                                                 final String... resources) {
        final List<Resource> result = new ArrayList<>();
        for (String resource : resources) {
            result.add(KieServices.Factory.get().getResources().newClassPathResource(resource, classLoaderFromClass));
        }

        return getKieBuilderFromResources(failIfBuildError, result.toArray(new Resource[]{}));
    }

    public static KieBuilder getKieBuilderFromResources(final boolean failIfBuildError, final Resource... resources) {
        return getKieBuilderFromFileSystemWithResources(
                KieServices.Factory.get().newKieFileSystem(), failIfBuildError, resources);
    }

    private static KieBuilder getKieBuilderFromFileSystemWithResources(final KieFileSystem kfs,
            final boolean failIfBuildError, final Resource... resources) {
        for (Resource res : resources) {
            kfs.write(res);
        }

        final KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();

        // Messages from KieBuilder with increasing severity
        List<Message> msgs = kbuilder.getResults().getMessages(Message.Level.INFO);
        if (msgs.size() > 0) {
            LOGGER.info("KieBuilder information: {}", msgs.toString());
        }

        msgs = kbuilder.getResults().getMessages(Message.Level.WARNING);
        if (msgs.size() > 0) {
            LOGGER.warn("KieBuilder warnings: {}", msgs.toString());
        }

        msgs = kbuilder.getResults().getMessages(Message.Level.ERROR);
        if (msgs.size() > 0) {
            LOGGER.error("KieBuilder errors: {}", msgs.toString());
        }

        if (failIfBuildError) {
            Assertions.assertThat(msgs.size()).as(msgs.toString()).isEqualTo(0);
        }

        return kbuilder;
    }

    private KieBaseUtil() {
        // Creating instances of util classes should not be possible.
    }
}
