/**
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
 * The base Drools exception for all internal thrown exceptions. If any exceptions are thrown during the
 * runtime execution of Drools they are considered non-recoverable and thus thrown as  Runtime exceptions
 * all the way up to the <Code>WorkingMemory</code> at which point they are nested inside the 
 * <code>CheckedDroolsException</code> for the user to decide how to respond.
 * 
 * @see RuntimeException
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public class RuntimeDroolsException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 510l;

    /**
     * @see java.lang.Exception#Exception()
     */
    public RuntimeDroolsException() {
        super();
    }

    /**
     * @see java.lang.Exception#Exception(String message)
     */
    public RuntimeDroolsException(final String message) {
        super( message );
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     */
    public RuntimeDroolsException(final String message,
                                  final Throwable cause) {
        super( message,
               cause );
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     */
    public RuntimeDroolsException(final Throwable cause) {
        super( cause );
    }

}
