package org.drools.runtime.pipeline;

public interface Splitter  extends Emitter, Receiver, Stage {
    void setJoin(Join join);
}
