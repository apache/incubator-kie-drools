package org.drools.runtime.pipeline.impl;

import java.io.InputStream;
import java.io.Reader;

import org.drools.common.InternalRuleBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.Resource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.Pipeline;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.vsm.ServiceManager;


public class ServiceManagerPipelineImpl extends BaseEmitter
    implements
    Pipeline {
    private ServiceManager sm;

    public ServiceManagerPipelineImpl(ServiceManager sm) {
        this.sm = sm;
    }

    public synchronized void insert(Object object,
                                    ResultHandler resultHandler) {
        emit( object,
              new ServiceManagerPipelineContextImpl(this.sm, null, resultHandler ) );

    }

}
