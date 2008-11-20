package org.drools.builder;

/**
 * ResourceConfiguration for decision tables. It allows for the type of the decision, XLS or CSV, to be specified
 * and optionally allows a worksheet name to also be specified.
 *
 * <p>
 * Simple example showing how to build a KnowledgeBase from an XLS resource.
 * <p>
 * 
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
 * dtconf.setInputType( DecisionTableInputType.XLS );
 * dtconf.setWorksheetName( "Tables_2" );
 * kbuilder.addResource( new URL( "file://IntegrationExampleTest.xls" ),
 *                       KnowledgeType.DTABLE,
 *                       dtconf );
 * assertFalse( kbuilder.hasErrors() );
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 * </pre>
 * 
 */
public interface DecisionTableConfiguration
    extends
    ResourceConfiguration {

    /**
     * Specify the type of decision table resource,  currently either XLS or CSV.
     * This parameter is mandatory.
     * @param inputType
     */
    void setInputType(DecisionTableInputType inputType);

    DecisionTableInputType getInputType();

    /**
     * Which named xls worksheet should be used.
     * This parameter is optional, and a default worksheet
     * will be used if not specified.
     * 
     * @param name
     */
    void setWorksheetName(String name);

    String getWorksheetName();
}
