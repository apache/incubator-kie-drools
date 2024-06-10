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
package org.kie.dmn.core.classloader;

import java.util.UUID;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNEvalHelperAccessorCacheTest extends BaseInterpretedVsCompiledTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNEvalHelperAccessorCacheTest.class);


    @ParameterizedTest
    @MethodSource("params")
    void classloaderFunctionInvocation(boolean useExecModelCompiler) throws Exception {
        init(useExecModelCompiler);
        final KieServices ks = KieServices.Factory.get();

        final KieContainer container1 = createKieContainer1(ks);
        final KieContainer container2 = createKieContainer2(ks);

        checkKieContainer1(container1);
        checkKieContainer2(container2);
    }

    private KieContainer createKieContainer1(KieServices ks) {
        final String javaSource = "package org.acme;\n" +
                "\n" +
                "import org.kie.dmn.feel.lang.FEELProperty;\n" +
                "\n" +
                "public class Person {\n" +
                "    private String firstName;\n" +
                "    private String lastName;\n" +
                "    private int age;\n" +
                "    \n" +
                "    public Person(String firstName, String lastName) {\n" +
                "        super();\n" +
                "        this.firstName = firstName;\n" +
                "        this.lastName = lastName;\n" +
                "    }\n" +
                "\n" +
                "    public Person(String firstName, String lastName, int age) {\n" +
                "        this(firstName, lastName);\n" +
                "        this.setAge(age);\n" +
                "    }\n" +
                "\n" +
                "    @FEELProperty(\"first name\")\n" +
                "    public String getFirstName() {\n" +
                "        return firstName;\n" +
                "    }\n" +
                "    \n" +
                "    public void setFirstName(String firstName) {\n" +
                "        this.firstName = firstName;\n" +
                "    }\n" +
                "    \n" +
                "    @FEELProperty(\"last name\")\n" +
                "    public String getLastName() {\n" +
                "        return lastName;\n" +
                "    }\n" +
                "    \n" +
                "    public void setLastName(String lastName) {\n" +
                "        this.lastName = lastName;   \n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public String toString() {\n" +
                "        StringBuilder builder = new StringBuilder();\n" +
                "        builder.append(\"Person [firstName=\").append(firstName).append(\", lastName=\").append(lastName).append(\"]\");\n" +
                "        return builder.toString();\n" +
                "    }\n" +
                "\n" +
                "    public int getAge() {\n" +
                "        return age;\n" +
                "    }\n" +
                "\n" +
                "    public void setAge(int age) {\n" +
                "        this.age = age;\n" +
                "    }\n" +
                "    \n" +
                "}";

        return createKieContainer(ks, javaSource, "DMNEvalHelperAccessorCacheTest-kjar1", "personCL.dmn");
    }

    private void checkKieContainer1(KieContainer container) throws Exception {
        final DMNRuntime runtime = KieRuntimeFactory.of(container.getKieBase()).get(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_78BDCBE4-32EA-486E-9D81-CCC0D2378C61", "personCL");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final Object johnDoePerson = container.getClassLoader().loadClass("org.acme.Person").getConstructor(String.class, String.class).newInstance("John", "Doe");

        checkDMNEvaluation(runtime, dmnModel, johnDoePerson);
    }

    private KieContainer createKieContainer2(KieServices ks) {
        final String javaSource2 = "package org.acme;\n" +
                                   "\n" +
                                   "import org.kie.dmn.feel.lang.FEELProperty;\n" +
                                   "\n" +
                                   "public class Person {\n" +
                                   "    private String firstName;\n" +
                                   "    \n" +
                                   "    public Person(String firstName) {\n" +
                                   "        this.firstName = firstName;\n" +
                                   "    }\n" +
                                   "\n" +
                                   "    @FEELProperty(\"first name\")\n" +
                                   "    public String getFirstName() {\n" +
                                   "        return firstName;\n" +
                                   "    }\n" +
                                   "    \n" +
                                   "    public void setFirstName(String firstName) {\n" +
                                   "        this.firstName = firstName;\n" +
                                   "    }\n" +
                                   "\n" +
                                   "    @Override\n" +
                                   "    public String toString() {\n" +
                                   "        StringBuilder builder = new StringBuilder();\n" +
                                   "        builder.append(\"Person [firstName=\").append(firstName).append(\"]\");\n" +
                                   "        return builder.toString();\n" +
                                   "    }\n" +
                                   "}";

        return createKieContainer(ks, javaSource2, "DMNEvalHelperAccessorCacheTest-kjar2", "personCL2.dmn");
    }

    private KieContainer createKieContainer(KieServices ks, String javaSourcePerson, String artifactID, String dmnModelFileName) {
        final ReleaseId kjarReleaseId2 = ks.newReleaseId("org.kie.dmn.core.classloader", artifactID, UUID.randomUUID().toString());

        final KieFileSystem kfs2 = ks.newKieFileSystem();
        kfs2.write("src/main/java/org/acme/Person.java", javaSourcePerson);
        kfs2.write(ks.getResources().newClassPathResource(dmnModelFileName, this.getClass()));
        kfs2.generateAndWritePomXML(kjarReleaseId2);

        final KieBuilder kieBuilder2 = ks.newKieBuilder(kfs2).buildAll();
        assertThat(kieBuilder2.getResults().getMessages()).as(kieBuilder2.getResults().getMessages().toString()).isEmpty();

        final KieContainer container2 = ks.newKieContainer(kjarReleaseId2);
        return container2;
    }

    private void checkKieContainer2(KieContainer container2) throws Exception {
        final DMNRuntime runtime2 = KieRuntimeFactory.of(container2.getKieBase()).get(DMNRuntime.class);
        final DMNModel dmnModel2 = runtime2.getModel("ns2", "personCL2");
        assertThat(dmnModel2).isNotNull();
        assertThat(dmnModel2.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel2.getMessages())).isFalse();

        final Object johnDoePerson2 = container2.getClassLoader().loadClass("org.acme.Person").getConstructor(String.class).newInstance("John Doe");

        checkDMNEvaluation(runtime2, dmnModel2, johnDoePerson2);
    }

    private void checkDMNEvaluation(final DMNRuntime runtime2, final DMNModel dmnModel2, final Object johnDoePerson) {
        final DMNContext context2 = DMNFactory.newContext();
        context2.set("my person", johnDoePerson);

        final DMNResult dmnResult2 = runtime2.evaluateAll(dmnModel2, context2);
        LOG.debug("{}", dmnResult2);
        assertThat(dmnResult2.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult2.getMessages())).isFalse();

        final DMNContext result = dmnResult2.getContext();
        assertThat(result.get("Decision-1")).isEqualTo("Hello, John Doe");
    }

}