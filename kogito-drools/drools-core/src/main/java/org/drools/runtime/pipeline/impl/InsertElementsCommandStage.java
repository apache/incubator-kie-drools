/**
 * 
 */
package org.drools.runtime.pipeline.impl;

import java.util.Collection;

import org.drools.command.InsertElementsCommand;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.PipelineContext;

public class InsertElementsCommandStage extends BaseEmitter
    implements
    KnowledgeRuntimeCommand {

    public void receive(Object object,
                        PipelineContext context) {
        InsertElementsCommand cmd = new InsertElementsCommand( (Collection) object );

        emit( cmd,
              context );
    }
}