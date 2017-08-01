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

package org.jbpm.kie.services.test;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.objects.Building;
import org.jbpm.kie.test.objects.House;
import org.jbpm.kie.test.objects.OtherPerson;
import org.jbpm.kie.test.objects.Person;
import org.jbpm.kie.test.objects.Thing;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.helper.FluentKieModuleDeploymentHelper;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.remote.Remotable;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class FilteredKModuleDeploymentServiceTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(FilteredKModuleDeploymentServiceTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    public void prepare(String packages) {
    	configureServices();
    	logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");

        List<String> processes2 = new ArrayList<String>();
        processes2.add("repo/processes/general/import.bpmn");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, processes2, packages);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
        close();
    }

    @Test
    public void testDeploymentOfProcessesFromCustomerPackageDeafultKBase() {
        prepare("customer.repo.processes.general");
        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION,
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(2, processes.size());

        processes = runtimeDataService.getProcessesByFilter("custom", new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(processes);
        assertEquals(2, processes.size());

        ProcessDefinition process = runtimeDataService.getProcessesByDeploymentIdProcessId(deploymentUnit.getIdentifier(), "customtask");
        assertNotNull(process);

    }

    @Test
    public void testDeploymentOfProcessesFromCustomerPackage() {
        prepare("customer.repo.processes.general");
        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test");

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION+":"+"KBase-test"+":"+"ksession-test",
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(2, processes.size());

        processes = runtimeDataService.getProcessesByFilter("custom", new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(processes);
        assertEquals(2, processes.size());

        ProcessDefinition process = runtimeDataService.getProcessesByDeploymentIdProcessId(deploymentUnit.getIdentifier(), "customtask");
        assertNotNull(process);

    }

    @Test
    public void testDeploymentOfProcessesFromOrderPackage() {
        prepare("order.repo.processes.general");
        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test");

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION+":"+"KBase-test"+":"+"ksession-test",
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        ProcessDefinition process = runtimeDataService.getProcessesByDeploymentIdProcessId(deploymentUnit.getIdentifier(), "Import");
        assertNotNull(process);

    }

    @Test
    public void testDeploymentOfProcessesWildcardPackage() {
        prepare("customer.*");
        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test");

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION+":"+"KBase-test"+":"+"ksession-test",
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(2, processes.size());

        processes = runtimeDataService.getProcessesByFilter("custom", new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(processes);
        assertEquals(2, processes.size());

        ProcessDefinition process = runtimeDataService.getProcessesByDeploymentIdProcessId(deploymentUnit.getIdentifier(), "customtask");
        assertNotNull(process);

    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, List<String> resources, List<String> resources2, String packages ) {


        KieFileSystem kfs = createKieFileSystemWithKProject(ks, packages);
        kfs.writePomXML( getPom(releaseId) );


        for (String resource : resources) {
            kfs.write("src/main/resources/customer/" + resource, ResourceFactory.newClassPathResource(resource));
        }
        for (String resource : resources2) {
            kfs.write("src/main/resources/order/" + resource, ResourceFactory.newClassPathResource(resource));
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        if (!kieBuilder.buildAll().getResults().getMessages().isEmpty()) {
            for (Message message : kieBuilder.buildAll().getResults().getMessages()) {
                logger.error("Error Message: ({}) {}", message.getPath(), message.getText());
            }
            throw new RuntimeException(
                    "There are errors builing the package, please check your knowledge assets!");
        }

        return ( InternalKieModule ) kieBuilder.getKieModule();
    }

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks, String packages) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test").setDefault(true).addPackage(packages)
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );


        kieBaseModel1.newKieSessionModel("ksession-test").setDefault(true)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") )
                .newWorkItemHandlerModel("Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()");
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }

    private void verifyDeployedUnitContainsCorrectClasses(DeploymentUnit deploymentUnit) {
        DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        DeploymentDescriptor descriptor = ((KModuleDeploymentUnit) deployedUnit.getDeploymentUnit()).getDeploymentDescriptor();
        boolean limitClasses = descriptor.getLimitSerializationClasses();

        boolean unAnnotatedClassesFound = false;
        for( Class<?> depdClass : deployedUnit.getDeployedClasses() ) {
            Annotation [] declAnnos = depdClass.getDeclaredAnnotations();
            boolean xmlOrRemotAnnoFound = false;
            for( Annotation depdClassAnno : declAnnos ) {
                Class annoType = depdClassAnno.annotationType();
                if( XmlRootElement.class.equals(annoType)
                    || Remotable.class.equals(annoType) ) {
                    xmlOrRemotAnnoFound = true;
                    break;
                }
            }
            assertTrue( "Expected to find annotations on " + depdClass.getSimpleName(),
                    (xmlOrRemotAnnoFound && limitClasses) || ! limitClasses );
            if( ! xmlOrRemotAnnoFound ) {
                unAnnotatedClassesFound = true;
            }
        }
        assertTrue( "Expected to find unannotated classes in " + deployedUnit.getDeploymentUnit().getIdentifier(),
                ( limitClasses && ! unAnnotatedClassesFound ) || (! limitClasses && unAnnotatedClassesFound) );

        if( limitClasses ) {
            try {
                JAXBContext.newInstance(deployedUnit.getDeployedClasses().toArray(new Class[0]));
            } catch( JAXBException e ) {
                e.printStackTrace();
                fail("JAXBContext creation with deployed unit classes failed: " + e.getMessage());
            }
        }
    }

    @Test
    public void testSerializationClassesLimitedInDeploymentItself() {
        String groupId = "org.test";
        String artifactId = "jbpm-kie-services-filter-test";
        String version = VERSION;

       FluentKieModuleDeploymentHelper.newFluentInstance()
           .setGroupId(groupId)
           .setArtifactId(artifactId)
           .setVersion(version)
           .addClass(Building.class, House.class, Person.class, OtherPerson.class, Thing.class)
           .createKieJarAndDeployToMaven();
       KModuleDeploymentUnit limitDeploymentUnit = new KModuleDeploymentUnit(groupId, artifactId, version);

       FluentKieModuleDeploymentHelper.newFluentInstance()
           .setGroupId(groupId)
           .setArtifactId(artifactId + "-all")
           .setVersion(version)
           .addClass(Building.class, House.class, Person.class, OtherPerson.class, Thing.class)
           .createKieJarAndDeployToMaven();
       KModuleDeploymentUnit allDeploymentUnit = new KModuleDeploymentUnit(groupId, artifactId + "-all", version);

       configureServices();

       DeploymentDescriptor depDesc = new DeploymentDescriptorImpl().getBuilder()
           .setLimitSerializationClasses(true)
           .get();
       limitDeploymentUnit.setDeploymentDescriptor(depDesc);

       deploymentService.deploy(limitDeploymentUnit);
       verifyDeployedUnitContainsCorrectClasses(limitDeploymentUnit);

       depDesc = new DeploymentDescriptorImpl().getBuilder()
           .setLimitSerializationClasses(false)
           .get();
       allDeploymentUnit.setDeploymentDescriptor(depDesc);

       deploymentService.deploy(allDeploymentUnit);
       verifyDeployedUnitContainsCorrectClasses(allDeploymentUnit);
    }

    @Test
    public void testSerializationClassesLimitedInDeploymentDescriptor() {
        String groupId = "org.test";
        String artifactId = "jbpm-kie-services-filter-test-desc";
        String version = VERSION;

       FluentKieModuleDeploymentHelper.newFluentInstance()
           .setGroupId(groupId)
           .setArtifactId(artifactId)
           .setVersion(version)
           .createKieJarAndDeployToMaven();

       configureServices();

       KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(groupId, artifactId, version);
       DeploymentDescriptor depDesc = new DeploymentDescriptorImpl().getBuilder()
           .setLimitSerializationClasses(true)
           .addClass(Building.class.getName()) // interface, no annotations!
           .addClass(House.class.getName()) // @XmlRootElement
           .addClass(Person.class.getName()) // @XmlRootElement
           .addClass(OtherPerson.class.getName()) // inherits from Person
           .addClass(Thing.class.getName()) // @Remotable
           .get();
       deploymentUnit.setDeploymentDescriptor(depDesc);

       deploymentService.deploy(deploymentUnit);
       units.add(deploymentUnit);

       verifyDeployedUnitContainsCorrectClasses(deploymentUnit);
    }

    @Test
    public void testSerializationClassesLimitedInDeploymentDependencies() {
        String groupId = "org.test";
        String childArtifactId = "jbpm-kie-services-filter-test-dep";
        String parentArtifactId = "jbpm-kie-services-filter-test-parent";
        String version = VERSION;

       FluentKieModuleDeploymentHelper.newFluentInstance()
           .setGroupId(groupId)
           .setArtifactId(childArtifactId)
           .setVersion(version)
           .addClass(Building.class, House.class, Person.class, OtherPerson.class, Thing.class)
           .createKieJarAndDeployToMaven();

       FluentKieModuleDeploymentHelper.newFluentInstance()
           .setGroupId(groupId)
           .setArtifactId(parentArtifactId)
           .setVersion(version)
           .addDependencies(groupId +":" + childArtifactId + ":" + version)
           .createKieJarAndDeployToMaven();

       configureServices();

       KModuleDeploymentUnit childDeploymentUnit = new KModuleDeploymentUnit(groupId, childArtifactId, version);
       DeploymentDescriptor depDesc = new DeploymentDescriptorImpl().getBuilder()
           .setLimitSerializationClasses(false) // parent dictates behavior
           .get();
       childDeploymentUnit.setDeploymentDescriptor(depDesc);
       deploymentService.deploy(childDeploymentUnit);

       KModuleDeploymentUnit parentDeploymentUnit = new KModuleDeploymentUnit(groupId, parentArtifactId, version);
       DeploymentDescriptor parentDepDesc = new DeploymentDescriptorImpl().getBuilder()
           .setLimitSerializationClasses(true)
           .get();
       parentDeploymentUnit.setDeploymentDescriptor(parentDepDesc);

       deploymentService.deploy(parentDeploymentUnit);

       verifyDeployedUnitContainsCorrectClasses(parentDeploymentUnit);
    }

    @Test
    public void testMultipleRemotableInPojoJar() {
        String groupId = "org.test";
        String pojoArtifactId = "jbpm-kie-services-filter-test-pojo";
        String projectArtifactId = "jbpm-kie-services-filter-test-project";
        String version = VERSION;

        KieServices ks = KieServices.Factory.get();
        ReleaseId pojoReleaseId = ks.newReleaseId( groupId, pojoArtifactId, VERSION );
        File pojojar = new File( "src/test/resources/multi-remotable/pojo.jar" ); // contains two @Remotable classes MyPojo, MyPojo2
        File pojopom = new File( this.getClass().getResource("/multi-remotable/pojo-pom.xml").getFile());
        KieMavenRepository.getKieMavenRepository().installArtifact( pojoReleaseId, pojojar, pojopom );

        FluentKieModuleDeploymentHelper.newFluentInstance()
            .setGroupId( groupId )
            .setArtifactId( projectArtifactId )
            .setVersion( version )
            .addDependencies( groupId + ":" + pojoArtifactId + ":" + version )
            .createKieJarAndDeployToMaven();

        configureServices();

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit( groupId, projectArtifactId, version );
        DeploymentDescriptor depDesc = new DeploymentDescriptorImpl().getBuilder().setLimitSerializationClasses( true ).get();
        deploymentUnit.setDeploymentDescriptor( depDesc );
        deploymentService.deploy( deploymentUnit );

        DeployedUnit deployedUnit = deploymentService.getDeployedUnit( deploymentUnit.getIdentifier() );
        Collection<Class<?>> deployedClasses = deployedUnit.getDeployedClasses();
        ClassLoader classLoader = deploymentUnit.getKieContainer().getClassLoader();

        try {
            assertTrue( "MyPojo is not added", deployedClasses.contains( classLoader.loadClass( "com.sample.MyPojo" ) ) );
            assertTrue( "MyPojo2 is not added", deployedClasses.contains( classLoader.loadClass( "com.sample.MyPojo2" ) ) );
        } catch ( ClassNotFoundException e ) {
            fail( e.getMessage() );
        }
    }
}