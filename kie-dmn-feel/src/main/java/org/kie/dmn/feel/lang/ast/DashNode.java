/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.UnaryTest;

public class DashNode
        extends BaseNode {

    public DashNode(ParserRuleContext ctx) {
        super( ctx );
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
}
