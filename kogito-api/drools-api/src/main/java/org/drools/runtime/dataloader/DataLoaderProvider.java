package org.drools.runtime.dataloader;

import org.drools.definition.pipeline.Receiver;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

public interface DataLoaderProvider {
    StatefulKnowledgeSessionDataLoader newStatefulKnowledgeSessionDataLoader(StatefulKnowledgeSession ksession,
                                                                             Receiver pipeline);

    StatelessKnowledgeSessionDataLoader newStatelessKnowledgeSessionDataLoader(StatelessKnowledgeSession ksession,
                                                                               Receiver pipeline);
}
