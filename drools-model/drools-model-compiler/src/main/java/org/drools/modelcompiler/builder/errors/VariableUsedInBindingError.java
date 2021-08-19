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

package org.drools.modelcompiler.builder.errors;

import org.drools.compiler.compiler.DroolsError;
import org.kie.internal.builder.ResultSeverity;

public class VariableUsedInBindingError extends DroolsError {

    private String usedDeclaration;
    private String constraintExpressionString;

    private int[] errorLines = new int[1];

    public VariableUsedInBindingError(String usedDeclaration, String constraintExpressionString) {
        super();
        this.usedDeclaration = usedDeclaration;
        this.constraintExpressionString = constraintExpressionString;
        this.errorLines[0] = -1;
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    @Override
    public String getMessage() {
        return String.format("Variables can not be used inside bindings. Variable [%s] is being used in binding '%s'",
                             usedDeclaration,
                             constraintExpressionString);
    }

    @Override
    public int[] getLines() {
        return errorLines;
    }
}
