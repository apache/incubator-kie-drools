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
package org.kie.dmn.core.compiler.alphanetbased.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.OutputClause;

import static org.kie.dmn.core.compiler.DMNEvaluatorCompiler.inferTypeRef;

public class OutputClausesWithType {

    private final DMNModelImpl dmnModel;
    private final DecisionTable decisionTable;

    public OutputClausesWithType(DMNModelImpl dmnModel, DecisionTable decisionTable) {
        this.dmnModel = dmnModel;
        this.decisionTable = decisionTable;
    }

    public List<OutputClauseWithType> inferTypeForOutputClauses(List<OutputClause> outputClauses) {
        List<OutputClauseWithType> outputClausesWithTypes = new ArrayList<>();
        for (OutputClause outputClause : outputClauses) {
            BaseDMNTypeImpl typeRef = inferTypeRef(dmnModel, decisionTable, outputClause);
            outputClausesWithTypes.add(new OutputClauseWithType(outputClause, typeRef));
        }
        return outputClausesWithTypes;
    }

    public static class OutputClauseWithType {

        private final OutputClause outputClause;
        private final BaseDMNTypeImpl dmnBaseType;

        public OutputClauseWithType(OutputClause outputClause, BaseDMNTypeImpl dmnBaseType) {
            this.outputClause = outputClause;
            this.dmnBaseType = dmnBaseType;
        }

        public OutputClause getOutputClause() {
            return outputClause;
        }

        public BaseDMNTypeImpl getDmnBaseType() {
            return dmnBaseType;
        }
    }
}
