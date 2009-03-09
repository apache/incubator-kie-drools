package org.drools.runtime.pipeline;


/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface Splitter
    extends
    Emitter,
    Receiver,
    Stage {
    void setJoin(Join join);
}
