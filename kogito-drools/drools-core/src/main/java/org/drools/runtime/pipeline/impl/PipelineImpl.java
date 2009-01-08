package org.drools.runtime.pipeline.impl;

public class PipelineImpl extends BaseEmitter {
    private PipelineContextFactory factory;
    
    public PipelineImpl(PipelineContextFactory factory) {
        this.factory = factory;
    }
    
    public void insert(Object object) {
        emit( object, this.factory.newPipelineContext() );
    }
}
