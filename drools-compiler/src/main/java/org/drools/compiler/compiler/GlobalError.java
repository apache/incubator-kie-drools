/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.parser.DroolsError;

public class GlobalError extends DroolsError {
    private final GlobalDescr globalDescr;

    public GlobalError(final GlobalDescr globalDescr, final String message) {
        super(globalDescr.getResource(), message);
        this.globalDescr = globalDescr;
    }

    @Override
    public String getNamespace() {
        return globalDescr.getNamespace();
    }

    public String getGlobal() {
        return globalDescr.getIdentifier();
    }
    
    public int[] getLines() {
        return new int[] { globalDescr.getLine() };
    }

    public String toString() {
        return "GlobalError: " + getGlobal() + " : " + getMessage();
    }

}
