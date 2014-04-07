package org.drools.workbench.models.guided.scorecard.backend.test1;

import java.util.List;

import org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardXMLPersistence;
import org.drools.workbench.models.guided.scorecard.shared.Attribute;
import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
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
        String xml1 = createEmptyGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/sc1.scgd",
                                                         xml1 );

        //Add complete Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );
    }

    @Test
    public void testCompletedScoreCardCompilation() throws Exception {
        String xml1 = createGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/sc1.scgd",
                                                         xml1 );

        //Add complete Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );
    }

    @Test
    public void testIncrementalCompilation() throws Exception {
        String xml1_1 = createEmptyGuidedScoreCardXML();
        String xml1_2 = createGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/sc1.scgd",
                                                         xml1_1 );

        //Add empty Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );

        //Update with complete Score Card
        kfs.write( "src/main/resources/sc1.scgd",
                   xml1_2 );
        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();

        final List<Message> addedMessages = results.getAddedMessages();
        final List<Message> removedMessages = results.getRemovedMessages();
        dumpMessages( addedMessages );
        assertEquals( 0,
                      addedMessages.size() );
        dumpMessages( removedMessages );
        assertEquals( 0,
                      removedMessages.size() );
    }

    private ScoreCardModel createEmptyGuidedScoreCard() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( "test" );
        return model;
    }

    private String createEmptyGuidedScoreCardXML() {
        final ScoreCardModel model = createEmptyGuidedScoreCard();
        return GuidedScoreCardXMLPersistence.getInstance().marshal( model );
    }

    private ScoreCardModel createGuidedScoreCard() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( "test" );

        model.setPackageName( "org.drools.workbench.models.guided.scorecard.backend.test1" );
        model.setReasonCodesAlgorithm( "none" );
        model.setBaselineScore( 0.0 );
        model.setInitialScore( 0.0 );

        model.setFactName( "org.drools.workbench.models.guided.scorecard.backend.test1.Applicant" );
        model.setFieldName( "score" );
        model.setUseReasonCodes( false );
        model.setReasonCodeField( "" );

        final Characteristic c = new Characteristic();
        c.setName( "c1" );
        c.setFact( "org.drools.workbench.models.guided.scorecard.backend.test1.ApplicantAttribute" );
        c.setDataType( "int" );
        c.setField( "attribute" );
        c.setBaselineScore( 0.0 );
        c.setReasonCode( "" );

        final Attribute a = new Attribute();
        a.setOperator( "=" );
        a.setValue( "10" );
        a.setPartialScore( 0.1 );
        a.setReasonCode( "" );

        c.getAttributes().add( a );
        model.getCharacteristics().add( c );

        return model;
    }

    private String createGuidedScoreCardXML() {
        final ScoreCardModel model = createGuidedScoreCard();
        return GuidedScoreCardXMLPersistence.getInstance().marshal( model );
    }

    private void dumpMessages( final List<Message> messages ) {
        if ( !messages.isEmpty() ) {
            for ( Message m : messages ) {
                System.out.println( m.getText() );
            }
        }
    }

}
