package org.drools.agent.impl;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentProvider;
import org.drools.agent.KnowledgeAgentEventListener;

public class KnowledgeAgentProviderImpl implements KnowledgeAgentProvider {

    public KnowledgeAgent newKnowledgeAgent(String name,
                                            KnowledgeBase kbase) {
        return new KnowledgeAgentImpl(name, kbase, null, null);
    }

    public KnowledgeAgent newKnowledgeAgent(String name,
                                            KnowledgeBase kbase,
                                            KnowledgeAgentConfiguration configuration) {
        return new KnowledgeAgentImpl(name, kbase, configuration, null);
    }

    public KnowledgeAgent newKnowledgeAgent(String name,
                                            KnowledgeBase kbase,
                                            KnowledgeAgentConfiguration configuration,
                                            KnowledgeAgentEventListener listener) {
        return new KnowledgeAgentImpl(name, kbase, configuration, listener);
    }

}
