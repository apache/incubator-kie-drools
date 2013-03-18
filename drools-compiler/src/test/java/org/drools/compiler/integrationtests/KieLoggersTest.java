package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.drools.compiler.Message;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.event.rule.AfterMatchFiredEvent;
import org.kie.event.rule.AgendaEventListener;
import org.kie.internal.io.ResourceFactory;
import org.kie.io.Resource;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;
import static org.mockito.Mockito.*;

public class KieLoggersTest {

    @Test
    public void testKieConsoleLogger() throws Exception {
        String drl = "package org.drools.integrationtests\n" + 
        		"import org.drools.compiler.Message;\n" +
        		"rule \"Hello World\"\n" + 
        		"    when\n" + 
        		"        m : Message( myMessage : message )\n" + 
        		"    then\n" + 
        		"end";
        // get the resource
        Resource dt = ResourceFactory.newByteArrayResource( drl.getBytes() ).setTargetPath( "org/drools/integrationtests/hello.drl" );
        
        // create the builder
        KieSession ksession = getKieSession( dt );
        KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newConsoleLogger( ksession );

        ksession.insert( new Message("Hello World") );
        int fired = ksession.fireAllRules();
        assertEquals( 1, fired ); 
        
        logger.close();
    }

    @Test
    public void testKieConsoleLoggerStateless() throws Exception {
        String drl = "package org.drools.integrationtests\n" + 
                "import org.drools.compiler.Message;\n" +
                "rule \"Hello World\"\n" + 
                "    when\n" + 
                "        m : Message( myMessage : message )\n" + 
                "    then\n" + 
                "end";
        // get the resource
        Resource dt = ResourceFactory.newByteArrayResource( drl.getBytes() ).setTargetPath( "org/drools/integrationtests/hello.drl" );
        
        // create the builder
        StatelessKieSession ksession = getStatelessKieSession( dt );
        KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newConsoleLogger( ksession );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );
        
        ksession.execute( new Message("Hello World") );
        
        verify( ael ).afterMatchFired( any(AfterMatchFiredEvent.class) );
        
        logger.close();
    }

    @Test
    public void testKieFileLogger() throws Exception {
        String drl = "package org.drools.integrationtests\n" + 
                "import org.drools.compiler.Message;\n" +
                "rule \"Hello World\"\n" + 
                "    when\n" + 
                "        m : Message( myMessage : message )\n" + 
                "    then\n" + 
                "end";
        // get the resource
        Resource dt = ResourceFactory.newByteArrayResource(drl.getBytes()).setTargetPath( "org/drools/integrationtests/hello.drl" );
        
        // create the builder
        KieSession ksession = getKieSession( dt );
        
        String fileName = "testKieFileLogger";
        File file = new File(fileName+".log");
        if( file.exists() ) {
            file.delete();
        }
        KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newFileLogger( ksession, 
                                                                                        fileName );

        ksession.insert( new Message("Hello World") );
        int fired = ksession.fireAllRules();
        assertEquals( 1, fired ); 
        
        logger.close();

        file = new File( fileName+".log" );
        assertTrue( file.exists() );
        file.delete();
    }

    private KieSession getKieSession(Resource dt) {
        KieServices ks = populateKieFileSystem( dt );

        // get the session
        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        return ksession;
    }

    private StatelessKieSession getStatelessKieSession(Resource dt) {
        KieServices ks = populateKieFileSystem( dt );

        // get the session
        StatelessKieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newStatelessKieSession();
        return ksession;
    }

    private KieServices populateKieFileSystem(Resource dt) {
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( dt );
        KieBuilder kb = ks.newKieBuilder( kfs ).buildAll();
        assertTrue( kb.getResults().getMessages().isEmpty() );
        return ks;
    }
}
