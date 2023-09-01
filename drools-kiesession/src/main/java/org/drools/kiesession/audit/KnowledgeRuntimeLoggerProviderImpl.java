package org.drools.kiesession.audit;

import org.drools.core.impl.AbstractRuntime;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.logger.KieLoggers;
import org.kie.api.logger.KieRuntimeLogger;

public class KnowledgeRuntimeLoggerProviderImpl implements KieLoggers {

    public KieRuntimeLogger newFileLogger(KieRuntimeEventManager session,
                                                String fileName) {
        return newFileLogger(session, fileName, WorkingMemoryFileLogger.DEFAULT_MAX_EVENTS_IN_MEMORY);
    }

    public KieRuntimeLogger newFileLogger(KieRuntimeEventManager session,
                                                String fileName,
                                                int maxEventsInMemory) {
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        logger.setMaxEventsInMemory( maxEventsInMemory );
        if ( fileName != null ) {
            logger.setFileName(fileName);
        }
        return registerRuntimeLogger(session, logger);
    }

    public KieRuntimeLogger newThreadedFileLogger(KieRuntimeEventManager session,
                                                        String fileName,
                                                        int interval) {
        ThreadedWorkingMemoryFileLogger logger = new ThreadedWorkingMemoryFileLogger( session );
        if ( fileName != null ) {
            logger.setFileName( fileName );
        }
        logger.start( interval );
        return registerRuntimeLogger(session, logger);
    }

    public KieRuntimeLogger newConsoleLogger(KieRuntimeEventManager session) {
        WorkingMemoryConsoleLogger logger = new WorkingMemoryConsoleLogger( session );
        return registerRuntimeLogger(session, logger);
    }

    private KieRuntimeLogger registerRuntimeLogger(KieRuntimeEventManager session, KieRuntimeLogger logger) {
        if (session instanceof AbstractRuntime) {
            ((AbstractRuntime) session).setLogger(logger);
        }
        return logger;
    }
}
