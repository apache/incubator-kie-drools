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
package org.kie.dmn.core.compiler.alphanetbased;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.ancompiler.CanInlineInANC;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.common.PropagationContext;

public class DMNResultCollectorAlphaSink extends LeftInputAdapterNode implements CanInlineInANC<DMNResultCollector> {

    private final int row;
    private final String columnName;
    private final String outputClass;

    public DMNResultCollectorAlphaSink(int id,
                                       ObjectSource source,
                                       BuildContext context,
                                       int row,
                                       String columnName,
                                       String outputClass) {
        super(id, source, context);
        this.row = row;
        this.columnName = columnName;
        this.outputClass = outputClass;
    }

    @Override
    public void assertObject(InternalFactHandle factHandle, PropagationContext propagationContext, ReteEvaluator reteEvaluator) {
        throwDoNotCallException();
    }

    @Override
    public void modifyObject(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, ReteEvaluator reteEvaluator) {
        throwDoNotCallException();
    }

    @Override
    public void byPassModifyToBetaNode(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, ReteEvaluator reteEvaluator) {
        throwDoNotCallException();
    }

    private void throwDoNotCallException() {
        throw new UnsupportedOperationException("This sink will never be called, it'll be inlined as a DMNResultCollector");
    }

    @Override
    public Expression toANCInlinedForm() {
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();

        objectCreationExpr.setType(StaticJavaParser.parseClassOrInterfaceType(DMNResultCollector.class.getCanonicalName()));
        objectCreationExpr.addArgument(new IntegerLiteralExpr(row));
        objectCreationExpr.addArgument(new StringLiteralExpr(columnName));
        objectCreationExpr.addArgument(StaticJavaParser.parseExpression("ctx.getResultCollector()"));

        Expression lambdaExpr = StaticJavaParser.parseExpression(String.format("(org.kie.dmn.feel.lang.EvaluationContext x) -> %s.getInstance().apply(x)", outputClass));
        objectCreationExpr.addArgument(lambdaExpr);

        return objectCreationExpr;
    }

    @Override
    public Class<DMNResultCollector> inlinedType() {
        return DMNResultCollector.class;
    }
}
