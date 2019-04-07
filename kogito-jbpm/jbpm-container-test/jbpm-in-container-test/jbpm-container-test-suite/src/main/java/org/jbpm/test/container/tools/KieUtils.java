/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.container.tools;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Set of convenience methods to help with Kie API
 */
public class KieUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(KieUtils.class);

    /**
     * Creates a new KieBuilder with KieFileSystem containing specified
     * resources. Then the module is built and it is asserted that there are no
     * build errors.
     * 
     * @param resources
     *            resources to be built
     * @return KieBuilder used to build the resources
     */
    public static final KieBuilder newKieBuilder(Collection<Resource> resources) {
        return buildResources(resources.toArray(new Resource[resources.size()]));
    }

    /**
     * Creates a new KieBuilder with KieFileSystem containing specified
     * resources. Then the module is built and it is asserted that there are no
     * build errors.
     * 
     * @param resources
     *            resources to be built
     * @return KieBuilder used to build the resources
     */
    public static final KieBuilder newKieBuilder(Resource... resources) {
        return buildResources(resources);
    }

    /**
     * Creates a new KieBuilder with KieFileSystem containing specified
     * resources. Then the module is built and if indicated, it is asserted that
     * there are no build errors.
     * 
     * @param assertNoErrors
     *            whether to assert there are no errors
     * @param resources
     *            resources to be built
     * @return KieBuilder used to build the resources
     */
    public static final KieBuilder newKieBuilder(boolean assertNoErrors, Resource... resources) {
        return buildResources(assertNoErrors, resources);
    }

    /**
     * Creates a new KieBuilder with KieFileSystem containing specified
     * resources and defining a KieBase in STREAM mode. Then the module is built
     * and it is asserted that there are no build errors.
     * 
     * @param resources
     *            resources to be built
     * @return KieBuilder used to build the resources
     */
    public static final KieBuilder newCEPKieBuilder(Resource... resources) {
        return buildCEPResources(resources);
    }

    /**
     * Creates a new KieBuilder with KieFileSystem containing specified
     * resources and defining a KieBase in STREAM mode. Then the module is built
     * and if indicated, it is asserted that there are no build errors.
     * 
     * @param assertNoErrors
     *            whether to assert there are no errors
     * @param resources
     *            resources to be built
     * @return KieBuilder used to build the resources
     */
    public static final KieBuilder newCEPKieBuilder(boolean assertNoErrors, Resource... resources) {
        return buildCEPResources(assertNoErrors, resources);
    }

    public static ReleaseId buildKieModule(Collection<Resource> resources) {
        return buildKieModule(resources.toArray(new Resource[resources.size()]));
    }

    /**
     * Creates the kmodule.xml file and the KieModule from given resources. Kie
     * module contains cross product of these KieBase and KieSession settings:
     * KieBases:
     * <ul>
     * <li>default settings - stream mode, identity based equality</li>
     * <li>cloud mode, identity based equality</li>
     * <li>stream mode, equality based equality</li>
     * </ul>
     * KieSessions:
     * <ul>
     * <li>default stateless</li>
     * <li>default stateful - realtime clock</li>
     * <li>stateful with pseudo clock</li>
     * </ul>
     * 
     * @param resources
     *            resources to be added to the KieBase
     * @return id to reference the KieModule
     */
    public static ReleaseId buildKieModule(Resource... resources) {
        ReleaseId id = generateReleaseId();
        buildKieModule(id, resources);

        return id;
    }

    /**
     * Returns ReleaseId with randomly generated UUID as artifactId.
     */
    public static ReleaseId generateReleaseId() {
        final KieServices ks = KieServices.Factory.get();
        final String moduleName = UUID.randomUUID().toString();
        return ks.newReleaseId("org.jboss.qa.brms", moduleName, "1.0.0");
    }

    /**
     * Creates the kmodule.xml file and the KieModule from given resources with
     * given ReleaseId. Kie module contains cross product of these KieBase and
     * KieSession settings: KieBases:
     * <ul>
     * <li>default settings - stream mode, identity based equality</li>
     * <li>cloud mode, identity based equality</li>
     * <li>stream mode, equality based equality</li>
     * </ul>
     * KieSessions:
     * <ul>
     * <li>default stateless</li>
     * <li>default stateful - realtime clock</li>
     * <li>stateful with pseudo clock</li>
     * </ul>
     * 
     * @param id
     *            id to reference the KieModule
     * @param resources
     *            resources to be added to the KieBase
     */
    public static void buildKieModule(ReleaseId id, Resource... resources) {
        KieServices ks = KieServices.Factory.get();
        KieModuleModel module = ks.newKieModuleModel();

        { // kie base and sessions with default options (STREAM mode!)
            final String name = KBASE_DEFAULT;
            KieBaseModel base = module.newKieBaseModel(name);
            base.setDefault(true);
            base.addPackage("*");
            base.setEventProcessingMode(EventProcessingOption.STREAM);
            base.newKieSessionModel(getSessionName(name, KSESSION_STATELESS)).setDefault(true)
                    .setType(KieSessionType.STATELESS);
            base.newKieSessionModel(getSessionName(name, KSESSION_DEFAULT)).setDefault(true)
                    .setType(KieSessionType.STATEFUL);
            base.newKieSessionModel(getSessionName(name, KSESSION_PSEUDO)).setType(KieSessionType.STATEFUL)
                    .setClockType(ClockTypeOption.get("pseudo"));
        }

        { // kie base and sessions working in CLOUD mode
            final String name = KBASE_CLOUD;
            KieBaseModel base = module.newKieBaseModel(name);
            base.addInclude(KBASE_DEFAULT); // include resources from default
                                            // KieBase
            base.setEventProcessingMode(EventProcessingOption.CLOUD);
            base.newKieSessionModel(getSessionName(name, KSESSION_STATELESS)).setType(KieSessionType.STATELESS);
            base.newKieSessionModel(getSessionName(name, KSESSION_DEFAULT)).setType(KieSessionType.STATEFUL);
            base.newKieSessionModel(getSessionName(name, KSESSION_PSEUDO)).setType(KieSessionType.STATEFUL)
                    .setClockType(ClockTypeOption.get("pseudo"));
        }

        { // kie base and sessions with equality behavior and STREAM mode
            final String name = KBASE_EQUALITY;
            KieBaseModel base = module.newKieBaseModel(name);
            base.addInclude(KBASE_DEFAULT); // include resources from default
                                            // KieBase
            base.setEventProcessingMode(EventProcessingOption.STREAM);
            base.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            base.newKieSessionModel(getSessionName(name, KSESSION_STATELESS)).setType(KieSessionType.STATELESS);
            base.newKieSessionModel(getSessionName(name, KSESSION_DEFAULT)).setType(KieSessionType.STATEFUL);
            base.newKieSessionModel(getSessionName(name, KSESSION_PSEUDO)).setType(KieSessionType.STATEFUL)
                    .setClockType(ClockTypeOption.get("pseudo"));
        }

        KieFileSystem kfs = ks.newKieFileSystem();
        LOGGER.debug("kmodule.xml: {}", module.toXML());
        kfs.writeKModuleXML(module.toXML());
        kfs.generateAndWritePomXML(id);

        KieBuilder kbuilder = buildResources(kfs, resources);
        ks.getRepository().addKieModule(kbuilder.getKieModule());
    }

    /**
     * Combines session name and base name to create unique identifier of a
     * session. See the constants
     * 
     * @param baseName
     *            name of the kie base
     * @param sessionName
     *            name of the kie session
     * @return session unique identifier
     */
    public static String getSessionName(String baseName, String sessionName) {
        return String.format("%s-%s", baseName, sessionName);
    }

    /**
     * Gets the KieBase name from the session name. It is expected, that session
     * name was created with method <code>getSessionName</code>.
     * 
     * @param sessionName
     * @return
     */
    public static String getBaseName(String sessionName) {
        String result = null;

        if (sessionName != null && sessionName.contains("-")) {
            result = sessionName.split("-")[0];
        }

        return result;
    }

    /**
     * Creates kmodule.xml defining single default KieBase in STREAM mode and
     * containing given resources.
     * 
     * @return KieBuilder for this KieBase
     */
    private static KieBuilder buildCEPResources(Resource... resources) {
        return buildResources(createDefaultKieFileSystemForCEP(), resources);
    }

    /**
     * Creates kmodule.xml defining single default KieBase in STREAM mode and
     * containing given resources.
     * 
     * @return KieBuilder for this KieBase
     */
    private static KieBuilder buildCEPResources(boolean assertNoErrors, Resource... resources) {
        return buildResources(createDefaultKieFileSystemForCEP(), assertNoErrors, resources);
    }

    private static KieFileSystem createDefaultKieFileSystemForCEP() {
        KieServices ks = KieServices.Factory.get();
        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel base = module.newKieBaseModel(KBASE_DEFAULT);
        base.setDefault(true);
        base.addPackage("*");
        base.setEventProcessingMode(EventProcessingOption.STREAM);

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(module.toXML());
        return kfs;
    }

    private static KieBuilder buildResources(Resource... resources) {
        return buildResources(KieServices.Factory.get().newKieFileSystem(), true, resources);
    }

    private static KieBuilder buildResources(boolean assertNoErrors, Resource... resources) {
        return buildResources(KieServices.Factory.get().newKieFileSystem(), assertNoErrors, resources);
    }

    private static KieBuilder buildResources(KieFileSystem kfs, Resource... resources) {
        return buildResources(kfs, true, resources);
    }

    private static KieBuilder buildResources(KieFileSystem kfs, boolean assertNoErrors, Resource... resources) {
        int counter = 1;
        for (Resource res : resources) {
            if (res.getSourcePath() == null && res.getTargetPath() == null) {
                res.setTargetPath(String.format("/org/jboss/qa/brms/asset%s.%s", counter++, getAssetExtension(res)));
            }
            kfs.write(res);
        }

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();

        List<Message> msgs;
        // Messages from KieBuilder with increasing severity
        msgs = kbuilder.getResults().getMessages(Level.INFO);
        if (msgs.size() > 0) {
            LOGGER.info("KieBuilder information: {}", msgs.toString());
        }

        msgs = kbuilder.getResults().getMessages(Level.WARNING);
        if (msgs.size() > 0) {
            LOGGER.warn("KieBuilder warnings: {}", msgs.toString());
        }

        msgs = kbuilder.getResults().getMessages(Level.ERROR);
        if (msgs.size() > 0) {
            LOGGER.error("KieBuilder errors: {}", msgs.toString());
        }

        if (assertNoErrors) {
            Assertions.assertThat(msgs.size()).as(msgs.toString()).isEqualTo(0);
        }

        return kbuilder;
    }

    private static String getAssetExtension(Resource res) {
        ResourceType type = res.getResourceType();
        if (type == null) {
            LOGGER.debug("Resource without ResourceType encountered: {}", res);
            return "drl";
        } else {
            return type.getDefaultExtension();
        }
    }

    public static final String KBASE_DEFAULT = "default";
    public static final String KBASE_CLOUD = "cloud";
    public static final String KBASE_EQUALITY = "equality";

    public static final String KSESSION_STATELESS = "stateless";
    public static final String KSESSION_DEFAULT = "stateful";
    public static final String KSESSION_PSEUDO = "stateful-pseudo";
}
