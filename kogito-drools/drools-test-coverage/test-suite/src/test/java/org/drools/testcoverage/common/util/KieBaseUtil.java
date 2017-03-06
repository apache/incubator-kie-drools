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

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.StringReader;

/**
 * Util class that provides various methods related to KieBase.
 */
public final class KieBaseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieBaseUtil.class);

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
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromClasspathResources(classLoaderFromClass, failIfBuildError, resources);
        return getDefaultKieBaseFromKieBuilder(kieBuilder);
    }

    public static KieBase getKieBaseFromResources(final boolean failIfBuildError, final Resource... resources) {
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(failIfBuildError, resources);
        return getDefaultKieBaseFromKieBuilder(kieBuilder);
    }

    public static KieBase getKieBaseFromDRLResources(final boolean failIfBuildError, final Resource... resources) {
        generateDRLResourceTargetPath(resources);
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(failIfBuildError, resources);
        return getDefaultKieBaseFromKieBuilder(kieBuilder);
    }

    private static void generateDRLResourceTargetPath(final Resource[] resources) {
        for (int index = 0; index < resources.length; index++) {
            resources[index].setTargetPath(String.format("rule-%d.drl", index));
        }
    }

    public static KieBase getKieBaseFromReleaseIdByName(final ReleaseId id, final String name) {
        final KieContainer container = KieServices.Factory.get().newKieContainer(id);
        if (name == null) {
            return container.getKieBase();
        } else {
            return container.getKieBase(name);
        }
    }

    public static KieBase getKieBaseAndBuildInstallModule(final String moduleGroupId,
                               final KieBaseTestConfiguration kieBaseTestConfiguration, final Resource... resources) {
        return getKieBaseAndBuildInstallModule(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, resources);
    }

    public static KieBase getKieBaseAndBuildInstallModule(final ReleaseId releaseId,
                               final KieBaseTestConfiguration kieBaseTestConfiguration, final Resource... resources) {
        KieModule kieModule = getKieModuleAndBuildInstallModule(releaseId, kieBaseTestConfiguration, resources);
        return getDefaultKieBaseFromReleaseId(kieModule.getReleaseId());
    }

    public static KieBase getKieBaseAndBuildInstallModuleFromDrl(final String moduleGroupId,
                               final KieBaseTestConfiguration kieBaseTestConfiguration, final String drl) {
        final Resource drlResource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(drl));
        drlResource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        return getKieBaseAndBuildInstallModule(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, drlResource);
    }

    public static KieModule getKieModuleAndBuildInstallModuleFromDrl(final String moduleGroupId,
                               final KieBaseTestConfiguration kieBaseTestConfiguration, final String drl) {
        return getKieModuleAndBuildInstallModuleFromDrl(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, drl);
    }

    public static KieModule getKieModuleAndBuildInstallModuleFromDrl(final ReleaseId releaseId,
                               final KieBaseTestConfiguration kieBaseTestConfiguration, final String drl) {
        final Resource drlResource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(drl));
        drlResource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);
        return getKieModuleAndBuildInstallModule(releaseId, kieBaseTestConfiguration, drlResource);
    }

    public static KieModule getKieModuleAndBuildInstallModule(final String moduleGroupId,
                               final KieBaseTestConfiguration kieBaseTestConfiguration, final Resource... resources) {
        return getKieModuleAndBuildInstallModule(KieUtil.generateReleaseId(moduleGroupId), kieBaseTestConfiguration, resources);
    }

    public static KieModule getKieModuleAndBuildInstallModule(final ReleaseId releaseId,
                               final KieBaseTestConfiguration kieBaseTestConfiguration, final Resource... resources) {
        final KieModuleModel module = KieUtil.createKieModuleModel();
        kieBaseTestConfiguration.getKieBaseModel(module);
        return KieUtil.buildAndInstallKieModuleIntoRepo(releaseId, module, resources);
    }

    private KieBaseUtil() {
        // Creating instances of util classes should not be possible.
    }
}
