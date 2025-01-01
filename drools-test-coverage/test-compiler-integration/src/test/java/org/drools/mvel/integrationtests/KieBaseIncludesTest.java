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
package org.drools.mvel.integrationtests;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

// DROOLS-1044
public class KieBaseIncludesTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    /**
     * Test the inclusion of a KieBase defined in one KJAR into the KieBase of another KJAR.
     * <p/>
     * The 2 KieBases use different package names for the rules (i.e. "rules" and "rules2").
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testKieBaseIncludesCrossKJarDifferentPackageNames(KieBaseTestConfiguration kieBaseTestConfiguration) {

        // @formatter:off
        String pomContent1 = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                             "<modelVersion>4.0.0</modelVersion>\n" +
                             "<groupId>org.kie</groupId>\n" +
                             "<artifactId>rules-1</artifactId>\n" +
                             "<version>1.0.0</version>\n" +
                             "<packaging>jar</packaging>\n" +
                             "<dependencies>\n" +
                             "<dependency>\n" +
                             "<groupId>org.kie</groupId>\n" +
                             "<artifactId>rules-2</artifactId>\n" +
                             "<version>1.0.0</version>\n" +
                             "</dependency>\n" +
                             "</dependencies>\n" +
                             "</project>\n";


        String kmoduleContent1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                 "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                                 "<kbase name=\"kbase1\" equalsBehavior=\"equality\" default=\"true\" packages=\"rules\" includes=\"kbase2\">\n" +
                                 "<ksession name=\"ksession1\" default=\"true\" type=\"stateful\"/>\n" +
                                 "</kbase>\n"+
                                 "</kmodule>";

        String drl1 = "package rules\n" +
                      "\n" +
                      "rule \"Rule in KieBase 1\"\n" +
                      "when\n" +
                      "then\n" +
                      "System.out.println(\"Rule in KieBase1\");\n" +
                      "end";


        String kmoduleContent2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                 "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                                 "<kbase name=\"kbase2\" equalsBehavior=\"equality\" default=\"false\" packages=\"rules2\">\n" +
                                 "<ksession name=\"ksession2\" default=\"false\" type=\"stateful\"/>\n" +
                                 "</kbase>\n"+
                                 "</kmodule>";

        String drl2 = "package rules2\n" +
                      "\n" +
                      "rule \"Rule in KieBase 2\"\n" +
                      "when\n" +
                      "then\n" +
                      "System.out.println(\"Rule in KieBase2\");\n" +
                      "end";

        // @formatter:on
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "rules-1", "1.0.0" );
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "rules-2", "1.0.0");

        //First deploy the second KJAR on which the first one depends.
        KieFileSystem kfs2 = ks.newKieFileSystem()
                               .generateAndWritePomXML(releaseId2)
                               .write("src/main/resources/rules2/rules.drl", drl2)
                               .writeKModuleXML(kmoduleContent2);

        KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs2, true);

        KieFileSystem kfs1 = ks.newKieFileSystem()
                               //.generateAndWritePomXML(releaseId1)
                               .writePomXML(pomContent1)
                               .write("src/main/resources/rules/rules.drl", drl1)
                               .writeKModuleXML(kmoduleContent1);

        KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs1, true);

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieBase kieBase = kc.getKieBase();

        // Assert the number of rules in the KieBase.
        long nrOfRules = getNumberOfRules(kieBase);

        // We should have 2 rules in our KieBase. One from our own DRL and one from the DRL in the KieBase we've included.
        assertThat(nrOfRules).isEqualTo(2);
    }

    /**
     * Test the inclusion of a KieBase defined in one KJAR into the KieBase of another KJAR.
     * <p/>
     * The 2 KieBases use the same package names for the rules (i.e. "rules").
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testKieBaseIncludesCrossKJarSamePackageNames(KieBaseTestConfiguration kieBaseTestConfiguration) {

        // @formatter:off
        String pomContent1 = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                             "<modelVersion>4.0.0</modelVersion>\n" +
                             "<groupId>org.kie</groupId>\n" +
                             "<artifactId>rules-1</artifactId>\n" +
                             "<version>1.0.0</version>\n" +
                             "<packaging>jar</packaging>\n" +
                             "<dependencies>\n" +
                             "<dependency>\n" +
                             "<groupId>org.kie</groupId>\n" +
                             "<artifactId>rules-2</artifactId>\n" +
                             "<version>1.0.0</version>\n" +
                             "</dependency>\n" +
                             "</dependencies>\n" +
                             "</project>\n";


        String kmoduleContent1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                 "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                                 "<kbase name=\"kbase1\" equalsBehavior=\"equality\" default=\"true\" packages=\"rules\" includes=\"kbase2\">\n" +
                                 "<ksession name=\"ksession1\" default=\"true\" type=\"stateful\"/>\n" +
                                 "</kbase>\n"+
                                 "</kmodule>";

        String drl1 = "package rules\n" +
                      "\n" +
                      "rule \"Rule in KieBase 1\"\n" +
                      "when\n" +
                      "then\n" +
                      "System.out.println(\"Rule in KieBase1\");\n" +
                      "end";


        String kmoduleContent2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                 "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                                 "<kbase name=\"kbase2\" equalsBehavior=\"equality\" default=\"false\" packages=\"rules\">\n" +
                                 "<ksession name=\"ksession2\" default=\"false\" type=\"stateful\"/>\n" +
                                 "</kbase>\n"+
                                 "</kmodule>";

        String drl2 = "package rules\n" +
                      "\n" +
                      "rule \"Rule in KieBase 2\"\n" +
                      "when\n" +
                      "then\n" +
                      "System.out.println(\"Rule in KieBase2\");\n" +
                      "end";

        // @formatter:on
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "rules-1", "1.0.0");
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "rules-2", "1.0.0");

        //First deploy the second KJAR on which the first one depends.
        KieFileSystem kfs2 = ks.newKieFileSystem()
                               .generateAndWritePomXML(releaseId2)
                               .write("src/main/resources/rules/rules.drl", drl2)
                               .writeKModuleXML(kmoduleContent2);

        KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs2, true);

        KieFileSystem kfs1 = ks.newKieFileSystem()
                               //.generateAndWritePomXML(releaseId1)
                               .writePomXML(pomContent1)
                               .write("src/main/resources/rules/rules.drl", drl1)
                               .writeKModuleXML(kmoduleContent1);

        KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs1, true);

        KieContainer kc = ks.newKieContainer(releaseId1);
        KieBase kieBase = kc.getKieBase();

        // Assert the number of rules in the KieBase.
        long nrOfRules = getNumberOfRules(kieBase);

        // We should have 2 rules in our KieBase. One from our own DRL and one from the DRL in the KieBase we've included.
        assertThat(nrOfRules).isEqualTo(2);
    }

    /**
     * Helper method which determines the number of rules in the {@link KieBase}.
     *
     * @param kieBase
     *            the {@link KieBase}
     * @return the number of rules in the {@link KieBase}
     */
    private static long getNumberOfRules(KieBase kieBase) {
        long nrOfRules = 0;

        Collection<KiePackage> kiePackages = kieBase.getKiePackages();
        for (KiePackage nextKiePackage : kiePackages) {
            Collection<Rule> rules = nextKiePackage.getRules();
            System.out.println(rules);
            nrOfRules += rules.size();
        }
        return nrOfRules;
    }

    /**
     * Test the inclusion of a KieBase defined in one KJAR into the KieBase of another KJAR.
     * <p/>
     * The 2 KieBases use the duplicate rule names, so an error should be reported
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void kieBaseIncludesCrossKJarDuplicateRuleNames_shouldReportError(KieBaseTestConfiguration kieBaseTestConfiguration) throws IOException {

        String pomContentMain = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "<modelVersion>4.0.0</modelVersion>\n" +
                "<groupId>org.kie</groupId>\n" +
                "<artifactId>rules-main</artifactId>\n" +
                "<version>1.0.0</version>\n" +
                "<packaging>jar</packaging>\n" +
                "<dependencies>\n" +
                "<dependency>\n" +
                "<groupId>org.kie</groupId>\n" +
                "<artifactId>rules-sub</artifactId>\n" +
                "<version>1.0.0</version>\n" +
                "</dependency>\n" +
                "</dependencies>\n" +
                "</project>\n";

        String kmoduleContentMain = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                "<kbase name=\"kbaseMain\" equalsBehavior=\"equality\" default=\"true\" packages=\"rules\" includes=\"kbaseSub\">\n" +
                "<ksession name=\"ksessionMain\" default=\"true\" type=\"stateful\"/>\n" +
                "</kbase>\n" +
                "</kmodule>";

        String drlMain = "package rules\n" +
                "\n" +
                "rule \"RuleA\"\n" +
                "when\n" +
                "then\n" +
                "System.out.println(\"Rule in KieBaseMain\");\n" +
                "end";

        String kmoduleContentSub = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                "<kbase name=\"kbaseSub\" equalsBehavior=\"equality\" default=\"false\" packages=\"rules\">\n" +
                "<ksession name=\"ksessionSub\" default=\"false\" type=\"stateful\"/>\n" +
                "</kbase>\n" +
                "</kmodule>";

        String drlSub = "package rules\n" +
                "\n" +
                "rule \"RuleA\"\n" +
                "when\n" +
                "then\n" +
                "System.out.println(\"Rule in KieBaseSub\");\n" +
                "end";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseIdSub = ks.newReleaseId("org.kie", "rules-sub", "1.0.0");

        //First deploy the second KJAR on which the first one depends.
        KieFileSystem kfsSub = ks.newKieFileSystem()
                .generateAndWritePomXML(releaseIdSub)
                .write("src/main/resources/rules/rules.drl", drlSub)
                .writeKModuleXML(kmoduleContentSub);

        KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfsSub, true);

        KieFileSystem kfsMain = ks.newKieFileSystem()
                .writePomXML(pomContentMain)
                .write("src/main/resources/rules/rules.drl", drlMain)
                .writeKModuleXML(kmoduleContentMain);

        KieBuilder kieBuilderMain = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfsMain, false);
        List<Message> messages = kieBuilderMain.getResults().getMessages(Message.Level.ERROR);

        assertThat(messages).as("Duplication error should be reported")
                .extracting(Message::getText).anyMatch(text -> text.contains("Duplicate rule name"));
    }

    /**
     * One KieBase that includes another KieBase from the same KJAR. Not duplicate names.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void kieBaseIncludesSameKJar(KieBaseTestConfiguration kieBaseTestConfiguration) {

        String pomContent = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "<modelVersion>4.0.0</modelVersion>\n" +
                "<groupId>org.kie</groupId>\n" +
                "<artifactId>rules-main-sub</artifactId>\n" +
                "<version>1.0.0</version>\n" +
                "<packaging>jar</packaging>\n" +
                "</project>\n";

        String kmoduleContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                "<kbase name=\"kbaseSub\" equalsBehavior=\"equality\" default=\"false\" packages=\"rules.sub\">\n" +
                "</kbase>\n" +
                "<kbase name=\"kbaseMain\" equalsBehavior=\"equality\" default=\"true\" packages=\"rules.main\" includes=\"kbaseSub\">\n" +
                "<ksession name=\"ksessionMain\" default=\"true\" type=\"stateful\"/>\n" +
                "</kbase>\n" +
                "</kmodule>";

        String drlMain = "package rules.main\n" +
                "\n" +
                "rule \"RuleA\"\n" +
                "when\n" +
                "  $s : String()\n" +
                "then\n" +
                "  System.out.println(\"Rule in KieBaseMain\");\n" +
                "end";

        String drlSub = "package rules.sub\n" +
                "\n" +
                "rule \"RuleB\"\n" +
                "when\n" +
                "  $s : String()\n" +
                "then\n" +
                "  System.out.println(\"Rule in KieBaseSub\");\n" +
                "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfsMain = ks.newKieFileSystem()
                .writePomXML(pomContent)
                .write("src/main/resources/rules/main/ruleMain.drl", drlMain)
                .write("src/main/resources/rules/sub/ruleSub.drl", drlSub)
                .writeKModuleXML(kmoduleContent);

        KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfsMain, true);
        ReleaseId releaseId = ks.newReleaseId("org.kie", "rules-main-sub", "1.0.0");
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieSession kieSession = kieContainer.newKieSession("ksessionMain");
        kieSession.insert("test");
        int fired = kieSession.fireAllRules();
        assertThat(fired).as("fire rules in main and sub").isEqualTo(2);
        kieSession.dispose();
    }

    /**
     * One KieBase that includes another KieBase from the same KJAR. Duplicate rule names.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void kieBaseIncludesSameKJarDuplicateRuleNames_shouldReportError(KieBaseTestConfiguration kieBaseTestConfiguration) {

        String pomContent = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "<modelVersion>4.0.0</modelVersion>\n" +
                "<groupId>org.kie</groupId>\n" +
                "<artifactId>rules-main-sub</artifactId>\n" +
                "<version>1.0.0</version>\n" +
                "<packaging>jar</packaging>\n" +
                "</project>\n";

        String kmoduleContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                "<kbase name=\"kbaseSub\" equalsBehavior=\"equality\" default=\"false\" packages=\"rules\">\n" +
                "</kbase>\n" +
                "<kbase name=\"kbaseMain\" equalsBehavior=\"equality\" default=\"true\" packages=\"rules\" includes=\"kbaseSub\">\n" +
                "<ksession name=\"ksessionMain\" default=\"true\" type=\"stateful\"/>\n" +
                "</kbase>\n" +
                "</kmodule>";

        String drlMain = "package rules\n" +
                "\n" +
                "rule \"RuleA\"\n" +
                "when\n" +
                "  $s : String()\n" +
                "then\n" +
                "  System.out.println(\"Rule in KieBaseMain\");\n" +
                "end";

        String drlSub = "package rules\n" + // same package, same rule name
                "\n" +
                "rule \"RuleA\"\n" +
                "when\n" +
                "  $s : String()\n" +
                "then\n" +
                "  System.out.println(\"Rule in KieBaseSub\");\n" +
                "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfsMain = ks.newKieFileSystem()
                .writePomXML(pomContent)
                .write("src/main/resources/rules/main/ruleMain.drl", drlMain)
                .write("src/main/resources/rules/sub/ruleSub.drl", drlSub)
                .writeKModuleXML(kmoduleContent);

        KieBuilder kieBuilderMain = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfsMain, false);
        List<Message> messages = kieBuilderMain.getResults().getMessages(Message.Level.ERROR);

        assertThat(messages).as("Duplication error should be reported")
                .extracting(Message::getText).anyMatch(text -> text.contains("Duplicate rule name"));
    }
}
