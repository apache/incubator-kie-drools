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
import org.kie.dmn.core.api.event.InternalDMNRuntimeEventManager;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.feel.model.v1_1.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DMNRelationEvaluator
        implements DMNExpressionEvaluator {
    private static final Logger logger = LoggerFactory.getLogger( DMNRelationEvaluator.class );

    private final String   name;
    private final String   nodeId;
    private final Relation relationDef;
    private final List<String> columns = new ArrayList<>(  );
    private final List<List<DMNExpressionEvaluator>> rows = new ArrayList<>();

    public DMNRelationEvaluator(String name, String nodeId, Relation relationDef) {
        this.name = name;
        this.nodeId = nodeId;
        this.relationDef = relationDef;
    }

    public void addColumn(String name) {
        this.columns.add( name );
    }

    public void addRow(List<DMNExpressionEvaluator> vals) {
        this.rows.add( vals );
    }

    public List<String> getColumns() {
        return this.columns;
    }

    public List<List<DMNExpressionEvaluator>> getRows() {
        return this.rows;
    }

    @Override
    public EvaluatorResult evaluate(InternalDMNRuntimeEventManager eventManager, DMNResultImpl result) {
        List<Map<String,Object>> results = new ArrayList<>();
        DMNContext previousContext = result.getContext();
        DMNContextImpl dmnContext = (DMNContextImpl) previousContext.clone();
        result.setContext( dmnContext );

        try {

            for ( int rowIndex = 0; rowIndex < rows.size(); rowIndex++ ) {
                List<DMNExpressionEvaluator> row = rows.get( rowIndex );
                Map<String, Object> element = new HashMap<>(  );
                for( int i = 0; i < columns.size(); i++ ) {
                    try {
                        EvaluatorResult er = row.get( i ).evaluate( eventManager, result );
                        if ( er.getResultType() == ResultType.SUCCESS ) {
                            element.put( columns.get( i ), er.getResult() );
                        } else {
                            String message = "Error evaluating row element on position '" + (i + 1) + "' on row '" + (rowIndex+1) + "' of relation '"+name+"'";
                            logger.error( message );
                            result.addMessage(
                                    DMNMessage.Severity.ERROR,
                                    message,
                                    nodeId );
                            return new EvaluatorResult( results, ResultType.FAILURE );
                        }
                    } catch ( Exception e ) {
                        String message = "Error evaluating row element on position '" + (i + 1) + "' on row '" + (rowIndex+1) + "' of relation '"+name+"'";
                        logger.error( message );
                        result.addMessage(
                                DMNMessage.Severity.ERROR,
                                message,
                                nodeId,
                                e );
                        return new EvaluatorResult( results, ResultType.FAILURE );
                    }
                }
                results.add( element );
            }
        } finally {
            result.setContext( previousContext );
        }
        return new EvaluatorResult( results, ResultType.SUCCESS );
    }

}
