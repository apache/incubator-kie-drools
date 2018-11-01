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

package org.jbpm.test.services;

import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.test.util.PoolingDataSource;
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
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServicesTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServicesTest.class);

    protected PoolingDataSource ds;
    protected DeploymentService deploymentService;

    protected abstract DeploymentUnit prepareDeploymentUnit() throws Exception;
    
    protected abstract DeploymentUnit createDeploymentUnit(String groupId, String artifactid, String version) throws Exception;
    
    protected abstract List<String> getProcessDefinitionFiles();

    protected void prepareDocumentStorage() {

    }

    protected void clearDocumentStorageProperty() {

    } 

    protected DeploymentUnit createAndDeployUnit(String groupId, String artifactid, String version) throws Exception {
        List<String> processes = getProcessDefinitionFiles();
        if (processes!= null && !processes.isEmpty()) {
            KieServices ks = KieServices.Factory.get();
            ReleaseId releaseId = ks.newReleaseId(groupId, artifactid, version);
            
            InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
            File pom = new File("target/kmodule", "pom.xml");
            pom.getParentFile().mkdir();
    
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
    
            KieMavenRepository repository = getKieMavenRepository();
            repository.deployArtifact(releaseId, kJar1, pom);
    
            DeploymentUnit deploymentUnit = createDeploymentUnit(groupId, artifactid, version); 
            deploymentService.deploy(deploymentUnit);
            return deploymentUnit;
        } 
        
        return null;
    }


    protected void close() {        
        EntityManagerFactoryManager.get().clear();
        closeDataSource();
    }


    protected String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" + "  <modelVersion>4.0.0</modelVersion>\n" + "\n" + "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" + "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" + "  <version>" + releaseId.getVersion() + "</version>\n" + "\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += "  <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        return pom;
    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources) {
        return createKieJar(ks, releaseId, resources, null);
    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources, Map<String, String> extraResources, ReleaseId... dependencies) {

        KieFileSystem kfs = createKieFileSystemWithKProject(ks);
        kfs.writePomXML(getPom(releaseId, dependencies));

        DeploymentDescriptor customDescriptor = createDeploymentDescriptor();

        if (extraResources == null) {
            extraResources = new HashMap<String, String>();
        }
        if (customDescriptor != null) {
            extraResources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());
        }

        for (String resource : resources) {
            kfs.write("src/main/resources/KBase-test/" + resource, ResourceFactory.newClassPathResource(resource));
        }
        if (extraResources != null) {
            for (Map.Entry<String, String> entry : extraResources.entrySet()) {
                kfs.write(entry.getKey(), ResourceFactory.newByteArrayResource(entry.getValue().getBytes()));
            }
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        if (!kieBuilder.buildAll().getResults().getMessages().isEmpty()) {
            for (Message message : kieBuilder.buildAll().getResults().getMessages()) {
                logger.error("Error Message: ({}) {}", message.getPath(), message.getText());
            }
            throw new RuntimeException("There are errors builing the package, please check your knowledge assets!");
        }

        return (InternalKieModule) kieBuilder.getKieModule();
    }

    protected abstract DeploymentDescriptor createDeploymentDescriptor();

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage("*").setEqualsBehavior(EqualityBehaviorOption.EQUALITY).setEventProcessingMode(EventProcessingOption.STREAM);

        KieSessionModel ksessionModel = kieBaseModel1.newKieSessionModel("ksession-test");

        ksessionModel.setDefault(true).setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get("realtime"));

        ksessionModel.newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");
        ksessionModel.newWorkItemHandlerModel("Service Task", "new org.jbpm.bpmn2.handler.ServiceTaskHandler(\"name\")");

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }

    protected void buildDatasource() {
        ds = new PoolingDataSource();
        ds.setUniqueName("jdbc/testDS1");

        //NON XA CONFIGS
        ds.setClassName("org.h2.jdbcx.JdbcDataSource");
        ds.getDriverProperties().put("user", "sa");
        ds.getDriverProperties().put("password", "sasa");
        ds.getDriverProperties().put("URL", "jdbc:h2:mem:mydb");

        ds.init();
    }

    protected void closeDataSource() {
        if (ds != null) {
            ds.close();
        }
    }

    public static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {

            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {

                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                logger.debug("Temp dir to be removed {} file {}", tempDir, file);
                new File(tempDir, file).delete();
            }
        }
    }

    protected void deleteFolder(String pathStr) {
        File path = new File(pathStr);
        if (path.exists()) {
            File[] directories = path.listFiles();
            if (directories != null) {
                for (File file : directories) {
                    if (file.isDirectory()) {
                        deleteFolder(file.getAbsolutePath());
                    }
                    file.delete();
                }
            }
        }
    }

    protected List<ObjectModel> getProcessListeners() {
        return new ArrayList<>();
    }

    protected List<NamedObjectModel> getWorkItemHandlers() {
        return new ArrayList<>();
    }
    
    protected List<ObjectModel> getTaskListeners() {
        return new ArrayList<>();
    }
}
