package org.drools.logger;

import org.drools.event.KnowledgeRuntimeEventManager;

public interface KnowledgeRuntimeLoggerFactoryService {

    KnowledgeRuntimeLogger newFileLogger(KnowledgeRuntimeEventManager session,
                                         String fileName);

    KnowledgeRuntimeLogger newThreadedFileLogger(KnowledgeRuntimeEventManager session,
                                                 String fileName,
                                                 int interval);

    KnowledgeRuntimeLogger newConsoleLogger(KnowledgeRuntimeEventManager session);

}
