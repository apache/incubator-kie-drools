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
package org.drools.model.codegen.execmodel;

import java.util.Set;
import java.util.UUID;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.drools.wiring.api.classloader.ProjectClassLoaderTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class GeneratedClassNamesTest extends BaseModelTest {

    private boolean enableStoreFirstOrig;

    public GeneratedClassNamesTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{RUN_TYPE.PATTERN_DSL};
    }

    @Before
    public void init() {
        enableStoreFirstOrig = ProjectClassLoader.isEnableStoreFirst();
        ProjectClassLoaderTestUtil.setEnableStoreFirst(true);
    }

    @After
    public void clear() {
        ProjectClassLoaderTestUtil.setEnableStoreFirst(enableStoreFirstOrig);
    }

    @Test
    public void testGeneratedClassNames() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "  System.out.println(\"hello\");\n" +
                     "end";

        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "kjar-test-" + UUID.randomUUID(), "1.0");

        createKieBuilder(ks, getDefaultKieModuleModel( ks ), releaseId, toKieFiles(new String[]{str}));
        KieContainer kcontainer = ks.newKieContainer(releaseId);

        KieModule kieModule = ((KieContainerImpl) kcontainer).getKieModuleForKBase("kbase");

        assertThat(kieModule instanceof CanonicalKieModule).isTrue();

        KieSession ksession = kcontainer.newKieSession();

        Set<String> generatedClassNames = ((CanonicalKieModule) kieModule).getGeneratedClassNames();
        assertGeneratedClassNames(generatedClassNames);

        Person me = new Person("Mario", 40);
        ksession.insert(me);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }

    private void assertGeneratedClassNames(Set<String> generatedClassNames) {
        assertThat(generatedClassNames).isNotNull();
        String[] nameFragments = new String[]{"Rules", "LambdaConsequence", "LambdaPredicate", "LambdaExtractor", "DomainClassesMetadata", "ProjectModel", "$"};
        for (String nameFragment : nameFragments) {
            boolean contains = false;
            for (String generatedClassName : generatedClassNames) {
                if (generatedClassName.contains(nameFragment)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                fail("generatedClassNames doesn't contain [" + nameFragment + "] class. : " + generatedClassNames);
            }
        }
    }

    @Test
    public void testModuleWithDepWithoutClassLoader() throws Exception {
        testModuleWithDep(null);
    }

    @Test
    public void testModuleWithDepWithClassLoader() throws Exception {
        ProjectClassLoader projectClassLoader = ProjectClassLoader.createProjectClassLoader(Thread.currentThread().getContextClassLoader());
        testModuleWithDep(projectClassLoader);
    }

    private void testModuleWithDep(ProjectClassLoader projectClassLoader) throws Exception {
        KieServices ks = KieServices.get();
        ReleaseId releaseIdDep = ks.newReleaseId("org.example", "depRule", "1.0.0");
        ReleaseId releaseIdMain = ks.newReleaseId("org.example", "mainRule", "1.0.0");

        String depStr =
                "package org.example.dep\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R_dep when\n" +
                        "  $p : Person(name == \"Mario\")\n" +
                        "then\n" +
                        "  System.out.println(\"hello dep rule\");\n" +
                        "end";

        String mainStr =
                "package org.example.main\n" +
                         "import " + Person.class.getCanonicalName() + ";\n" +
                         "rule R_main when\n" +
                         "  $p : Person(name == \"Luca\")\n" +
                         "then\n" +
                         "  System.out.println(\"hello main rule\");\n" +
                         "end";

        String mainPom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                         "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                         "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                         "  <modelVersion>4.0.0</modelVersion>\n" +
                         "\n" +
                         "  <groupId>" + releaseIdMain.getGroupId() + "</groupId>\n" +
                         "  <artifactId>" + releaseIdMain.getArtifactId() + "</artifactId>\n" +
                         "  <version>" + releaseIdMain.getVersion() + "</version>\n" +
                         "  <dependencies>\n" +
                         "    <dependency>\n" +
                         "      <groupId>" + releaseIdDep.getGroupId() + "</groupId>\n" +
                         "      <artifactId>" + releaseIdDep.getArtifactId() + "</artifactId>\n" +
                         "      <version>" + releaseIdDep.getVersion() + "</version>\n" +
                         "    </dependency>\n" +
                         "  </dependencies>" +
                         "</project>";

        // create dep
        KieFileSystem depKfs = ks.newKieFileSystem();
        KieModuleModel depModuleModel = ks.newKieModuleModel();
        KieBaseModel depKieBaseModel = depModuleModel.newKieBaseModel("dep").setDefault(false);
        depKieBaseModel.addPackage("*");
        depKfs.writeKModuleXML(depModuleModel.toXML());
        depKfs.generateAndWritePomXML(releaseIdDep);
        Resource depResource = ResourceFactory.newByteArrayResource(depStr.getBytes());
        depResource.setSourcePath("src/main/resources/org/example/dep/r1.drl");
        depKfs.write(depResource);
        KieBuilder depKieBuilder = ks.newKieBuilder(depKfs);
        depKieBuilder.buildAll(ExecutableModelProject.class);
        CanonicalKieModule depKieModule = (CanonicalKieModule) depKieBuilder.getKieModule();

        MemoryKieModule depMemoryKieModule = (MemoryKieModule) depKieModule.getInternalKieModule();
        MemoryFileSystem depMfs = depMemoryKieModule.getMemoryFileSystem();
        byte[] depBinary = depMfs.writeAsBytes();
        Resource depJarResource = ks.getResources().newByteArrayResource(depBinary);

        // create main
        KieFileSystem mainKfs = ks.newKieFileSystem();
        KieModuleModel mainModuleModel = ks.newKieModuleModel();
        KieBaseModel mainKieBaseModel = mainModuleModel.newKieBaseModel("main").setDefault(true);
        mainKieBaseModel.addPackage("*");
        mainKieBaseModel.addInclude("dep");
        Resource mainResource = ResourceFactory.newByteArrayResource(mainStr.getBytes());
        mainResource.setSourcePath("src/main/resources/org/example/main/r2.drl");
        mainKfs.write(mainResource);
        mainKfs.writeKModuleXML(mainModuleModel.toXML());
        mainKfs.write("pom.xml", mainPom);
        KieBuilder mainKieBuilder = ks.newKieBuilder(mainKfs);
        mainKieBuilder.setDependencies(depJarResource);
        mainKieBuilder.buildAll(ExecutableModelProject.class);
        CanonicalKieModule mainKieModule = (CanonicalKieModule) mainKieBuilder.getKieModule();

        MemoryKieModule mainMemoryKieModule = (MemoryKieModule) mainKieModule.getInternalKieModule();
        MemoryFileSystem mainMfs = mainMemoryKieModule.getMemoryFileSystem();
        byte[] mainBinary = mainMfs.writeAsBytes();
        Resource mainJarResource = ks.getResources().newByteArrayResource(mainBinary);

        // cleanup
        ks.getRepository().removeKieModule(releaseIdMain);
        ks.getRepository().removeKieModule(releaseIdDep);

        ks.getRepository().addKieModule(mainJarResource, depJarResource);

        KieContainerImpl kieContainer;
        if (projectClassLoader == null) {
            kieContainer = (KieContainerImpl) ks.newKieContainer(releaseIdMain);
        } else {
            kieContainer = (KieContainerImpl) ks.newKieContainer(releaseIdMain, projectClassLoader);
        }

        CanonicalKieModule mainLoadedModule = (CanonicalKieModule) kieContainer.getKieModuleForKBase("main");

        KieBase kbase = kieContainer.getKieBase();

        Set<String> mainGeneratedClassNames = mainLoadedModule.getGeneratedClassNames();
        assertGeneratedClassNamesWithDep(mainGeneratedClassNames);

        KieSession ksession = kbase.newKieSession();
        ksession.insert(new Person("Mario"));
        ksession.insert(new Person("Luca"));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);
        ksession.dispose();
    }

    private void assertGeneratedClassNamesWithDep(Set<String> generatedClassNames) {
        assertThat(generatedClassNames).isNotNull();
        String[] nameFragments = new String[]{"Rules", "LambdaConsequence", "LambdaPredicate", "LambdaExtractor", "DomainClassesMetadata", "$"};
        for (String nameFragment : nameFragments) {
            boolean containsDep = false;
            boolean containsMain = false;
            for (String generatedClassName : generatedClassNames) {
                if (generatedClassName.contains(nameFragment)) {
                    if (generatedClassName.startsWith("org.example.dep")) {
                        containsDep = true;
                    } else if (generatedClassName.startsWith("org.example.main"))
                        containsMain = true;
                }
            }
            if (!containsDep) {
                fail("generatedClassNames doesn't contain [" + nameFragment + "] class for dep. : " + generatedClassNames);
            }
            if (!containsMain) {
                fail("generatedClassNames doesn't contain [" + nameFragment + "] class for main. : " + generatedClassNames);
            }
        }
    }

    @Test
    public void testKjarWithoutGeneratedClassNames() {
        // Build a kjar without generated-class-names file (= simulating a build by old drools version)

        ProjectClassLoaderTestUtil.setEnableStoreFirst(false);

        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "  System.out.println(\"hello\");\n" +
                     "end";

        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "kjar-test-" + UUID.randomUUID(), "1.0");

        KieBuilder kieBuilder = createKieBuilder(ks, getDefaultKieModuleModel( ks ), releaseId, toKieFiles(new String[]{str}));

        final InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();
        byte[] kjar = kieModule.getBytes();
        Resource kjarResource = ks.getResources().newByteArrayResource(kjar);

        // cleanup
        ks.getRepository().removeKieModule(releaseId);

        // Load the kjar with the current drools version
        ProjectClassLoaderTestUtil.setEnableStoreFirst(true);

        ks.getRepository().addKieModule(kjarResource);
        KieContainer kcontainer = ks.newKieContainer(releaseId);

        KieModule kieModule2 = ((KieContainerImpl) kcontainer).getKieModuleForKBase("kbase");

        assertThat(kieModule2 instanceof CanonicalKieModule).isTrue();

        KieSession ksession = kcontainer.newKieSession();

        Set<String> generatedClassNames = ((CanonicalKieModule) kieModule2).getGeneratedClassNames();
        assertThat(generatedClassNames.isEmpty()).isTrue();

        Person me = new Person("Mario", 40);
        ksession.insert(me);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }

}
