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

package org.kie.dmn.feel11;

import org.kie.dmn.lang.ast.ASTBuilderFactory;
import org.kie.dmn.lang.ast.BaseNode;

public class ASTBuilderVisitor
        extends FEEL_1_1BaseVisitor<BaseNode> {

    @Override
    public BaseNode visitNumberLiteral(FEEL_1_1Parser.NumberLiteralContext ctx) {
        return ASTBuilderFactory.newNumberNode( ctx );
    }

    @Override
    public BaseNode visitBooleanLiteral(FEEL_1_1Parser.BooleanLiteralContext ctx) {
        return ASTBuilderFactory.newBooleanNode( ctx );
    }

    @Override
    public BaseNode visitSignedUnaryExpression(FEEL_1_1Parser.SignedUnaryExpressionContext ctx) {
        BaseNode node = visit( ctx.unaryExpression() );
        return ASTBuilderFactory.newSignedUnaryNode( ctx, node );
    }
}
