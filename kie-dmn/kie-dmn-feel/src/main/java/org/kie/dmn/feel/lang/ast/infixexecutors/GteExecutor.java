/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.lang.ast.infixexecutors;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.util.EvalHelper;

import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.or;

public class GteExecutor implements InfixExecutor {

    private static final GteExecutor INSTANCE = new GteExecutor();

    private GteExecutor() {
    }

    public static GteExecutor instance() {
        return INSTANCE;
    }

    @Override
    public Object evaluate(Object left, Object right, EvaluationContext ctx) {
        return evaluate(new EvaluatedParameters(left, right), ctx);
    }

    @Override
    public Object evaluate(InfixOpNode infixNode, EvaluationContext ctx) {
        return evaluate(new EvaluatedParameters(infixNode.getLeft().evaluate(ctx), infixNode.getRight().evaluate(ctx)), ctx);
    }

    private Object evaluate(EvaluatedParameters params, EvaluationContext ctx) {
        Object leftLTE = params.getLeft();
        Object rightLTE = params.getRight();
        return or(EvalHelper.compare(leftLTE, rightLTE, ctx, (l, r) -> l.compareTo(r) > 0),
                EvalHelper.isEqual(leftLTE, rightLTE, ctx),
                ctx); // do not use Java || to avoid potential NPE due to FEEL 3vl.
    }

}
