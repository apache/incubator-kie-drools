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

public class AndExecutor implements InfixExecutor {

    private static final AndExecutor INSTANCE = new AndExecutor();

    private AndExecutor() {
    }

    public static AndExecutor instance() {
        return INSTANCE;
    }

    @Override
    public Object evaluate(Object left, Object right, EvaluationContext ctx) {
        Boolean l = EvalHelper.getBooleanOrNull(left);
        Boolean r = EvalHelper.getBooleanOrNull(right);
        // have to check for all nulls first to avoid NPE
        if ((l == null && r == null) || (l == null && r == true) || (r == null && l == true)) {
            return null;
        } else if (l == null || r == null) {
            return false;
        }
        return l && r;
    }

    @Override
    public Object evaluate(InfixOpNode infixNode, EvaluationContext ctx) {
        Boolean leftAND = EvalHelper.getBooleanOrNull(infixNode.getLeft().evaluate(ctx));
        if (leftAND != null) {
            if (leftAND.booleanValue()) {
                return EvalHelper.getBooleanOrNull(infixNode.getRight().evaluate(ctx));
            } else {
                return Boolean.FALSE; //left hand operand is false, we do not need to evaluate right side
            }
        } else {
            Boolean rightAND = EvalHelper.getBooleanOrNull(infixNode.getRight().evaluate(ctx));
            return Boolean.FALSE.equals(rightAND) ? Boolean.FALSE : null;
        }
    }

}
