package org.drools.runtime.pipeline.impl;

import java.util.List;

import org.drools.command.runtime.rule.InsertElementsCommand;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.pipeline.Action;
import org.drools.runtime.pipeline.Callable;
import org.drools.runtime.pipeline.CorePipelineProvider;
import org.drools.runtime.pipeline.Expression;
import org.drools.runtime.pipeline.Join;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.ListAdapter;
import org.drools.runtime.pipeline.Pipeline;
import org.drools.runtime.pipeline.Splitter;

public class CorePipelineProviderImpl
    implements
    CorePipelineProvider {

    public Pipeline newStatefulKnowledgeSessionPipeline(StatefulKnowledgeSession ksession) {
        return new StatefulKnowledgeSessionPipelineImpl( ksession );
    }

    public Pipeline newStatefulKnowledgeSessionPipeline(StatefulKnowledgeSession ksession,
                                                        String entryPointName) {
        return new StatefulKnowledgeSessionPipelineImpl( ksession,
                                                         entryPointName );
    }

    public Pipeline newStatelessKnowledgeSessionPipeline(StatelessKnowledgeSession ksession) {
        return new StatelessKnowledgeSessionPipelineImpl( ksession );
    }
    
    public KnowledgeRuntimeCommand newCommandExecutor() {
        return new ExecutorStage();
    }

    public KnowledgeRuntimeCommand newStatefulKnowledgeSessionInsert() {
        return new StatefulKnowledgeSessionInsertStage();
    }

    public KnowledgeRuntimeCommand newStatefulKnowledgeSessionGetGlobal() {
        return new StatefulKnowledgeSessionGetGlobalStage();
    }

    public KnowledgeRuntimeCommand newStatefulKnowledgeSessionSetGlobal() {
        return new StatefulKnowledgeSessionSetGlobalStage();
    }

    public KnowledgeRuntimeCommand newStatefulKnowledgeSessionSetGlobal(String identifier) {
        return new StatefulKnowledgeSessionSetGlobalStage( identifier );
    }
    
    public KnowledgeRuntimeCommand newStatefulKnowledgeSessionGetObject() {
        return new StatefulKnowledgeSessionGetObjectStage(  );
    }    

    public KnowledgeRuntimeCommand newStatefulKnowledgeSessionSignalEvent(String eventType) {
        return new StatefulKnowledgeSessionSignalEventStage( eventType );
    }

    public KnowledgeRuntimeCommand newStatefulKnowledgeSessionSignalEvent(String eventType,
                                                                          long id) {
        return new StatefulKnowledgeSessionSignalEventStage( eventType,
                                                             id );
    }

    public KnowledgeRuntimeCommand newStatefulKnowledgeSessionStartProcess(String id) {
        return new StatefulKnowledgeSessionStartProcessStage( id );
    }
    
    public Action newAssignObjectAsResult() {
        return new AssignObjectAsResult();
    }
    
    public Action newExecuteResultHandler() {
        return new ExecuteResultHandler();
    }
    
    public Action newMvelAction(String action) {
        return new MvelAction( action );
    }

    public Expression newMvelExpression(String expression) {
        return new MvelExpression( expression );
    }

    public Splitter newIterateSplitter() {
        return new IterateSplitter();
    }
    
    public Join newListCollectJoin() {
        return new ListCollectJoin();
    }

    public ListAdapter newListAdapter(List<Object> list,
                                      boolean syncAccessors) {
        return new ListAdapterImpl( list,
                                    syncAccessors );
    }

    public Callable newCallable() {
        return new CallableImpl();
    }

    public KnowledgeRuntimeCommand newInsertElementsCommand() {
        return new InsertElementsCommandStage();
    }

    public KnowledgeRuntimeCommand newInsertObjectCommand() {
        return new InsertObjectCommandStage();
    }
}
