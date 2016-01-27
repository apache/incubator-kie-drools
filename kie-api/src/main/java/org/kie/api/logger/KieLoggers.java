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
