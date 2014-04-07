package org.drools.workbench.models.guided.scorecard.backend.test2;

import java.util.List;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardXMLPersistence;
import org.drools.workbench.models.guided.scorecard.shared.Attribute;
import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static org.junit.Assert.*;

public class GuidedScoreCardIntegrationJavaClassesAddedToKieFileSystemTest {

    @Test
    public void testEmptyScoreCardCompilation() throws Exception {
        String xml1 = createEmptyGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/Applicant.java",
                   getApplicant() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/ApplicantAttribute.java",
                   getApplicantAttribute() );
        kfs.write( "src/main/resources/org/drools/workbench/models/guided/scorecard/backend/test2/sc1.scgd",
                   xml1 );

        //Add complete Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );
    }

    private String getApplicant() {
        return "package org.drools.workbench.models.guided.scorecard.backend.test2;\n" +
                "public class Applicant {\n" +
                "    private double score;\n" +
                "    public double getScore() {\n" +
                "        return score;\n" +
                "    }\n" +
                "    public void setScore( double score ) {\n" +
                "        this.score = score;\n" +
                "    }\n" +
                "}\n";
    }

    private String getApplicantAttribute() {
        return "package org.drools.workbench.models.guided.scorecard.backend.test2;\n" +
                "public class ApplicantAttribute {\n" +
                "    private int attribute;\n" +
                "    public int getAttribute() {\n" +
                "        return attribute;\n" +
                "    }\n" +
                "    public void setAttribute( int attribute ) {\n" +
                "        this.attribute = attribute;\n" +
                "    }\n" +
                "}";
    }

    @Test
    public void testCompletedScoreCardCompilation() throws Exception {
        String xml1 = createGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/Applicant.java",
                   getApplicant() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/ApplicantAttribute.java",
                   getApplicantAttribute() );
        kfs.write( "src/main/resources/org/drools/workbench/models/guided/scorecard/test2/backend/sc1.scgd",
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
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/Applicant.java",
                   getApplicant() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/ApplicantAttribute.java",
                   getApplicantAttribute() );
        kfs.write( "src/main/resources/org/drools/workbench/models/guided/scorecard/backend/test2/sc1.scgd",
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
        model.setName( "sc1" );
        model.setReasonCodesAlgorithm( "none" );
        model.setBaselineScore( 0.0 );
        model.setInitialScore( 0.0 );
        model.setUseReasonCodes( false );
        model.setPackageName( "org.drools.workbench.models.guided.scorecard.backend.test2" );
        model.setFactName( "Applicant" );
        model.setFieldName( "score" );
        model.setReasonCodeField( "" );

        final Characteristic c = new Characteristic();
        c.setName( "a1" );
        c.setFact( "ApplicantAttribute" );
        c.setField( "attribute" );
        c.setBaselineScore( 0.0 );
        c.setReasonCode( "" );
        c.setDataType( "int" );

        final Attribute a = new Attribute();
        a.setOperator( "=" );
        a.setValue( "10" );
        a.setPartialScore( 0.0 );
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
