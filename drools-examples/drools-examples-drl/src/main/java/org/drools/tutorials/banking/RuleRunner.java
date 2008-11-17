package org.drools.tutorials.banking;

import java.io.InputStreamReader;
import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.definition.KnowledgePackage;
import org.drools.runtime.StatefulKnowledgeSession;


public class RuleRunner {

    public RuleRunner() {
    }

    public void runRules(String[] rules,
                         Object[] facts) throws Exception {

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        for ( int i = 0; i < rules.length; i++ ) {
            String ruleFile = rules[i];
            System.out.println( "Loading file: " + ruleFile );            
            builder.addResource( new InputStreamReader( RuleRunner.class.getResourceAsStream( ruleFile ) ), KnowledgeType.DRL );
        }

        Collection<KnowledgePackage> pkgs = builder.getKnowledgePackages();
        knowledgeBase.addKnowledgePackages( pkgs );
        StatefulKnowledgeSession statefullKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();

        for ( int i = 0; i < facts.length; i++ ) {
            Object fact = facts[i];
            System.out.println( "Inserting fact: " + fact );
            statefullKnowledgeSession.insert( fact );
        }

        statefullKnowledgeSession.fireAllRules();
    }
}
