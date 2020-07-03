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

package org.kie.dmn.core.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeEventManagerUtils;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.Context;
import org.kie.dmn.model.api.ContextEntry;
import org.kie.dmn.model.api.FunctionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNContextEvaluator
        implements DMNExpressionEvaluator {
    private static final Logger logger       = LoggerFactory.getLogger( DMNContextEvaluator.class );
    public static final  String RESULT_ENTRY = "__RESULT__";

    private final String  name;
    private final Context contextDef;
    private List<ContextEntryDef> entries = new ArrayList<>();

    public DMNContextEvaluator(String name, Context contextDef) {
        this.name = name;
        this.contextDef = contextDef;
    }

    public void addEntry(String name, DMNType type, DMNExpressionEvaluator evaluator, ContextEntry ce) {
        this.entries.add( new ContextEntryDef( name, type, evaluator, ce ) );
    }

    public List<ContextEntryDef> getEntries() {
        return this.entries;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;
        // when this evaluator is executed, it should either return a Map of key/value pairs
        // where keys are the name of the entries and values are the result of the evaluations
        // OR if a default result is implemented, it should return the result instead
        Map<String, Object> results = new HashMap<>();
        DMNContext previousContext = result.getContext();
        DMNContext dmnContext = previousContext.clone();
        result.setContext( dmnContext );

        try {
            for ( ContextEntryDef ed : entries ) {
                try {
                    String entryVarId = getEntryVarId(ed);
                    String entryExprId = getEntryExprId(ed);
                    DMNRuntimeEventManagerUtils.fireBeforeEvaluateContextEntry( eventManager, name, ed.getName(), entryVarId, entryExprId, result );
                    EvaluatorResult er = ed.getEvaluator().evaluate( eventManager, result );
                    if ( er.getResultType() == ResultType.SUCCESS ) {
                        Object value = er.getResult();
                        if( ! ed.getType().isCollection() && value instanceof Collection &&
                            ((Collection)value).size()==1 ) {
                            // spec defines that "a=[a]", i.e., singleton collections should be treated as the single element
                            // and vice-versa
                            value = ((Collection)value).toArray()[0];
                        }
                        
                        if (((DMNRuntimeImpl) eventManager.getRuntime()).performRuntimeTypeCheck(result.getModel())) {
                            if (!(ed.getContextEntry().getExpression() instanceof FunctionDefinition)) {
                                // checking directly the result type...
                                if (ed.getType() != null && !ed.getType().isAssignableValue(value)) {
                                    MsgUtil.reportMessage(logger,
                                                          DMNMessage.Severity.ERROR,
                                                          contextDef,
                                                          result,
                                                          null,
                                                          null,
                                                          Msg.ERROR_EVAL_NODE_RESULT_WRONG_TYPE,
                                                          ed.getName(),
                                                          ed.getType(),
                                                          value);
                                    return new EvaluatorResultImpl(results, ResultType.FAILURE);
                                }
                            } else {
                                // TODO ...will need calculation/inference of function return type.
                            }
                        }
                        
                        results.put( ed.getName(), value );
                        dmnContext.set( ed.getName(), value );
                        DMNRuntimeEventManagerUtils.fireAfterEvaluateContextEntry( eventManager, name, ed.getName(), entryVarId, entryExprId, value, result );
                    } else {
                        MsgUtil.reportMessage( logger,
                                               DMNMessage.Severity.ERROR,
                                               contextDef,
                                               result,
                                               null,
                                               null,
                                               Msg.ERR_EVAL_CTX_ENTRY_ON_CTX,
                                               ed.getName(),
                                               name );
                        DMNRuntimeEventManagerUtils.fireAfterEvaluateContextEntry( eventManager, name, ed.getName(), entryVarId, entryExprId, er.getResult(), result );
                        return new EvaluatorResultImpl( results, ResultType.FAILURE );
                    }
                } catch ( Exception e ) {
                    logger.error("Error invoking expression for node '" + name + "'.", e);
                    MsgUtil.reportMessage(logger,
                                          DMNMessage.Severity.ERROR,
                                          contextDef,
                                          result,
                                          e,
                                          null,
                                          Msg.ERR_EVAL_CTX_ENTRY_ON_CTX_MSG,
                                          ed.getName(),
                                          name,
                                          e.getLocalizedMessage());
                    String entryVarId = getEntryVarId(ed);
                    String entryExprId = getEntryExprId(ed);
                    DMNRuntimeEventManagerUtils.fireAfterEvaluateContextEntry( eventManager, name, ed.getName(), entryVarId, entryExprId, null, result );
                    return new EvaluatorResultImpl( results, ResultType.FAILURE );
                }
            }
        } finally {
            result.setContext( previousContext );
        }
        if( results.containsKey( RESULT_ENTRY ) ) {
            return new EvaluatorResultImpl( results.get( RESULT_ENTRY ), ResultType.SUCCESS );
        } else {
            return new EvaluatorResultImpl( results, ResultType.SUCCESS );
        }
    }

    private String getEntryExprId(ContextEntryDef ed) {
        return ed.getContextEntry().getExpression() != null ? ed.getContextEntry().getExpression().getId() : null;
    }

    private String getEntryVarId(ContextEntryDef ed) {
        return ed.getContextEntry().getVariable() != null ? ed.getContextEntry().getVariable().getId() : null;
    }

    public static class ContextEntryDef {
        private String                 name;
        private DMNType                type;
        private DMNExpressionEvaluator evaluator;
        private ContextEntry           ce;

        public ContextEntryDef(String name, DMNType type, DMNExpressionEvaluator evaluator, ContextEntry ce) {
            this.name = name;
            this.type = type;
            this.evaluator = evaluator;
            this.ce = ce;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public DMNType getType() {
            return type;
        }

        public void setType(DMNType type) {
            this.type = type;
        }

        public DMNExpressionEvaluator getEvaluator() {
            return evaluator;
        }

        public void setEvaluator(DMNExpressionEvaluator evaluator) {
            this.evaluator = evaluator;
        }
        
        public ContextEntry getContextEntry() {
            return ce;
        }
        
        public void setContextEntry(ContextEntry ce) {
            this.ce = ce;
        }
    }

}
