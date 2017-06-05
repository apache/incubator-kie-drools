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
import org.kie.api.logger.KieLoggers;
import org.kie.api.logger.KieRuntimeLogger;

import javax.inject.Singleton;

@Singleton
public class KnowledgeRuntimeLoggerProviderImpl
    implements
    KieLoggers {

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
