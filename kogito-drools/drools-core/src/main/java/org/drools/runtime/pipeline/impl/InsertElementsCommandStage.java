/**
 * 
 */
package org.drools.runtime.pipeline.impl;

import java.util.Collection;
import java.util.List;

import org.drools.command.runtime.rule.InsertElementsCommand;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.PipelineContext;

public class InsertElementsCommandStage extends BaseEmitter
    implements
    KnowledgeRuntimeCommand {

    public void receive(Object object,
                        PipelineContext context) {
        InsertElementsCommand cmd = new InsertElementsCommand( (List<Object>) object );

        emit( cmd,
              context );
    }
}