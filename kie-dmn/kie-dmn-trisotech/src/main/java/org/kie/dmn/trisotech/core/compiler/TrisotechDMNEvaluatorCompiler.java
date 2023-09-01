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
package org.kie.dmn.trisotech.core.compiler;

import java.util.UUID;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNEvaluatorCompiler;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.trisotech.core.ast.DMNConditionalEvaluator;
import org.kie.dmn.trisotech.core.ast.DMNFilterEvaluator;
import org.kie.dmn.trisotech.core.ast.DMNIteratorEvaluator;
import org.kie.dmn.trisotech.core.util.Msg;
import org.kie.dmn.trisotech.model.api.Conditional;
import org.kie.dmn.trisotech.model.api.Filter;
import org.kie.dmn.trisotech.model.api.Iterator;
import org.kie.dmn.trisotech.model.api.Iterator.IteratorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrisotechDMNEvaluatorCompiler extends DMNEvaluatorCompiler {

    private static final Logger logger = LoggerFactory.getLogger(TrisotechDMNEvaluatorCompiler.class);

    protected TrisotechDMNEvaluatorCompiler(DMNCompilerImpl compiler) {
        super(compiler);
    }

    @Override
    public DMNExpressionEvaluator compileExpression(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, Expression expression) {
        if (expression instanceof Conditional) {
            return compileConditional(ctx, model, node, exprName, (Conditional) expression);
        } else if (expression instanceof Iterator) {
            return compileIterator(ctx, model, node, exprName, (Iterator) expression);
        } else if (expression instanceof Filter) {
            return compileFilter(ctx, model, node, exprName, (Filter) expression);
        }
        return super.compileExpression(ctx, model, node, exprName, expression);
    }

    private DMNExpressionEvaluator compileConditional(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, Conditional expression) {
        DMNExpressionEvaluator ifEvaluator = compileExpression(ctx, model, node, exprName + " [if]", expression.getIf());
        DMNExpressionEvaluator thenEvaluator = compileExpression(ctx, model, node, exprName + " [then]", expression.getThen());
        DMNExpressionEvaluator elseEvaluator = compileExpression(ctx, model, node, exprName + " [else]", expression.getElse());

        if (ifEvaluator == null) {
            MsgUtil.reportMessage(logger, DMNMessage.Severity.ERROR, node.getSource(), model, null, null, Msg.MISSING_EXPRESSION_FOR_CONDITION, "if",
                    node.getIdentifierString());
            return null;
        }

        if (thenEvaluator == null) {
            MsgUtil.reportMessage(logger, DMNMessage.Severity.ERROR, node.getSource(), model, null, null, Msg.MISSING_EXPRESSION_FOR_CONDITION, "then",
                    node.getIdentifierString());
            return null;
        }

        if (elseEvaluator == null) {
            MsgUtil.reportMessage(logger, DMNMessage.Severity.ERROR, node.getSource(), model, null, null, Msg.MISSING_EXPRESSION_FOR_CONDITION, "else",
                    node.getIdentifierString());
            return null;
        }

        return new DMNConditionalEvaluator(exprName, node.getSource(), ifEvaluator, thenEvaluator, elseEvaluator);
    }

    private DMNExpressionEvaluator compileIterator(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, Iterator expression) {

        if (expression.getVariable() == null || expression.getVariable().length() == 0) {
            MsgUtil.reportMessage(logger, DMNMessage.Severity.ERROR, node.getSource(), model, null, null, Msg.MISSING_EXPRESSION_FOR_ITERATOR,
                    expression.getTypeRef().toString().toLowerCase(), node.getIdentifierString());
            return null;
        }

        DMNExpressionEvaluator inEvaluator = null;
        DMNExpressionEvaluator returnEvaluator = null;

        inEvaluator = compileExpression(ctx, model, node, exprName + " [in]", expression.getIn());

        try {
            ctx.enterFrame();
            DMNType outputType = compiler.resolveTypeRef(model, null, node.getSource(), expression.getTypeRef());
            DMNType elementType = compiler.resolveTypeRef(model, null, node.getSource(), expression.getIn().getTypeRef());
            if (elementType != null && elementType.isCollection() && elementType instanceof BaseDMNTypeImpl) {
                elementType = extractOrSynthesizeGeneric(model, (BaseDMNTypeImpl) elementType);
            }

            ctx.setVariable(expression.getVariable(), elementType != null ? elementType : model.getTypeRegistry().unknown());
            ctx.setVariable("partial", outputType != null ? outputType : model.getTypeRegistry().unknown());
            returnEvaluator = compileExpression(ctx, model, node, exprName + " [return]", expression.getReturn());
        } finally {
            ctx.exitFrame();
        }

        if (inEvaluator == null) {
            MsgUtil.reportMessage(logger, DMNMessage.Severity.ERROR, node.getSource(), model, null, null, Msg.MISSING_EXPRESSION_FOR_ITERATOR, "in",
                    node.getIdentifierString());
            return null;
        }

        if (returnEvaluator == null) {
            MsgUtil.reportMessage(logger, DMNMessage.Severity.ERROR, node.getSource(), model, null, null, Msg.MISSING_EXPRESSION_FOR_ITERATOR,
                    expression.getIteratorType() == IteratorType.FOR ? "return" : "satisfies", node.getIdentifierString());
            return null;
        }

        return new DMNIteratorEvaluator(exprName, node.getSource(), expression.getIteratorType(), expression.getVariable(), inEvaluator, returnEvaluator);
    }

    /**
     * extract the generic T from the DMN representation of FEEL:list<T>
     */
    private DMNType extractOrSynthesizeGeneric(DMNModelImpl model, BaseDMNTypeImpl elementType) {
        if (elementType.getBaseType() != null) {
            return elementType.getBaseType();
        } else if (elementType instanceof CompositeTypeImpl) {
            CompositeTypeImpl orig = (CompositeTypeImpl) elementType;
            return new CompositeTypeImpl(orig.getNamespace(),
                                         UUID.randomUUID().toString() + orig.getName(),
                                         UUID.randomUUID().toString() + orig.getId(),
                                         false, // synth T.
                                         orig.getFields(),
                                         null,
                                         null);
        }
        return model.getTypeRegistry().unknown();
    }

    private DMNExpressionEvaluator compileFilter(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, Filter expression) {
        DMNExpressionEvaluator inEvaluator = compileExpression(ctx, model, node, exprName + " [in]", expression.getIn());
        DMNExpressionEvaluator filterEvaluator;

        try {
            ctx.enterFrame();

            DMNType outputType = compiler.resolveTypeRef(model, null, node.getSource(), expression.getTypeRef());
            DMNType elementType = outputType;
            if (elementType != null && elementType.isCollection() && elementType instanceof BaseDMNTypeImpl) {
                elementType = extractOrSynthesizeGeneric(model, (BaseDMNTypeImpl) elementType);
            }

            ctx.setVariable("item", elementType != null ? elementType : model.getTypeRegistry().unknown());
            if (elementType != null && elementType.isComposite()) {
                elementType.getFields().forEach((k, v) -> ctx.setVariable(k, v != null ? v : model.getTypeRegistry().unknown()));
            }
            filterEvaluator = compileExpression(ctx, model, node, exprName + " [filter]", expression.getMatch());
        } finally {
            ctx.exitFrame();
        }

        if (inEvaluator == null) {
            MsgUtil.reportMessage(logger, DMNMessage.Severity.ERROR, node.getSource(), model, null, null, Msg.MISSING_EXPRESSION_FOR_FILTER, "in",
                    node.getIdentifierString());
            return null;
        }

        if (filterEvaluator == null) {
            MsgUtil.reportMessage(logger, DMNMessage.Severity.ERROR, node.getSource(), model, null, null, Msg.MISSING_EXPRESSION_FOR_FILTER, "filter",
                    node.getIdentifierString());
            return null;
        }

        return new DMNFilterEvaluator(exprName, node.getSource(), inEvaluator, filterEvaluator);
    }
}
