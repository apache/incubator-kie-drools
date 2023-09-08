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
package org.kie.dmn.feel.codegen.feel11;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.Type;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.util.Msg;

import static com.github.javaparser.StaticJavaParser.parseType;

public class FunctionDefs {

    public static Expression asMethodCall(
            String className,
            String methodSignature,
            List<String> params) {
        // creating a simple algorithm to find the method in java
        // without using any external libraries in this initial implementation
        // might need to explicitly use a classloader here
        String[] mp = FunctionDefNode.parseMethod(methodSignature);
        try {
            String methodName = mp[0];
            String[] paramTypeNames = FunctionDefNode.parseParams(mp[1]);
            ArrayList<Expression> paramExprs = new ArrayList<>();
            if (paramTypeNames.length == params.size()) {
                for (int i = 0; i < params.size(); i++) {
                    String paramName = params.get(i);
                    String paramTypeName = paramTypeNames[i];
                    Type paramTypeCanonicalName =
                            parseType(
                                      FunctionDefNode.getType(paramTypeName, null).getCanonicalName());

                    Expression param =
                        new CastExpr(paramTypeCanonicalName,
                            new MethodCallExpr(
                                null,
                                "coerceTo",
                                new NodeList<>(
                                        new ClassExpr(paramTypeCanonicalName),
                                        new MethodCallExpr(
                                            new NameExpr("feelExprCtx"),
                                            "getValue",
                                            new NodeList<>(new StringLiteralExpr(paramName))
                                        ))));

                    paramExprs.add(param);
                }

                return new MethodCallExpr(
                        new NameExpr(className),
                        methodName,
                        new NodeList<>(paramExprs));
            } else {
                throw new FEELCompilationError(
                        Msg.createMessage(Msg.ERROR_RESOLVING_EXTERNAL_FUNCTION_AS_DEFINED_BY, methodSignature));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
