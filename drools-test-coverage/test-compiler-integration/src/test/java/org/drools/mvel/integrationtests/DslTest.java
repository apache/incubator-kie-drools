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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.drl.parser.lang.Expander;
import org.drools.drl.parser.lang.dsl.DefaultExpanderResolver;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DslTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DslTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }
   
    @Test
    public void testMultiLineTemplates() throws Exception {
        final Reader source = new InputStreamReader( getClass().getResourceAsStream( "rule_with_expander_multiline.dslr" ) );
        final Reader dsl = new InputStreamReader( getClass().getResourceAsStream( "test_dsl_multiline.dsl" ) );
        Expander ex =  new DefaultExpanderResolver(dsl).get("*", null);
        String r = ex.expand(source);
        assertThat(r.trim()).isEqualTo("when Car(color==\"Red\") then doSomething();");
    }

    @Test
    public void testWithExpanderDSL() throws Exception {
        final Resource resource1 = KieServices.Factory.get().getResources().newClassPathResource("test_expander.dsl", getClass());
        resource1.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "test_expander.dsl");
        final Resource resource2 = KieServices.Factory.get().getResources().newClassPathResource("rule_with_expander_dsl.dslr", getClass());
        resource2.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "rule_with_expander_dsl.dslr");
        
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, false, resource1, resource2);

        checkDSLExpanderTest(kieBuilder);
    }

    private void checkDSLExpanderTest(KieBuilder kieBuilder) throws IOException, ClassNotFoundException {
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();

        // the compiled package
        final Collection<KiePackage> pkgs = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kieBuilder).getKiePackages();
        assertThat(pkgs.size()).isEqualTo(2);

        KieBase kbase = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kieBuilder);

        KieSession session = kbase.newKieSession();
        session.insert( new Person( "Bob",
                               "http://foo.bar" ) );
        session.insert( new Cheese( "stilton",
                               42 ) );

        final List messages = new ArrayList();
        session.setGlobal( "messages",
                      messages );
        session.fireAllRules();

        assertThat(messages.size()).isEqualTo(1);
    }

    @Test
    public void testWithExpanderMore() throws Exception {
        final Resource resource1 = KieServices.Factory.get().getResources().newClassPathResource("test_expander.dsl", getClass());
        resource1.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "test_expander.dsl");
        final Resource resource2 = KieServices.Factory.get().getResources().newClassPathResource("rule_with_expander_dsl_more.dslr", getClass());
        resource2.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "rule_with_expander_dsl_more.dslr");
        
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, false, resource1, resource2);

        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
        
        // the compiled package
        final Collection<KiePackage> pkgs = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kieBuilder).getKiePackages();
        assertThat(pkgs.size()).isEqualTo(2);

        KieBase kbase = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kieBuilder);

        KieSession session = kbase.newKieSession();
        session.insert( new Person( "rage" ) );
        session.insert( new Cheese( "cheddar",
                               15 ) );

        final List messages = new ArrayList();
        session.setGlobal( "messages",
                      messages );
