/*
 * Copyright 2010 JBoss Inc
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

package org.drools.audit;

import org.kie.event.KieRuntimeEventManager;
import org.kie.event.KnowledgeRuntimeEventManager;
import org.kie.logger.KieLoggers;
import org.kie.logger.KnowledgeRuntimeLogger;

import javax.inject.Singleton;

@Singleton
public class KnowledgeRuntimeLoggerProviderImpl
    implements
    KieLoggers {

    public KnowledgeRuntimeLogger newFileLogger(KieRuntimeEventManager session,
                                                String fileName) {
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( (KnowledgeRuntimeEventManager) session );
        if ( fileName != null ) {
            logger.setFileName( fileName );
        }
        return new KnowledgeRuntimeFileLoggerWrapper( logger );
    }

    public KnowledgeRuntimeLogger newThreadedFileLogger(KieRuntimeEventManager session,
                                                        String fileName,
                                                        int interval) {
        ThreadedWorkingMemoryFileLogger logger = new ThreadedWorkingMemoryFileLogger( (KnowledgeRuntimeEventManager) session );
        if ( fileName != null ) {
            logger.setFileName( fileName );
        }
        logger.start( interval );
        return new KnowledgeRuntimeThreadedFileLoggerWrapper( logger );
    }

    public KnowledgeRuntimeLogger newConsoleLogger(KieRuntimeEventManager session) {
        WorkingMemoryConsoleLogger logger = new WorkingMemoryConsoleLogger( (KnowledgeRuntimeEventManager) session );
        return new KnowledgeRuntimeConsoleLoggerWrapper( logger );
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
