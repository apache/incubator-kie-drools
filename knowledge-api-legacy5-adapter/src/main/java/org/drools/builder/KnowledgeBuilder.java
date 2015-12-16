/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.builder;

import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;
import org.drools.io.Resource;

/**
 * <p>
 * The KnowledgeBuilder is responsible for taking source files, such as a .drl file, a .bpmn2 file or an .xls file,
 * and turning them into a KnowledgePackage of rule and process definitions which a KnowledgeBase
 * can consume. It uses the ResourceType enum to tell it the type of the resource it is being asked to build.
 * 
 * </p>
 * 
 * <p>
 * The ResourceFactory provides capabilities to load Resources from a number of sources; such as
 * Reader, ClassPath, URL, File, ByteArray. Binaries, such as XLS decision tables,
 * should not use a Reader based Resource handler, which is only suitable for text based resources.
 * </p>
 * 
 * <p>
 * It is best practice to always check the hasErrors() method after an addition, you should not add 
 * more resources or get the KnowledgePackages if there are errors. getKnowledgePackages() will return 
 * an empty list if there are errors.
 * </p>
 * 
 * <p>
 * You can create a new KnowledgeBase for all the resources that were added using this builder using
 * the newKnowledgeBase() method.  This will throw an exception if there are errors for any of the
 * resources.
 * </p>
 * 
 * <p>
 * Simple example showing how to build a KnowledgeBase from an DRL rule resource.
 * </p>
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * kbuilder.add( ResourceFactory.newUrlResource( "file://myrules.drl" ),
 *                       ResourceType.DRL);
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
 *               ResourceType.DTABLE,
                 dtconf );
 * assertFalse( kbuilder.hasErrors() );
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 * </pre>
 * 
 * <p>
 * Simple example showing how to build a KnowledgeBase from an BPMN2 process resource.
 * <p>
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * kbuilder.add( ResourceFactory.newUrlResource( "file://myProcess.bpmn2" ),
 *               ResourceType.BPMN2 );
 * KnowledgeBase kbase = kbuilder.newKnowledgeBase();
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
 * <p>
 * The KnowledgeBuilder can also be built from a configuration using the XML change-set format and
 * the ResourceType.CHANGE_SET value. While change-set supports add, remove, and modify as elements.
 * KnowledgeBuilder will only process add. If the resource element provided points to a directory, all
 * files found in that directory will be added. Currently the knowledge type is not derived from the
 * file extension and must be explicitly set in the XML for the resource. It is expected that all
 * resources in a directory given resource are of the specified type.
 * </p>
 * <pre>
 * &lt;change-set xmlns='http://drools.org/drools-5.0/change-set'
 *             xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'
 *             xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' &gt;
 *  &lt;add&gt;
 *       &lt;resource source='http:org/domain/myrules.drl' type='DRL' /&gt;
 *       &lt;resource source='classpath:data/IntegrationExampleTest.xls' type="DTABLE"&gt;
 *           &lt;decisiontable-conf input-type="XLS" worksheet-name="Tables_2" /&gt;
 *       &lt;/resource&gt;
 *       &lt;resource source='file:org/drools/decisiontable/myflow.drf' type='DRF' /&gt;
 *   &lt;/add&gt;
 * &lt;/change-set&gt;
 * </pre>
 * 
 * <pre>
 * KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "test agent", // the name of the agent
 *                                                                  kaconf );
 * kagent.applyChangeSet( ResourceFactory.newUrlResource( url ) ); // resource to the change-set xml for the resources to add
 * </pre>
 */
public interface KnowledgeBuilder
    extends
    RuleBuilder,
    ProcessBuilder {

    /**
     * Add a resource of the given ResourceType, using the default resource configuration.
     * 
     * @param resource the Resource to add
     * @param type the resource type
     */
    void add(Resource resource,
             ResourceType type);

   
    /**
     * Add a resource of the given ResourceType, using the provided ResourceConfiguration.
     * Resources can be created by calling any of the "newX" factory methods of
     * ResourceFactory. The kind of resource (DRL,  XDRL, DSL,... CHANGE_SET) must be
     * indicated by the second argument.
     *
     * @param resource the Resource to add
     * @param type the resource type
     * @param configuration the resource configuration
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
     * Creates a new KnowledgeBase from the knowledge packages that have been added to
     * this builder.  An exception is thrown if there are any errors.
     */
    KnowledgeBase newKnowledgeBase();

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
    
    /**
     * Return the knowledge builder results for the listed severities.
     * 
     * @param severities
     * @return
     */
    KnowledgeBuilderResults getResults(ResultSeverity...severities );
    
        
    /**
     * Checks if the builder generated any results of the listed severities
     * @param severities
     * @return
     */
    boolean hasResults(ResultSeverity...severities );

    /**
     * Remove the last added Resource.
     * Can be useful in case this last addition generated some compilation problem.
     * If multiple Resources have been added in batch using a CompositeKnowledgeBuilder,
     * it removes all of them.
     */
    void undo();

    /**
     * Return a CompositeKnowledgeBuilder allowing to add multiple Resources
     * at the same time, without worrying about cross dependencies among them.
     * @return
     */
    CompositeKnowledgeBuilder batch();
}
