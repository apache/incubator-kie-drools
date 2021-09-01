/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.classloader;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.junit.Test;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class DMNEvalHelperAccessorCacheTest extends BaseInterpretedVsCompiledTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNEvalHelperAccessorCacheTest.class);

    public DMNEvalHelperAccessorCacheTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testClassloaderFunctionInvocation() throws Exception {
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
        final ReleaseId kjarReleaseId = ks.newReleaseId("org.kie.dmn.core.classloader", "DMNEvalHelperAccessorCacheTest-kjar1", UUID.randomUUID().toString());

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/java/org/acme/Person.java", javaSource);
        kfs.write(ks.getResources().newClassPathResource("personCL.dmn", this.getClass()));
        kfs.generateAndWritePomXML(kjarReleaseId);

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertTrue(kieBuilder.getResults().getMessages().toString(), kieBuilder.getResults().getMessages().isEmpty());

        return ks.newKieContainer(kjarReleaseId);
    }

    private void checkKieContainer1(KieContainer container) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        final DMNRuntime runtime = KieRuntimeFactory.of(container.getKieBase()).get(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_78BDCBE4-32EA-486E-9D81-CCC0D2378C61", "personCL");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final Object johnDoePerson = container.getClassLoader().loadClass("org.acme.Person").getConstructor(String.class, String.class).newInstance("John", "Doe");

        final DMNContext context = DMNFactory.newContext();
        context.set("my person", johnDoePerson);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.info("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision-1"), is("Hello, John Doe"));
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
        final ReleaseId kjarReleaseId2 = ks.newReleaseId("org.kie.dmn.core.classloader", "DMNEvalHelperAccessorCacheTest-kjar2", UUID.randomUUID().toString());

        final KieFileSystem kfs2 = ks.newKieFileSystem();
        kfs2.write("src/main/java/org/acme/Person.java", javaSource2);
        kfs2.write(ks.getResources().newClassPathResource("personCL2.dmn", this.getClass()));
        kfs2.generateAndWritePomXML(kjarReleaseId2);

        final KieBuilder kieBuilder2 = ks.newKieBuilder(kfs2).buildAll();
        assertTrue(kieBuilder2.getResults().getMessages().toString(), kieBuilder2.getResults().getMessages().isEmpty());

        final KieContainer container2 = ks.newKieContainer(kjarReleaseId2);
        return container2;
    }

    private void checkKieContainer2(KieContainer container2) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        final DMNRuntime runtime2 = KieRuntimeFactory.of(container2.getKieBase()).get(DMNRuntime.class);
        final DMNModel dmnModel2 = runtime2.getModel("ns2", "personCL2");

        final Object johnDoePerson2 = container2.getClassLoader().loadClass("org.acme.Person").getConstructor(String.class).newInstance("John Doe");
        final DMNContext context2 = DMNFactory.newContext();
        context2.set("my person", johnDoePerson2);

        final DMNResult dmnResult2 = runtime2.evaluateAll(dmnModel2, context2);
        LOG.info("{}", dmnResult2);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult2.getMessages()), dmnResult2.hasErrors(), is(false));

        final DMNContext result = dmnResult2.getContext();
        assertThat(result.get("Decision-1"), is("Hello, John Doe"));
    }

}