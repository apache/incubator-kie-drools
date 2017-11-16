/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.drools.compiler.Bar;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.FactB;
import org.drools.compiler.Foo;
import org.drools.compiler.Message;
import org.drools.compiler.Person;
import org.drools.compiler.Primitives;
import org.drools.core.rule.MapBackedClassLoader;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.mockito.ArgumentCaptor;
import org.mvel2.MVEL;

public class JBRULESTest extends CommonTestMethodBase {

    @Test
    public void testJBRules2055() {
        final KieBase kbase = loadKnowledgeBase("test_JBRules2055.drl");
        final KieSession ksession = createKnowledgeSession(kbase);
        final List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);
        ksession.insert(new Cheese("stilton"));
        ksession.insert(new Cheese("brie"));
        ksession.insert(new Cheese("muzzarella"));
        ksession.insert(new Person("bob", "stilton"));
        ksession.fireAllRules();
        assertEquals(2, results.size());
        assertEquals("stilton", results.get(0));
        assertEquals("brie", results.get(1));

    }

    @Test
    public void testJBRules2369() {
        final KieBase kbase = loadKnowledgeBase("test_JBRules2369.drl");
        final KieSession ksession = createKnowledgeSession(kbase);
        final List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        final FactA a = new FactA();
        final FactB b = new FactB(Integer.valueOf(0));

        final FactHandle aHandle = ksession.insert(a);
        final FactHandle bHandle = ksession.insert(b);

        ksession.fireAllRules();

        assertEquals(1, results.size());

        ksession.update(aHandle, a);

        ksession.fireAllRules();
        assertEquals(2, results.size());
    }

    @Test
    public void testJBRules2140() {
        final KieBase kbase = loadKnowledgeBase("test_JBRules2140.drl");
        final KieSession ksession = createKnowledgeSession(kbase);
        final List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);
        ksession.fireAllRules();
        assertEquals(2, results.size());
        assertTrue(results.contains("java"));
        assertTrue(results.contains("mvel"));
    }

    @Test
    public void testJBRULES_2995() {
        final String str = "package org.drools.compiler\n" +
                "rule r1\n" +
                "when\n" +
                "    Primitives( classAttr == java.lang.String.class, \n" +
                "                eval(classAttr.equals( java.lang.String.class ) ),\n" +
                "                classAttr == String.class )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);
        final Primitives primitives = new Primitives();
        primitives.setClassAttr(String.class);
        ksession.insert(primitives);
        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testJBRULES2872() {
        final String str = "package org.drools.compiler.test\n" +
                "import org.drools.compiler.FactA\n" +
                "rule X\n" +
                "when\n" +
                "    FactA( enumVal == TestEnum.ONE || == TestEnum.TWO )\n" +
                "then\n" +
                "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());
        final KnowledgeBuilderErrors errors = kbuilder.getErrors();
        assertEquals(1, errors.size());
        final KnowledgeBuilderError error = errors.iterator().next();
        assertEquals(5, error.getLines()[0]);
    }

    @Test
    public void testJBRULES3030() {
        final String str = "package org.drools.compiler\n" +
                "rule X\n" +
                "when\n" +
                "    $gp : GrandParent()" +
                "    $ch : ChildHolder( child == $gp )\n" +
                "then\n" +
                "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        assertFalse(kbuilder.hasErrors());
    }

    @Test
    public void testJBRULES3111() {
        final String str = "package org.drools.compiler\n" +
                "declare Bool123\n" +
                "    bool1 : boolean\n" +
                "    bool2 : boolean\n" +
                "    bool3 : boolean\n" +
                "end\n" +
                "declare Thing\n" +
                "    name : String\n" +
                "    bool123 : Bool123\n" +
                "end\n" +
                "rule kickOff\n" +
                "when\n" +
                "then\n" +
                "    insert( new Thing( \"one\", new Bool123( true, false, false ) ) );\n" +
                "    insert( new Thing( \"two\", new Bool123( false, false, false ) ) );\n" +
                "    insert( new Thing( \"three\", new Bool123( false, false, false ) ) );\n" +
                "end\n" +
                "rule r1\n" +
                "when\n" +
                "    $t: Thing( bool123.bool1 == true )\n" +
                "then\n" +
                "end\n" +
                "rule r2\n" +
                "when\n" +
                "    $t: Thing( bool123.bool2 == true )\n" +
                "then\n" +
                "end\n" +
                "rule r3\n" +
                "when\n" +
                "    $t: Thing( bool123.bool3 == true )\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final org.kie.api.event.rule.AgendaEventListener ael = mock(org.kie.api.event.rule.AgendaEventListener.class);
        ksession.addEventListener(ael);

        final int rulesFired = ksession.fireAllRules();
        assertEquals(2, rulesFired);

        final ArgumentCaptor<AfterMatchFiredEvent> captor = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael, times(2)).afterMatchFired(captor.capture());
        final List<org.kie.api.event.rule.AfterMatchFiredEvent> aafe = captor.getAllValues();

        Assert.assertThat(aafe.get(0).getMatch().getRule().getName(), is("kickOff"));
        Assert.assertThat(aafe.get(1).getMatch().getRule().getName(), is("r1"));

        final Object value = aafe.get(1).getMatch().getDeclarationValue("$t");
        final String name = (String) MVEL.eval("$t.name", Collections.singletonMap("$t", value));

        Assert.assertThat(name, is("one"));
    }

    @Test
    public void testJBRULES3323() throws Exception {

        //adding rules. it is important to add both since they reciprocate
        final StringBuilder rule = new StringBuilder();
        rule.append( "package de.orbitx.accumulatetesettest;\n" );
        rule.append( "import java.util.Set;\n" );
        rule.append( "import java.util.HashSet;\n" );
        rule.append( "import org.drools.compiler.Foo;\n" );
        rule.append( "import org.drools.compiler.Bar;\n" );

        rule.append( "rule \"Sub optimal foo parallelism - this rule is causing NPE upon reverse\"\n" );
        rule.append( "when\n" );
        rule.append( "$foo : Foo($leftId : id, $leftBar : bar != null)\n" );
        rule.append( "$fooSet : Set()\n" );
        rule.append( "from accumulate ( Foo(id > $leftId, bar != null && != $leftBar, $bar : bar),\n" );
        rule.append( "collectSet( $bar ) )\n" );
        rule.append( "then\n" );
        rule.append( "//System.out.println(\"ok\");\n" );
        rule.append( "end\n" );

        //building stuff
        final KieBase kbase = loadKnowledgeBaseFromString(rule.toString());
        final KieSession ksession = createKnowledgeSession(kbase);

        //adding test data
        final Bar[] barList = new Bar[3];
        for (int i = 0; i < barList.length; i++) {
            barList[i] = new Bar(String.valueOf(i));
        }

        final Foo[] fooList = new Foo[4];
        for (int i = 0; i < fooList.length; i++) {
            fooList[i] = new Foo(String.valueOf(i), i == 3 ? barList[2] : barList[i]);
        }

        for (final Foo foo : fooList) {
            ksession.insert(foo);
        }

        //the NPE is caused by exactly this sequence. of course there are more sequences but this
        //appears to be the most short one
        final int[] magicFoos = new int[]{3, 3, 1, 1, 0, 0, 2, 2, 1, 1, 0, 0, 3, 3, 2, 2, 3, 1, 1};
        final int[] magicBars = new int[]{1, 2, 0, 1, 1, 0, 1, 2, 2, 1, 2, 0, 0, 2, 0, 2, 0, 0, 1};

        //upon final rule firing an NPE will be thrown in org.drools.core.rule.Accumulate
        for (int i = 0; i < magicFoos.length; i++) {
            final Foo tehFoo = fooList[magicFoos[i]];
            final FactHandle fooFactHandle = ksession.getFactHandle(tehFoo);
            tehFoo.setBar(barList[magicBars[i]]);
            ksession.update(fooFactHandle, tehFoo);
            ksession.fireAllRules();
        }
        ksession.dispose();
    }

    @Test
    public void testJBRULES3326() throws Exception {
        final StringBuilder rule = new StringBuilder();
        rule.append("package org.drools.compiler\n");
        rule.append("rule X\n");
        rule.append("when\n");
        rule.append("    Message(!!!false)\n");
        rule.append("then\n");
        rule.append("end\n");

        //building stuff
        final KieBase kbase = loadKnowledgeBaseFromString(rule.toString());
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Message("test"));
        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
        ksession.dispose();
    }

    @Test
    public void testGUVNOR578_2() throws Exception {
        final MapBackedClassLoader loader = new MapBackedClassLoader( this.getClass().getClassLoader() );

        final JarInputStream jis = new JarInputStream( this.getClass().getResourceAsStream( "/primespoc.jar" ) );

        JarEntry entry = null;
        final byte[] buf = new byte[1024];
        int len = 0;
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ( (len = jis.read( buf )) >= 0 ) {
                    out.write( buf,
                            0,
                            len );
                }
                loader.addResource( entry.getName(),
                        out.toByteArray() );
            }
        }

        final List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add(jis);

        final KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, loader);
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);

        final String header = "import fr.gouv.agriculture.dag.agorha.business.primes.SousPeriodePrimeAgent\n";

        kbuilder.add(ResourceFactory.newByteArrayResource(header.getBytes()), ResourceType.DRL);
        assertFalse(kbuilder.hasErrors());

        final String passingRule = "rule \"rule1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "SousPeriodePrimeAgent( echelle == \"abc\" )"
                + "then\n"
                + "end\n";

        final String failingRule = "rule \"rule2\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "SousPeriodePrimeAgent( quotiteRemuneration == 123 , echelle == \"abc\" )"
                + "then\n"
                + "end\n";

        kbuilder.add(ResourceFactory.newByteArrayResource(passingRule.getBytes()), ResourceType.DRL);
        assertFalse(kbuilder.hasErrors());

        kbuilder.add(ResourceFactory.newByteArrayResource(failingRule.getBytes()), ResourceType.DRL);
        assertFalse(kbuilder.hasErrors());
    }
}
