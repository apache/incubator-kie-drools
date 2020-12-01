/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.mvel.integrationtests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.lang.Expander;
import org.drools.compiler.lang.dsl.DefaultExpanderResolver;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Person;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DslTest extends CommonTestMethodBase {
   
    @Test
    public void testMultiLineTemplates() throws Exception {
        final Reader source = new InputStreamReader( getClass().getResourceAsStream( "rule_with_expander_multiline.dslr" ) );
        final Reader dsl = new InputStreamReader( getClass().getResourceAsStream( "test_dsl_multiline.dsl" ) );
        Expander ex =  new DefaultExpanderResolver(dsl).get("*", null);
        String r = ex.expand(source);
        assertEquals("when Car(color==\"Red\") then doSomething();", r.trim());
    }

    @Test
    public void testWithExpanderDSL() throws Exception {
        //final PackageBuilder builder = new PackageBuilder();
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( "test_expander.dsl", getClass() ),
                              ResourceType.DSL );
        kbuilder.add(ResourceFactory.newClassPathResource("rule_with_expander_dsl.dslr", getClass()),
                ResourceType.DSLR);

        checkDSLExpanderTest(kbuilder);
    }

    @Test
    public void testWithExpanderDSLUsingCompositeBuiler() throws Exception {
        //final PackageBuilder builder = new PackageBuilder();
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.batch()
                .add( ResourceFactory.newClassPathResource( "test_expander.dsl", getClass() ),
                        ResourceType.DSL )
                .add( ResourceFactory.newClassPathResource( "rule_with_expander_dsl.dslr", getClass() ) ,
                        ResourceType.DSLR )
                .build();

        checkDSLExpanderTest(kbuilder);
    }

    private void checkDSLExpanderTest(KnowledgeBuilder kbuilder) throws IOException, ClassNotFoundException {
        assertFalse( kbuilder.hasErrors() );
        final String err = kbuilder.getErrors().toString();
        assertEquals( "",
                      err );
        assertEquals( 0,
                      kbuilder.getErrors().size() );

        // the compiled package
        final Collection<KiePackage> pkgs = kbuilder.getKnowledgePackages();
        assertEquals( 2, pkgs.size() );

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( pkgs );
        kbase    = SerializationHelper.serializeObject(kbase);

        KieSession session = createKnowledgeSession(kbase);
        session.insert( new Person( "Bob",
                               "http://foo.bar" ) );
        session.insert( new Cheese( "stilton",
                               42 ) );

        final List messages = new ArrayList();
        session.setGlobal( "messages",
                      messages );
        session.fireAllRules();

        assertEquals( 1, messages.size() );
    }

    @Test
    public void testWithExpanderMore() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kbuilder.add( ResourceFactory.newClassPathResource( "test_expander.dsl", getClass() ),
                              ResourceType.DSL );
        kbuilder.add( ResourceFactory.newClassPathResource("rule_with_expander_dsl_more.dslr", getClass()) ,
                              ResourceType.DSLR );

        assertFalse( kbuilder.hasErrors() );

        // Check errors
        final String err = kbuilder.getErrors().toString();
        assertEquals( "",
                      err );
        assertEquals( 0,
                      kbuilder.getErrors().size() );
        
        // the compiled package
        final Collection<KiePackage> pkgs = kbuilder.getKnowledgePackages();
        assertEquals( 2, pkgs.size() );
        
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( pkgs );
        kbase    = SerializationHelper.serializeObject(kbase);

        KieSession session = createKnowledgeSession(kbase);
        session.insert( new Person( "rage" ) );
        session.insert( new Cheese( "cheddar",
                               15 ) );

        final List messages = new ArrayList();
        session.setGlobal( "messages",
                      messages );
//        wm  = SerializationHelper.serializeObject(wm);
        session.fireAllRules();

        // should have NONE, as both conditions should be false.
        assertEquals( 0,
                      messages.size() );

        session.insert( new Person( "fire" ) );
        session.fireAllRules();

        // still no firings
        assertEquals( 0,
                      messages.size() );

        session.insert( new Cheese( "brie",
                               15 ) );

        session.fireAllRules();

        // YOUR FIRED
        assertEquals( 1,
                      messages.size() );
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

        assertFalse( kbuilder.hasErrors() ); // trying to expand Cheese() pattern

        // Check errors
        final String err = kbuilder.getErrors().toString();
        assertEquals( "",
                      err );
        assertEquals( 0,
                      kbuilder.getErrors().size() );
        
        // the compiled package
        Collection<KiePackage> pkgs = kbuilder.getKnowledgePackages();
        assertEquals( 0, pkgs.size() );
        
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( pkgs );
        kbase    = SerializationHelper.serializeObject(kbase);

        KieSession session = createKnowledgeSession(kbase);

        pkgs = SerializationHelper.serializeObject(pkgs);
        assertNull( pkgs );
    }

    @Test
    public void testDSLWithIndividualConstraintMappings() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_dslWithIndividualConstraints.dsl", getClass() ),
                              ResourceType.DSL );
        kbuilder.add( ResourceFactory.newClassPathResource( "test_dslWithIndividualConstraints.dslr", getClass() ) ,
                              ResourceType.DSLR );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        // Check errors
        final String err = kbuilder.getErrors().toString();
        assertEquals( "",
                      err );
        assertEquals(0,
                     kbuilder.getErrors().size());

        // the compiled package
        final Collection<KiePackage> pkgs = kbuilder.getKnowledgePackages();
        assertEquals(1, pkgs.size());

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(pkgs);
        kbase    = SerializationHelper.serializeObject(kbase);

        KieSession session = createKnowledgeSession(kbase);
        List results = new ArrayList();
        session.setGlobal("results",
                          results);
        Cheese cheese = new Cheese( "stilton",
                                    42 );
        session.insert(cheese);

//        wm  = SerializationHelper.serializeObject(wm);
        session.fireAllRules();

        // should have fired
        assertEquals( 1,
                      results.size() );
        assertEquals(cheese,
                     results.get(0));

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

        assertTrue(doTest(dsl, drl).contains("OK"));
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

        assertTrue(doTest(dsl, drl).contains("Mario"));
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

        assertTrue(doTest(dsl, drl).contains("Mario"));
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

        assertTrue(doTest(dsl, drl).contains("OK"));
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

        assertTrue(doTest(dsl, drl).contains("X"));
    }

    private List doTest(String dsl, String drl) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(dsl.getBytes()), ResourceType.DSL );
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DSLR );

        assertFalse(kbuilder.hasErrors());

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
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
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(0, results.getMessages().size());
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(dsl.getBytes()), ResourceType.DSL );
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DSLR);

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
    }

}
