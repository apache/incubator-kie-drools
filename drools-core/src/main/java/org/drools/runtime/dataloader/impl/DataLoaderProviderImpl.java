package org.drools.runtime.dataloader.impl;

import org.drools.definition.pipeline.Receiver;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.dataloader.DataLoaderProvider;
import org.drools.runtime.dataloader.StatefulKnowledgeSessionDataLoader;
import org.drools.runtime.dataloader.StatelessKnowledgeSessionDataLoader;

public class DataLoaderProviderImpl
    implements
    DataLoaderProvider {
    
    public StatefulKnowledgeSessionDataLoader newStatefulKnowledgeSessionDataLoader(StatefulKnowledgeSession ksession,
                                                                                    Receiver pipeline) {
        return new StatefulKnowledgeSessionDataLoaderImpl(ksession, pipeline);
    }

    public StatelessKnowledgeSessionDataLoader newStatelessKnowledgeSessionDataLoader(StatelessKnowledgeSession ksession,
                                                                                      Receiver pipeline) {
        return newStatelessKnowledgeSessionDataLoader(ksession, pipeline);
    }

}
