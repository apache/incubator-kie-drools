/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder.dialect;

import org.drools.compiler.compiler.DroolsError;
import org.kie.api.io.Resource;

public class DialectError extends DroolsError {
    private String message;
    private static final int[] line = new int[0];

    public DialectError(final Resource resource, final String message) {
        super(resource);
        this.message = message;
    }

    public int[] getLines() {
        return line;
    }
    
    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "[DialectError message='" + this.message + "']";
    }

}
