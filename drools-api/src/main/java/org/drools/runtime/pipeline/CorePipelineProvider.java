package org.drools.runtime.pipeline;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

public interface CorePipelineProvider {

    Pipeline newStatefulKnowledgeSessionPipeline(StatefulKnowledgeSession ksession);

    Pipeline newStatefulKnowledgeSessionPipeline(StatefulKnowledgeSession ksession,
                                                 String entryPointName);

    Pipeline newStatelessKnowledgeSessionPipelineImpl(StatelessKnowledgeSession ksession);

    KnowledgeRuntimeCommand newStatefulKnowledgeSessionInsert();

    KnowledgeRuntimeCommand newStatelessKnowledgeSessionExecute();

    KnowledgeRuntimeCommand newStatefulKnowledgeSessionGetGlobal();

    KnowledgeRuntimeCommand newStatefulKnowledgeSessionSetGlobal();

    KnowledgeRuntimeCommand newStatefulKnowledgeSessionGetObject();

    KnowledgeRuntimeCommand newStatefulKnowledgeSessionSetGlobal(String identifier);

    KnowledgeRuntimeCommand newStatefulKnowledgeSessionSignalEvent(String eventType);

    KnowledgeRuntimeCommand newStatefulKnowledgeSessionSignalEvent(String eventType,
                                                                   long id);

    KnowledgeRuntimeCommand newStatefulKnowledgeSessionStartProcess(String id);

    Action newAssignObjectAsResult();

    Action newExecuteResultHandler();

    Action newMvelAction(String action);

    Expression newMvelExpression(String expression);

    Splitter newIterateSplitter();

    Join newListCollectJoin();
}
