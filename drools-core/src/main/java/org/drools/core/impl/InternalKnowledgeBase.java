package org.drools.core.impl;

import org.drools.core.RuleBase;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.kie.internal.KnowledgeBase;

public interface InternalKnowledgeBase extends KnowledgeBase, KieBase {

    RuleBase getRuleBase();
}
