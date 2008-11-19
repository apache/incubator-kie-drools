package org.drools.runtime.rule;

import org.drools.runtime.Parameters;
import org.drools.runtime.StatelessKnowledgeSessionResults;

public interface StatelessRuleSession {
    void executeObject(Object object);

    void executeIterable(Iterable< ? > objects);

    StatelessKnowledgeSessionResults executeObjectWithParameters(Object object,
                                                                 Parameters parameters);

    StatelessKnowledgeSessionResults executeIterableWithParameters(Iterable< ? > objects,
                                                                   Parameters parameters);

    Parameters newParameters();
}
