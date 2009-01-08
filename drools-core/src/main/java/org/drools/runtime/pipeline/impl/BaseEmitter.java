package org.drools.runtime.pipeline.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.runtime.pipeline.Emitter;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;

public class BaseEmitter extends BaseStage
    implements
    Emitter {
    protected Receiver receiver;

    public BaseEmitter() {
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public Receiver getReceiver() {
        return this.receiver;
    }

    protected void emit(Object object,
                        PipelineContext context) {
        if ( this.receiver != null ) {
            receiver.receive( object,
                              context );
        }
    }

}
