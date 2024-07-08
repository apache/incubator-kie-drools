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
package org.kie.dmn.feel.lang.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.runtime.functions.CustomFEELFunction;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;
import org.kie.dmn.feel.runtime.functions.JavaFunction;
import org.kie.dmn.feel.util.Msg;
import org.kie.dmn.feel.util.StringEvalHelper;

public class ContextNode
        extends BaseNode {

    private List<ContextEntryNode> entries = new ArrayList<>();
    private MapBackedType parsedResultType = new MapBackedType();

    public ContextNode(ParserRuleContext ctx) {
        super( ctx );
    }

    public ContextNode(ParserRuleContext ctx, ListNode list) {
        super( ctx );
        List<ContextEntryNode> entryNodes = list.getElements().stream()
                .map(ContextEntryNode.class::cast)
                .toList();
        setListNode(entryNodes);
    }

    public ContextNode(List<ContextEntryNode> entryNodes, String text) {
        setListNode(entryNodes);
        this.setText(text);
    }

    public List<ContextEntryNode> getEntries() {
        return entries;
    }

    public void setEntries(List<ContextEntryNode> entries) {
        this.entries = entries;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        try {
            ctx.enterFrame();
            Map<String, Object> c = new LinkedHashMap<>();
            for( ContextEntryNode cen : entries ) {
                String name = StringEvalHelper.normalizeVariableName(cen.evaluateName(ctx ) );
                if (c.containsKey(name)) {
                    ctx.notifyEvt( astEvent( FEELEvent.Severity.ERROR, Msg.createMessage( Msg.DUPLICATE_KEY_CTX, name)) );
                    return null;
                }
                Object value = cen.evaluate( ctx );
                if( value instanceof CustomFEELFunction ) {
                    // helpful for debugging
                    ((CustomFEELFunction) value).setName( name );
                } else if( value instanceof JavaFunction ) {
                    ((JavaFunction) value).setName( name );
                } else if ( value instanceof DTInvokerFunction ) {
                    ((DTInvokerFunction) value).setName(name);
                }

                ctx.setValue( name, value );
                c.put( name, value );
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
        return entries.toArray( new ASTNode[entries.size()] );
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    private void setListNode(List<ContextEntryNode> entryNodes) {
        for( ContextEntryNode entry : entryNodes ) {
            entries.add( entry );
            parsedResultType.addField(entry.getName().getText(), entry.getResultType());
        }
    }
}
