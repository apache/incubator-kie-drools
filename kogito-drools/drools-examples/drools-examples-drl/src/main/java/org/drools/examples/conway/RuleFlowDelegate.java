package org.drools.examples.conway;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;

public class RuleFlowDelegate implements ConwayRuleDelegate {
    private StatefulSession session;
    
    public RuleFlowDelegate() {
        final Reader drl = new InputStreamReader( CellGridImpl.class.getResourceAsStream( "/org/drools/examples/conway/conway-ruleflow.drl" ) );
        final Reader generationRf = new InputStreamReader( CellGridImpl.class.getResourceAsStream( "/org/drools/examples/conway/generation.rf" ) );
        final Reader killAllRf = new InputStreamReader( CellGridImpl.class.getResourceAsStream( "/org/drools/examples/conway/killAll.rf" ) );
        final Reader registerNeighborRf = new InputStreamReader( CellGridImpl.class.getResourceAsStream( "/org/drools/examples/conway/registerNeighbor.rf" ) );

        try {
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( drl );
            builder.addRuleFlow( generationRf );
            builder.addRuleFlow( killAllRf );
            builder.addRuleFlow( registerNeighborRf );

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
        this.session.startProcess( "register neighbor" );
        this.session.fireAllRules();
        session.clearRuleFlowGroup( "calculate" );
    }
    
    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#nextGeneration()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#nextGeneration()
     */
    public boolean nextGeneration() {
        // System.out.println( "next generation" );
        
        session.startProcess( "generation" );
        return session.fireAllRules() != 0;
        //return session.getAgenda().getRuleFlowGroup( "calculate" ).size() != 0;
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#killAll()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#killAll()
     */
    public void killAll() {
        this.session.startProcess( "kill all" );
        this.session.fireAllRules();
    }
    
}
