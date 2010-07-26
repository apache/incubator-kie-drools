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

/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.drools.examples.sudoku.rules;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;

public class DroolsUtil {
    private static DroolsUtil INSTANCE;

    private DroolsUtil() {

    }

    public static DroolsUtil getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new DroolsUtil();
        }

        return INSTANCE;
    }

    public KnowledgeBase readRuleBase(String drlFileName) throws Exception {
        //optionally read in the DSL (if you are using it).
        //Reader dsl = new InputStreamReader( DroolsTest.class.getResourceAsStream( "/mylang.dsl" ) );

        //Use package builder to build up a rule package.
        //An alternative lower level class called "DrlParser" can also be used...

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        //this will parse and compile in one step
        kbuilder.add( ResourceFactory.newClassPathResource( drlFileName,DroolsSudokuGridModel.class),
                             ResourceType.DRL );

        //Use the following instead of above if you are using a DSL:
        //builder.addPackageFromDrl( source, dsl );

        //add the package to a rulebase (deploy the rule package).
        KnowledgeBaseConfiguration kbaseconfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbaseconfiguration.setProperty( "drools.removeIdentities",
                                   "true" );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseconfiguration );
        //        RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
        //                                                         conf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }
}
