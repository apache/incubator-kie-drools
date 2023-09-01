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
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.drools.model.codegen.execmodel.generator.DrlxParseUtil;

public class LambdaUtil {

    private LambdaUtil() {

    }

    public static Expression appendNewLambdaToOld(LambdaExpr l1, LambdaExpr l2) {
        ExpressionStmt l1ExprStmt = (ExpressionStmt) l1.getBody();
        ExpressionStmt l2ExprStmt = (ExpressionStmt) l2.getBody();

        DrlxParseUtil.RemoveRootNodeResult removeRootNodeResult = DrlxParseUtil.removeRootNode(l2ExprStmt.getExpression());

        NodeWithOptionalScope<?> newExpr = (NodeWithOptionalScope<?>) removeRootNodeResult.getFirstChild();

        newExpr.setScope(l1ExprStmt.getExpression());
        l1.setBody(new ExpressionStmt(removeRootNodeResult.getWithoutRootNode()));
        return l1;
    }
}
