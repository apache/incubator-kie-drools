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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.ObjectTypeNode;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.command.KieCommands;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
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
        final KieFileSystem fileSystem = getKieFileSystemWithKieModule(kieModuleModel, releaseId, resources);
        return buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, fileSystem);
    }

    public static KieModule buildAndInstallKieModuleIntoRepo(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                             final KieFileSystem kieFileSystem) {
        final KieServices kieServices = KieServices.Factory.get();
        final KieBuilder builder = getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kieFileSystem, true);
        final KieModule kieModule = builder.getKieModule();
        kieServices.getRepository().addKieModule(kieModule);
        return kieModule;
    }

    public static KieModuleModel createKieModuleModel(final Boolean alphaNetworkEnabled) {
        final KieServices kieServices = KieServices.Factory.get();
        final KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
        if (alphaNetworkEnabled) {
            kieModuleModel.setConfigurationProperty("drools.alphaNetworkCompiler", "true");
        }
        return kieModuleModel;
    }

    public static KieFileSystem getKieFileSystemWithKieModule(final KieModuleModel kieModuleModel,
                                                              final ReleaseId releaseId, final Resource... resources) {
        final KieFileSystem fileSystem = KieServices.Factory.get().newKieFileSystem();
        fileSystem.generateAndWritePomXML(releaseId);
        for (final Resource resource : resources) {
            fileSystem.write(resource);
        }
        fileSystem.writeKModuleXML(kieModuleModel.toXML());
        return fileSystem;
    }

    public static KieBuilder getKieBuilderFromDrls(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                   final boolean failIfBuildError, final String... drls) {
        final List<Resource> resources = getResourcesFromDrls(drls);
        return getKieBuilderFromResources(kieBaseTestConfiguration, failIfBuildError, resources.toArray(new Resource[]{}));
    }

    public static KieBuilder getKieBuilderFromResources(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                        final boolean failIfBuildError, final Resource... resources) {
        final KieModuleModel kieModuleModel = createKieModuleModel(kieBaseTestConfiguration.useAlphaNetworkCompiler());
        final KieFileSystem kieFileSystem = getKieFileSystemWithKieModule(kieModuleModel, KieServices.get().getRepository().getDefaultReleaseId(), resources);
        return getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kieFileSystem, failIfBuildError);
    }

    public static KieBuilder getKieBuilderFromKieFileSystem(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                          final KieFileSystem kfs, final boolean failIfBuildError) {

        final KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);

        if (kieBaseTestConfiguration.getExecutableModelProjectClass().isPresent()) {
            kbuilder.buildAll(kieBaseTestConfiguration.getExecutableModelProjectClass().get());
        } else {
            kbuilder.buildAll(DrlProject.class);
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
        return kieServices.newReleaseId(groupId, UUID.randomUUID().toString(), "1.0.0");
    }

    public static KieBuilder getKieBuilderFromClasspathResources(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                                 final Class classLoaderFromClass, final boolean failIfBuildError, final String... resources) {
        final List<Resource> result = new ArrayList<>();
        for (final String resource : resources) {
            result.add(KieServices.Factory.get().getResources().newClassPathResource(resource, classLoaderFromClass));
        }

        return getKieBuilderFromResources(kieBaseTestConfiguration, failIfBuildError, result.toArray(new Resource[]{}));
    }

    public static Resource[] createResources(final String drlFile, final Class<?> clazz) {
        return new Resource[]{getResources().newClassPathResource(drlFile, clazz)};
    }

    public static Resource[] createResources(final String drl) {
        final Resource drlResource = getResources().newReaderResource(new StringReader(drl));
        drlResource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        return new Resource[]{drlResource};
    }

    public static KieContainer getKieContainerFromDrls(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                       final KieSessionTestConfiguration kieSessionTestConfiguration,
                                                       final String... drls) {
        final KieServices kieServices = KieServices.get();
        final ReleaseId releaseId = kieServices.newReleaseId(UUID.randomUUID().toString(), "test-artifact", "1.0");
        final KieModule kieModule = getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, kieSessionTestConfiguration,
                                                         new HashMap<>(), drls);
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }

    public static KieModule getKieModuleFromDrls(final String moduleGroupId,
                                                 final KieBaseTestConfiguration kieBaseTestConfiguration, final String... drls) {
        return getKieModuleFromDrls(generateReleaseId(moduleGroupId), kieBaseTestConfiguration, drls);
    }

    public static KieModule getKieModuleFromDrls(final ReleaseId releaseId,
                                                 final KieBaseTestConfiguration kieBaseTestConfiguration, final String... drls) {
        return getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_REALTIME,
                                    new HashMap<>(), drls);
    }

    public static KieModule getKieModuleFromDrls(final ReleaseId releaseId,
                                                 final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                 final KieSessionTestConfiguration kieSessionTestConfiguration,
                                                 final Map<String, String> kieModuleConfigurationProperties,
                                                 final String... drls) {
        final List<Resource> resources = getResourcesFromDrls(drls);
        return getKieModuleFromResources(releaseId, kieBaseTestConfiguration, kieSessionTestConfiguration,
                                         kieModuleConfigurationProperties, resources.toArray(new Resource[]{}));
    }

    public static KieModule getKieModuleFromResources(final String moduleGroupId,
                                                      final KieBaseTestConfiguration kieBaseTestConfiguration, final Resource... resources) {
        return getKieModuleFromResources(generateReleaseId(moduleGroupId), kieBaseTestConfiguration, resources);
    }

    public static KieModule getKieModuleFromResources(final ReleaseId releaseId,
                                                      final KieBaseTestConfiguration kieBaseTestConfiguration, final Resource... resources) {
        return getKieModuleFromResources(releaseId, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_REALTIME,
                                         new HashMap<>(), resources);
    }

    public static KieModule getKieModuleFromResources(final ReleaseId releaseId,
                                                      final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                      final KieSessionTestConfiguration kieSessionTestConfiguration,
                                                      final Map<String, String> kieModuleConfigurationProperties,
                                                      final Resource... resources) {
        final KieModuleModel kieModuleModel =
                getKieModuleModel(kieBaseTestConfiguration, kieSessionTestConfiguration, kieModuleConfigurationProperties);
        return buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId, kieModuleModel, resources);
    }

    public static KieModuleModel getKieModuleModel(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                                   final KieSessionTestConfiguration kieSessionTestConfiguration,
                                                   final Map<String, String> kieModuleConfigurationProperties) {
        final KieModuleModel module = createKieModuleModel(kieBaseTestConfiguration.useAlphaNetworkCompiler());
        kieModuleConfigurationProperties.forEach(module::setConfigurationProperty);
        final KieBaseModel kieBaseModel = kieBaseTestConfiguration.getKieBaseModel(module);
        kieSessionTestConfiguration.getKieSessionModel(kieBaseModel);
        return module;
    }

    public static List<Resource> getResourcesFromDrls(final String... drls) {
        final List<Resource> resources = new ArrayList<>();
        for (int i = 0; i < drls.length; i++) {
            // This null check can be used to skip unwanted filenames.
            if (drls[i] != null) {
                final Resource drlResource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(drls[i]));
                drlResource.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "rules" + (i + 1) + ".drl");
                resources.add(drlResource);
            }
        }
        return resources;
    }

    public static List<Resource> getClasspathResources(final String... classpathResources) {
        final List<Resource> resources = new ArrayList<>();
        for (final String classpathResource : classpathResources) {
            // This null check can be used to skip unwanted filenames.
            if (classpathResource != null) {
                resources.add(KieServices.Factory.get().getResources().newClassPathResource(classpathResource));
            }
        }
        return resources;
    }

    public static KieCommands getCommands() {
        return getServices().getCommands();
    }

    public static KieResources getResources() {
        return getServices().getResources();
    }

    public static KieServices getServices() {
        return KieServices.Factory.get();
    }

    public static Resource getResource(final String content, final String path) {
        final KieServices kieServices = KieServices.get();
        final Resource resource = kieServices.getResources().newByteArrayResource( content.getBytes() );
        resource.setSourcePath(path);
        return resource;
    }

    public static ObjectTypeNode getObjectTypeNode(final KieBase kbase, final Class<?> nodeClass) {
        final List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl) kbase).getRete().getObjectTypeNodes();
        for (final ObjectTypeNode n : nodes) {
            if (((ClassObjectType) n.getObjectType()).getClassType() == nodeClass) {
                return n;
            }
        }
        return null;
    }

    private KieUtil() {
        // Creating instances of util classes should not be possible.
    }
}