//        wm  = SerializationHelper.serializeObject(wm);
        session.fireAllRules();

        // should have NONE, as both conditions should be false.
        assertThat(messages.size()).isEqualTo(0);

        session.insert( new Person( "fire" ) );
        session.fireAllRules();

        // still no firings
        assertThat(messages.size()).isEqualTo(0);

        session.insert( new Cheese( "brie",
                               15 ) );

        session.fireAllRules();

        // YOUR FIRED
        assertThat(messages.size()).isEqualTo(1);
    }

    @Test @Ignore("antlr cannot parse correctly if the file ends with a comment without a further line break")
    public void testEmptyDSL() throws Exception {
        // FIXME etirelli / mic_hat not sure what to do with this?
        final String DSL = "# This is an empty dsl file.";  // gives antlr <EOF> error
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( "test_expander.dsl", getClass() ),
                              ResourceType.DSL );
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( DSL)  ) ,
                              ResourceType.DSLR );

        assertThat(kbuilder.hasErrors()).isFalse(); // trying to expand Cheese() pattern

        // Check errors
        final String err = kbuilder.getErrors().toString();
        assertThat(err).isEqualTo("");
        assertThat(kbuilder.getErrors().size()).isEqualTo(0);
        
        // the compiled package
        Collection<KiePackage> pkgs = kbuilder.getKnowledgePackages();
        assertThat(pkgs.size()).isEqualTo(0);
        
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( pkgs );
        kbase    = SerializationHelper.serializeObject(kbase);

        KieSession session = kbase.newKieSession();

        pkgs = SerializationHelper.serializeObject(pkgs);
        assertThat(pkgs).isNull();
    }

    @Test
    public void testDSLWithIndividualConstraintMappings() throws Exception {
        final Resource resource1 = KieServices.Factory.get().getResources().newClassPathResource("test_dslWithIndividualConstraints.dsl", getClass());
        resource1.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "test_dslWithIndividualConstraints.dsl");
        final Resource resource2 = KieServices.Factory.get().getResources().newClassPathResource("test_dslWithIndividualConstraints.dslr", getClass());
        resource2.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "test_dslWithIndividualConstraints.dslr");

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, false, resource1, resource2);

        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
        
        // the compiled package
        final Collection<KiePackage> pkgs = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kieBuilder).getKiePackages();
        assertThat(pkgs.size()).isEqualTo(1);

        KieBase kbase = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kieBuilder);

        KieSession session = kbase.newKieSession();
        List results = new ArrayList();
        session.setGlobal("results",
                          results);
        Cheese cheese = new Cheese( "stilton",
                                    42 );
        session.insert(cheese);

