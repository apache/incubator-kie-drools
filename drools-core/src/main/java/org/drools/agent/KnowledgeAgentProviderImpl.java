package org.drools.agent;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.impl.KnowledgeBaseImpl;

public class KnowledgeAgentProviderImpl
    implements
    KnowledgeAgentProvider {

    public KnowledgeAgent newKnowledgeAgent(String name,
                                            Properties config) {
        return new KnowledgeAgentWrapper( name,
                                          RuleAgent.newRuleAgent( config ) );
    }

    public KnowledgeAgent newKnowledgeAgent(String name,
                                            Properties config,
                                            KnowledgeBaseConfiguration kbaseConf) {
        return new KnowledgeAgentWrapper( name,
                                          RuleAgent.newRuleAgent( config ) );
    }

    public KnowledgeAgent newKnowledgeAgent(String name,
                                            Properties config,
                                            KnowledgeEventListener listener) {
        return new KnowledgeAgentWrapper( name,
                                          RuleAgent.newRuleAgent( config ) );
    }

    public KnowledgeAgent newKnowledgeAgent(String name,
                                            Properties config,
                                            KnowledgeEventListener listener,
                                            KnowledgeBaseConfiguration kbaseConf) {
        return new KnowledgeAgentWrapper( name,
                                          RuleAgent.newRuleAgent( config,
                                                                  null,
                                                                  ((RuleBaseConfiguration) kbaseConf) ) );
    }

    public static class KnowledgeAgentWrapper
        implements
        KnowledgeAgent {
        private String        name;
        private RuleAgent     ruleAgent;
        private RuleBase      ruleBase;
        private KnowledgeBase kbase;

        KnowledgeAgentWrapper(String name,
                              RuleAgent ruleAgent) {
            this.ruleAgent = ruleAgent;
            ruleAgent.listener.setAgentName( name );
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }

        public synchronized KnowledgeBase getKnowledgeBase() {
            RuleBase newRuleBase = this.ruleAgent.getRuleBase();
            if ( newRuleBase != this.ruleBase ) {
                // if ruleBase is null or newRuleBase is a new instance then create a new kbase.
                this.ruleBase = newRuleBase;
                this.kbase = new KnowledgeBaseImpl( this.ruleBase );
            }
            return this.kbase;
        }
    }

}
