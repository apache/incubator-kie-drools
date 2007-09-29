package org.drools.examples.conway;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;

public class AgendaGroupDelegate implements ConwayRuleDelegate {
    private StatefulSession session;
    
    public AgendaGroupDelegate() {
        final Reader drl = new InputStreamReader( AgendaGroupDelegate.class.getResourceAsStream( "/org/drools/examples/conway/conway-agendagroup.drl" ) );

        try {
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( drl );

            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
            ruleBase.addPackage( builder.getPackage() );

            this.session = ruleBase.newStatefulSession();

        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
    
    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#getSession()
     */
    public StatefulSession getSession() {
        return this.session;
    }
    
    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#init()
     */
    public void init() {
        this.session.setFocus( "register neighbor" );
        this.session.fireAllRules();     
        session.clearAgendaGroup( "calculate" );
    }
    
    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#nextGeneration()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#nextGeneration()
     */
    public boolean nextGeneration() {
        // System.out.println( "next generation" );
        session.setFocus( "kill" );
        session.setFocus( "birth" );
        session.setFocus( "reset calculate" );
        session.setFocus( "rest" );
        session.setFocus( "evaluate" );
        session.setFocus( "calculate" );        
        session.fireAllRules();
        return session.getAgenda().getAgendaGroup( "calculate" ).size() != 0;
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#killAll()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#killAll()
     */
    public void killAll() {
        this.session.setFocus( "calculate" );
        this.session.setFocus( "kill all" );
        this.session.setFocus( "calculate" );
        this.session.fireAllRules();
    }
    
}
