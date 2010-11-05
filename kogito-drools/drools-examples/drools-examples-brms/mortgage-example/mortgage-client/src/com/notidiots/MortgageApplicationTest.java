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

package com.notidiots;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class MortgageApplicationTest {

    /**
     * Entry point demonstrating use of KnowledgeAgent and changesets retrieving
     * a rule package from a running instance of Guvnor.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

    	StatefulKnowledgeSession ksession = null;
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase();

            //Dynamic fact creation as the model was declared in the DRL
            FactType appType = kbase
                    .getFactType( "mortgages",
                                  "LoanApplication" );
            Object application = appType.newInstance();
            appType.set( application,
                         "amount",
                         25000 );
            appType.set( application,
                         "deposit",
                         1500 );
            appType.set( application,
                         "lengthYears",
                         20 );

            FactType incomeType = kbase
                    .getFactType( "mortgages",
                                  "IncomeSource" );
            Object income = incomeType.newInstance();
            incomeType.set( income,
                            "type",
                            "Job" );
            incomeType.set( income,
                            "amount",
                            65000 );

            //Invoke the magic
            ksession = kbase.newStatefulKnowledgeSession();
            ksession.insert( application );
            ksession.insert( income );
            ksession.fireAllRules();

            //Voila!
            System.out.println( application );

        } catch ( Throwable t ) {
            t.printStackTrace();
        } finally {
        	if ( ksession != null ) {
                ksession.dispose();
            }
        }
    }

    /**
     * Load KnowledgeBase using KnowledgeAgent configured with accompanying changeset.xml
     * 
     * @return A KnowledgeBase
     * @throws Exception
     */
    private static KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeAgent kagent = KnowledgeAgentFactory
                .newKnowledgeAgent( "MortgageAgent" );
        kagent.applyChangeSet( ResourceFactory
                .newClassPathResource( "changeset.xml" ) );
        KnowledgeBase kbase = kagent.getKnowledgeBase();
        kagent.dispose();
        return kbase;
    }

}
