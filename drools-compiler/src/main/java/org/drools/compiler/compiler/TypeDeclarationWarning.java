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
package org.drools.compiler.compiler;

import org.drools.drl.parser.BaseKnowledgeBuilderResultImpl;
import org.kie.internal.builder.ResultSeverity;

public class TypeDeclarationWarning extends BaseKnowledgeBuilderResultImpl {

    private int[]  line;

    public TypeDeclarationWarning(final String message, final int line) {
        super(null, message);
        this.line = new int[] { line };
    }

    public int[] getLines() {
        return this.line;
    }

    public String toString() {
        return this.getMessage();
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.WARNING;
    }

}
