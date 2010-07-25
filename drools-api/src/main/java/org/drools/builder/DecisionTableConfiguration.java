/**
 * Copyright 2010 JBoss Inc
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
 * kbuilder.add( ResourceFactory.newUrlResource( "file://IntegrationExampleTest.xls" ),
 *               ResourceType.DTABLE,
 *               dtconf );
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
