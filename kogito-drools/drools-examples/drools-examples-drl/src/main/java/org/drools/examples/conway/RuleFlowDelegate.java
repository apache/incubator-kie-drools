package org.drools.examples.conway;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class RuleFlowDelegate implements ConwayRuleDelegate {
    private StatefulKnowledgeSession session;
    
    public RuleFlowDelegate() {
        try {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add( ResourceFactory.newClassPathResource( "conway-ruleflow.drl",getClass()), KnowledgeType.DRL );
            kbuilder.add( ResourceFactory.newClassPathResource( "generation.rf",getClass()), KnowledgeType.DRF );
            kbuilder.add( ResourceFactory.newClassPathResource( "killAll.rf",getClass()), KnowledgeType.DRF );
            kbuilder.add( ResourceFactory.newClassPathResource( "registerNeighbor.rf",getClass()), KnowledgeType.DRF );
            
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
        this.session.startProcess( "register neighbor" );
        this.session.fireAllRules();
        session.getAgenda().getRuleFlowGroup( "calculate" ).clear();       
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
