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
package org.drools.model.codegen.execmodel.util;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import org.junit.Test;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.assertj.core.api.Assertions.assertThat;

public class LambdaUtilTest {

    @Test
    public void appendaLambdaToOld() {

        LambdaExpr l1 = parseExpression("(_this) -> _this.getTimeFieldAsDate()");
        LambdaExpr l2 = parseExpression("(_this) -> _this.getTime()");

        Expression expected = parseExpression("(_this) -> _this.getTimeFieldAsDate().getTime()");

        Expression actual = LambdaUtil.appendNewLambdaToOld(l1, l2);
        assertThat(actual.toString()).isEqualTo(expected.toString());
    }

    @Test
    public void appendTwoMethodsToLambda() {

        LambdaExpr l1 = parseExpression("(_this) -> _this.getDueDate()");
        LambdaExpr l2 = parseExpression("(_this) -> _this.getTime().getTime()");

        Expression expected = parseExpression("(_this) -> _this.getDueDate().getTime().getTime()");

        Expression actual = LambdaUtil.appendNewLambdaToOld(l1, l2);
        assertThat(actual.toString()).isEqualTo(expected.toString());
    }
}