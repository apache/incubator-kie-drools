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

import org.kie.internal.builder.ResultSeverity;

public class TypeDeclarationWarning extends BaseKnowledgeBuilderResultImpl {
    private String message;
    private int[]  line;

    public TypeDeclarationWarning(final String message, final int line) {
        super(null);
        this.message = message;
        this.line = new int[] { line };
    }

    public int[] getLines() {
        return this.line;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return this.getMessage();
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.WARNING;
    }

}
