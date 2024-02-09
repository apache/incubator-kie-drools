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

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;

import static org.assertj.core.api.Assertions.assertThat;

// DROOLS-1044
@RunWith(Parameterized.class)
public class KieBaseIncludesTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public KieBaseIncludesTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    /**
     * Test the inclusion of a KieBase defined in one KJAR into the KieBase of another KJAR.
     * <p/>
     * The 2 KieBases use different package names for the rules (i.e. "rules" and "rules2").
     */
    @Test
    public void testKieBaseIncludesCrossKJarDifferentPackageNames() {

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
    @Test
    public void testKieBaseIncludesCrossKJarSamePackageNames() {

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
}
