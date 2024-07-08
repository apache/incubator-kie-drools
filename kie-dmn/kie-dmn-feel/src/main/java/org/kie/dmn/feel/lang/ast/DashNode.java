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

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;

public class DashNode
        extends BaseNode {

    public DashNode(ParserRuleContext ctx) {
        super( ctx );
    }

    public DashNode(String text) {
        this.setText(text);
    }

    @Override
    public UnaryTest evaluate(EvaluationContext ctx) {
        // a dash is a unary test that always evaluates to true
        return DashUnaryTest.INSTANCE;
    }

    public static class DashUnaryTest implements UnaryTest {
        public static DashUnaryTest INSTANCE = new DashUnaryTest();

        private DashUnaryTest() {
        }

        @Override
        public Boolean apply(EvaluationContext evaluationContext, Object o) {
            return Boolean.TRUE;
        }

        @Override
        public String toString() {
            return "UnaryTest{-}";
        }
    }

    @Override
    public Type getResultType() {
        return BuiltInType.BOOLEAN;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
