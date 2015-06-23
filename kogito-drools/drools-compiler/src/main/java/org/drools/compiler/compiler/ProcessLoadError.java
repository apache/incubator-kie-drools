/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.compiler;


import org.kie.api.io.Resource;

/**
 * This is used for reporting errors with loading a ruleflow.
 */
public class ProcessLoadError extends DroolsError {

    private String message;
    private Exception exception;
    private static final int[] lines = new int[0];

    public ProcessLoadError(Resource resource, String message, Exception nested) {
        super(resource);
        this.message = message;
        this.exception = nested;
    }
    
    public int[] getLines() {
        return this.lines;
    }
    
    public String getMessage() {
        if (exception != null) {
            return message + " : Exception " + exception.getClass() + " : "+ exception.getMessage();
        } else {
            return message;
        }
    }

}
