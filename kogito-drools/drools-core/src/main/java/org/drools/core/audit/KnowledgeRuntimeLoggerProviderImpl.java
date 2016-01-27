/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.audit;

import org.drools.core.impl.AbstractRuntime;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.logger.KnowledgeRuntimeLogger;
import org.kie.api.logger.KieLoggers;

import javax.inject.Singleton;

@Singleton
public class KnowledgeRuntimeLoggerProviderImpl
    implements
    KieLoggers {

    public KnowledgeRuntimeLogger newFileLogger(KieRuntimeEventManager session,
                                                String fileName) {
        return newFileLogger(session, fileName, WorkingMemoryFileLogger.DEFAULT_MAX_EVENTS_IN_MEMORY);
    }

    public KnowledgeRuntimeLogger newFileLogger(KieRuntimeEventManager session,
                                                String fileName,
                                                int maxEventsInMemory) {
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( (KnowledgeRuntimeEventManager) session );
        logger.setMaxEventsInMemory( maxEventsInMemory );
        if ( fileName != null ) {
            logger.setFileName(fileName);
        }
        return registerRuntimeLogger(session, new KnowledgeRuntimeFileLoggerWrapper(logger));
    }

    public KnowledgeRuntimeLogger newThreadedFileLogger(KieRuntimeEventManager session,
                                                        String fileName,
                                                        int interval) {
        ThreadedWorkingMemoryFileLogger logger = new ThreadedWorkingMemoryFileLogger( (KnowledgeRuntimeEventManager) session );
        if ( fileName != null ) {
            logger.setFileName( fileName );
        }
        logger.start( interval );
        return registerRuntimeLogger(session, new KnowledgeRuntimeFileLoggerWrapper(logger));
    }

    public KnowledgeRuntimeLogger newConsoleLogger(KieRuntimeEventManager session) {
        WorkingMemoryConsoleLogger logger = new WorkingMemoryConsoleLogger( (KnowledgeRuntimeEventManager) session );
        return registerRuntimeLogger(session, new KnowledgeRuntimeConsoleLoggerWrapper(logger));
    }

    private KnowledgeRuntimeLogger registerRuntimeLogger(KieRuntimeEventManager session, KnowledgeRuntimeLogger logger) {
        if (session instanceof AbstractRuntime) {
            ((AbstractRuntime) session).setLogger(logger);
        }
        return logger;
    }

    private class KnowledgeRuntimeFileLoggerWrapper
        implements
        KnowledgeRuntimeLogger {

        private WorkingMemoryFileLogger logger;

        public KnowledgeRuntimeFileLoggerWrapper(WorkingMemoryFileLogger logger) {
            this.logger = logger;
        }

        public void close() {
            logger.stop();
        }

    }

    private class KnowledgeRuntimeThreadedFileLoggerWrapper
        implements
            KnowledgeRuntimeLogger {

        private ThreadedWorkingMemoryFileLogger logger;

        public KnowledgeRuntimeThreadedFileLoggerWrapper(ThreadedWorkingMemoryFileLogger logger) {
            this.logger = logger;
        }

        public void close() {
            logger.stop();
        }

    }

    private class KnowledgeRuntimeConsoleLoggerWrapper
        implements
        KnowledgeRuntimeLogger {

        // private WorkingMemoryConsoleLogger logger;

        public KnowledgeRuntimeConsoleLoggerWrapper(WorkingMemoryConsoleLogger logger) {
            // this.logger = logger;
        }

        public void close() {
            // Do nothing
        }

    }

}
