package org.drools.runtime.dataloader;

import org.drools.runtime.Parameters;
import org.drools.runtime.StatelessKnowledgeSessionResults;

public interface StatelessKnowledgeSessionDataLoader {

    void executeObject(Object object);

    void executeIterable(Object object);

    StatelessKnowledgeSessionResults executeObjectWithParameters(Object object,
                                                                 Parameters parameters);

    StatelessKnowledgeSessionResults executeIterableWithParameters(Object object,
                                                                   Parameters parameters);

}