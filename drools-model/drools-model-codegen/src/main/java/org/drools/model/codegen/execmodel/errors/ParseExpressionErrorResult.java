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
package org.drools.model.codegen.execmodel.errors;

import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import org.drools.drl.parser.DroolsError;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.mvel.parser.printer.PrintUtil;
import org.kie.internal.builder.ResultSeverity;

public class ParseExpressionErrorResult extends DroolsError {

    private Expression expr;

    private int[] errorLines = new int[1];

    public ParseExpressionErrorResult(Expression expr) {
        super("Unable to Analyse Expression " + PrintUtil.printNode(expr) + ":");
        this.expr = expr;
        this.errorLines[0] = -1;
    }

    public ParseExpressionErrorResult(Expression expr, Optional<BaseDescr> descrOpt) {
        this(expr);
        descrOpt.ifPresent(descr -> this.errorLines[0] = descr.getLine());
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    @Override
    public int[] getLines() {
        return errorLines;
    }
}
