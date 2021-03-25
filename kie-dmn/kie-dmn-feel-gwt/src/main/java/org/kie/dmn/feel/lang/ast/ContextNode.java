/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.lang.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.runtime.functions.CustomFEELFunction;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;
import org.kie.dmn.feel.util.EvalHelper;

public class ContextNode extends BaseNode {

    private List<ContextEntryNode> entries = new ArrayList<>();
    private MapBackedType parsedResultType = new MapBackedType();

    public ContextNode(final ParserRuleContext ctx) {
        super(ctx);
    }

    public ContextNode(final ParserRuleContext ctx,
                       final ListNode list) {
        super(ctx);
        for (final BaseNode node : list.getElements()) {
            final ContextEntryNode entry = (ContextEntryNode) node;
            entries.add(entry);
            parsedResultType.addField(entry.getName().getText(), entry.getResultType());
        }
    }

    public List<ContextEntryNode> getEntries() {
        return entries;
    }

    public void setEntries(final List<ContextEntryNode> entries) {
        this.entries = entries;
    }

    @Override
    public Object evaluate(final EvaluationContext ctx) {
        try {
            ctx.enterFrame();
            final Map<String, Object> c = new LinkedHashMap<>();

            for (final ContextEntryNode cen : entries) {
                final String name = EvalHelper.normalizeVariableName(cen.evaluateName(ctx));
                final Object value = cen.evaluate(ctx);

                if (value instanceof CustomFEELFunction) {
                    ((CustomFEELFunction) value).setName(name);
                } else if (value instanceof DTInvokerFunction) {
                    ((DTInvokerFunction) value).setName(name);
                }

                ctx.setValue(name, value);
                c.put(name, value);
            }
            return c;
        } finally {
            ctx.exitFrame();
        }
    }

    @Override
    public Type getResultType() {
        return parsedResultType;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return entries.toArray(new ASTNode[entries.size()]);
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
