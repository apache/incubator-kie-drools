/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.scanner.embedder.logger;

/**
 * Convenient class for consuming the log traces produced by the maven infrastructure Mojos. The maven Mojos are the
 * building blocks for the different maven plugins. Interested parties in consuming this log traces can register a
 * LocalLoggerConsumer in the LocalLoggerManager when the maven request is executed.
 * @see LocalLoggerManager
 */
public interface LocalLoggerConsumer {

    /**
     * Consumes the debug traces eventually produced by the Mojos.
     * @param message the log message. There's no warranty that the parameter holds a non null value.
     * @param throwable A throwable instance. There's no warranty that the parameter holds a non null value.
     */
    void debug( String message, Throwable throwable );

    /**
     * Consumes the info traces eventually produced by the Mojos.
     * @param message the log message. There's no warranty that the parameter holds a non null value.
     * @param throwable A throwable instance. There's no warranty that the parameter holds a non null value.
     */
    void info( String message, Throwable throwable );

    /**
     * Consumes the warn traces eventually produced by the Mojos.
     * @param message the log message. There's no warranty that the parameter holds a non null value.
     * @param throwable A throwable instance. There's no warranty that the parameter holds a non null value.
     */
    void warn( String message, Throwable throwable );

    /**
     * Consumes the error traces eventually produced by the Mojos.
     * @param message the log message. There's no warranty that the parameter holds a non null value.
     * @param throwable A throwable instance. There's no warranty that the parameter holds a non null value.
     */
    void error( String message, Throwable throwable );

    /**
     * Consumes the fatal error traces eventually produced by the Mojos.
     * @param message the log message. There's no warranty that the parameter holds a non null value.
     * @param throwable A throwable instance. There's no warranty that the parameter holds a non null value.
     */
    void fatalError( String message, Throwable throwable );
}