/**
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

package org.drools.repository;

/**
 * The main exception thrown by classes in this package. May contain an error message and/or another
 * nested exception.
 * 
 * @author btruitt
 */
public class RulesRepositoryException extends RuntimeException {

    /**
     * version id for serialization purposes
     */
    private static final long serialVersionUID = 510l;

    /**
     * Default constructor. constructs a RulesRepositoryException object with null as its detail 
     * message
     */
    public RulesRepositoryException() {
        //nothing extra
    }

    /**
     * Constructs a new instance of this class with the specified detail message.
     * 
     * @param message the message to set for the exception
     */
    public RulesRepositoryException(String message) {
        super(message);
    }

    /**
     * Constructs a new instance of this class with the specified root cause.
     * 
     * @param rootCause root failure cause
     */
    public RulesRepositoryException(Throwable rootCause) {
        super(rootCause);
    }
    
    /**
     * Constructs a new instance of this class with the specified detail message and root cause.
     * 
     * @param message the message to set for the exception
     * @param rootCause root failure cause
     */
    public RulesRepositoryException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
