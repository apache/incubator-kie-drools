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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;

public abstract class ColumnValidator {

    protected abstract List<UnaryTest> validationInputTests();

    protected abstract DMNType dmnType();

    protected abstract String validValues();

    protected abstract String columnName();

    protected abstract String decisionTableName();

    public Optional<InvalidInputEvent> validate(EvaluationContext evaluationContext, Object actualValue) {
        if (validationInputTests() != null) {
            boolean satisfies = true;
            if (dmnType() != null && dmnType().isCollection() && actualValue instanceof Collection) {
                for (Object parameterItem : (Collection<?>) actualValue) {
                    satisfies &= applyAllValidationUnitTests(evaluationContext, parameterItem);
                }
            } else {
                satisfies = applyAllValidationUnitTests(evaluationContext, actualValue);
            }
            if (!satisfies) {
                return Optional.of(new InvalidInputEvent(FEELEvent.Severity.ERROR,
                                                         String.format("%s='%s' does not match any of the valid values %s for decision table '%s'.", columnName(), actualValue, validValues(), decisionTableName()),
                                                         decisionTableName(),
                                                         null,
                                                         validValues()));
            }
        }
        return Optional.empty();
    }

    private Boolean applyAllValidationUnitTests(EvaluationContext ctx, Object parameterItem) {
        return validationInputTests()
                .stream()
                .map(ut -> ut.apply(ctx, parameterItem))
                .filter(x -> x != null && x)
                .findAny()
                .orElse(false);
    }
}
