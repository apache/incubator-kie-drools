package org.drools.runtime.dataloader;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.pipeline.Receiver;

public interface DataLoaderProvider {
    StatefulKnowledgeSessionDataLoader newStatefulKnowledgeSessionDataLoader(StatefulKnowledgeSession ksession,
                                                                             Receiver pipeline);

    StatefulKnowledgeSessionDataLoader newStatefulKnowledgeSessionDataLoader(StatefulKnowledgeSession ksession,
                                                                             String entryPointName,
                                                                             Receiver pipeline);
    
    StatelessKnowledgeSessionDataLoader newStatelessKnowledgeSessionDataLoader(StatelessKnowledgeSession ksession,
                                                                               Receiver pipeline);
}
