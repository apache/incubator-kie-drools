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

package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.functions.CustomFEELFunction;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;
import org.kie.dmn.feel.runtime.functions.JavaFunction;
import org.kie.dmn.feel.util.EvalHelper;

import java.util.*;

public class ContextNode
        extends BaseNode {

    private List<ContextEntryNode> entries = new ArrayList<>();

    public ContextNode(ParserRuleContext ctx) {
        super( ctx );
    }

    public ContextNode(ParserRuleContext ctx, ListNode list) {
        super( ctx );
        for( BaseNode node : list.getElements() ) {
            entries.add( (ContextEntryNode) node );
        }
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
                String name = EvalHelper.normalizeVariableName( cen.evaluateName( ctx ) );
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

}
