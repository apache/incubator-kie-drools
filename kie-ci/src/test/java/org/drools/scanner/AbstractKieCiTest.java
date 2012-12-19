package org.drools.scanner;

import org.kie.KieServices;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieSessionModel;
import org.kie.builder.ReleaseId;
import org.kie.builder.impl.InternalKieModule;
import org.kie.conf.EqualityBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.conf.ClockTypeOption;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class AbstractKieCiTest {

    protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, String... rules) throws IOException {
        KieFileSystem kfs = ks.newKieFileSystem();
        for (String rule : rules) {
            String file = "org/test/" + rule + ".drl";
            kfs.write("src/main/resources/KBase1/" + file, createDRL(rule));
        }

        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") );

        kfs.writeKModuleXML(kproj.toXML());
        kfs.writePomXML( getPom(releaseId) );

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return ( InternalKieModule ) kieBuilder.getKieModule();
    }

    protected InternalKieModule createKieJarWithClass(KieServices ks, ReleaseId releaseId, int value, int factor, ReleaseId... dependencies) throws IOException {
        KieFileSystem kieFileSystem = ks.newKieFileSystem();

        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType( ClockTypeOption.get("realtime") );

        kieFileSystem
                .writeKModuleXML(kproj.toXML())
                .writePomXML(getPom(releaseId, dependencies))
                .write("src/main/resources/" + kieBaseModel1.getName() + "/rule1.drl", createDRLForJavaSource(value))
                .write("src/main/java/org/kie/test/Bean.java", createJavaSource(factor));

        KieBuilder kieBuilder = ks.newKieBuilder(kieFileSystem);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return ( InternalKieModule ) kieBuilder.getKieModule();
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
                //"import org.kie.test.Bean;\n" +
                "global java.util.List list\n" +
                "rule Init\n" +
                "when\n" +
                "then\n" +
                "insert( new Bean(" + value + ") );\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "   $b : Bean()\n" +
                "then\n" +
                "   list.add( $b.getValue() );\n" +
                "end\n";
    }
}
