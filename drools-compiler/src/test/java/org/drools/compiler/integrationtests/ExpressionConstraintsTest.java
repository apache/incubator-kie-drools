/*
 * Copyright 2015 JBoss Inc
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

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Mailbox;
import org.drools.compiler.Mailbox.FolderType;
import org.drools.compiler.Message;
import org.drools.core.io.impl.ByteArrayResource;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * This is a sample class to launch a rule.
 */
public class ExpressionConstraintsTest extends CommonTestMethodBase {
    
    @Test
    public void testExpressionConstraints1() {
        String drl = "package org.drools.compiler.integrationtests\n" +
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
        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        Mailbox mbox = new Mailbox(Mailbox.TEST_EMAIL);
        Message message = new Message();
        message.setMessage("Welcome");
        message.setStatus(Message.HELLO);
        mbox.getFolder(FolderType.INBOX).add(message);

        ksession.insert(mbox);
        ksession.insert(message);
        int rules = ksession.fireAllRules();
        
        Assert.assertEquals( 1, rules );
    }

    @Test
    public void testExpressionConstraints2() {
        String drl = "package org.drools.compiler.integrationtests\n" +
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
        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        Mailbox mbox = new Mailbox(Mailbox.TEST_EMAIL);
        Message message = new Message();
        message.setMessage("Welcome");
        message.setStatus(Message.HELLO);
        mbox.getFolder(FolderType.INBOX).add(message);

        ksession.insert(mbox);
        ksession.insert(message);
        int rules = ksession.fireAllRules();
        
        Assert.assertEquals( 1, rules );
    }

    @Test
    public void testExpressionConstraints3() {
        String drl = "package org.drools.compiler.integrationtests\n" +
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
        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        Mailbox mbox = new Mailbox(Mailbox.TEST_EMAIL);
        Message message = new Message();
        message.setMessage("Welcome");
        message.setStatus(Message.HELLO);
        mbox.getFolder(FolderType.INBOX).add(message);

        ksession.insert(mbox);
        ksession.insert(message);
        int rules = ksession.fireAllRules();
        
        Assert.assertEquals( 1, rules );
    }

    @Test
    public void testExpressionConstraints4() {
        String drl = "package org.drools.compiler.integrationtests\n" +
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
        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Mailbox("foo@mail"));
        int rules = ksession.fireAllRules();
        Assert.assertEquals( 0, rules );

        ksession.insert(new Mailbox("john@mail"));
        rules = ksession.fireAllRules();
        Assert.assertEquals( 2, rules );
    }

    @Test
    public void testDeeplyNestedCompactExpressions() {
        String drl = "package org.drools\n" +
                     "rule R1\n" +
                     " when\n" +
                     " Person( age > 10 && ( < 20 || > 30 ) )\n" +
                     // nested () are not supported with compact constraints.
                     // workaround : use field names explicitly:
                     //" Person( age > 10 && ( age < 20 || age > 30 ) )\n" +
                     " then\n" +
                     "end\n";

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new ByteArrayResource( drl.getBytes() ), ResourceType.DRL );
        assertTrue( knowledgeBuilder.hasErrors() );
    }
}
