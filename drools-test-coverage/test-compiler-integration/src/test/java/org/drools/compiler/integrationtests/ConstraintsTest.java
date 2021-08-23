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

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Message;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.StockTick;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ConstraintsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ConstraintsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testExpressionConstraints1() {
        final String drl = "package org.drools.compiler.integrationtests\n" +
                "import " + Mailbox.FolderType.class.getCanonicalName() + ";\n" +
                "import " + Mailbox.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "        dialect \"mvel\"\n" +
                "    when\n" +
                "        $m : Mailbox( \n" +
                "                $folderType : getDefaultFolderType(),\n" +
                "                FolderType.INBOX == $folderType,\n" +
                "                $folderType == FolderType.INBOX,\n" +
                "                $mailForFolder2 : getMailTypeForFolderType(getDefaultFolderType()),\n" +
                "                FolderType.SENT != getDefaultFolderType(), \n" +
                "                getDefaultFolderType() != FolderType.SENT, \n" +
                "                getMailTypeForFolderType($folderType) == MailType.WORK,\n" +
                "                1 > 0\n" +
                "        )\n" +
                "    then\n" +
                "end\n";

        testExpressionConstraints(drl);
    }

    @Test
    public void testExpressionConstraints2() {
        final String drl = "package org.drools.compiler.integrationtests\n" +
                "import " + Mailbox.FolderType.class.getCanonicalName() + ";\n" +
                "import " + Mailbox.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "    dialect \"mvel\"\n" +
                "    when\n" +
                "        $m : Mailbox( \n" +
                "                $me : owneremail,\n" +
                "                recentContacts[Mailbox.TEST_EMAIL] != null,\n" +
                "                recentContacts[\"me@test.com\"] != null,\n" +
                "                $d1 : recentContacts[Mailbox.TEST_EMAIL],\n" +
                "                $d2 : recentContacts[\"me@test.com\"],\n" +
                "                recentContacts.get(owneremail) != null,\n" +
                "                recentContacts.get($me) != null,\n" +
                "                recentContacts[$me] != null,\n" +
                "                $d3: recentContacts.get(owneremail),\n" +
                "                // Waiting for MVEL fixes for the following 2 scenarios" +
                "                // recentContacts[owneremail] != null,\n" +
                "                // recentContacts[getOwneremail()] != null,\n" +
                "                0 < 1\n" +
                "        )\n" +
                "    then\n" +
                "end\n";

        testExpressionConstraints(drl);
    }

    @Test
    public void testExpressionConstraints3() {
        final String drl = "package org.drools.compiler.integrationtests\n" +
                "import " + Mailbox.FolderType.class.getCanonicalName() + ";\n" +
                "import " + Mailbox.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "    dialect \"mvel\"\n" +
                "    when\n" +
                "        $m : Mailbox( \n" +
                "                $type1 : FolderType.INBOX,\n" +
                "                $type2 : " + Mailbox.class.getCanonicalName() + ".FolderType.INBOX,\n" +
                "                $work1 : getFolder(null),\n" +
                "                $work2 : getFolder(" + Mailbox.class.getCanonicalName() + ".FolderType.INBOX),\n" +
                "                $work3 : getFolder(FolderType.INBOX),\n" +
                "                getFolder($type1) != null,\n" +
                "                getFolder($type1).size() > 0,\n" +
                "                ! getFolder($type1).isEmpty(),\n" +
                "                $work6 : folders,\n" +
                "                $work7 : folders.size,\n" +
                "                //folders.containsKey(FolderType.INBOX),\n" +
                "                folders.containsKey(" + Mailbox.class.getCanonicalName() + ".FolderType.INBOX),\n" +
                "                folders.containsKey($type2),\n" +
                "                !folders.isEmpty,\n" +
                "                getFolder(FolderType.INBOX) != null,\n" +
                "                //$v1 : folders[FolderType.INBOX], \n" +
                "                //$v2 : folders[com.sample.Mailbox.FolderType.INBOX],\n" +
                "                //$v3 : folders[com.sample.Mailbox$FolderType.INBOX],\n" +
                "                //folders[$type1]!=null,\n" +
                "                //folders.get(FolderType.INBOX)!=null, // sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl cannot be cast to java.lang.Class\n" +
                "                //folders.isEmpty(),  // SAME ERROR\n" +
                "                0 < 1\n" +
                "        )\n" +
                "    then\n" +
                "end";

        testExpressionConstraints(drl);
    }

    private void testExpressionConstraints(final String drl) {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("constraints-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Mailbox mbox = new Mailbox(Mailbox.TEST_EMAIL);
            final Message message = new Message();
            message.setMessage("Welcome");
            message.setStatus(Message.HELLO);
            mbox.getFolder(Mailbox.FolderType.INBOX).add(message);

            ksession.insert(mbox);
            ksession.insert(message);
            assertEquals(1, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testExpressionConstraints4() {
        final String drl = "package org.drools.compiler.integrationtests\n" +
                "import " + Mailbox.FolderType.class.getCanonicalName() + ";\n" +
                "import " + Mailbox.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "        dialect \"mvel\"\n" +
                "    when\n" +
                "        Mailbox( owneremail == 'bob@mail' || owneremail == 'john@mail' )\n" +
                "    then\n" +
                "end\n" +
                "rule R2\n" +
                "        dialect \"mvel\"\n" +
                "    when\n" +
                "        Mailbox( ( owneremail == 'bob@mail' ) || ( owneremail == 'john@mail' ) )\n" +
                "    then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("constraints-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Mailbox("foo@mail"));
            assertEquals(0, ksession.fireAllRules());

            ksession.insert(new Mailbox("john@mail"));
            assertEquals(2, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testDeeplyNestedCompactExpressions() {
        final String drl = "package org.drools\n" +
                "rule R1\n" +
                " when\n" +
                " Person( age > 10 && ( < 20 || > 30 ) )\n" +
                // nested () are not supported with compact constraints.
                // workaround : use field names explicitly:
                //" Person( age > 10 && ( age < 20 || age > 30 ) )\n" +
                " then\n" +
                "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testConstraintConnectors() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("constraints-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_ConstraintConnectors.drl");
        final KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            final Person youngChili1 = new Person("young chili1");
            youngChili1.setAge(12);
            youngChili1.setHair("blue");
            final Person youngChili2 = new Person("young chili2");
            youngChili2.setAge(25);
            youngChili2.setHair("purple");

            final Person chili1 = new Person("chili1");
            chili1.setAge(35);
            chili1.setHair("red");

            final Person chili2 = new Person("chili2");
            chili2.setAge(38);
            chili2.setHair("indigigo");

            final Person oldChili1 = new Person("old chili1");
            oldChili1.setAge(45);
            oldChili1.setHair("green");

            final Person oldChili2 = new Person("old chili2");
            oldChili2.setAge(48);
            oldChili2.setHair("blue");

            final Person veryold = new Person("very old");
            veryold.setAge(99);
            veryold.setHair("gray");

            ksession.insert(youngChili1);
            ksession.insert(youngChili2);
            ksession.insert(chili1);
            ksession.insert(chili2);
            ksession.insert(oldChili1);
            ksession.insert(oldChili2);
            ksession.insert(veryold);

            ksession.fireAllRules();

            assertEquals(4, results.size());
            assertEquals(chili1, results.get(0));
            assertEquals(oldChili1, results.get(1));
            assertEquals(youngChili1, results.get(2));
            assertEquals(veryold, results.get(3));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testConnectorsAndOperators() {
        final String drl = "package org.drools.compiler\n" +
                "\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "\n" +
                "declare StockTick\n" +
                "    @role( event )\n" +
                "    @timestamp( dateTimestamp )\n" +
                "end\n" +
                "\n" +
                "rule \"operator\"\n" +
                "    when\n" +
                "        $t1 : StockTick( company == \"RHT\" )\n" +
                "        $t2 : StockTick( company == \"IBM\", this after $t1 || before $t1 )\n" +
                "    then\n" +
                "        // do something\n" +
                "end  ";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("constraints-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new StockTick(1, "RHT", 10, 1000));
            ksession.insert(new StockTick(2, "IBM", 10, 1100));
            assertEquals(1, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testConstraintExpression() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    Person( 5*2 > 3 );\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("constraints-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Person("Bob"));
            assertEquals(1, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMethodConstraint() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    Person( isAlive() );\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("constraints-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Person person = new Person("Bob");
            person.setAlive(true);
            ksession.insert(person);
            assertEquals(1, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testDeepNestedConstraints() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"deep nested constraints\"\n" +
                "when\n" +
                "    Person( $likes : likes )\n" +
                "    Cheese( ( ( type == \"stilton\" || type == $likes ) && ( price < 10 || price > 50 ) ) || eval( type.equals(\"brie\") ) )\n" +
                "then\n" +
                "    results.add( \"OK\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("constraints-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            ksession.insert(new Person("bob", "muzzarela"));
            ksession.insert(new Cheese("brie", 10));
            ksession.insert(new Cheese("muzzarela", 80));

            ksession.fireAllRules();
            assertEquals("should have fired twice", 2, list.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMultiRestrictionFieldConstraint() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("constraints-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_MultiRestrictionFieldConstraint.drl");
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list1 = new ArrayList();
            ksession.setGlobal("list1", list1);
            final List list2 = new ArrayList();
            ksession.setGlobal("list2", list2);
            final List list3 = new ArrayList();
            ksession.setGlobal("list3", list3);
            final List list4 = new ArrayList();
            ksession.setGlobal("list4", list4);

            final Person youngChili1 = new Person("young chili1");
            youngChili1.setAge(12);
            youngChili1.setHair("blue");
            final Person youngChili2 = new Person("young chili2");
            youngChili2.setAge(25);
            youngChili2.setHair("purple");

            final Person chili1 = new Person("chili1");
            chili1.setAge(35);
            chili1.setHair("red");

            final Person chili2 = new Person("chili2");
            chili2.setAge(38);
            chili2.setHair("indigigo");

            final Person oldChili1 = new Person("old chili1");
            oldChili1.setAge(45);
            oldChili1.setHair("green");

            final Person oldChili2 = new Person("old chili2");
            oldChili2.setAge(48);
            oldChili2.setHair("blue");

            ksession.insert(youngChili1);
            ksession.insert(youngChili2);
            ksession.insert(chili1);
            ksession.insert(chili2);
            ksession.insert(oldChili1);
            ksession.insert(oldChili2);

            ksession.fireAllRules();

            assertEquals(1, list1.size());
            assertTrue(list1.contains(chili1));

            assertEquals(2, list2.size());
            assertTrue(list2.contains(chili1));
            assertTrue(list2.contains(chili2));

            assertEquals(2, list3.size());
            assertTrue(list3.contains(youngChili1));
            assertTrue(list3.contains(youngChili2));

            assertEquals(2, list4.size());
            assertTrue(list4.contains(youngChili1));
            assertTrue(list4.contains(chili1));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNonBooleanConstraint() {
        final String drl = "package org.drools.compiler\n" +
                "import java.util.List\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    $p1: Person( name + name )\n" +
                "then\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<org.kie.api.builder.Message> messages = kieBuilder.getResults().getMessages();
        Assertions.assertThat(messages).hasSize(1);
        Assertions.assertThat(messages.iterator().next().getText())
                .contains("Predicate 'name + name' must be a Boolean expression")
                .isNotEmpty();
    }

    @Test
    public void testVarargConstraint() {
        // JBRULES-3268
        final String drl = "package org.drools.compiler.test;\n" +
                "import " + VarargBean.class.getCanonicalName() + ";\n" +
                " global java.util.List list;\n" +
                "\n" +
                "rule R1 when\n" +
                "   VarargBean( isOddArgsNr(1, 2, 3) )\n" +
                "then\n" +
                "   list.add(\"odd\");\n" +
                "end\n" +
                "rule R2 when\n" +
                "   VarargBean( isOddArgsNr(1, 2, 3, 4) )\n" +
                "then\n" +
                "   list.add(\"even\");\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("constraints-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.insert(new VarargBean());
            ksession.fireAllRules();
            assertEquals(1, list.size());
            assertTrue(list.contains("odd"));
        } finally {
            ksession.dispose();
        }
    }

    public static class VarargBean {

        public boolean isOddArgsNr(final int... args) {
            return args.length % 2 == 1;
        }
    }

    public static class Mailbox {

        public enum FolderType {
            INBOX,
            SENT,
            TRASH
        }

        public enum MailType {
            WORK,
            HOME,
            OTHER
        }

        public static final String TEST_EMAIL = "me@test.com";

        private final Map<FolderType, List<Message>> folders = new HashMap<>();
        private final Map<String, Date> recentContacts = new HashMap<>();
        private final String owneremail;

        public Mailbox(final String username) {
            owneremail = username;

            // create contact for self
            recentContacts.put(owneremail, new Date());

            // create default folders
            folders.put(FolderType.SENT, new ArrayList<>());
            folders.put(FolderType.TRASH, new ArrayList<>());
            folders.put(FolderType.INBOX, new ArrayList<>());
        }

        /**
         * parameterized accessor
         */
        public List<Message> getFolder(final FolderType t) {
            return folders.get(t);
        }

        public FolderType getDefaultFolderType() {
            return FolderType.INBOX;
        }

        public MailType getMailType() {
            return MailType.WORK;
        }

        public MailType getMailTypeForFolderType(final FolderType pType) {
            return MailType.WORK;
        }

        public Map<FolderType, List<Message>> getFolders() {
            return folders;
        }

        public Map<String, Date> getRecentContacts() {
            return recentContacts;
        }

        public String getOwneremail() {
            return owneremail;
        }
    }
}
