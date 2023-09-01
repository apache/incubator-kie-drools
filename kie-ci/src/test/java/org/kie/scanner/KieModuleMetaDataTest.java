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
package org.kie.scanner;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.base.rule.TypeMetaInfo;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.type.Role;
import org.kie.maven.integration.MavenRepository;
import org.kie.util.maven.support.DependencyFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.generatePomXml;

public class KieModuleMetaDataTest extends AbstractKieCiTest {

    private String junitVersion;

    private synchronized String getJunitVersion() throws IOException {
        if (junitVersion == null) {
            final String propertiesPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("versions.properties")).getPath();
            final Properties versionProperties = new Properties();
            versionProperties.load(new FileInputStream(propertiesPath));
            junitVersion = versionProperties.getProperty("version.junit");
        }
        return junitVersion;
    }

    @Test
    public void testKieModuleMetaData() throws Exception {
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(getTestDependencyJarReleaseId());
        checkDependency(kieModuleMetaData);
        assertThat((kieModuleMetaData.getPackages()).contains("org.junit")).isTrue();
    }

    @Test
    public void testKieModuleMetaDataWithoutTestDependencies() throws Exception {
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(getTestDependencyJarReleaseId(),
                new DependencyFilter.ExcludeScopeFilter("test"));
        checkDependency(kieModuleMetaData);
        assertThat(kieModuleMetaData.getPackages()).doesNotContain("org.junit");
    }

    @Test
    public void testKieModuleMetaDataForNonExistingGAV() throws Exception {
        // DROOLS-1562
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId("org.drools", "drools-core", "300.222.761");
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(releaseId);
        assertThat(kieModuleMetaData.getPackages().size()).isEqualTo(0);
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithJavaClass() throws Exception {
        testKieModuleMetaDataInMemory(false);
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithTypeDeclaration() throws Exception {
        testKieModuleMetaDataInMemory(true);
    }

    @Test
    public void testKieModuleMetaDataInMemoryUsingPOMWithTypeDeclaration() throws Exception {
        testKieModuleMetaDataInMemoryUsingPOM(true);
    }

    @Test
    public void testKieModuleMetaDataForDependenciesInMemory() throws Exception {
        testKieModuleMetaDataForDependenciesInMemory(false);
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithJavaClassDefaultPackage() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId("org.kie", "javaDefaultPackage", "1.0-SNAPSHOT");
        final KieModuleModel kproj = ks.newKieModuleModel();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML())
                .writePomXML(generatePomXml(releaseId))
                .write("src/main/java/test/Bean.java", createJavaSource());

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        assertThat(messages.isEmpty()).isTrue();

        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);

        //The call to kieModuleMetaData.getClass() assumes a Java file has an explicit package
        final Class<?> beanClass = kieModuleMetaData.getClass("", "test.Bean");
        assertThat(beanClass).isNotNull();

        final TypeMetaInfo beanMetaInfo = kieModuleMetaData.getTypeMetaInfo(beanClass);
        assertThat(beanMetaInfo).isNotNull();
    }

    @Test
    public void testGetPackageNames() {
        final KieServices ks = KieServices.Factory.get();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/test.drl",
                "package org.test declare Bean end");

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        assertThat(messages.isEmpty()).isTrue();

        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);

        assertThat(kieModuleMetaData.getPackages()).isNotEmpty();
        assertThat(kieModuleMetaData.getPackages().contains("org.test")).isTrue();
    }

    @Test
    public void testIncludeAllDeps() throws IOException {
        final KieServices ks = KieServices.Factory.get();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writePomXML(getPomWithTestDependency());

        final KieModule kieModule = ks.newKieBuilder(kfs).getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        assertThat(("" + kieModuleMetaData.getPackages()).contains("junit")).isTrue();
    }

    @Test
    public void testExcludeTestDeps() throws IOException {
        final KieServices ks = KieServices.Factory.get();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writePomXML(getPomWithTestDependency());

        final KieModule kieModule = ks.newKieBuilder(kfs).getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule, new DependencyFilter.ExcludeScopeFilter("test"));
        assertThat(kieModuleMetaData.getPackages()).doesNotContain("junit");
    }

    private String getPomWithTestDependency() throws IOException {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>org.kie</groupId>\n" +
                "  <artifactId>test</artifactId>\n" +
                "  <version>1.0</version>\n" +
                "\n" +
                "    <dependencies>\n" +
                "      <dependency>\n" +
                "        <groupId>junit</groupId>\n" +
                "        <artifactId>junit</artifactId>\n" +
                "        <version>" + getJunitVersion() + "</version>\n" +
                "        <scope>test</scope>\n" +
                "      </dependency>\n" +
                "    </dependencies>\n" +
                "</project>";
    }

    @Test
    public void testGetRuleNames() {
        final KieServices ks = KieServices.Factory.get();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/test1.drl",
                "package org.test\n" +
                        "rule A\n" +
                        " when\n" +
                        "then\n" +
                        "end\n" +
                        "rule B\n" +
                        " when\n" +
                        "then\n" +
                        "end\n");
        kfs.write("src/main/resources/test2.drl",
                "package org.test\n" +
                        "rule C\n" +
                        " when\n" +
                        "then\n" +
                        "end\n");

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        assertThat(messages.isEmpty()).isTrue();

        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);

        Collection<String> rules = kieModuleMetaData.getRuleNamesInPackage("org.test");
        assertThat(rules.size()).isEqualTo(3);
        assertThat(rules.containsAll(asList("A", "B", "C"))).isTrue();
    }

    private String createJavaSource() {
        return "package test;\n" +
                "public class Bean {\n" +
                "   private int value;\n" +
                "   public int getValue() {\n" +
                "       return value;\n" +
                "   }\n" +
                "}";
    }

    private void testKieModuleMetaDataInMemory(boolean useTypeDeclaration) throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "metadata-test", "1.0-SNAPSHOT");

        InternalKieModule kieModule = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 2, 7, getTestDependencyJarReleaseId());
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        checkDependency(kieModuleMetaData);

        Collection<String> testClasses = kieModuleMetaData.getClasses("org.kie.test");
        assertThat(testClasses.size()).isEqualTo(1);
        assertThat(testClasses.iterator().next()).isEqualTo("Bean");
        Class<?> beanClass = kieModuleMetaData.getClass("org.kie.test", "Bean");
        assertThat(beanClass.getMethod("getValue")).isNotNull();

        TypeMetaInfo beanTypeInfo = kieModuleMetaData.getTypeMetaInfo(beanClass);
        assertThat(beanTypeInfo).isNotNull();

        assertThat(beanTypeInfo.isEvent()).isTrue();

        Role role = beanClass.getAnnotation(Role.class);
        assertThat(role).isNotNull();
        assertThat(role.value()).isEqualTo(Role.Type.EVENT);

        assertThat(beanTypeInfo.isDeclaredType()).isEqualTo(useTypeDeclaration);
    }

    private void testKieModuleMetaDataInMemoryUsingPOM(boolean useTypeDeclaration) throws Exception {
        //Build a KieModule jar, deploy it into local Maven repository
        KieServices ks = KieServices.Factory.get();
        ReleaseId dependency = getTestDependencyJarReleaseId();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "metadata-test", "1.0-SNAPSHOT");
        InternalKieModule kieModule = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 2, 7, dependency);
        String pomText = getPom(dependency);
        File pomFile = new File(System.getProperty("java.io.tmpdir"), MavenRepository.toFileName(releaseId, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomText.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        KieMavenRepository.getKieMavenRepository().installArtifact(releaseId, kieModule, pomFile);

        //Build a second KieModule, depends on the first KieModule jar which we have deployed into Maven
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "metadata-test-using-pom", "1.0-SNAPSHOT");
        String pomText2 = getPom(releaseId2, releaseId);
        File pomFile2 = new File(System.getProperty("java.io.tmpdir"), MavenRepository.toFileName(releaseId2, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile2);
            fos.write(pomText2.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(pomFile2);
        //checkDroolsCoreDep(kieModuleMetaData);

        Collection<String> testClasses = kieModuleMetaData.getClasses("org.kie.test");
        assertThat(testClasses.size()).isEqualTo(1);
        assertThat(testClasses.iterator().next()).isEqualTo("Bean");
        Class<?> beanClass = kieModuleMetaData.getClass("org.kie.test", "Bean");
        assertThat(beanClass.getMethod("getValue")).isNotNull();

        if (useTypeDeclaration) {
            assertThat(kieModuleMetaData.getTypeMetaInfo(beanClass).isEvent()).isTrue();
        }
    }

    private void checkDependency(KieModuleMetaData kieModuleMetaData) throws NoSuchMethodException {
        assertThat(kieModuleMetaData.getClasses("org.kie.ci.test.dep").size()).isEqualTo(1);
        Class<?> mainClass = kieModuleMetaData.getClass("org.kie.ci.test.dep", "Main");
        assertThat(mainClass).hasDeclaredMethods("someCustomMethod");
    }

    private void testKieModuleMetaDataForDependenciesInMemory(boolean useTypeDeclaration) throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "metadata-test", "1.0-SNAPSHOT");

        InternalKieModule kieModule = createKieJarWithClass(ks, releaseId, useTypeDeclaration, 2, 7, getTestDependencyJarReleaseId());
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        checkDependency(kieModuleMetaData);

        Collection<String> classes = kieModuleMetaData.getClasses("org.kie.ci.test.dep");
        assertThat(classes.size()).isEqualTo(1);
        Class<?> beanClass = kieModuleMetaData.getClass("org.kie.ci.test.dep", "Main");
        assertThat(beanClass).isNotNull();

        //Classes in dependencies should have TypeMetaInfo
        TypeMetaInfo beanTypeInfo = kieModuleMetaData.getTypeMetaInfo(beanClass);
        assertThat(beanTypeInfo).isNotNull();

        if (useTypeDeclaration) {
            assertThat(beanTypeInfo.isEvent()).isTrue();
        }

        assertThat(beanTypeInfo.isDeclaredType()).isEqualTo(useTypeDeclaration);
    }

    @Test
    public void testKieMavenPluginEmptyProject() {
        // According to https://bugzilla.redhat.com/show_bug.cgi?id=1049674#c2 the below is the minimal POM required to use KieMavenPlugin.
        final KieServices ks = KieServices.Factory.get();

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("pom.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                        + "  <modelVersion>4.0.0</modelVersion>"
                        + "  <groupId>org.kie</groupId>"
                        + "  <artifactId>plugin-test</artifactId>"
                        + "  <version>1.0</version>"
                        + "  <packaging>kjar</packaging>"
                        + "  <build>"
                        + "    <plugins>"
                        + "      <plugin>"
                        + "        <groupId>org.kie</groupId>"
                        + "        <artifactId>kie-maven-plugin</artifactId>"
                        + "        <version>the-test-does-not-need-proper-version-here</version>"
                        + "        <extensions>true</extensions>"
                        + "      </plugin>"
                        + "    </plugins>"
                        + "  </build>"
                        + "</project>");

        kfs.write("/src/main/resources/META-INF/kmodule.xml",
                "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>");

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        assertThat(messages.isEmpty()).isTrue();

        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);

        boolean fail = false;
        for (final String packageName : kieModuleMetaData.getPackages()) {
            for (final String className : kieModuleMetaData.getClasses(packageName)) {
                try {
                    kieModuleMetaData.getClass(packageName, className);
                } catch (Throwable e) {
                    fail = true;
                    System.out.println(e);
                }
            }
        }
        if (fail) {
            fail("See console for details.");
        }
    }

    @Test
    public void loadClassInJarKieModuleMetaData() {
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId("org.kie.ci.test", "kie-ci-test-jar", "1.2.3.Final");
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newInJarKieModuleMetaData(releaseId, DependencyFilter.COMPILE_FILTER);
        Class<?> sessionClockClass = kieModuleMetaData.getClass("org.kie.ci.test", "Main");
        assertThat(sessionClockClass).isNotNull();
    }
}
