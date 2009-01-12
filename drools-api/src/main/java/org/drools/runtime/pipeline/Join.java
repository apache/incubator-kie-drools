package org.drools.runtime.pipeline;

public interface Join extends Emitter, Receiver, Stage {
    void completed(PipelineContext context);
}
