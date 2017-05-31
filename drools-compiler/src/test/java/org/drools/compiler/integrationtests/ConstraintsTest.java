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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Mailbox;
import org.drools.compiler.Mailbox.FolderType;
import org.drools.compiler.Message;
import org.drools.compiler.Person;
import org.drools.compiler.StockTick;
import org.drools.core.io.impl.ByteArrayResource;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class ConstraintsTest extends CommonTestMethodBase {
    
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

        final KieBase kbase = loadKnowledgeBaseFromString(drl);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Mailbox mbox = new Mailbox(Mailbox.TEST_EMAIL);
        final Message message = new Message();
        message.setMessage("Welcome");
        message.setStatus(Message.HELLO);
        mbox.getFolder(FolderType.INBOX).add(message);

        ksession.insert(mbox);
        ksession.insert(message);
        final int rules = ksession.fireAllRules();

        Assert.assertEquals(1, rules);
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

        final KieBase kbase = loadKnowledgeBaseFromString(drl);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Mailbox mbox = new Mailbox(Mailbox.TEST_EMAIL);
        final Message message = new Message();
        message.setMessage("Welcome");
        message.setStatus(Message.HELLO);
        mbox.getFolder(FolderType.INBOX).add(message);

        ksession.insert(mbox);
        ksession.insert(message);
        final int rules = ksession.fireAllRules();

        Assert.assertEquals(1, rules);
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
                     "                $type2 : org.drools.compiler.Mailbox$FolderType.INBOX,\n" +
                     "                $work1 : getFolder(null),\n" + 
                     "                $work2 : getFolder(org.drools.compiler.Mailbox$FolderType.INBOX),\n" +
                     "                $work3 : getFolder(FolderType.INBOX),\n" + 
                     "                getFolder($type1) != null,\n" + 
                     "                getFolder($type1).size() > 0,\n" + 
                     "                ! getFolder($type1).isEmpty(),\n" + 
                     "                $work6 : folders,\n" + 
                     "                $work7 : folders.size,\n" + 
                     "                //folders.containsKey(FolderType.INBOX),\n" + 
                     "                folders.containsKey(org.drools.compiler.Mailbox$FolderType.INBOX),\n" +
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

        final KieBase kbase = loadKnowledgeBaseFromString(drl);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Mailbox mbox = new Mailbox(Mailbox.TEST_EMAIL);
        final Message message = new Message();
        message.setMessage("Welcome");
        message.setStatus(Message.HELLO);
        mbox.getFolder(FolderType.INBOX).add(message);

        ksession.insert(mbox);
        ksession.insert(message);
        final int rules = ksession.fireAllRules();

        Assert.assertEquals(1, rules);
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

        final KieBase kbase = loadKnowledgeBaseFromString(drl);
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Mailbox("foo@mail"));
        int rules = ksession.fireAllRules();
        Assert.assertEquals(0, rules);

        ksession.insert(new Mailbox("john@mail"));
        rules = ksession.fireAllRules();
        Assert.assertEquals(2, rules);
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

        final KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(new ByteArrayResource(drl.getBytes()), ResourceType.DRL);
        assertTrue(knowledgeBuilder.hasErrors());
    }

    @Test
    public void testConstraintConnectors() throws IOException, ClassNotFoundException {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ConstraintConnectors.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

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
    }

    @Test
    public void testConnectorsAndOperators() throws IOException, ClassNotFoundException {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ConstraintConnectorsAndOperators.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new StockTick(1, "RHT", 10, 1000));
        ksession.insert(new StockTick(2, "IBM", 10, 1100));
        final int fired = ksession.fireAllRules();

        assertEquals(1, fired);
    }

    @Test
    public void testConstraintExpression() {
        final String str = "package org.drools.compiler\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    Person( 5*2 > 3 );\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Person("Bob"));

        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testMethodConstraint() {
        final String str = "package org.drools.compiler\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    Person( isAlive() );\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Person person = new Person("Bob");
        person.setAlive(true);
        ksession.insert(person);

        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testDeepNestedConstraints() throws IOException, ClassNotFoundException {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_DeepNestedConstraints.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        ksession.insert(new Person("bob", "muzzarela"));
        ksession.insert(new Cheese("brie", 10));
        ksession.insert(new Cheese("muzzarela", 80));

        ksession.fireAllRules();

        assertEquals("should have fired twice", 2, list.size());
    }

    @Test
    public void testMultiRestrictionFieldConstraint() throws IOException, ClassNotFoundException {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_MultiRestrictionFieldConstraint.drl"));
        final KieSession ksession = kbase.newKieSession();

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
    }

    @Test
    public void testNonBooleanConstraint() {
        final String str = "package org.drools.compiler\n" +
                "import java.util.List\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    $p1: Person( name + name )\n" +
                "then\n" +
                "end";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testVarargConstraint() throws Exception {
        // JBRULES-3268
        final String str = "package org.drools.compiler.test;\n" +
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

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert(new VarargBean());
        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertTrue(list.contains("odd"));
    }

    public static class VarargBean {
        public boolean isOddArgsNr(final int... args) {
            return args.length % 2 == 1;
        }
    }
}
