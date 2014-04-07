package org.drools.workbench.models.guided.scorecard.backend.test1;

import java.util.List;

import org.drools.workbench.models.guided.scorecard.backend.base.Helper;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static org.junit.Assert.*;

public class GuidedScoreCardIntegrationJavaClassesOnClassPathTest {

    @Test
    public void testEmptyScoreCardCompilation() throws Exception {
        String xml1 = Helper.createEmptyGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "pom.xml",
                   Helper.getPom() );
        kfs.write( "src/main/resources/META-INF/kmodule.xml",
                   Helper.getKModule() );
        kfs.write( "src/main/resources/sc1.scgd",
                   xml1 );

        //Add complete Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        Helper.dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );
    }

    @Test
    public void testCompletedScoreCardCompilation() throws Exception {
        String xml1 = Helper.createGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "pom.xml",
                   Helper.getPom() );
        kfs.write( "src/main/resources/META-INF/kmodule.xml",
                   Helper.getKModule() );
        kfs.write( "src/main/resources/sc1.scgd",
                   xml1 );

        //Add complete Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        Helper.dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );
    }

    @Test
    public void testIncrementalCompilation() throws Exception {
        String xml1_1 = Helper.createEmptyGuidedScoreCardXML();
        String xml1_2 = Helper.createGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "pom.xml",
                   Helper.getPom() );
        kfs.write( "src/main/resources/META-INF/kmodule.xml",
                   Helper.getKModule() );
        kfs.write( "src/main/resources/sc1.scgd",
                   xml1_1 );

        //Add empty Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        Helper.dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );

        //Update with complete Score Card
        kfs.write( "src/main/resources/sc1.scgd",
                   xml1_2 );
        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();

        final List<Message> addedMessages = results.getAddedMessages();
        final List<Message> removedMessages = results.getRemovedMessages();
        Helper.dumpMessages( addedMessages );
        assertEquals( 0,
                      addedMessages.size() );
        Helper.dumpMessages( removedMessages );
        assertEquals( 0,
                      removedMessages.size() );
    }

}
