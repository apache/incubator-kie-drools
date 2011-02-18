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

package org.drools.examples.conway;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class AgendaGroupDelegate
    implements
    ConwayRuleDelegate {
    private StatefulKnowledgeSession session;

    public AgendaGroupDelegate() {
        try {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add( ResourceFactory.newClassPathResource( "conway-agendagroup.drl",
                                                                        getClass() ),
                                  ResourceType.DRL );

            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

            this.session = kbase.newStatefulKnowledgeSession();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#getSession()
     */
    public StatefulKnowledgeSession getSession() {
        return this.session;
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#init()
     */
    public void init() {
        this.session.getAgenda().getAgendaGroup( "register neighbor" ).setFocus();
        this.session.fireAllRules();
        this.session.getAgenda().getAgendaGroup( "calculate" ).clear();
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#nextGeneration()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#nextGeneration()
     */
    public boolean nextGeneration() {
        // System.out.println( "next generation" );
        this.session.getAgenda().getAgendaGroup( "kill" ).setFocus();
        this.session.getAgenda().getAgendaGroup( "birth" ).setFocus();
        this.session.getAgenda().getAgendaGroup( "reset calculate" ).setFocus();
        this.session.getAgenda().getAgendaGroup( "rest" ).setFocus();
        this.session.getAgenda().getAgendaGroup( "evaluate" ).setFocus();
        this.session.getAgenda().getAgendaGroup( "calculate" ).setFocus();
        return session.fireAllRules() != 0;
        //return session.getAgenda().getAgendaGroup( "calculate" ).size() != 0;
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#killAll()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#killAll()
     */
    public void killAll() {
        this.session.getAgenda().getAgendaGroup( "calculate" ).setFocus();
        this.session.getAgenda().getAgendaGroup( "kill all" ).setFocus();
        this.session.getAgenda().getAgendaGroup( "calculate" ).setFocus();
        this.session.fireAllRules();
    }

}