//        wm  = SerializationHelper.serializeObject(wm);
        session.fireAllRules();

        // should have fired
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(cheese);

    }

    @Test
    public void testDSLWithSpaceBetweenParenthesis() {
        // JBRULES-3438
        String dsl = "[when]There is a Person=Person( )\n"
                + "[when]-named {name}=name == \"{name}\"\n"
                + "[when]-aged less than {age}=age < {age}\n"
                + "[then]Log {message}=list.add({message});";

        String drl = "import org.drools.mvel.compiler.Person;\n"
                + "global java.util.List list\n"
                + "rule R1\n"
                + "when\n"
                + "There is a Person\n"
                + "-named Mario\n"
                + "-aged less than 40\n"
                + "then\n"
                + "Log \"OK\"\n"
                + "end\n";

        assertThat(doTest(dsl, drl).contains("OK")).isTrue();
    }

    @Test
    public void testDSLWithVariableBinding() {
        String dsl = "[when]There is a Person=$p : Person()\n"
                + "[when]-named {name}=name == \"{name}\"\n"
                + "[when]-aged less than {age}=age < {age}\n"
                + "[then]Log person name=list.add($p.getName());";

        String drl = "import org.drools.mvel.compiler.Person;\n"
                + "global java.util.List list\n"
                + "rule R1\n"
                + "when\n"
                + "There is a Person\n"
                + "-named Mario\n"
                + "-aged less than 40\n"
                + "then\n"
                + "Log person name\n"
                + "end\n";

        assertThat(doTest(dsl, drl).contains("Mario")).isTrue();
    }

    @Test
    public void testDSLWithApostrophe() {
        String dsl = "[when]Person's name is {name}=$p : Person(name == \"{name}\")\n"
                + "[then]Log person name=list.add($p.getName());";

        String drl = "import org.drools.mvel.compiler.Person;\n"
                + "global java.util.List list\n"
                + "rule R1\n"
                + "when\n"
                + "Person's name is Mario\n"
                + "then\n"
                + "Log person name\n"
                + "end\n";

        assertThat(doTest(dsl, drl).contains("Mario")).isTrue();
    }

    @Test
    public void testDSLWithCommentedBlock() {
        // JBRULES-3445
        String dsl = "[when]There is a Person=Person()\n"
                + "[when]-named {name}=name == \"{name}\"\n"
                + "[when]-aged less than {age}=age < {age}\n"
                + "[then]Log {message}=list.add({message});";

        String drl = "import org.drools.mvel.compiler.Person;\n"
                + "global java.util.List list\n"
                + "rule R1\n"
                + "when\n"
                + "/*There is a Cheese\n"
                + "-of type Gorgonzola*/\n"
                + "There is a Person\n"
                + "-named Mario\n"
                + "-aged less than 40\n"
                + "then\n"
                + "Log \"OK\"\n"
                + "end\n";

        assertThat(doTest(dsl, drl).contains("OK")).isTrue();
    }

    @Test
    public void testDSLWithSingleDotRegex() {
        // DROOLS-430
        String dsl = "[then]Log {message:.}=list.add(\"{message}\");";

        String drl = "import org.drools.mvel.compiler.Person;\n"
                + "global java.util.List list\n"
                + "rule R1\n"
                + "when\n"
                + "then\n"
                + "Log X\n"
                + "end\n";

        assertThat(doTest(dsl, drl).contains("X")).isTrue();
    }

    private List doTest(String dsl, String drl) {
        final Resource resource1 = KieServices.Factory.get().getResources().newReaderResource(new StringReader(dsl));
        resource1.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "test_dsl.dsl");
        final Resource resource2 = KieServices.Factory.get().getResources().newReaderResource(new StringReader(drl));
        resource2.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "test_dslr.dslr");
        
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, false, resource1, resource2);

        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();

        KieBase kbase = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kieBuilder);
        
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert(new Person("Mario", 38));
        ksession.fireAllRules();
        ksession.dispose();

        return list;
    }

    @Test
    public void testGreedyDsl() {
        // BZ-1078839
        String dsl = "[when]There is a number with value of {value}=i:Integer(intValue() == {value})\n"
                + "[when]There is a number with=i:Integer()\n";

        String dslr = "package org.test \n"
                     + "rule 'sample rule' \n"
                     + "when \n" + "  There is a number with value of 10\n"
                     + "then \n" + "end \n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem()
                              .write("src/main/resources/r1.dslr", dslr)
                              .write("src/main/resources/r1.dsl", dsl);

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        final List<Message> messages = kieBuilder.getResults().getMessages();

        assertThat(messages.size()).isEqualTo(0);
    }

    @Test
    public void testDSLWithSingleDot() {
        // DROOLS-768
        String dsl = "[when][]if there is a simple event\n" +
                     "{evtName}={evtName}" +
                     ": SimpleEvent()\n" +
                     "[when][]and a simple event 2\n" +
                     "{evtName2} with the same {attribute} as {evtName}={evtName2} " +
                     ": SimpleEvent2(" +
                     "{attribute}=={evtName}.{attribute}" +
                     ")\n" +
                     "[then][]ok=System.out.println(\"that works\");\n" +
                     "\n";

        String drl = "declare SimpleEvent\n" +
                     "  code: String\n" +
                     "end\n" +
                     "\n" +
                     "declare SimpleEvent2\n" +
                     "  code: String\n" +
                     "end\n" +
                     "rule \"RG_CORR_RECOK_OK\"\n" +
                     "when\n" +
                     "if there is a simple event $evt\n" +
                     "and a simple event 2 $evt2 with the same code as $evt\n" +
                     "then\n" +
                     "ok\n" +
                     "end\n";

        final Resource resource1 = KieServices.Factory.get().getResources().newReaderResource(new StringReader(dsl));
        resource1.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "test_dsl.dsl");
        final Resource resource2 = KieServices.Factory.get().getResources().newReaderResource(new StringReader(drl));
        resource2.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "test_dslr.dslr");
        
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, false, resource1, resource2);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }
}
