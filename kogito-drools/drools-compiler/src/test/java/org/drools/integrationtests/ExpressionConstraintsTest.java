package org.drools.integrationtests;

import static org.junit.Assert.fail;

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
public class ExpressionConstraintsTest {
    
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
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

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
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

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
