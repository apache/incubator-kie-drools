package org.drools.integrationtests;

import static org.junit.Assert.fail;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Mailbox;
import org.drools.Mailbox.FolderType;
import org.drools.Message;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Assert;
import org.junit.Test;

/**
 * This is a sample class to launch a rule.
 */
public class ExpressionConstraintsTest extends CommonTestMethodBase {
    
    @Test
    public void testExpressionConstraints1() {
        String drl = "package org.drools\n" + 
        		     "import org.drools.Mailbox.FolderType;\n" + 
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
        String drl = "package org.drools\n" +
                     "import org.drools.Mailbox.FolderType;\n" + 
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
        String drl = "package org.drools\n" +
                     "import org.drools.Mailbox.FolderType\n" + 
                     "rule R1\n" + 
                     "    dialect \"mvel\"\n" + 
                     "    when\n" + 
                     "        $m : Mailbox( \n" + 
                     "                $type1 : FolderType.INBOX,\n" + 
                     "                $type2 : org.drools.Mailbox$FolderType.INBOX,\n" + 
                     "                $work1 : getFolder(null),\n" + 
                     "                $work2 : getFolder(org.drools.Mailbox$FolderType.INBOX),\n" + 
                     "                $work3 : getFolder(FolderType.INBOX),\n" + 
                     "                getFolder($type1) != null,\n" + 
                     "                getFolder($type1).size() > 0,\n" + 
                     "                ! getFolder($type1).isEmpty(),\n" + 
                     "                $work6 : folders,\n" + 
                     "                $work7 : folders.size,\n" + 
                     "                //folders.containsKey(FolderType.INBOX),\n" + 
                     "                folders.containsKey(org.drools.Mailbox$FolderType.INBOX),\n" + 
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
        String drl = "package org.drools\n" + 
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

    
    
    private KnowledgeBase loadKnowledgeBaseFromString( String... drlContentStrings ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for ( String drlContentString : drlContentStrings ) {
            kbuilder.add( ResourceFactory.newByteArrayResource( drlContentString.getBytes() ),
                          ResourceType.DRL );
        }

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

}
