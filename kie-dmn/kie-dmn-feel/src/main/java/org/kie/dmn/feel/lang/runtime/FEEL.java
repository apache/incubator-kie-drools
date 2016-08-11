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

package org.kie.dmn.feel.lang.runtime;

import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.lang.feel11.FEELParser;
import org.kie.dmn.feel.lang.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.lang.impl.CompiledExpressionImpl;
import org.kie.dmn.feel.lang.impl.CompilerContextImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.EvalHelper;

import java.util.Map;

/**
 * Language runtime entry point
 */
public class FEEL {

    public static CompilerContext newCompilerContext() {
        return new CompilerContextImpl();
    }

    public static CompiledExpression compile(String expression, CompilerContext ctx) {
        FEEL_1_1Parser parser = FEELParser.parse( expression, ctx.getInputVariables().keySet().toArray( new String[0] ) );
        ParseTree tree = parser.expression();
        ASTBuilderVisitor v = new ASTBuilderVisitor();
        BaseNode expr = v.visit( tree );
        CompiledExpression ce = new CompiledExpressionImpl( expr );
        return ce;
    }

    public static Object evaluate(String expression, Map<String, Object> inputVariables) {
        CompilerContext ctx = newCompilerContext();
        if ( inputVariables != null ) {
            inputVariables.entrySet().stream().forEach( e -> ctx.addInputVariable( e.getKey(), BuiltInType.determineTypeFromInstance( e ) ) );
        }
        CompiledExpression expr = compile( expression, ctx );
        return evaluate( expr, inputVariables );
    }

    public static Object evaluate(CompiledExpression expr, Map<String, Object> inputVariables) {
        return ((CompiledExpressionImpl) expr).evaluate( inputVariables );
    }

}
