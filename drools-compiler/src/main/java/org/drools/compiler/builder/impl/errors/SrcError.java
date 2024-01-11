/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.builder.impl.errors;

import org.drools.drl.parser.DroolsError;
import org.kie.internal.jci.CompilationProblem;

public class SrcError extends DroolsError {

    private Object object;
    private int[]  errorLines = new int[0];

    public SrcError(Object object,
                    String message) {
        super(null, message);
        this.object = object;
    }

    public Object getObject() {
        return this.object;
    }

    public int[] getLines() {
        return this.errorLines;
    }


    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(getMessage());
        buf.append(" : ");
        buf.append("\n");
        if (this.object instanceof CompilationProblem[]) {
            final CompilationProblem[] problem = (CompilationProblem[]) this.object;
            for (CompilationProblem aProblem : problem) {
                buf.append("\t");
                buf.append(aProblem);
                buf.append("\n");
            }
        } else if (this.object != null) {
            buf.append(this.object);
        }
        return buf.toString();
    }
}
