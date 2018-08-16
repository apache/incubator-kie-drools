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

package org.jbpm.test.container.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.appformer.maven.integration.embedder.MavenSettings;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.test.container.AbstractEJBServicesTest;
import org.jbpm.test.container.AbstractRuntimeEJBServicesTest;
import org.jbpm.test.container.JbpmContainerTest;
import org.jbpm.test.container.listeners.TrackingAgendaEventListener;
import org.jbpm.test.container.tools.IntegrationMavenResolver;
import org.jbpm.test.container.webspherefix.WebSphereFixedJtaPlatform;
import org.jbpm.test.listener.process.DefaultCountDownProcessEventListener;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJBService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EJBService.class);

    public static final String ARCHIVE_NAME = "ejb-services";

    public static final String SERVICE_URL = "http://localhost:" + System.getProperty("container.port") +
            "/" + ARCHIVE_NAME + "/";

    public static final String EJB_SERVICES_PACKAGE = "org.jbpm.test.container.archive.ejbservices";
    public static final String TEST_EJB_SERVICES_PACKAGE = "org.jbpm.test.container.test.ejbservices";
    public static final String EJB_SERVICES_ASSETS_PATH = "/org/jbpm/test/container/archive/ejbservices/assets/";

    protected List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    protected List<Long> pids = new ArrayList<Long>();

    private WebArchive war;

    private DeploymentService deploymentService;

    private ProcessService processService;

    public WebArchive buildArchive() throws Exception {
        System.out.println("### Building archive '" + ARCHIVE_NAME + ".war'");

        PomEquippedResolveStage resolver = IntegrationMavenResolver.get("jbpm", "jbpm-ejb-services", "jbpm-persistence");
        File[] dependencies = resolver.importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();
        LOGGER.debug("Archive dependencies:");
        for (File d : dependencies) {
            LOGGER.debug(d.getName());
        }

        war = ShrinkWrap
                .create(WebArchive.class, ARCHIVE_NAME + ".war")
                .addAsLibraries(dependencies)
                .addClass(EJBService.class)
                // Workaroud for https://hibernate.atlassian.net/browse/HHH-11606
                .addClass(WebSphereFixedJtaPlatform.class)
                .addClass(DefaultCountDownProcessEventListener.class)
                .addClass(JbpmContainerTest.class)
                .addClass(AbstractEJBServicesTest.class)
                .addClass(AbstractRuntimeEJBServicesTest.class)
                .addClass(TrackingAgendaEventListener.class)
                .addPackages(true, "org.jbpm.test.container.groups", EJB_SERVICES_PACKAGE, TEST_EJB_SERVICES_PACKAGE)
                .addAsWebResource(getClass().getResource("/logback.xml"), ArchivePaths.create("logback.xml"))
                .addAsResource(getClass().getResource("ejbservices/persistence.xml"),
                        ArchivePaths.create("META-INF/persistence.xml"))
                        .addAsResource(getClass().getResource("ejbservices/usergroups.properties"),
                                ArchivePaths.create("usergroups.properties"))
                                .addAsResource(getClass().getResource("ejbservices/UserGroupsAssignmentsOne.mvel"),
                                        ArchivePaths.create("org/jbpm/services/task/identity/UserGroupsAssignmentsOne.mvel"));

     // CDI beans.xml
        war.addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("classes/META-INF/beans.xml"));

        try {
            String pkg = EJB_SERVICES_ASSETS_PATH;
            File f = new File(getClass().getResource(pkg).toURI());
            for (File res : f.listFiles()) {
                if (!res.isDirectory()) {
                    war.addAsResource(res, ArchivePaths.create(pkg, res.getName()));
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to build archive.", ex);
            throw ex;
        }

        return war;
    }

    public Resource getResource(String resourceName) {
        return KieServices.Factory.get().getResources().newClassPathResource(EJB_SERVICES_ASSETS_PATH + resourceName);
    }

    public WebArchive getWar() {
        return war;
    }

    public static String getContext() {
        return SERVICE_URL;
    }

    public List<DeploymentUnit> getUnits() {
        return units;
    }

    public List<Long> getPids() {
        return pids;
    }

    public void setDeploymentService(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    public DeploymentUnit deployTransactionKieJar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(KieJar.TX.groupId, KieJar.TX.artifactId, KieJar.TX.version);
        List<String> assets = new ArrayList<String>();
        assets.add("ScriptTask.bpmn2");
        assets.add("TxProcess.bpmn2");
        assets.add("TxRules.drl");
        assets.add("TxBoundaryEventProcess.bpmn2");
        deployKieJar(ks, releaseId, assets);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(KieJar.TX.getGroupId(), KieJar.TX.getArtifactId(), KieJar.TX.getVersion());
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        return deploymentUnit;
    }

    public DeploymentUnit deployBasicKieJar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(KieJar.BASIC.groupId, KieJar.BASIC.artifactId, KieJar.BASIC.version);
        List<String> assets = new ArrayList<String>();
        assets.add("ScriptTask.bpmn2");
        assets.add("HumanTask.bpmn2");
        assets.add("IntermediateSignalProcess.bpmn2");
        deployKieJar(ks, releaseId, assets);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(KieJar.BASIC.getGroupId(), KieJar.BASIC.getArtifactId(), KieJar.BASIC.getVersion());
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        return deploymentUnit;
    }

    public DeploymentUnit deployVariableKieJar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(KieJar.VARIABLE.groupId, KieJar.VARIABLE.artifactId, KieJar.VARIABLE.version);
        List<String> assets = new ArrayList<String>();
        assets.add("ObjectVariableProcess.bpmn2");
        deployKieJar(ks, releaseId, assets);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(KieJar.VARIABLE.getGroupId(), KieJar.VARIABLE.getArtifactId(), KieJar.VARIABLE.getVersion());
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        return deploymentUnit;
    }

    public DeploymentUnit deployServiceKieJar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(KieJar.SERVICE.groupId, KieJar.SERVICE.artifactId, KieJar.SERVICE.version);
        List<String> assets = new ArrayList<String>();
        assets.add("RestWorkItem.bpmn2");
        deployKieJar(ks, releaseId, assets);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(KieJar.SERVICE.getGroupId(), KieJar.SERVICE.getArtifactId(), KieJar.SERVICE.getVersion());
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        return deploymentUnit;
    }

    public DeploymentUnit deployEJBComplianceKieJar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(KieJar.EJBCOMPL.groupId, KieJar.EJBCOMPL.artifactId, KieJar.EJBCOMPL.version);
        List<String> assets = new ArrayList<String>();
        assets.add("ThreadInfo.bpmn2");
        assets.add("TxRules.drl");
        assets.add("hello-world_1.0.bpmn");
        deployKieJar(ks, releaseId, assets);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(KieJar.EJBCOMPL.getGroupId(), KieJar.EJBCOMPL.getArtifactId(), KieJar.EJBCOMPL.getVersion());
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        return deploymentUnit;
    }

    public DeploymentUnit deployMigrationV1KieJar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(KieJar.MIGRATIONV1.groupId, KieJar.MIGRATIONV1.artifactId, KieJar.MIGRATIONV1.version);
        List<String> assets = new ArrayList<String>();
        assets.add("evaluation1.bpmn2");
        deployKieJar(ks, releaseId, assets);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(KieJar.MIGRATIONV1.getGroupId(), KieJar.MIGRATIONV1.getArtifactId(), KieJar.MIGRATIONV1.getVersion());
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        return deploymentUnit;
    }

    public DeploymentUnit deployMigrationV2KieJar() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(KieJar.MIGRATIONV2.groupId, KieJar.MIGRATIONV2.artifactId, KieJar.MIGRATIONV2.version);
        List<String> assets = new ArrayList<String>();
        assets.add("evaluation2.bpmn2");
        deployKieJar(ks, releaseId, assets);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(KieJar.MIGRATIONV2.getGroupId(), KieJar.MIGRATIONV2.getArtifactId(), KieJar.MIGRATIONV2.getVersion());
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        return deploymentUnit;
    }

    public void undeployDeploymentUnit(DeploymentUnit deploymentUnit) {
        deploymentService.undeploy(deploymentUnit);
        units.remove(deploymentUnit);
    }

    public Long startProcess(String deploymentUnitId, String processId) {
        return startProcess(deploymentUnitId, processId, new HashMap<String, Object>());
    }

    public Long startProcess(String deploymentUnitId, String processId, Map<String, Object> params) {
        Long processInstanceId = processService.startProcess(deploymentUnitId, processId, params);
        if (processInstanceId != null) {
            pids.add(processInstanceId);
        }
        return processInstanceId;
    }

    private void deployKieJar(KieServices ks, ReleaseId releaseId, List<String> assets) {
        deployKieJar(ks, releaseId, assets, null);
    }

    private void deployKieJar(KieServices ks, ReleaseId releaseId, List<String> assets, Map<String, String> extraResources) {
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdirs();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        InternalKieModule kJar1 = null;
        if (extraResources == null) {
            kJar1 = createKieJar(ks, releaseId, assets);
        } else {
            kJar1 = createKieJar(ks, releaseId, assets, extraResources);
        }

        KieMavenRepository repository = KieMavenRepository.getKieMavenRepository();
        System.out.println( "Local repo is: " + MavenSettings.getSettings().getLocalRepository() );
        repository.installArtifact(releaseId, kJar1, pom);
    }

    private static String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + " xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n"
                + " <modelVersion>4.0.0</modelVersion>\n" + "\n" + " <groupId>" + releaseId.getGroupId() + "</groupId>\n" + " <artifactId>"
                + releaseId.getArtifactId() + "</artifactId>\n" + " <version>" + releaseId.getVersion() + "</version>\n" + "\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += " <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += " <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += " <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        return pom;
    }

    private static InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources) {
        return createKieJar(ks, releaseId, resources, null);
    }

    private static InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources, Map<String, String> extraResources) {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks);
        kfs.writePomXML(getPom(releaseId));

        DeploymentDescriptor descriptor = createDeploymentDescriptor();
        kfs.write("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, descriptor.toXml());

        for (String resource : resources) {
            kfs.write("src/main/resources/KBase-test/" + resource,
                    ResourceFactory.newClassPathResource(EJB_SERVICES_ASSETS_PATH + resource));
        }
        if (extraResources != null) {
            for (Map.Entry<String, String> entry : extraResources.entrySet()) {
                kfs.write(entry.getKey(), ResourceFactory.newByteArrayResource(entry.getValue().getBytes()));
            }
        }
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        if (!kieBuilder.buildAll().getResults().getMessages().isEmpty()) {
            for (Message message : kieBuilder.buildAll().getResults().getMessages()) {
                LOGGER.error("Error Message: ({}) {}", message.getPath(), message.getText());
            }
            throw new RuntimeException("There are errors building the package, please check your knowledge assets!");
        }
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    private static KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage("*")
                .setEqualsBehavior(EqualityBehaviorOption.EQUALITY).setEventProcessingMode(EventProcessingOption.STREAM);
        kieBaseModel1.newKieSessionModel("ksession-test").setDefault(true).setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType(ClockTypeOption.get("realtime"));
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }

    private static DeploymentDescriptor createDeploymentDescriptor() {
        return new DeploymentDescriptorImpl("org.jbpm.domain")
                .getBuilder()
                .runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE)
                .addWorkItemHandler(new NamedObjectModel("Log", "org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler"))
                .addWorkItemHandler(new NamedObjectModel("Rest", "org.jbpm.process.workitem.rest.RESTWorkItemHandler"))
                .addWorkItemHandler(new NamedObjectModel("ThreadInfo", "org.jbpm.test.container.archive.ejbservices.ThreadInfoWorkItemHandler"))
                .get();
    }

    public static enum KieJar {
        BASIC("org.jbpm.test.container", "ejb-services-basic", "1.0"),
        VARIABLE("org.jbpm.test.container", "ejb-services-variable", "1.0"),
        SERVICE("org.jbpm.test.container", "ejb-services-service", "1.0"),
        TX("org.jbpm.test.container", "ejb-services-tx", "1.0"),
        EJBCOMPL("org.jbpm.test.container", "ejb-services-compl", "1.0"),
        MIGRATIONV1("org.jbpm.test.container", "ejb-migration", "1.0"),
        MIGRATIONV2("org.jbpm.test.container", "ejb-migration", "2.0");

        private String groupId;

        private String artifactId;

        private String version;

        private KieJar(String groupId, String artifactId, String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public String getVersion() {
            return version;
        }
    }

}
