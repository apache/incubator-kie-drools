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
        final Reader calculateRf = new InputStreamReader( CellGridImpl.class.getResourceAsStream( "/org/drools/examples/conway/calculate.rfm" ) );
        final Reader generationRf = new InputStreamReader( CellGridImpl.class.getResourceAsStream( "/org/drools/examples/conway/generation.rfm" ) );
        final Reader killAllRf = new InputStreamReader( CellGridImpl.class.getResourceAsStream( "/org/drools/examples/conway/killAll.rfm" ) );
        final Reader registerNeighborRf = new InputStreamReader( CellGridImpl.class.getResourceAsStream( "/org/drools/examples/conway/registerNeighbor.rfm" ) );

        try {
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( drl );
            builder.addRuleFlow( calculateRf );
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
        session.fireAllRules();
        return session.getAgenda().getRuleFlowGroup( "evaluate" ).size() != 0;
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
    
    /* (non-Javadoc)
     * @see org.drools.examples.conway.ConwayRuleDelegate#setPattern()
     */
    public void setPattern() {
        session.startProcess( "calculate" );
        session.fireAllRules();        
    }
}
