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

package org.drools.compiler;

import org.drools.builder.KnowledgeBuilderError;

public abstract class DroolsError implements KnowledgeBuilderError {

    /**
     * Classes that extend this must provide a printable message,
     * which summarises the error.
     */
    public abstract String getMessage();
    
    /**
     * Returns the lines of the error in the source file
     * @return
     */
    public abstract int[] getErrorLines();
    
    public String toString() {
        return getClass().getName() + ": " + getMessage();
    }
}
