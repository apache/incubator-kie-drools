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

package org.drools.examples;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class HonestPoliticianExample {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        KnowledgeBuilderConfiguration kbuilderconfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbuilderconfiguration.setProperty( "drools.dump.dir",
                                           "target" );

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "HonestPolitician.drl",
                                                                    HonestPoliticianExample.class ),
                              ResourceType.DRL );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        KnowledgeRuntimeLogger klogger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "log/honest-politician");

        final Politician blair = new Politician( "blair", true );
        final Politician bush = new Politician( "bush", true );
        final Politician chirac = new Politician( "chirac", true );
        final Politician schroder = new Politician( "schroder", true );

        ksession.insert( blair );
        ksession.insert( bush );
        ksession.insert( chirac );
        ksession.insert( schroder );

        ksession.fireAllRules();

        klogger.close();

        ksession.dispose();
    }

    public static class Politician {
        private String  name;

        private boolean honest;

        public Politician() {

        }

        public Politician(String name,
                          boolean honest) {
            super();
            this.name = name;
            this.honest = honest;
        }

        public boolean isHonest() {
            return honest;
        }

        public void setHonest(boolean honest) {
            this.honest = honest;
        }

        public String getName() {
            return name;
        }
    }

    public static class Hope {

        public Hope() {

        }

    }

}
