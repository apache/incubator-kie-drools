/*
 * Copyright 2005 JBoss Inc
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

package org.drools;

/**
 * Base exception indicating an error in manipulating facts.
 * 
 */
public class FactException extends RuntimeDroolsException {
    /**
     * 
     */
    private static final long serialVersionUID = 510l;

    /**
     * @see java.lang.Exception#Exception()
     */
    public FactException() {
        super();
    }

    /**
     * @see java.lang.Exception#Exception(String message)
     */
    public FactException(final String message) {
        super( message );
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     */
    public FactException(final String message,
                         final Throwable cause) {
        super( message,
               cause );
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     */
    public FactException(final Throwable cause) {
        super( cause );
    }
}
