/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package org.kie.dmn.feel.codegen.feel11;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.Msg;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;

public class Functions {
    public static final ClassOrInterfaceType TYPE_CUSTOM_FEEL_FUNCTION =
            parseClassOrInterfaceType(CompiledCustomFEELFunction.class.getSimpleName());
    private static final Expression ANONYMOUS_STRING_LITERAL = new StringLiteralExpr("<anonymous>");
    private static final Expression EMPTY_LIST = parseExpression("java.util.Collections.emptyList()");

    public static Expression external(List<String> paramNames, BaseNode body) {
        EvaluationContextImpl emptyEvalCtx =
                new EvaluationContextImpl(Functions.class.getClassLoader(), new FEELEventListenersManager());


        Map<String, Object> conf = (Map<String, Object>) body.evaluate(emptyEvalCtx);
        Map<String, String> java = (Map<String, String>) conf.get( "java" );

        if (java != null) {

            String className = java.get("class");
            String methodSignature = java.get("method signature");
            if (className == null || methodSignature == null) {
                throw new FEELCompilationError(
                        Msg.createMessage(Msg.UNABLE_TO_FIND_EXTERNAL_FUNCTION_AS_DEFINED_BY, methodSignature));
            }

            return FunctionDefs.asMethodCall(className, methodSignature, paramNames);
        } else {
            throw new FEELCompilationError(Msg.createMessage(Msg.UNABLE_TO_FIND_EXTERNAL_FUNCTION_AS_DEFINED_BY, null));
        }
    }

    public static ObjectCreationExpr internal(Expression parameters, Expression body) {
        ObjectCreationExpr functionDefExpr = new ObjectCreationExpr();
        functionDefExpr.setType(TYPE_CUSTOM_FEEL_FUNCTION);
        functionDefExpr.addArgument(ANONYMOUS_STRING_LITERAL);
        functionDefExpr.addArgument(parameters);
        functionDefExpr.addArgument(body);
        functionDefExpr.addArgument(new MethodCallExpr(new NameExpr("feelExprCtx"), "current"));
        return functionDefExpr;
    }

    public static DirectCompilerResult declaration(FunctionDefNode n, MethodCallExpr list, Expression fnBody) {
        LambdaExpr lambda = Expressions.lambda(fnBody);
        String fnName = Constants.functionName(n.getBody().getText());
        DirectCompilerResult r = DirectCompilerResult.of(
                Functions.internal(list, new NameExpr(fnName)),
                BuiltInType.FUNCTION);
        r.addFieldDesclaration(Constants.function(fnName, lambda));
        return r;
    }




}
