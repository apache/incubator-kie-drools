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
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.model.functions.Function1;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.decisiontables.DecisionTable;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
import org.kie.dmn.feel.runtime.decisiontables.Indexed;
import org.kie.dmn.feel.runtime.events.DecisionTableRulesMatchedEvent;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;

import static java.util.stream.Collectors.toList;
import static org.kie.dmn.feel.runtime.decisiontables.DecisionTableImpl.checkResults;

public class Results {

    private final Items items = new Items();
    private final List<FEELEvent> events = new ArrayList<>();

    static class Items {

        private final TreeMap<Integer, List<ResultObject>> resultGroupedByRow = new TreeMap<>();

        public void addResult(ResultObject resultObject) {
            resultGroupedByRow
                    .computeIfAbsent(resultObject.row, i -> new ArrayList<>(1)) // 10 (the default for Java) columns output are not that usual
                    .add(resultObject);
        }

        public void clearResults() {
            resultGroupedByRow.clear();
        }

        List<Indexed> matches() {
            return indexes().map(i -> (Indexed) () -> i).collect(toList());
        }

        private Stream<Integer> indexes() {
            return resultGroupedByRow.keySet().stream();
        }

        public boolean hasNoIndexes() {
            return indexes().findAny().isEmpty();
        }

        public List<Object> evaluateResults(EvaluationContext evaluationContext) {
            if (!resultGroupedByRow.isEmpty() && resultGroupedByRow.firstEntry().getValue().size() > 1) {
                return multipleResultWithColumnNameAsKey(evaluationContext);
            } else {
                return singleColumnResult(evaluationContext);
            }
        }

        private List<Object> singleColumnResult(EvaluationContext evaluationContext) {
            return resultGroupedByRow.values()
                    .stream()
                    .map(r -> r.get(0)) // we know there's only one result see evaluateResults
                    .map(r -> r.eval(evaluationContext))
                    .collect(toList());
        }

        // Actually List<Map<String, Object>>
        private List<Object> multipleResultWithColumnNameAsKey(EvaluationContext evaluationContext) {
            return resultGroupedByRow
                    .values()
                    .stream()
                    .map(resultsGroupedByRow -> resultsGroupedByRow.stream().collect(
                            Collectors.toMap(ResultObject::getColumnName,
                                             (ResultObject resultObject) -> resultObject.eval(evaluationContext))))
                    .collect(toList());
        }
    }

    public void addResult(int row, String columnName, Function1<EvaluationContext, Object> outputEvaluationFunction) {
        items.addResult(new ResultObject(row, columnName, outputEvaluationFunction));
    }

    static class ResultObject {

        private final int row;
        private final String columnName;
        private final Function1<EvaluationContext, Object> outputEvaluationFunction;

        public ResultObject(int row, String columnName, Function1<EvaluationContext, Object> outputEvaluationFunction) {
            this.row = row;
            this.columnName = columnName;
            this.outputEvaluationFunction = outputEvaluationFunction;
        }

        public int getRow() {
            return row;
        }

        public String getColumnName() {
            return columnName;
        }

        public Object eval(EvaluationContext evaluationContext) {
            return outputEvaluationFunction.apply(evaluationContext);
        }
    }

    public void clearResults() {
        items.clearResults();
    }

    public List<FEELEvent> getEvents() {
        return events;
    }

    public Object applyHitPolicy(EvaluationContext evaluationContext,
                                 HitPolicy hitPolicy,
                                 DecisionTable decisionTable) {

        if (items.hasNoIndexes()) {
//            if (evaluator.hasDefaultValues()) { TODO DT-ANC default values
//                return evaluator.defaultToOutput(evalCtx);
//            }
            if (hitPolicy.getDefaultValue() != null) {
                return hitPolicy.getDefaultValue();
            }
            events.add(new HitPolicyViolationEvent(
                    FEELEvent.Severity.WARN,
                    String.format("No rule matched for decision table '%s' and no default values were defined. Setting result to null.", decisionTable.getName()),
                    decisionTable.getName(),
                    Collections.emptyList()));
        }

        List<? extends Indexed> matchIndexes = items.matches();
        evaluationContext.notifyEvt( () -> {
                               List<Integer> matchedIndexes = matchIndexes.stream().map( dr -> dr.getIndex() + 1 ).collect(Collectors.toList() );
                               return new DecisionTableRulesMatchedEvent(FEELEvent.Severity.INFO,
                                                                         String.format("Rules matched for decision table '%s': %s", decisionTable.getName(), matchIndexes),
                                                                         decisionTable.getName(),
                                                                         decisionTable.getName(),
                                                                         matchedIndexes );
                           }
        );

        List<Object> resultObjects = items.evaluateResults(evaluationContext);

        Map<Integer, String> errorMessages = checkResults(decisionTable.getOutputs(), evaluationContext, matchIndexes, resultObjects );
        if (!errorMessages.isEmpty()) {
            List<Integer> offending = new ArrayList<>(errorMessages.keySet());
            events.add(new HitPolicyViolationEvent(
                    FEELEvent.Severity.ERROR,
                    String.format("Errors found evaluating decision table '%s': \n%s",
                                  decisionTable.getName(),
                                  String.join("\n", errorMessages.values())),
                    decisionTable.getName(),
                    offending));
            return null;
        }

        return hitPolicy.getDti().dti(evaluationContext, decisionTable, matchIndexes, resultObjects);
    }
}
