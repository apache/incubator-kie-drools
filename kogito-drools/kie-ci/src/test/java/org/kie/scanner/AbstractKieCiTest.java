package org.kie.scanner;

import org.drools.core.util.FileManager;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.ReleaseId;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class AbstractKieCiTest {

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, boolean isdefault, String... rules) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks, isdefault);
        kfs.writePomXML(getPom(releaseId));

        for (String rule : rules) {
            String file = "org/test/" + rule + ".drl";
            kfs.write("src/main/resources/KBase1/" + file, createDRL(rule));
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    protected InternalKieModule createKieJarWithDependencies(KieServices ks, ReleaseId releaseId, boolean isdefault, String rule, ReleaseId... dependencies) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks, isdefault);
        kfs.writePomXML(getPom(releaseId, dependencies));

        String file = "org/test/" + rule + ".drl";
        kfs.write("src/main/resources/KBase1/" + file, createDRL(rule));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, String pomXml, boolean isdefault,  String... rules) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks, isdefault);
        kfs.writePomXML(pomXml);


        for (String rule : rules) {
            String file = "org/test/" + rule + ".drl";
            kfs.write("src/main/resources/KBase1/" + file, createDRL(rule));
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, String... rules) throws IOException {
        return createKieJar(ks, releaseId, false, rules);
    }

    protected InternalKieModule createKieJarWithClass(KieServices ks, ReleaseId releaseId, boolean useTypeDeclaration, int value, int factor, ReleaseId... dependencies) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks, false);
        kfs.writePomXML(getPom(releaseId, dependencies));

        if (useTypeDeclaration) {
            kfs.write("src/main/resources/KBase1/rule1.drl", createDRLWithTypeDeclaration(value, factor));
        } else {
            kfs.write("src/main/resources/KBase1/rule1.drl", createDRLForJavaSource(value))
                    .write("src/main/java/org/kie/test/Bean.java", createJavaSource(factor));
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
        return createKieFileSystemWithKProject(ks, false);
    }

    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks, boolean isdefault) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1").setDefault(isdefault)
                .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
                .setEventProcessingMode(EventProcessingOption.STREAM);

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1").setDefault(isdefault)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType(ClockTypeOption.get("realtime"));

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }

    protected String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                        "  <modelVersion>4.0.0</modelVersion>\n" +
                        "\n" +
                        "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                        "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                        "  <version>" + releaseId.getVersion() + "</version>\n" +
                        "\n";
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

    protected String createDRL(String ruleName) {
        return "package org.kie.test\n" +
                "global java.util.List list\n" +
                "rule " + ruleName + "\n" +
                "when\n" +
                "then\n" +
                "list.add( drools.getRule().getName() );\n" +
                "end\n";
    }

    private String createJavaSource(int factor) {
        return "package org.kie.test;\n" +
                "import org.kie.api.definition.type.Role;\n" +
                "@Role(Role.Type.EVENT)\n" +
                "public class Bean {\n" +
                "   private final int value;\n" +
                "   public Bean(int value) {\n" +
                "       this.value = value;\n" +
                "   }\n" +
                "   public int getValue() {\n" +
                "       return value * " + factor + ";\n" +
                "   }\n" +
                "}";
    }

    private String createDRLForJavaSource(int value) {
        return "package org.kie.test\n" +
                "global java.util.List list\n" +
                "rule Init salience 100\n" +
                "when\n" +
                "then\n" +
                "insert( new Bean(" + value + ") );\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "   $b : Bean( value > 0 )\n" +
                "then\n" +
                "   list.add( $b.getValue() );\n" +
                "end\n";
    }

    protected String createDRLWithTypeDeclaration(int value, int factor) {
        return "package org.kie.test\n" +
                getDRLWithType() +
                getDRLWithRules(value, factor);
    }

    protected String getDRLWithType() {
        return "declare Bean @role(event)\n" +
                "   value : int\n" +
                "end\n";
    }

    protected String getDRLWithRules(int value, int factor) {
        return "global java.util.List list\n" +
                "rule Init salience 100\n" +
                "when\n" +
                "then\n" +
                "insert( new Bean(" + value + ") );\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "   $b : Bean()\n" +
                "then\n" +
                "   list.add( $b.getValue() * " + factor + " );\n" +
                "end\n";
    }

    public static byte[] createKJar(KieServices ks,
            ReleaseId releaseId,
            String pom,
            String... drls) {
        KieFileSystem kfs = ks.newKieFileSystem();
        if (pom != null) {
            kfs.write("pom.xml", pom);
        } else {
            kfs.generateAndWritePomXML(releaseId);
        }
        for (int i = 0; i < drls.length; i++) {
            if (drls[i] != null) {
                kfs.write("src/main/resources/r" + i + ".drl", drls[i]);
            }
        }
        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        if (kb.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            for (org.kie.api.builder.Message result : kb.getResults().getMessages()) {
                System.out.println(result.getText());
            }
            return null;
        }
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository()
                .getKieModule(releaseId);
        byte[] jar = kieModule.getBytes();
        return jar;
    }

    public static KieModule deployJar(KieServices ks, byte[] jar) {
        // Deploy jar into the repository
        Resource jarRes = ks.getResources().newByteArrayResource(jar);
        KieModule km = ks.getRepository().addKieModule(jarRes);
        return km;
    }

    protected File createKPom(FileManager fileManager, ReleaseId releaseId, ReleaseId... dependencies) throws IOException {
        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, getPom(releaseId, dependencies));
        return pomFile;
    }
}
