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
package org.kie.dmn.feel.lang.ast;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.infixexecutors.*;

/**
 * Enum responsible to execute Infix operations.
 * Each entry provide
 * 1. the symbol of the operator
 * 2. the actual <code>InfixExecutor</code> instance to be executed
 */
public enum InfixOperator {
    ADD("+", AddExecutor.instance()),
    SUB("-", SubExecutor.instance()),
    MULT("*", MultExecutor.instance()),
    DIV("/", DivExecutor.instance()),
    POW("**", PowExecutor.instance()),
    LTE("<=", LteExecutor.instance()),
    LT("<", LtExecutor.instance()),
    GT(">", GtExecutor.instance()),
    GTE(">=", GteExecutor.instance()),
    EQ("=", EqExecutor.instance()),
    NE("!=", NeExecutor.instance()),
    AND("and", AndExecutor.instance()),
    OR("or", OrExecutor.instance());

    public final String symbol;
    private final InfixExecutor infixExecutor;

    InfixOperator(String symbol,
                  InfixExecutor infixExecutor) {
        this.symbol = symbol;
        this.infixExecutor = infixExecutor;
    }

    public static InfixOperator determineOperator(String symbol) {
        for (InfixOperator op : InfixOperator.values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("No operator found for symbol '" + symbol + "'");
    }

    public boolean isBoolean() {
        return this == LTE || this == LT || this == GT || this == GTE || this == EQ || this == NE || this == AND || this == OR;
    }

    public String getSymbol() {
        return symbol;
    }

    public Object evaluate(final Object left, final Object right, EvaluationContext ctx) {
        return infixExecutor.evaluate(left, right, ctx);
    }

    public Object evaluate(InfixOpNode opNode, EvaluationContext ctx) {
        return infixExecutor.evaluate(opNode, ctx);
    }

}
