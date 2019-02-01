/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.guided.scorecard.backend.base;

import java.util.List;

import org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardXMLPersistence;
import org.drools.workbench.models.guided.scorecard.shared.Attribute;
import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.kie.api.builder.Message;

public class Helper {

    public static String getPom() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "  <groupId>org.anstis</groupId>\n" +
                "  <artifactId>p0</artifactId>\n" +
                "  <version>1.0</version>\n" +
                "  <packaging>kjar</packaging>\n" +
                "  <name>p0</name>\n" +
                "</project>";
    }

    public static String getKModule() {
        return "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>";
    }

    public static String getApplicant() {
        return "package org.drools.workbench.models.guided.scorecard.backend.test2;\n" +
                "public class Applicant {\n" +
                "    private double calculatedScore;\n" +
                "    public double getCalculatedScore() {\n" +
                "        return calculatedScore;\n" +
                "    }\n" +
                "    public void setCalculatedScore( double score ) {\n" +
                "        this.calculatedScore = score;\n" +
                "    }\n" +
                "}\n";
    }

    public static String getApplicantAttribute() {
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

    public static ScoreCardModel createEmptyGuidedScoreCard() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( "test" );
        model.setPackageName("org.drools.workbench.models.guided.scorecard.backend.test2");
        model.setBaselineScore(0.0);
        model.setInitialScore(0.0);
        model.setFactName( "Applicant" );
        model.setFieldName( "calculatedScore" );
        model.setUseReasonCodes( false );
        model.setReasonCodeField( "" );
        return model;
    }

    public static String createEmptyGuidedScoreCardXML() {
        final ScoreCardModel model = createEmptyGuidedScoreCard();
        return GuidedScoreCardXMLPersistence.getInstance().marshal( model );
    }

    /**
     * This method creates a scorecard that does not have the fully qualified
     * class names for the Applicant and ApplicantAttribute fact types.
     * Note: The generated classes must be in the same package as the scorecard.
     *
     * @return
     */
    public static ScoreCardModel createGuidedScoreCardShortFactName() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName("test_short");

        model.setPackageName("org.drools.workbench.models.guided.scorecard.backend.test2");
        model.setReasonCodesAlgorithm( "none" );
        model.setBaselineScore( 0.0 );
        model.setInitialScore( 0.0 );

        model.setFactName( "Applicant" );
        model.setFieldName( "calculatedScore" );
        model.setUseReasonCodes( false );
        model.setReasonCodeField( "" );

        final Characteristic c = new Characteristic();
        c.setName( "c1" );
        c.setFact( "ApplicantAttribute" );
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

    /**
     * Creates a scorecard that has the fully qualified class names
     * for the Applicant and ApplicantAttribute fact types.
     *
     * @return
     */
    public static ScoreCardModel createGuidedScoreCard() {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( "test" );

        model.setPackageName( "org.drools.workbench.models.guided.scorecard.backend.test1" );
        model.setReasonCodesAlgorithm( "none" );
        model.setBaselineScore( 0.0 );
        model.setInitialScore( 0.0 );

        model.setFactName( "org.drools.workbench.models.guided.scorecard.backend.test1.Applicant" );
        model.setFieldName( "calculatedScore" );
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

    public static String createGuidedScoreCardXML(boolean useShortFactName) {
        final ScoreCardModel model = useShortFactName ? createGuidedScoreCardShortFactName() : createGuidedScoreCard();
        return GuidedScoreCardXMLPersistence.getInstance().marshal( model );
    }

    public static void dumpMessages( final List<Message> messages ) {
        if ( !messages.isEmpty() ) {
            for ( Message m : messages ) {
                System.out.println( m.getText() );
            }
        }
    }

}
