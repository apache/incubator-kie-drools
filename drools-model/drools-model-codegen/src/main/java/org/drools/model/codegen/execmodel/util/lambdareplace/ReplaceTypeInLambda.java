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
package org.drools.model.codegen.execmodel.util.lambdareplace;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.model.codegen.execmodel.PackageModel.DOMAIN_CLASS_METADATA_INSTANCE;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toVar;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.ACCUMULATE_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.ALPHA_INDEXED_BY_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.BETA_INDEXED_BY_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.BIND_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EVAL_EXPR_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EXPR_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PATTERN_CALL;
import static org.drools.modelcompiler.util.ClassUtil.asJavaSourceName;
import static org.drools.modelcompiler.util.ClassUtil.javaSourceNameToClass;

public class ReplaceTypeInLambda {

    private static final Logger logger = LoggerFactory.getLogger(ReplaceTypeInLambda.class);

    private ReplaceTypeInLambda() {

    }

    public static void replaceTypeInExprLambdaAndIndex(String bindingId, Class accumulateFunctionResultType, Expression expression) {
        if (expression instanceof MethodCallExpr && (( MethodCallExpr ) expression).getNameAsString().equals( ACCUMULATE_CALL )) {
            return;
        }
        replaceTypeInExprLambda(bindingId, accumulateFunctionResultType, expression);
        replaceTypeInIndex(bindingId, accumulateFunctionResultType, expression);
    }

    private static void replaceTypeInExprLambda(String bindingId, Class accumulateFunctionResultType, Expression expression) {
        expression.findAll(MethodCallExpr.class).forEach(mc -> {
            if (mc.getArguments().stream().anyMatch(a -> a.toString().equals(toVar(bindingId)))) {
                List<LambdaExpr> allLambdas = new ArrayList<>();

                if (mc.getNameAsString().equals(EXPR_CALL)) {
                    allLambdas.addAll(expression.findAll(LambdaExpr.class));
                }

                if (mc.getNameAsString().equals(EVAL_EXPR_CALL)) {
                    allLambdas.addAll(expression.findAll(LambdaExpr.class));
                }

                Optional<Expression> optScope = mc.getScope();
                if (optScope.isPresent() && optScope.get().isMethodCallExpr() && optScope.get().asMethodCallExpr().getNameAsString().equals(BIND_CALL)) {
                    allLambdas.addAll(expression.findAll(LambdaExpr.class));
                }

                Optional<Node> optParent = mc.getParentNode(); // In the Pattern DSL they're in the direct pattern
                if (mc.getNameAsString().equals(PATTERN_CALL) && optParent.isPresent()) {
                    List<LambdaExpr> all = expression.findAll(LambdaExpr.class);
                    allLambdas.addAll(all);
                }
                allLambdas.forEach(lambdaExpr -> replaceLambdaParameter(accumulateFunctionResultType, lambdaExpr, bindingId));
            }
        });
    }

    private static void replaceLambdaParameter(Class accumulateFunctionResultType, LambdaExpr lambdaExpr, String bindingId) {
        for (Parameter a : lambdaExpr.getParameters()) {

            if (!a.getType().isUnknownType() &&
                    (a.getNameAsString().equals("_this") || a.getNameAsString().equals(bindingId))) {
                a.setType( toClassOrInterfaceType(accumulateFunctionResultType) );
            }
        }
    }

    private static void replaceTypeInIndex(String bindingId, Class accumulateFunctionResultType, Expression expression) {
        Optional<MethodCallExpr> optPattern = expression.findAll(MethodCallExpr.class)
                                                        .stream()
                                                        .filter(mce -> mce.getNameAsString().equals(PATTERN_CALL))
                                                        .filter(mce -> mce.getArguments().stream().anyMatch(a -> a.toString().equals(toVar(bindingId))))
                                                        .findAny();
        if (optPattern.isEmpty()) {
            return;
        }

        expression.findAll(MethodCallExpr.class)
                  .stream()
                  .filter(mce -> {
                      String methodName = mce.getNameAsString();
                      return (methodName.equals(ALPHA_INDEXED_BY_CALL) || methodName.equals(BETA_INDEXED_BY_CALL));
                  })
                  .forEach(mce -> mce.getArguments()
                                     .stream()
                                     .filter(MethodCallExpr.class::isInstance)
                                     .map(MethodCallExpr.class::cast)
                                     .filter(argMce -> argMce.getName().asString().equals("getPropertyIndex"))
                                     .map(MethodCallExpr::getScope)
                                     .filter(Optional::isPresent)
                                     .map(Optional::get)
                                     .filter(FieldAccessExpr.class::isInstance)
                                     .map(FieldAccessExpr.class::cast)
                                     .forEach(fieldAccessExpr -> {
                                         Class<?> domainClass = extractDomainClass(fieldAccessExpr.getName().asString());
                                         if (domainClass != null && domainClass != accumulateFunctionResultType && domainClass.isAssignableFrom(accumulateFunctionResultType)) {
                                             // e.g. from java_lang_Number_Metadata_INSTANCE to java_lang_Long_Metadata_INSTANCE
                                             fieldAccessExpr.setName(asJavaSourceName(accumulateFunctionResultType) + DOMAIN_CLASS_METADATA_INSTANCE);
                                         }
                                     }));
    }

    private static Class<?> extractDomainClass(String domainClassInstance) {
        if (!domainClassInstance.endsWith(DOMAIN_CLASS_METADATA_INSTANCE)) {
            return null;
        }
        String javaSourceName = domainClassInstance.substring(0, domainClassInstance.lastIndexOf(DOMAIN_CLASS_METADATA_INSTANCE));
        try {
            return javaSourceNameToClass(javaSourceName);
        } catch (ClassNotFoundException e) {
            logger.info("Class not found. Not an issue unless the generated code causes a compile error : domainClassInstance = {} ", domainClassInstance);
            return null;
        }
    }

}
