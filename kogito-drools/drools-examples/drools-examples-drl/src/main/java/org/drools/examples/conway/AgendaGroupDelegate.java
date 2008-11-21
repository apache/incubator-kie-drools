package org.drools.examples.conway;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.compiler.PackageBuilder;
import org.drools.runtime.StatefulKnowledgeSession;

public class AgendaGroupDelegate implements ConwayRuleDelegate {
    private StatefulKnowledgeSession session;
    
    public AgendaGroupDelegate() {
        final Reader drl = new InputStreamReader( AgendaGroupDelegate.class.getResourceAsStream( "/org/drools/examples/conway/conway-agendagroup.drl" ) );

        try {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.addResource( drl, KnowledgeType.DRL );
            
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
