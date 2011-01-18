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

public class TypeDeclarationError extends DroolsError {
    private String errorMessage;
    private int[]  line;

    public TypeDeclarationError(final String errorMessage, final int line) {
        this.errorMessage = errorMessage;
        this.line = new int[] { line };
    }

    public int[] getErrorLines() {
        return this.line;
    }

    public String getMessage() {
        return this.errorMessage;
    }
    
    public String toString() {
        return this.getMessage();
    }

}
