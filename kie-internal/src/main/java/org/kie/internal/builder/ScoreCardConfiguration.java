/*
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

package org.kie.internal.builder;

import org.kie.api.io.ResourceConfiguration;

/**
 * ResourceConfiguration for score cards. It allows for the worksheet name to be specified.
 *
 * <p>
 * Simple example showing how to build a KnowledgeBase from an XLS resource.
 * <p>
 * 
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
 * scconf.setWorksheetName( "Tables_2" );
 * kbuilder.add( ResourceFactory.newUrlResource( "file://IntegrationExampleTest.xls" ),
 *               ResourceType.SCARD,
 *               scconf );
 * assertFalse( kbuilder.hasErrors() );
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 * </pre>
 */
public interface ScoreCardConfiguration
    extends
    ResourceConfiguration {

    /**
     * Which named xls worksheet should be used.
     * This parameter is optional, and a default worksheet
     * will be used if not specified.
     * 
     * @param name
     */
    void setWorksheetName( String name );

    String getWorksheetName();
}
