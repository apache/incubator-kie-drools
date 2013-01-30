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

package org.drools.exception;

import org.drools.command.impl.AbstractInterceptor;
import org.kie.command.Command;

/**
 * This interceptor contains the basic logic to handle an exception thrown by the engine. </p>
 * If the interceptor is meant to catch and handle any exceptions thrown, then there are 
 * 2 things that should have been done beforehand:<ul>
 * <li>In the "cause-chain" (e.g. from Throwable.getCause()), a {@link HandlerRuntimeException} should be present.</li>
 * <li>The {@link HandlerRuntimeException} instance should contain a {@link JavaExceptionHandler} implementation that
 * will be able to handle this type of exception.</li>
 * </ul>
 */
public class ExceptionHandlingInterceptor extends AbstractInterceptor {

    /*
     * (non-Javadoc)
     * @see org.kie.runtime.CommandExecutor#execute(org.kie.command.Command)
     */
    public <T> T execute(Command<T> command) {
        T result = null;
        
        try { 
            result = executeNext(command);
        } catch( RuntimeException exception ) {
            result = handleThrowable(command, exception);
        } catch( Error error) { 
            result = handleThrowable(command, error);
        }
        
        return result;
    }

    /**
     * This method determines if there's a {@link JavaExceptionHandler} present, 
     * and if so, executes it and returns what it returns. 
     * 
     * @param command The command that produced the exception.
     * @param exception The exception thrown. 
     * @return A result comparable with what would have been returned if the exception had not been thrown. 
     */
    protected <T> T handleThrowable(Command<T> command, Throwable exception) { 
            JavaExceptionHandler exceptionHandler = getExceptionHandler(exception);
            T result = null;
            if( exceptionHandler != null && exceptionHandler.shouldBeHandled(command) ) { 
                result = (T) exceptionHandler.handleException(getNext(), exception);
            } else {
                if( exception instanceof RuntimeException) { 
                    throw (RuntimeException) exception;
                } else if( exception instanceof Error ) { 
                    throw (Error) exception;
                }
            }
            return result;
    } 
    
    /**
     * Retrieve the {@link JavaExceptionHandler} from exception cause chain. 
     * 
     * @param exception The exception thrown. 
     * @return The {@link JavaExceptionHandler} instance, if present. 
     */
    protected JavaExceptionHandler getExceptionHandler(Throwable exception) { 
        Throwable cause = exception;
        while( cause != null && ! (cause instanceof HandlerRuntimeException) ) { 
            Throwable newCause = cause.getCause();
            if( newCause == cause || newCause == null) { 
                return null;
            }
            cause = newCause;
        }
        
        return ((HandlerRuntimeException) cause).getExceptionHandler();
    }
    
}
