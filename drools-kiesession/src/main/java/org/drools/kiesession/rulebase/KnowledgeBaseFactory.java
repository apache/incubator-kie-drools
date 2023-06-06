package org.drools.kiesession.rulebase;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.InternalRuleBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.conf.CompositeBaseConfiguration;

public class KnowledgeBaseFactory {

    public static InternalKnowledgeBase newKnowledgeBase() {
        return new SessionsAwareKnowledgeBase();
    }

    public static InternalKnowledgeBase newKnowledgeBase(KieBaseConfiguration kbaseConfiguration) {
        return new SessionsAwareKnowledgeBase(kbaseConfiguration);
    }

    public static InternalKnowledgeBase newKnowledgeBase(String kbaseId, KieBaseConfiguration conf) {
        return newKnowledgeBase(new KnowledgeBaseImpl(kbaseId, (CompositeBaseConfiguration) conf));
    }

    public static InternalKnowledgeBase newKnowledgeBase(InternalRuleBase delegate) {
        return new SessionsAwareKnowledgeBase(delegate);
    }
}
