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

import org.kie.dmn.core.api.DMNContext;
import org.kie.dmn.core.api.DMNMessage;
import org.kie.dmn.core.api.DMNType;
import org.kie.dmn.core.api.event.InternalDMNRuntimeEventManager;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.feel.model.v1_1.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void addEntry(String name, DMNType type, DMNExpressionEvaluator evaluator) {
        this.entries.add( new ContextEntryDef( name, type, evaluator ) );
    }

    public List<ContextEntryDef> getEntries() {
        return this.entries;
    }

    @Override
    public EvaluatorResult evaluate(InternalDMNRuntimeEventManager eventManager, DMNResultImpl result) {
        // when this evaluator is executed, it should either return a Map of key/value pairs
        // where keys are the name of the entries and values are the result of the evaluations
        // OR if a default result is implemented, it should return the result instead
        Map<String, Object> results = new HashMap<>();
        DMNContext previousContext = result.getContext();
        DMNContextImpl dmnContext = (DMNContextImpl) previousContext.clone();
        result.setContext( dmnContext );

        try {
            for ( ContextEntryDef ed : entries ) {
                try {
                    EvaluatorResult er = ed.getEvaluator().evaluate( eventManager, result );
                    if ( er.getResultType() == ResultType.SUCCESS ) {
                        results.put( ed.getName(), er.getResult() );
                        dmnContext.set( ed.getName(), er.getResult() );
                    } else {
                        String message = "Error evaluating context extry '" + ed.getName() + "' on context '" + name + "'";
                        logger.error( message );
                        result.addMessage(
                                DMNMessage.Severity.ERROR,
                                message,
                                null ); // can we retrieve the source ID here?
                        return new EvaluatorResult( results, ResultType.FAILURE );
                    }
                } catch ( Exception e ) {
                    logger.error( "Error invoking expression for node '" + name + "'.", e );
                    return new EvaluatorResult( results, ResultType.FAILURE );
                }
            }
        } finally {
            result.setContext( previousContext );
        }
        if( results.containsKey( RESULT_ENTRY ) ) {
            return new EvaluatorResult( results.get( RESULT_ENTRY ), ResultType.SUCCESS );
        } else {
            return new EvaluatorResult( results, ResultType.SUCCESS );
        }
    }

    public static class ContextEntryDef {
        private String                 name;
        private DMNType                type;
        private DMNExpressionEvaluator evaluator;

        public ContextEntryDef(String name, DMNType type, DMNExpressionEvaluator evaluator) {
            this.name = name;
            this.type = type;
            this.evaluator = evaluator;
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
    }

}
