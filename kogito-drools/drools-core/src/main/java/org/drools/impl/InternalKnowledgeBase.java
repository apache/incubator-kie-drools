package org.drools.impl;

import org.drools.RuleBase;
import org.kie.KnowledgeBase;
import org.kie.runtime.KieBase;

public interface InternalKnowledgeBase extends KnowledgeBase, KieBase {

    RuleBase getRuleBase();

}
