package org.drools.builder;

import java.io.Reader;
import java.net.URL;
import java.util.Collection;

import org.drools.definition.KnowledgePackage;
import org.drools.io.Resource;

/**
 * <p>
 * The KnowledgeBuilder is responsible for taking source files, such as a .drl file or an xls file,
 * and turning them into a KnowledgePackage of rule and process definitions which a KnowledgeBase
 * can consume. It uses the KnowledgeType enum to tell it the type of the resource it is being asked to build.
 * </p>
 * 
 * <p>
 * The ResourceFactory provides capabilities to load Resources from a number of sources; such as
 * Reader, ClassPath, URL, File, ByteArray. Binaries, such as xls decision tables,
 * should not use a Reader based Resource handler, which is only suitable for text based resources.
 * </p>
 * 
 * <p>
 * Always check the hasErrors() method after an addition, you should not add more resources
 * or get the KnowledgePackages if there are errors; KnowledgePackages() will return empty
 * if there are errors.
 * </p>
 * 
 * <p>
 * Simple example showing how to build a KnowledgeBase from an DRL rule resource.
 * </p>
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * kbuilder.add( ResourceFactory.newUrlResource( "file://myrules.drl" ),
 *                       KnowledgeType.DRL);
 * assertFalse( kbuilder.hasErrors() );
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 * </pre>
 * 
 * <p>
 * Simple example showing how to build a KnowledgeBase from an XLS decision table resource.
 * </p>
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
 * dtconf.setInputType( DecisionTableInputType.XLS );
 * dtconf.setWorksheetName( "Tables_2" );
 * kbuilder.add( ResourceFactory.newUrlResource( "file://IntegrationExampleTest.xls" ),
 *               KnowledgeType.DTABLE,
                 dtconf );
 * assertFalse( kbuilder.hasErrors() );
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 * </pre>
 * 
 * <p>
 * Simple example showing how to build a KnowledgeBase from an DRF flow resource.
 * <p>
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * kbuilder.add( ResourceFactory.newUrlResource( "file://myflow.rf" ),
 *               KnowledgeType.DRF);
 * assertFalse( kbuilder.hasErrors() );
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 * </pre>
 * <p>
 * If there are errors a simple toString can print the errors
 * </p>
 * <pre>
 * if ( kbuilder.hasErrors() ) {
 *     log.exception( kbuilder.getErrors().toString() )
 * }
 * </pre>     
 * 
 */
public interface KnowledgeBuilder
    extends
    RuleBuilder,
    ProcessBuilder {

    /**
     * A a resource of the KnowledgeType from a given URL.
     * 
     * @param url
     * @param type
     */
    void add(Resource resource,
             ResourceType type);

    /**
    * A a resource of the KnowledgeType from a given URL, using the provided ResourceConfiguration.
    * Currently only only decision tables use this, via the DecisionTableResourceConfiguration class.
    */
    void add(Resource resource,
             ResourceType type,
             ResourceConfiguration configuration);

    /**
     * Returns the built packages.
     * 
     * If the KnowledgeBuilder has errors the Collection will be empty. The hasErrors()
     * method should always be checked first, to make sure you are getting the packages
     * that you wanted built.
     * 
     * @return
     *     The Collection of KnowledgePackages
     */
    Collection<KnowledgePackage> getKnowledgePackages();

    /**
     * If errors occurred during the build process they are added here
     * @return
     */
    boolean hasErrors();

    /**
     * Return errors that occurred during the build process.
     * @return
     */
    KnowledgeBuilderErrors getErrors();

}
