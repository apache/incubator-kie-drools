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

package org.kie.dmn.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel11.FEEL_1_1Parser;

public class ASTBuilderFactory {

    public static BaseNode newNumberNode(ParserRuleContext ctx) {
        return new NumberNode( ctx );
    }

    public static BaseNode newBooleanNode(ParserRuleContext ctx) {
        return new BooleanNode( ctx );
    }

    public static BaseNode newSignedUnaryNode(FEEL_1_1Parser.SignedUnaryExpressionContext ctx, BaseNode expr) {
        return new SignedUnaryNode( ctx, expr );
    }
}
