/**
 * 
 */
package org.drools.runtime.pipeline.impl;

import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.PipelineContext;

public class InsertObjectCommandStage extends BaseEmitter
    implements
    KnowledgeRuntimeCommand {
    private String outIdentifier;

    public InsertObjectCommandStage() {

    }

    public InsertObjectCommandStage(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public void receive(Object object,
                        PipelineContext context) {
        InsertObjectCommand cmd = new InsertObjectCommand( object );
        if ( outIdentifier != null ) {
            cmd.setOutIdentifier( this.outIdentifier );
        }

        emit( cmd,
              context );
    }
}