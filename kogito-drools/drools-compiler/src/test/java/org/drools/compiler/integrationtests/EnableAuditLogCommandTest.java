package org.drools.compiler.integrationtests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.Cheese;
import org.junit.After;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertTrue;

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

        KieSession kSession = new KieHelper().addContent( str, ResourceType.DRL )
                                             .build().newKieSession();

        List<Command> commands = new ArrayList<Command>();
        commands.add( CommandFactory.newEnableAuditLog( auditFileDir, auditFileName ) );
        commands.add( CommandFactory.newInsert( new Cheese() ) );
        commands.add( CommandFactory.newFireAllRules() );
        kSession.execute( CommandFactory.newBatchExecution( commands ) );
        kSession.dispose();

        File file = new File( auditFileDir + File.separator + auditFileName + ".log" );

        assertTrue( file.exists() );

    }
}
