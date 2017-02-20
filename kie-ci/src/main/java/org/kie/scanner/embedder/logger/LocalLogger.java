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

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

/**
 * This class is used internally by the LocalLoggerManager and basically acts as a bridge with a LocalLoggerConsumer.
 */
public class LocalLogger
        extends AbstractLogger {

    private LocalLoggerConsumer consumer;

    public LocalLogger( int threshold, String name, LocalLoggerConsumer consumer ) {
        super( threshold, name );
        this.consumer = consumer;
    }

    public void debug( String message, Throwable throwable ) {
        if ( isDebugEnabled( ) ) {
            consumer.debug( message, throwable );
        }
    }

    public void info( String message, Throwable throwable ) {
        if ( isInfoEnabled( ) ) {
            consumer.info( message, throwable );
        }
    }

    public void warn( String message, Throwable throwable ) {
        if ( isWarnEnabled( ) ) {
            consumer.warn( message, throwable );
        }
    }

    public void error( String message, Throwable throwable ) {
        if ( isErrorEnabled( ) ) {
            consumer.error( message, throwable );
        }
    }

    public void fatalError( String message, Throwable throwable ) {
        if ( isFatalErrorEnabled( ) ) {
            consumer.fatalError( message, throwable );
        }
    }

    public Logger getChildLogger( String name ) {
        return this;
    }

}