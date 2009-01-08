package org.drools.runtime.dataloader.impl;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.dataloader.DataLoaderProvider;
import org.drools.runtime.dataloader.WorkingMemoryDataLoader;
import org.drools.runtime.dataloader.StatelessKnowledgeSessionDataLoader;
import org.drools.runtime.pipeline.Receiver;

public class DataLoaderProviderImpl
    implements
    DataLoaderProvider {
      
    public WorkingMemoryDataLoader newStatefulRuleSessionDataLoader(StatefulKnowledgeSession ksession,
                                                                                    Receiver pipeline) {
        return new StatefulKnowledgeSessionDataLoaderImpl(ksession, pipeline);
    }    

    public WorkingMemoryDataLoader newStatefulKnowledgeSessionDataLoader(StatefulKnowledgeSession ksession,
                                                                                    String entryPointName,
                                                                                    Receiver pipeline) {
        return new StatefulKnowledgeSessionDataLoaderImpl(ksession, entryPointName, pipeline);
    }
    
    public StatelessKnowledgeSessionDataLoader newStatelessKnowledgeSessionDataLoader(StatelessKnowledgeSession ksession,
                                                                                      Receiver pipeline) {
        return newStatelessKnowledgeSessionDataLoader(ksession, pipeline);
    }

}
