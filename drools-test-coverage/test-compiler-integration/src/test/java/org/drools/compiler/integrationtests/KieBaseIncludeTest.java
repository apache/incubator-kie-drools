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
package org.drools.compiler.integrationtests;

import java.io.File;
import java.io.IOException;

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.FileManager;
import org.drools.model.codegen.ExecutableModelProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class KieBaseIncludeTest {
    private final Class<? extends KieBuilder.ProjectType> projectType;

    public KieBaseIncludeTest(boolean useModel) {
        this.projectType = useModel ? ExecutableModelProject.class : DrlProject.class;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[] {false, true};
    }

    @Test
    public void testKJarIncludedDependency() throws Exception {
        String rule = "package org.test rule R when String() then end";

        KieServices ks = KieServices.Factory.get();

        ReleaseId includedReleaseId = KieServices.Factory.get().newReleaseId( "org.kie", "test-project", "1.0.0-SNAPSHOT" );

        FileManager fileManager = new FileManager();
        fileManager.setUp();

        InternalKieModule kJar1 = createKieJar( ks, includedReleaseId, rule );

        fileManager.tearDown();
        fileManager = new FileManager();
        fileManager.setUp();

        ReleaseId containerReleaseId = KieServices.Factory.get().newReleaseId( "org.kie", "test-container", "1.0.0-SNAPSHOT" );
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel( "KBase2" ).addInclude( "KBase1" ).newKieSessionModel( "KSession2" );
        kfs.writeKModuleXML( kproj.toXML() );
        kfs.writePomXML( getPom( containerReleaseId, includedReleaseId ) );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        assertThat(kieBuilder.buildAll(projectType).getResults().getMessages().isEmpty()).isTrue();
        InternalKieModule containerKJar = ( InternalKieModule ) kieBuilder.getKieModule();

        KieContainer kieContainer = ks.newKieContainer( containerReleaseId );
        KieSession ksession = kieContainer.newKieSession( "KSession2" );
        ksession.insert( "test" );
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        ksession.dispose();
        fileManager.tearDown();
    }

    private InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, String... rules) throws IOException {
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1");
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1");

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());

        kfs.writePomXML(getPom(releaseId));

        for (int i = 0; i < rules.length; i++) {
            String file = "org/test/r" + i + ".drl";
            kfs.write("src/main/resources/KBase1/" + file, rules[i]);
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertThat(kieBuilder.buildAll(projectType).getResults().getMessages().isEmpty()).isTrue();
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    private File createKPom( FileManager fileManager, ReleaseId releaseId, ReleaseId... dependencies) throws IOException {
        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, getPom(releaseId, dependencies));
        return pomFile;
    }

    private String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
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
}
