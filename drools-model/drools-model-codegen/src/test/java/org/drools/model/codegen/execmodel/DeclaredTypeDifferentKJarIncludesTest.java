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

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.io.ByteArrayResource;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieSession;
import org.kie.util.maven.support.ReleaseIdImpl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.codegen.execmodel.BaseModelTest.RUN_TYPE.PATTERN_DSL;
import static org.drools.model.codegen.execmodel.BaseModelTest.RUN_TYPE.PATTERN_WITH_ALPHA_NETWORK;

// DROOLS-4188
public class DeclaredTypeDifferentKJarIncludesTest extends BaseModelTest {

    private final String CHILD_KBASE_NAME = "ChildKBase";
    private final String CHILD_KBASE_PACKAGE = "org.childkbase";
    private final ReleaseIdImpl CHILD_RELEASE_ID = new ReleaseIdImpl(CHILD_KBASE_PACKAGE, "childkbase", "1.0.0");

    private final String SUPER_KBASE_NAME = "SuperKbase";
    private final String SUPER_KBASE_PACKAGE = "org.superkbase";
    private final ReleaseIdImpl SUPER_RELEASE_ID = new ReleaseIdImpl(SUPER_KBASE_PACKAGE, "superkbase", "1.0.0");

    public DeclaredTypeDifferentKJarIncludesTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    private static final String SUPER_RULE = "package org.superkbase;\n" +
            "\n" +
            "declare MyType\n" +
            "    value: java.lang.String\n" +
            "end\n" +
            "\n" +
            "rule \"Will Convert a number to string\"\n" +
            "when\n" +
            "  $i: Integer()\n" +
            "then\n" +
            "  insert(new MyType(String.valueOf($i)));\n" +
            "  delete($i);\n" +
            "end\n";

    private static final String CHILD_RULE = "\n" +
            "package org.childkbase\n" +
            "\n" +
            "import org.superkbase.MyType;\n" +
            "\n" +
            "rule \"Will remove a string\"\n" +
            "when\n" +
            "  $s: MyType( value == \"10\")\n" +
            "then\n" +
            "    System.out.println(\"Firing!\");\n" +
            "  delete($s);\n" +
            "end\n";

    @Test
    public void testChildIncludingSuper() {
        KieBase kBase = createKieBase();

        KieSession newSuperKieBase = kBase.newKieSession();
        newSuperKieBase.insert(10);

        int numberOfRulesFired = newSuperKieBase.fireAllRules();
        assertThat(numberOfRulesFired).isEqualTo(2);
        assertThat(newSuperKieBase.getObjects().size()).isEqualTo(0);
    }

    private KieBase createKieBase() {
        KieServices kieServices = KieServices.Factory.get();

        superKieBase(kieServices);
        childKieBase(kieServices);

        return kieServices.newKieContainer(CHILD_RELEASE_ID).getKieBase(CHILD_KBASE_NAME);
    }

    private void superKieBase(KieServices kieServices) {
        KieModuleModel superKModule = kieServices.newKieModuleModel();
        KieBaseModel superKieBase = superKModule.newKieBaseModel(SUPER_KBASE_NAME);

        superKieBase.addPackage(SUPER_KBASE_PACKAGE);

        KieFileSystem superFileSystem = kieServices.newKieFileSystem();

        ByteArrayResource rule = new ByteArrayResource(SUPER_RULE.getBytes(UTF_8), UTF_8.name());
        superFileSystem.write("src/main/resources/org/superkbase/superrules.drl", rule);


        superFileSystem.writeKModuleXML(superKModule.toXML());
        superFileSystem.write("pom.xml", generatePomXmlWithDependencies(SUPER_RELEASE_ID));

        kieServices.newKieBuilder(superFileSystem).buildAll(buildProjectClass());
    }

    private void childKieBase(KieServices kieServices) {
        KieModuleModel childKModule = kieServices.newKieModuleModel();
        KieBaseModel childKbase = childKModule.newKieBaseModel(CHILD_KBASE_NAME)
                .setDefault(true)
                .addInclude(SUPER_KBASE_NAME);

        childKbase.addPackage(CHILD_KBASE_PACKAGE);

        KieFileSystem childFileSystem = kieServices.newKieFileSystem();

        ByteArrayResource rule = new ByteArrayResource(CHILD_RULE.getBytes(UTF_8), UTF_8.name());
        childFileSystem.write("src/main/resources/org/childkbase/childrules.drl", rule);

        childFileSystem.writeKModuleXML(childKModule.toXML());
        childFileSystem.write("pom.xml", generatePomXmlWithDependencies(CHILD_RELEASE_ID, SUPER_RELEASE_ID));

        kieServices.newKieBuilder(childFileSystem).buildAll(buildProjectClass());
    }

    private static String generatePomXmlWithDependencies(ReleaseId releaseId, ReleaseId... dependencies) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n");
        sBuilder.append("         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"> \n");
        sBuilder.append("    <modelVersion>4.0.0</modelVersion> \n");

        toGAV(releaseId, sBuilder);

        sBuilder.append("    <packaging>jar</packaging> \n");
        sBuilder.append("    <name>Default</name> \n");
        sBuilder.append("    <dependencies> \n");

        for(ReleaseId d : dependencies) {
            sBuilder.append("    <dependency> \n");
            toGAV(d, sBuilder);
            sBuilder.append("    </dependency> \n");
        }

        sBuilder.append("    </dependencies> \n");
        sBuilder.append("</project>  \n");

        return sBuilder.toString();
    }

    private static void toGAV(ReleaseId releaseId, StringBuilder sBuilder) {
        sBuilder.append("    <groupId>");
        sBuilder.append(releaseId.getGroupId());
        sBuilder.append("</groupId> \n");

        sBuilder.append("    <artifactId>");
        sBuilder.append(releaseId.getArtifactId());
        sBuilder.append("</artifactId> \n");

        sBuilder.append("    <version>");
        sBuilder.append(releaseId.getVersion());
        sBuilder.append("</version> \n");
    }

    private Class<? extends KieBuilder.ProjectType> buildProjectClass() {
        if (asList(PATTERN_DSL, PATTERN_WITH_ALPHA_NETWORK).contains(testRunType)) {
            return ExecutableModelProject.class;
        } else {
            return DrlProject.class;
        }
    }
}
