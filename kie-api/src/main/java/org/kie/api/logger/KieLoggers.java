package org.kie.api.logger;

import org.kie.api.event.KieRuntimeEventManager;

/**
 * KieLoggers is a factory for KieRuntimeLogger
 */
public interface KieLoggers {

    /**
     * Creates a new FileLogger with the given name for the given session.
     * The maximum number of log events that are allowed in memory by default is 1000.
     * If this number is reached, all events are written to the file.
     */
    KieRuntimeLogger newFileLogger(KieRuntimeEventManager session,
                                   String fileName);

    /**
     * Creates a new FileLogger with the given name for the given session.
     * also setting the maximum number of log events that are allowed in memory.
     * If this number is reached, all events are written to the file.
     *
     * By setting maxEventsInMemory to 0 makes all events to be immediately flushed to the file.
     * This option is slow and then not suggested in production but can be useful while debugging.
     */
    KieRuntimeLogger newFileLogger(KieRuntimeEventManager session,
                                   String fileName,
                                   int maxEventsInMemory);

    KieRuntimeLogger newThreadedFileLogger(KieRuntimeEventManager session,
                                           String fileName,
                                           int interval);

    KieRuntimeLogger newConsoleLogger(KieRuntimeEventManager session);

}
