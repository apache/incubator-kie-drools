package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.Cheese;
import org.junit.After;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class EnableAuditLogCommandTest {

    private String auditFileDir = "target";
    private String auditFileName = "EnableAuditLogCommandTest";

    @After
    public void cleanUp() {
        File file = new File( auditFileDir + File.separator + auditFileName + ".log" );
        if ( file.exists() ) {
            file.delete();
        }
    }

    @Test
    public void testEnableAuditLogCommand() throws Exception {

        String str = "";
        str += "package org.drools.compiler.integrationtests \n";
        str += "import " + Cheese.class.getCanonicalName() + " \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += " $c : Cheese() \n";
        str += " then \n";
        str += " System.out.println($c); \n";
        str += "end \n";

        StatefulKnowledgeSession kSession = getSession( str );

        List<Command> commands = new ArrayList<Command>();
        commands.add( CommandFactory.newEnableAuditLog( auditFileDir, auditFileName ) );
        commands.add( CommandFactory.newInsert( new Cheese() ) );
        commands.add( CommandFactory.newFireAllRules() );
        kSession.execute( CommandFactory.newBatchExecution( commands ) );
        kSession.dispose();

        File file = new File( auditFileDir + File.separator + auditFileName + ".log" );

        assertTrue( file.exists() );

    }

    private StatefulKnowledgeSession getSession( String drl ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.err.println( kbuilder.getErrors() );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return kbase.newStatefulKnowledgeSession();
    }
}
