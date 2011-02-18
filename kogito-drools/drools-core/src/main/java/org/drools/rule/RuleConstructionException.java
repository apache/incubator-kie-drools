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

package org.drools.rule;

import org.drools.RuntimeDroolsException;

/**
 * Base exception for errors during <code>Rule</code> construction.
 * 
 */
public class RuleConstructionException extends RuntimeDroolsException {
    private static final long serialVersionUID = 510l;

    /**
     * @see java.lang.Exception#Exception()
     */
    RuleConstructionException() {
        super();
    }

    /**
     * @see java.lang.Exception#Exception(String message)
     */
    RuleConstructionException(final String message) {
        super( message );
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     */
    RuleConstructionException(final String message,
                              final Throwable cause) {
        super( message,
               cause );
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     */
    RuleConstructionException(final Throwable cause) {
        super( cause );
    }
}
