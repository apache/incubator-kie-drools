package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.drools.compiler.Message;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.Resource;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
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
    public void testDeclarativeKieConsoleLogger() throws Exception {
        String drl = "package org.drools.integrationtests\n" +
                     "import org.drools.compiler.Message;\n" +
                     "rule \"Hello World\"\n" +
                     "    when\n" +
                     "        m : Message( myMessage : message )\n" +
                     "    then\n" +
                     "end";

        KieServices ks = KieServices.Factory.get();
        KieModuleModel kproj = ks.newKieModuleModel();

        kproj.newKieBaseModel("KBase1").newKieSessionModel("KSession1").setConsoleLogger("logger");

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        kfs.write("src/main/resources/KBase1/rule.drl", drl);

        KieModule kieModule = ks.newKieBuilder(kfs).buildAll().getKieModule();
        KieContainer kieContainer = ks.newKieContainer(kieModule.getReleaseId());

        KieSession ksession = kieContainer.newKieSession("KSession1");
        ksession.insert( new Message("Hello World") );
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);

        KieRuntimeLogger logger = ksession.getLogger();
        assertNotNull(logger);
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
        Resource dt = ResourceFactory.newByteArrayResource( drl.getBytes() ).setTargetPath("org/drools/integrationtests/hello.drl");
        
        // create the builder
        StatelessKieSession ksession = getStatelessKieSession(dt);
        KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newConsoleLogger( ksession );

        AgendaEventListener ael = mock( AgendaEventListener.class );
        ksession.addEventListener( ael );
        
        ksession.execute( new Message("Hello World") );
        
        verify( ael ).afterMatchFired( any(AfterMatchFiredEvent.class) );
        
        logger.close();
    }

    @Test
    public void testDeclarativeKieConsoleLoggerStateless() throws Exception {
        String drl = "package org.drools.integrationtests\n" +
                     "import org.drools.compiler.Message;\n" +
                     "rule \"Hello World\"\n" +
                     "    when\n" +
                     "        m : Message( myMessage : message )\n" +
                     "    then\n" +
                     "end";

        KieServices ks = KieServices.Factory.get();
        KieModuleModel kproj = ks.newKieModuleModel();

        kproj.newKieBaseModel("KBase1")
             .newKieSessionModel("KSession1")
             .setType(KieSessionModel.KieSessionType.STATELESS)
             .setConsoleLogger("logger");

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        kfs.write("src/main/resources/KBase1/rule.drl", drl);

        KieModule kieModule = ks.newKieBuilder(kfs).buildAll().getKieModule();
        KieContainer kieContainer = ks.newKieContainer(kieModule.getReleaseId());

        StatelessKieSession ksession = kieContainer.newStatelessKieSession("KSession1");
        ksession.execute( new Message("Hello World") );

        KieRuntimeLogger logger = ksession.getLogger();
        assertNotNull(logger);
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
        KieSession ksession = getKieSession(dt);

        String fileName = "testKieFileLogger";
        File file = new File(fileName+".log");
        if( file.exists() ) {
            file.delete();
        }
        KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newFileLogger( ksession,
                                                                                        fileName );

        ksession.insert(new Message("Hello World"));
        int fired = ksession.fireAllRules();
        assertEquals( 1, fired );

        logger.close();

        file = new File( fileName+".log" );
        assertTrue( file.exists() );
        file.delete();
    }

    @Test
    public void testDeclarativeKieFileLogger() throws Exception {
        String drl = "package org.drools.integrationtests\n" + 
                "import org.drools.compiler.Message;\n" +
                "rule \"Hello World\"\n" + 
                "    when\n" + 
                "        m : Message( myMessage : message )\n" + 
                "    then\n" + 
                "end";

        String fileName = "testKieFileLogger";
        File file = new File(fileName+".log");
        if( file.exists() ) {
            file.delete();
        }

        KieServices ks = KieServices.Factory.get();
        KieModuleModel kproj = ks.newKieModuleModel();

        kproj.newKieBaseModel("KBase1").newKieSessionModel("KSession1").setFileLogger( fileName );

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        kfs.write("src/main/resources/KBase1/rule.drl", drl);

        KieModule kieModule = ks.newKieBuilder(kfs).buildAll().getKieModule();
        KieContainer kieContainer = ks.newKieContainer(kieModule.getReleaseId());

        KieSession ksession = kieContainer.newKieSession("KSession1");
        
        ksession.insert( new Message("Hello World") );
        int fired = ksession.fireAllRules();
        assertEquals( 1, fired );

        // disposing the ksession also flushes and closes the logger
        ksession.dispose();

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
