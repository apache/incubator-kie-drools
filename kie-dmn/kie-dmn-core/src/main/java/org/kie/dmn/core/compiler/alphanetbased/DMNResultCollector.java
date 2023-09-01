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

import org.drools.ancompiler.ResultCollectorSink;
import org.drools.model.functions.Function1;
import org.kie.dmn.feel.lang.EvaluationContext;

public class DMNResultCollector implements ResultCollectorSink {

    private final int row;
    private final String columnName;
    private final Function1<EvaluationContext, Object> outputEvaluationFunction;
    private final Results results;

    public DMNResultCollector(int row,
                              String columnName,
                              Results results,
                              Function1<EvaluationContext, Object> outputEvaluationFunction) {
        this.row = row;
        this.columnName = columnName;
        this.results = results;
        this.outputEvaluationFunction = outputEvaluationFunction;
    }

    @Override
    public void collectObject() {
        results.addResult(row, columnName, outputEvaluationFunction);
    }
}
