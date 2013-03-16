package org.drools.core.impl;

import org.drools.RuleBase;
import org.kie.KieBase;
import org.kie.KnowledgeBase;

public interface InternalKnowledgeBase extends KnowledgeBase, KieBase {

    RuleBase getRuleBase();

}
