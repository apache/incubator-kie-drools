/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.scanner;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.drools.compiler.kproject.xml.DependencyFilter;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.drools.core.util.DroolsAssert.assertUrlEnumerationContainsMatch;
import static org.junit.Assert.*;
import static org.kie.scanner.MavenRepository.getMavenRepository;

public class KieModuleMavenTest extends AbstractKieCiTest {

    @Test
    public void testKieModuleFromMavenNoDependencies() throws Exception {
        final KieServices ks = new KieServicesImpl() {

            @Override
            public KieRepository getRepository() {
                return new KieRepositoryImpl(); // override repository to not store the artifact on deploy to trigger load from maven repo
            }
        };

        ReleaseId releaseId = ks.newReleaseId("org.kie", "maven-test", "1.0-SNAPSHOT");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, true, "rule1", "rule2");
        String pomText = getPom(releaseId);
        File pomFile = new File(System.getProperty("java.io.tmpdir"), MavenRepository.toFileName(releaseId, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomText.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MavenRepository.getMavenRepository().installArtifact(releaseId, kJar1, pomFile);

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieBaseModel kbaseModel = ((KieContainerImpl) kieContainer).getKieProject().getDefaultKieBaseModel();
        assertNotNull("Default kbase was not found", kbaseModel);
        String kbaseName = kbaseModel.getName();
        assertEquals("KBase1", kbaseName);

        // Check classloader
        assertUrlEnumerationContainsMatch(".*org/kie/maven\\-test/1.0\\-SNAPSHOT.*", kieContainer.getClassLoader().getResources(""));
        assertUrlEnumerationContainsMatch(".*org/kie/maven\\-test/1.0\\-SNAPSHOT.*", kieContainer.getClassLoader().getResources("KBase1/org/test"));
        assertUrlEnumerationContainsMatch(".*org/kie/maven\\-test/1.0\\-SNAPSHOT.*", kieContainer.getClassLoader().getResources("KBase1/org/test/"));
    }

    @Test
    public void testKieModuleFromMavenWithDependencies() throws Exception {
        final KieServices ks = new KieServicesImpl() {

            @Override
            public KieRepository getRepository() {
                return new KieRepositoryImpl(); // override repository to not store the artifact on deploy to trigger load from maven repo
            }
        };

        ReleaseId dependency = ks.newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        ReleaseId releaseId = ks.newReleaseId("org.kie", "maven-test", "1.0-SNAPSHOT");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, true, "rule1", "rule2");
        String pomText = getPom(releaseId, dependency);
        File pomFile = new File(System.getProperty("java.io.tmpdir"), MavenRepository.toFileName(releaseId, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomText.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MavenRepository.getMavenRepository().installArtifact(releaseId, kJar1, pomFile);

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieBaseModel kbaseModel = ((KieContainerImpl) kieContainer).getKieProject().getDefaultKieBaseModel();
        assertNotNull("Default kbase was not found", kbaseModel);
        String kbaseName = kbaseModel.getName();
        assertEquals("KBase1", kbaseName);
    }

    @Test
    public void testKieModuleFromMavenWithTransitiveDependencies() throws Exception {
        final KieServices ks = new KieServicesImpl() {

            @Override
            public KieRepository getRepository() {
                return new KieRepositoryImpl(); // override repository to not store the artifact on deploy to trigger load from maven repo
            }
        };

        ReleaseId dependency = ks.newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        ReleaseId releaseId = ks.newReleaseId("org.kie", "maven-test", "1.0-SNAPSHOT");

        String pomText = getPom(releaseId, dependency);

        InternalKieModule kJar1 = createKieJar(ks, releaseId, pomText, true, "rule1", "rule2");

        File pomFile = new File(System.getProperty("java.io.tmpdir"), MavenRepository.toFileName(releaseId, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomText.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MavenRepository.getMavenRepository().installArtifact(releaseId, kJar1, pomFile);

        KieContainer kieContainer = ks.newKieContainer(releaseId);

        Collection<ReleaseId> expectedDependencies = new HashSet<ReleaseId>();
        expectedDependencies.add(ks.newReleaseId("org.drools", "knowledge-api", "5.5.0.Final"));
        expectedDependencies.add(ks.newReleaseId("org.drools", "knowledge-internal-api", "5.5.0.Final"));
        expectedDependencies.add(ks.newReleaseId("org.drools", "drools-core", "5.5.0.Final"));
        expectedDependencies.add(ks.newReleaseId("org.mvel", "mvel2", "2.1.3.Final"));
        expectedDependencies.add(ks.newReleaseId("org.slf4j", "slf4j-api", "1.6.4"));

        Collection<ReleaseId> dependencies = ((InternalKieModule)((KieContainerImpl) kieContainer)
                .getKieModuleForKBase( "KBase1" ))
                .getJarDependencies( DependencyFilter.TAKE_ALL_FILTER );
        assertNotNull(dependencies);
        assertEquals(5, dependencies.size());

        boolean matchedAll = dependencies.containsAll(expectedDependencies);
        assertTrue(matchedAll);
    }

    @Test
    public void testKieModulePojoDependencies() throws Exception {
        KieServices ks = KieServices.Factory.get();

        // Create and deploy a standard mavenized pojo jar
        String pojoNS = "org.kie.pojos";
        ReleaseId pojoID = KieServices.Factory.get().newReleaseId(pojoNS, "pojojar", "2.0.0");
        String className = "Message";

        ClassDefinition def = new ClassDefinition(pojoNS + "." + className);
        def.addField(new FieldDefinition("text", String.class.getName()));
        byte[] messageClazz = ClassBuilderFactory.getDefaultBeanClassBuilder().buildClass(def,null);
        MemoryFileSystem mfs = new MemoryFileSystem();
        mfs.write(pojoNS.replace('.', '/') + "/" + className+".class", messageClazz);
        byte[] pomContent = generatePomXml(pojoID).getBytes();
        mfs.write("META-INF/maven/" + pojoID.getGroupId() + "/" + pojoID.getArtifactId() + "/pom.xml", pomContent);
        mfs.write("META-INF/maven/" + pojoID.getGroupId() + "/" + pojoID.getArtifactId() + "/pom.properties", generatePomProperties(pojoID).getBytes());
        byte[] pojojar = mfs.writeAsBytes();
        MavenRepository.getMavenRepository().installArtifact(pojoID, pojojar, pomContent);

        // Create and deploy a kjar that depends on the previous pojo jar
        String kjarNS = "org.kie.test1";
        ReleaseId kjarID = KieServices.Factory.get().newReleaseId(kjarNS, "rkjar", "1.0.0");
        String rule = getRule(kjarNS, pojoNS, "R1");
        String pom = generatePomXml(kjarID, pojoID);
        byte[] rkjar = createKJar(ks, kjarID, pom, rule);

        KieModule kmodule = deployJar(ks, rkjar);
        assertNotNull(kmodule);

        KieContainer kContainer = ks.newKieContainer(kjarID);

        KieSession kSession = kContainer.newKieSession();
        List<?> list = new ArrayList<Object>();
        kSession.setGlobal("list", list);
        kSession.fireAllRules();

        assertEquals(1, list.size());
    }

    @Test
    public void testKieContainerBeforeAndAfterDeployOfSnapshot() throws Exception {
        // BZ-1007977
        KieServices ks = KieServices.Factory.get();

        String group = "org.kie.test";
        String artifact = "test-module";
        String version = "1.0.0-SNAPSHOT";

        ReleaseId releaseId = ks.newReleaseId(group, artifact, version);

        String prefix = new File(".").getAbsolutePath().contains("kie-ci") ? "" : "kie-ci/";

        File kjar = new File(prefix + "src/test/resources/kjar/kjar-module-before.jar");
        File pom = new File(prefix + "src/test/resources/kjar/pom-kjar.xml");
        MavenRepository repository = getMavenRepository();
        repository.installArtifact(releaseId, kjar, pom);

        KieContainer kContainer = ks.newKieContainer(releaseId);
        KieBase kbase = kContainer.getKieBase();
        assertNotNull(kbase);
        Collection<KiePackage> packages = kbase.getKiePackages();
        assertNotNull(packages);
        assertEquals(1, packages.size());
        Collection<Rule> rules = packages.iterator().next().getRules();
        assertEquals(2, rules.size());

        ks.getRepository().removeKieModule(releaseId);

        // deploy new version
        File kjar1 = new File(prefix + "src/test/resources/kjar/kjar-module-after.jar");
        File pom1 = new File(prefix + "src/test/resources/kjar/pom-kjar.xml");

        repository.installArtifact(releaseId, kjar1, pom1);

        KieContainer kContainer2 = ks.newKieContainer(releaseId);
        KieBase kbase2 = kContainer2.getKieBase();
        assertNotNull(kbase2);
        Collection<KiePackage> packages2 = kbase2.getKiePackages();
        assertNotNull(packages2);
        assertEquals(1, packages2.size());
        Collection<Rule> rules2 = packages2.iterator().next().getRules();
        assertEquals(4, rules2.size());

        ks.getRepository().removeKieModule(releaseId);
    }

    @Test
    public void testKieModuleFromMavenWithDependenciesProperties() throws Exception {
        final KieServices ks = new KieServicesImpl() {

            @Override
            public KieRepository getRepository() {
                return new KieRepositoryImpl(); // override repository to not store the artifact on deploy to trigger load from maven repo
            }
        };

        ReleaseId dependency = ks.newReleaseId("org.drools", "drools-core", "${version.org.drools}");
        ReleaseId releaseId = ks.newReleaseId("org.kie.test", "maven-test", "1.0-SNAPSHOT");
        InternalKieModule kJar1 = createKieJarWithProperties(ks, releaseId, true, "5.5.0.Final", new ReleaseId[]{dependency}, "rule1", "rule2");
        String pomText = generatePomXmlWithProperties(releaseId, "5.5.0.Final", dependency);
        File pomFile = new File(System.getProperty("java.io.tmpdir"), MavenRepository.toFileName(releaseId, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomText.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MavenRepository.getMavenRepository().installArtifact(releaseId, kJar1, pomFile);

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieBaseModel kbaseModel = ((KieContainerImpl) kieContainer).getKieProject().getDefaultKieBaseModel();
        assertNotNull("Default kbase was not found", kbaseModel);
        String kbaseName = kbaseModel.getName();
        assertEquals("KBase1", kbaseName);
    }

    public static String generatePomXml(ReleaseId releaseId, ReleaseId... dependencies) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n");
        sBuilder.append("         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"> \n");
        sBuilder.append("    <modelVersion>4.0.0</modelVersion> \n");

        sBuilder.append("    <groupId>").append(releaseId.getGroupId()).append("</groupId> \n");
        sBuilder.append("    <artifactId>").append(releaseId.getArtifactId()).append("</artifactId> \n");
        sBuilder.append("    <version>").append(releaseId.getVersion()).append("</version> \n");
        sBuilder.append("    <packaging>jar</packaging> \n");
        sBuilder.append("    <name>Default</name> \n");

        if (dependencies.length > 0) {
            sBuilder.append("<dependencies>\n");
            for (ReleaseId dep : dependencies) {
                sBuilder.append("  <dependency>\n");
                sBuilder.append("    <groupId>").append(dep.getGroupId()).append("</groupId> \n");
                sBuilder.append("    <artifactId>").append(dep.getArtifactId()).append("</artifactId> \n");
                sBuilder.append("    <version>").append(dep.getVersion()).append("</version> \n");
                sBuilder.append("  </dependency>\n");
            }
            sBuilder.append("</dependencies>\n");
        }

        sBuilder.append("</project>  \n");
        return sBuilder.toString();
    }

    public static String generatePomXmlWithProperties(ReleaseId releaseId, String droolsVersion, ReleaseId... dependencies) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n");
        sBuilder.append(" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"> \n");
        sBuilder.append(" <modelVersion>4.0.0</modelVersion> \n");

        sBuilder.append(" <groupId>").append(releaseId.getGroupId()).append("</groupId> \n");
        sBuilder.append(" <artifactId>").append(releaseId.getArtifactId()).append("</artifactId> \n");
        sBuilder.append(" <version>").append(releaseId.getVersion()).append("</version> \n");
        sBuilder.append(" <packaging>jar</packaging> \n");
        sBuilder.append(" <name>Default</name> \n");
        sBuilder.append(" <properties> \n");
        sBuilder.append(" <version.org.drools>"+droolsVersion+"</version.org.drools> \n");
        sBuilder.append(" </properties> \n");

        if (dependencies.length > 0) {
            sBuilder.append("<dependencies>\n");
            for (ReleaseId dep : dependencies) {
                sBuilder.append(" <dependency>\n");
                sBuilder.append(" <groupId>").append(dep.getGroupId()).append("</groupId> \n");
                sBuilder.append(" <artifactId>").append(dep.getArtifactId()).append("</artifactId> \n");
                sBuilder.append(" <version>").append(dep.getVersion()).append("</version> \n");
                sBuilder.append(" </dependency>\n");
            }
            sBuilder.append("</dependencies>\n");
        }

        sBuilder.append("</project> \n");
        return sBuilder.toString();
    }

    protected InternalKieModule createKieJarWithProperties(KieServices ks, ReleaseId releaseId, boolean isdefault,
                                                           String droolsVersion, ReleaseId[] dependencies, String... rules) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks, isdefault);
        kfs.writePomXML(generatePomXmlWithProperties(releaseId, droolsVersion, dependencies));

        for (String rule : rules) {
            String file = "org/test/" + rule + ".drl";
            kfs.write("src/main/resources/KBase1/" + file, createDRL(rule));
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    public static String generatePomProperties(ReleaseId releaseId) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("version=").append(releaseId.getVersion()).append("\n");
        sBuilder.append("groupId=").append(releaseId.getGroupId()).append("\n");
        sBuilder.append("artifactId=").append(releaseId.getArtifactId()).append("\n");
        return sBuilder.toString();
    }

    public String getRule(String namespace,
            String messageNS,
            String ruleName) {
        String s = "package " + namespace + "\n" +
                "import " + messageNS + ".Message\n" +
                "global java.util.List list\n" +
                "rule " + ruleName + " when \n" +
                "then \n" +
                "  Message msg = new Message('hello');\n" +
                "  list.add(msg);\n " +
                "end \n" +
                "";
        return s;
    }

}
