/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ancompiler;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.NamedEntryPoint;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.ObjectSinkPropagator;
import org.drools.core.reteoo.ObjectTypeNode;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.AlphaNetworkCompilerOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AlphaNetworkCompilerTest extends BaseModelTest {

    public AlphaNetworkCompilerTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    public class Message implements Serializable {
        private final String value;

        public Message( String value ) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Test
    public void testNonHashedAlphaNode() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global java.util.List resultsM;\n" +
                        "global java.util.List resultsL;\n" +
                        "rule M when\n" +
                        "  $p : Person( name.startsWith(\"M\"))\n" +
                        "then\n" +
                        "  resultsM.add($p);\n" +
                        "end\n" +
                        "rule L when\n" +
                        "  $p : Person( name.startsWith(\"L\"))\n" +
                        "then\n" +
                        "  resultsL.add($p);\n" +
                        "end";

        final List<Person> resultsM = new ArrayList<>();
        final List<Person> resultsL = new ArrayList<>();

        KieSession ksession = getKieSession( str );

        ksession.setGlobal("resultsM", resultsM);
        ksession.setGlobal("resultsL", resultsL);

        final Person mario = new Person("Mario", 40);
        final Person luca = new Person("Luca", 33);

        ksession.insert(mario);
        ksession.insert(luca);

        ksession.fireAllRules();

        assertEquals( 1, resultsM.size() );
        assertEquals( mario, resultsM.iterator().next() );

        assertEquals( 1, resultsL.size() );
        assertEquals( luca, resultsL.iterator().next() );
    }

    @Test
    public void testNormalizationForAlphaIndexing() {
        final String str =
                "package org.drools.test;\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R1 when \n" +
                        " $p : Person(\"Toshiya\" == name)\n" +
                        "then\n" +
                        "end\n" +
                        "rule R2 when \n" +
                        " $p : Person(\"Mario\" == name)\n" +
                        "then\n" +
                        "end\n" +
                        "rule R3 when \n" +
                        " $p : Person(\"Luca\" == name)\n" +
                        "then\n" +
                        "end\n";

        final KieSession ksession = getKieSession(str);

        ObjectTypeNode otn = ((NamedEntryPoint) ksession.getEntryPoint("DEFAULT")).getEntryPointNode().getObjectTypeNodes().entrySet()
                .stream()
                .filter(e -> e.getKey().getClassName().equals(Person.class.getCanonicalName()))
                .map(e -> e.getValue())
                .findFirst()
                .get();
        ObjectSinkPropagator objectSinkPropagator = otn.getObjectSinkPropagator();
        if(this.testRunType.isAlphaNetworkCompiler()) {
            objectSinkPropagator = ((CompiledNetwork)objectSinkPropagator).getOriginalSinkPropagator();
        }
        CompositeObjectSinkAdapter sinkAdaptor = (CompositeObjectSinkAdapter) objectSinkPropagator;

        assertNotNull(sinkAdaptor.getHashedSinkMap());
        assertEquals(3, sinkAdaptor.getHashedSinkMap().size());

        final Person p = new Person("Toshiya", 45);
        ksession.insert(p);
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testNodeHashingWithMultipleConditions() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global java.util.List results;\n" +
                        "rule r1 when\n" +
                        "  $p : Person( name == \"Luca\", likes == \"food\", age >= 33)\n" +
                        "then\n" +
                        "  results.add($p);\n" +
                        "end\n" +
                        "rule r2 when\n" +
                        "  $p : Person( name == \"Luca\", likes == \"videogames\", age < 19)\n" +
                        "then\n" +
                        "end\n" +
                        "rule r3 when\n" +
                        "  $p : Person( name == \"Luca\", likes == \"music\", age == 20)\n" +
                        "then\n" +
                        "end";

        final List<Person> results = new ArrayList<>();

        KieSession ksession = getKieSession( str );

        ksession.setGlobal("results", results);

        final Person luca33 = new Person("Luca", 33).setLikes("food");
        final Person luca20 = new Person("Luca", 20).setLikes("music");
        final Person luca18 = new Person("Luca", 18).setLikes("videogames");

        ksession.insert(luca33);
        ksession.insert(luca20);
        ksession.insert(luca18);

        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( luca33, results.iterator().next() );

    }

    @Test
    public void testHashedInteger() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global java.util.List resultsM;\n" +
                        "global java.util.List resultsL;\n" +
                        "rule M when\n" +
                        "  $p : Person( age == 40)\n" +
                        "then\n" +
                        "  resultsM.add($p);\n" +
                        "end\n" +
                        "rule L when\n" +
                        "  $p : Person( age == 33)\n" +
                        "then\n" +
                        "  resultsL.add($p);\n" +
                        "end";

        final List<Person> resultsM = new ArrayList<>();
        final List<Person> resultsL = new ArrayList<>();

        KieSession ksession = getKieSession( str );

        ksession.setGlobal("resultsM", resultsM);
        ksession.setGlobal("resultsL", resultsL);

        final Person mario = new Person("Mario", 40);
        final Person luca = new Person("Luca", 33);

        ksession.insert(mario);
        ksession.insert(luca);

        ksession.fireAllRules();

        assertEquals( 1, resultsM.size() );
        assertEquals( mario, resultsM.iterator().next() );

        assertEquals( 1, resultsL.size() );
        assertEquals( luca, resultsL.iterator().next() );
    }

    @Test
    public void testAlphaConstraint() {
        String str =
                "rule \"Bind\"\n" +
                        "when\n" +
                        "  $s : String( length > 4, length < 10)\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("Luca");
        ksession.insert("Asdrubale");

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAlphaConstraintsSwitchString() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"Bind1\"\n" +
                        "when\n" +
                        "  $s : Person( name == \"Luca\") \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind2\"\n" +
                        "when\n" +
                        "  $s : Person( name == \"Mario\") \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind3\"\n" +
                        "when\n" +
                        "  $s : Person( name == \"Matteo\") \n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Luca"));
        ksession.insert(new Person("Asdrubale"));

        assertEquals(1, ksession.fireAllRules());
    }

    /*
        This generates the switch but not the inlining
     */
    @Test
    public void testAlphaConstraintsSwitchBigDecimal() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + BigDecimal.class.getCanonicalName() + ";" +
                        "rule \"Bind1\"\n" +
                        "when\n" +
                        "  $s : Person( money == new BigDecimal(0)) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind2\"\n" +
                        "when\n" +
                        "  $s : Person( money == new BigDecimal(1)) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind3\"\n" +
                        "when\n" +
                        "  $s : Person( money == new BigDecimal(2)) \n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Luca", new BigDecimal(0)));
        ksession.insert(new Person("Asdrubale", new BigDecimal(10)));

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAlphaConstraintsSwitchPerson() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"Bind1\"\n" +
                        "when\n" +
                        "  $s : Person( this == new Person(\"Luca\")) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind2\"\n" +
                        "when\n" +
                        "  $s : Person( this == new Person(\"Mario\")) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind3\"\n" +
                        "when\n" +
                        "  $s : Person( this == new Person(\"Matteo\")) \n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Luca"));
        ksession.insert(new Person("Asdrubale"));

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAlphaConstraintsSwitchIntegers() {
        String str =
                "rule \"Bind1\"\n" +
                        "when\n" +
                        "  $s : String( length == 4) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind2\"\n" +
                        "when\n" +
                        "  $s : String( length == 5) \n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Bind3\"\n" +
                        "when\n" +
                        "  $s : String( length == 6) \n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert("Luca");
        ksession.insert("Asdrubale");

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testEnum() {
        String str =
                "import " + EnumFact1.class.getCanonicalName() + ";\n" +
                        "import " + ChildFactWithEnum1.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "    $factWithEnum : ChildFactWithEnum1(  enumValue == EnumFact1.FIRST ) \n" +
                        "then\n" +
                        "end\n" +
                        "rule R2 when\n" +
                        "    $factWithEnum : ChildFactWithEnum1(  enumValue == EnumFact1.SECOND ) \n" +
                        "then\n" +
                        "end\n" +
                        "rule R3 when\n" +
                        "    $factWithEnum : ChildFactWithEnum1(  enumValue == EnumFact1.THIRD ) \n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);
        ksession.insert(new ChildFactWithEnum1(1, 3, EnumFact1.FIRST));
        ksession.insert(new ChildFactWithEnum1(1, 3, EnumFact1.SECOND));
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testAlphaConstraintWithModification() {
        String str =
                        "global java.util.List results;\n" +
                        "rule \"Bind\"\n" +
                        "when\n" +
                        "  $s : String( length > 4, length < 10)\n" +
                        "then\n" +
                        "  results.add($s + \" is greater than 4 and smaller than 10\");\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("Luca");
        ksession.insert("Asdrubale");

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        assertEquals(1, ksession.fireAllRules());

        ksession.fireAllRules();
        assertEquals("Asdrubale is greater than 4 and smaller than 10", results.iterator().next());
    }

    @Test
    public void testModify() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"Modify\"\n" +
                        "when\n" +
                        "  $p : Person( age == 30 )\n" +
                        "then\n" +
                        "   modify($p) { setName($p.getName() + \"30\"); }" +
                        "end";

        KieSession ksession = getKieSession(str);

        final Person luca = new Person("Luca", 30);
        ksession.insert(luca);

        assertEquals(1, ksession.fireAllRules());

        ksession.fireAllRules();
        assertEquals("Luca30", luca.getName());
    }

    @Test
    public void testModify2() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global java.util.List results;\n" +
                        "rule \"Modify\"\n" +
                        "when\n" +
                        "  $p : Person( age < 40 )\n" +
                        "then\n" +
                        "   modify($p) { setAge($p.getAge() + 1); }" +
                        "end";

        KieSession ksession = getKieSession(str);

        final Person luca = new Person("Luca", 30);
        ksession.insert(luca);

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        assertEquals(10, ksession.fireAllRules());

        ksession.fireAllRules();
        assertTrue(luca.getAge() == 40);
    }

    @Test
    public void testAlphaConstraintNagate() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R1 when\n" +
                        "    Person( !(age > 18) )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(str);
        try {
            ksession.insert(new Person("Mario", 45));
            assertEquals(0, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testKJarUpgradeWithDeclaredType() throws Exception {
        String drl1 = "package org.drools.incremental\n" +
                "declare Message value : String end\n" +
                "rule Init when then insert(new Message( \"Hello World\" )); end\n" +
                "rule R1 when\n" +
                "   $m : Message( value.startsWith(\"H\") )\n" +
                "then\n" +
                "   System.out.println($m.getValue());" +
                "end\n";

        String drl2_1 = "package org.drools.incremental\n" +
                "rule R2 when\n" +
                "   $m : Message( value == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2_2 = "package org.drools.incremental\n" +
                "rule R2 when\n" +
                "   $m : Message( value == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl2_3 = "package org.drools.incremental\n" +
                "global java.util.List list;\n" +
                "rule R2 when\n" +
                "   $m : Message( value == \"Hello World\" )\n" +
                "then\n" +
                "   list.add($m.getValue());\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );

        KieModuleModel kieModuleModel = ks.newKieModuleModel();
        if(this.testRunType.isAlphaNetworkCompiler()) {
            kieModuleModel.setConfigurationProperty("drools.alphaNetworkCompiler", AlphaNetworkCompilerOption.INMEMORY.toString());
        }
        createAndDeployJar( ks, kieModuleModel, releaseId1, drl1, drl2_1 );

        KieContainer kc = ks.newKieContainer( releaseId1 );

        // Create a session and fire rules
        KieSession ksession = kc.newKieSession();
        if(this.testRunType.isAlphaNetworkCompiler()) {
            this.assertReteIsAlphaNetworkCompiled(ksession);
        }
        assertEquals( 2, ksession.fireAllRules() );

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, kieModuleModel, releaseId2, drl1, drl2_2 );

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        // try with a new session
        KieSession ksession2 = kc.newKieSession();
        if(this.testRunType.isAlphaNetworkCompiler()) {
            this.assertReteIsAlphaNetworkCompiled(ksession2);
        }

        assertEquals( 3, ksession2.fireAllRules() );

        // Create a new jar for version 1.2.0
        ReleaseId releaseId3 = ks.newReleaseId( "org.kie", "test-upgrade", "1.2.0" );
        createAndDeployJar( ks, kieModuleModel, releaseId3, drl1, drl2_3 );

        // try to update the container to version 1.2.0
        kc.updateToVersion( releaseId3 );
        KieSession kieSession3 = kc.newKieSession();
        if(this.testRunType.isAlphaNetworkCompiler()) {
            this.assertReteIsAlphaNetworkCompiled(kieSession3);
        }

        List<String> list = new ArrayList<>();
        ksession2.setGlobal( "list", list );
        ksession2.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "Hello World", list.get(0) );
    }

}
