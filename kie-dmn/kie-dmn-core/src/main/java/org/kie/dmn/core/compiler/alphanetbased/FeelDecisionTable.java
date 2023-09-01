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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.compiler.alphanetbased.evaluator.OutputClausesWithType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.model.api.UnaryTests;

// This is a trimmed down version of the original Decision Table used to evaluate Hit Policies
public class FeelDecisionTable implements org.kie.dmn.feel.runtime.decisiontables.DecisionTable {

    private final String name;
    private final List<OutputClause> outputs;

    public FeelDecisionTable(String decisionTableName,
                             List<OutputClausesWithType.OutputClauseWithType> outputs,
                             DMNFEELHelper feelHelper,
                             Map<String, Type> allVariableTypes,
                             DMNType unknownType) {
        this.name = decisionTableName;
        List<OutputClause> list = new ArrayList<>();
        for (OutputClausesWithType.OutputClauseWithType output : outputs) {
            FEELOutputClause feelOutputClause = new FEELOutputClause(output.getOutputClause(),
                                                                     output.getDmnBaseType(),
                                                                     feelHelper,
                                                                     allVariableTypes,
                                                                     unknownType);
            list.add(feelOutputClause);
        }
        this.outputs = list;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<? extends OutputClause> getOutputs() {
        return outputs;
    }

    public static class FEELOutputClause implements org.kie.dmn.feel.runtime.decisiontables.DecisionTable.OutputClause {

        protected String name;
        protected List<UnaryTest> outputValues;
        protected CompiledFEELExpression compiledDefault;
        private BaseDMNTypeImpl outputClauseType;

        FEELOutputClause(org.kie.dmn.model.api.OutputClause outputClause,
                         BaseDMNTypeImpl outputClauseType,
                         DMNFEELHelper feelHelper,
                         Map<String, Type> allVariableTypes,
                         DMNType unknownType) {
            this.name = outputClause.getName();
            this.outputClauseType = outputClauseType;
            this.outputValues = getOutputValuesTests(feelHelper, allVariableTypes, unknownType, outputClause.getOutputValues());
        }

        protected List<UnaryTest> getOutputValuesTests(DMNFEELHelper feel,
                                                       Map<String, Type> variableTypes,
                                                       DMNType unknownType,
                                                       UnaryTests outputValues) {

            String outputValuesText = Optional.ofNullable(outputValues)
                    .map(UnaryTests::getText).orElse(null);

            if (outputValuesText != null && !outputValuesText.isEmpty()) {
                return feel.evaluateUnaryTests(outputValuesText, variableTypes);
            }

            if (outputClauseType != unknownType && outputClauseType.getAllowedValuesFEEL() != null) {
                return outputClauseType.getAllowedValuesFEEL();
            }
            return Collections.emptyList();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<UnaryTest> getOutputValues() {
            return outputValues;
        }

        @Override
        public Type getType() {
            return outputClauseType.getFeelType();
        }

        @Override
        public boolean isCollection() {
            return outputClauseType.isCollection();
        }
    }
}
