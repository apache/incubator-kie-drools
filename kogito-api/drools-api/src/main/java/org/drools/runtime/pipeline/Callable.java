package org.drools.runtime.pipeline;

/**
 * 
 * Must be the first and last Stage in the Pipeline
 *
 * @param <E>
 */
public interface Callable<E>
    extends
    Receiver,
    Emitter,
    Stage {
    E call(Object signal,
           PipelineContext context);
}
