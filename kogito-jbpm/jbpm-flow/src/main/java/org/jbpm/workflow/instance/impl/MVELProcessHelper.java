/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workflow.instance.impl;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.drools.mvel.MVELSafeHelper;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.mvel2.ErrorDetail;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

public class MVELProcessHelper {

    private static final boolean IS_JDK = System.getProperty("org.graalvm.nativeimage.imagecode") == null;
    private static final Supplier<MVELEvaluator> EVALUATOR_SUPPLIER =
            IS_JDK ?
                    MVELSafeHelper::getEvaluator :
                    () -> {
                        throw new UnsupportedOperationException("MVEL evaluation is not supported in native image");
                    };

    private static final Function<String, Serializable> EXPR_COMPILER =
            IS_JDK ?
                    MVEL::compileExpression :
                    expr -> {
                        throw new UnsupportedOperationException("MVEL compilation is not supported in native image");
                    };

    private static final Function<String, List<ErrorDetail>> EXPR_COMPILER_DETAILED =
            IS_JDK ?
                    MVELProcessHelper::expressionCompiler :
                    expr -> {
                        throw new UnsupportedOperationException("MVEL compilation is not supported in native image");
                    };

    public static MVELEvaluator evaluator() {
        return EVALUATOR_SUPPLIER.get();
    }

    public static Serializable compileExpression(String expr) {
        return EXPR_COMPILER.apply(expr);
    }

    public static List<ErrorDetail> validateExpression(String expression) {
        return EXPR_COMPILER_DETAILED.apply(expression);
    }

    private static List<ErrorDetail> expressionCompiler(String actionString) {
        ParserContext parserContext = new ParserContext();
        ExpressionCompiler compiler = new ExpressionCompiler(actionString,
                                                             parserContext);
        compiler.setVerifying(true);
        compiler.compile();
        return parserContext.getErrorList();
    }
}
