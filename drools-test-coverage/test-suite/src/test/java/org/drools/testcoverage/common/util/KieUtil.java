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

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.command.KieCommands;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util class that provides various methods related to Kie API.
 */
public final class KieUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieUtil.class);

    public static KieModule buildAndInstallKieModuleIntoRepo(final KieBaseTestConfiguration kieBaseTestConfiguration,
            final String groupId, final KieModuleModel kieModuleModel, final Resource... resources) {
        final ReleaseId releaseId = generateReleaseId(groupId);
        return buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId, kieModuleModel, resources);
    }

    public static KieModule buildAndInstallKieModuleIntoRepo(final KieBaseTestConfiguration kieBaseTestConfiguration,
            final ReleaseId releaseId, final KieModuleModel kieModuleModel, final Resource... resources) {
        final KieServices kieServices = KieServices.Factory.get();
        final KieFileSystem fileSystem = getKieFileSystemWithKieModule(kieModuleModel, releaseId, resources);
        final KieBuilder builder = getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, fileSystem, true);
        KieModule kieModule = builder.getKieModule();
        if (kieBaseTestConfiguration.useCanonicalModel()) {
            final File kjarFile = FileUtil.bytesToTempKJARFile(releaseId, ((InternalKieModule) kieModule).getBytes(), ".jar" );
            kieModule = new CanonicalKieModule(releaseId, getDefaultKieModuleModel(kieServices), kjarFile );
        }
        kieServices.getRepository().addKieModule(kieModule);

        return kieModule;
    }

    public static KieModuleModel createKieModuleModel() {
        final KieServices kieServices = KieServices.Factory.get();
        return kieServices.newKieModuleModel();
    }

    public static KieFileSystem getKieFileSystemWithKieModule(final KieModuleModel kieModuleModel,
                                                              final ReleaseId releaseId, final Resource... resources) {
        final KieFileSystem fileSystem = KieServices.Factory.get().newKieFileSystem();
        fileSystem.generateAndWritePomXML(releaseId);
        for (Resource resource : resources) {
            fileSystem.write(resource);
        }
        fileSystem.writeKModuleXML(kieModuleModel.toXML());
        return fileSystem;
    }

    public static KieBuilder getKieBuilderFromKieFileSystem(final KieBaseTestConfiguration kieBaseTestConfiguration,
            final KieFileSystem fileSystem, final boolean failIfBuildError) {
        return getKieBuilderFromFileSystemWithResources(kieBaseTestConfiguration, fileSystem, failIfBuildError);
    }

    public static KieBuilder getKieBuilderFromResources(final KieBaseTestConfiguration kieBaseTestConfiguration,
            final boolean failIfBuildError, final Resource... resources) {
        return getKieBuilderFromFileSystemWithResources(kieBaseTestConfiguration,
                KieServices.Factory.get().newKieFileSystem(), failIfBuildError, resources);
    }

    public static KieModuleModel getDefaultKieModuleModel(KieServices ks) {
        final KieModuleModel kproj = KieServices.get().newKieModuleModel();
        final KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("kbase").setDefault(true);
        final KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("ksession").setDefault(true);
        return kproj;
    }

    private static KieBuilder getKieBuilderFromFileSystemWithResources(final KieBaseTestConfiguration kieBaseTestConfiguration,
            final KieFileSystem kfs, final boolean failIfBuildError, final Resource... resources) {
        for (Resource res : resources) {
            kfs.write(res);
        }

        final KieBuilderImpl kbuilder = (KieBuilderImpl) KieServices.Factory.get().newKieBuilder(kfs);
        if (kieBaseTestConfiguration.useCanonicalModel()) {
            kbuilder.buildAll(CanonicalModelKieProject::new);
        } else {
            kbuilder.buildAll();
        }

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
            Assertions.assertThat(msgs).as(msgs.toString()).isEmpty();
        }

        return kbuilder;
    }

    public static ReleaseId generateReleaseId(final String groupId) {
        final KieServices kieServices = KieServices.Factory.get();
        return kieServices.newReleaseId(groupId, UUID.randomUUID().toString(), "1.0.0-SNAPSHOT");
    }

    public static KieBuilder getKieBuilderFromClasspathResources(final KieBaseTestConfiguration kieBaseTestConfiguration,
            final Class classLoaderFromClass, final boolean failIfBuildError, final String... resources) {
        final List<Resource> result = new ArrayList<>();
        for (String resource : resources) {
            result.add(KieServices.Factory.get().getResources().newClassPathResource(resource, classLoaderFromClass));
        }

        return getKieBuilderFromResources(kieBaseTestConfiguration, failIfBuildError, result.toArray(new Resource[]{}));
    }

    public static Resource[] createResources(final String drlFile, final Class<?> clazz) {
        return new Resource[] { getResources().newClassPathResource(drlFile, clazz)};
    }

    public static Resource[] createResources(final String drl) {
        final Resource drlResource = getResources().newReaderResource(new StringReader(drl));
        drlResource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        return new Resource[] { drlResource };
    }

    public static final KieCommands getCommands() {
        return getServices().getCommands();
    }

    public static final KieResources getResources() {
        return getServices().getResources();
    }

    public static final KieServices getServices() {
        return KieServices.Factory.get();
    }

    private KieUtil() {
        // Creating instances of util classes should not be possible.
    }
}


