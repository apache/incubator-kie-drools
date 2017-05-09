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
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.Msg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QualifiedNameNode
        extends BaseNode {

    private List<NameRefNode> parts;

    public QualifiedNameNode(ParserRuleContext ctx, List<NameRefNode> parts) {
        super( ctx );
        this.parts = parts;
    }

    public List<NameRefNode> getParts() {
        return parts;
    }

    public void setParts(List<NameRefNode> parts) {
        this.parts = parts;
    }

    public String[] getPartsAsStringArray() {
        return parts.stream().map( p -> p.getText() ).toArray( String[]::new );
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        Object current = parts.get( 0 ).evaluate( ctx );
        try {
            if ( current != null ) {
                for ( int i = 1; i < parts.size(); i++ ) {
                    String n = parts.get( i ).getText();
                    if ( current instanceof Collection ) {
                        // e.g.: FEEL: MyList.property1
                        // can't use Stream API as from EvalHelper.getValue I need to listen for checked exception
                        Collection<Object> result = new ArrayList<>();
                        for ( Object e : (Collection<?>) current ) {
                            result.add( EvalHelper.getValue( e, EvalHelper.normalizeVariableName( n ) ) );
                        }
                        current = result;
                    } else {
                        current = EvalHelper.getValue( current, EvalHelper.normalizeVariableName( n ) );
                    }
                }
                return current;
            }
        } catch ( Exception e ) {
            ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.ERROR_ACCESSING_QUALIFIED_NAME, getText()), e) );
            return null;
        }
        return null;
    }

}
