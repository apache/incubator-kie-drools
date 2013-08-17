package org.kie.scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

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
        String pomText = getPom(releaseId, null);
        File pomFile = new File(System.getProperty("java.io.tmpdir"), MavenRepository.toFileName(releaseId, null) + ".pom");
        try {
            FileOutputStream fos = new FileOutputStream(pomFile);
            fos.write(pomText.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MavenRepository.getMavenRepository().deployArtifact(releaseId, kJar1, pomFile);

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieBaseModel kbaseModel = ((KieContainerImpl) kieContainer).getKieProject().getDefaultKieBaseModel();
        assertNotNull("Default kbase was not found", kbaseModel);
        String kbaseName = kbaseModel.getName();
        assertEquals("KBase1", kbaseName);
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
        MavenRepository.getMavenRepository().deployArtifact(releaseId, kJar1, pomFile);

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieBaseModel kbaseModel = ((KieContainerImpl) kieContainer).getKieProject().getDefaultKieBaseModel();
        assertNotNull("Default kbase was not found", kbaseModel);
        String kbaseName = kbaseModel.getName();
        assertEquals("KBase1", kbaseName);
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
        byte[] messageClazz = ClassBuilderFactory.getDefaultBeanClassBuilder().buildClass(def);
        MemoryFileSystem mfs = new MemoryFileSystem();
        mfs.write(pojoNS.replace('.', '/') + "/" + className+".class", messageClazz);
        byte[] pomContent = generatePomXml(pojoID).getBytes();
        mfs.write("META-INF/maven/" + pojoID.getGroupId() + "/" + pojoID.getArtifactId() + "/pom.xml", pomContent);
        mfs.write("META-INF/maven/" + pojoID.getGroupId() + "/" + pojoID.getArtifactId() + "/pom.properties", generatePomProperties(pojoID).getBytes());
        byte[] pojojar = mfs.writeAsBytes();
        MavenRepository.getMavenRepository().deployArtifact(pojoID, pojojar, pomContent);

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
