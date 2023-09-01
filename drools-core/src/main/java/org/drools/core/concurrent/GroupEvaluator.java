package org.drools.core.concurrent;

import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.kie.api.runtime.rule.AgendaFilter;

public interface GroupEvaluator {
    int evaluateAndFire( InternalAgendaGroup group, AgendaFilter filter, int fireCount, int fireLimit );

    KnowledgeHelper getKnowledgeHelper();

    void resetKnowledgeHelper();

    void haltEvaluation();
}
