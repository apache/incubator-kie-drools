/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scenariosimulation.backend.expression;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.extended.CodeFunction;

import static java.util.Collections.singletonList;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.UNARY_PARAMETER_IDENTIFIER;
import static org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity.ERROR;

public class DMNFeelExpressionEvaluator extends AbstractExpressionEvaluator {

    private final ClassLoader classLoader;
    private final CodeFunction codeFunction = new CodeFunction();

    public DMNFeelExpressionEvaluator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String fromObjectToExpression(Object value) {
        FEELFnResult<String> invoke = codeFunction.invoke(value);
        return invoke.getOrElseThrow(feelEvent -> new IllegalArgumentException("This should not happen",
                                                                               feelEvent.getSourceException()));
    }

    protected EvaluationContext newEvaluationContext() {
        final FEELEventListenersManager eventsManager = new FEELEventListenersManager();
        return new EvaluationContextImpl(classLoader, eventsManager);
    }

    protected FEEL newFeelEvaluator(AtomicReference<FEELEvent> errorHolder) {
        // cleanup existing error
        errorHolder.set(null);
        FEEL feel = FEEL.newInstance(singletonList(new ExtendedDMNProfile()));
        feel.addListener(event -> {
            FEELEvent feelEvent = errorHolder.get();
            if (!(feelEvent instanceof SyntaxErrorEvent) &&
                    ERROR.equals(event.getSeverity())) {
                errorHolder.set(event);
            }
        });
        return feel;
    }

    @Override
    protected Object internalLiteralEvaluation(String raw, String className) {
        EvaluationContext evaluationContext = newEvaluationContext();
        return executeAndVerifyErrors(feel -> feel.evaluate(raw, evaluationContext));
    }

    @Override
    protected boolean internalUnaryEvaluation(String rawExpression, Object resultValue, Class<?> resultClass, boolean skipEmptyString) {
        if (rawExpression != null && skipEmptyString && rawExpression.isEmpty()) {
            return true;
        }

        Map<String, Type> variables = new HashMap<>();
        variables.put(UNARY_PARAMETER_IDENTIFIER, BuiltInType.UNKNOWN);
        List<UnaryTest> unaryTests = executeAndVerifyErrors(feel -> feel.evaluateUnaryTests(rawExpression, variables));

        EvaluationContext evaluationContext = newEvaluationContext();
        evaluationContext.setValue(UNARY_PARAMETER_IDENTIFIER, resultValue);
        return unaryTests.stream()
                .allMatch(unaryTest -> Optional
                        .ofNullable(unaryTest.apply(evaluationContext, resultValue))
                        .orElse(false));
    }

    /**
     * Common internal method that execute the command and manage error
     * @param command
     * @param <T>
     * @return
     */
    protected <T> T executeAndVerifyErrors(Function<FEEL, T> command) {
        AtomicReference<FEELEvent> errorHolder = new AtomicReference<>();
        FEEL feel = newFeelEvaluator(errorHolder);

        T result = command.apply(feel);
        FEELEvent errorEvent = errorHolder.get();
        if (errorEvent != null) {
            if (errorEvent instanceof SyntaxErrorEvent) {
                throw new IllegalArgumentException("Syntax error: " + errorEvent.getMessage());
            } else {
                throw new IllegalArgumentException("Error during evaluation: " + errorEvent.getMessage());
            }
        }
        return result;
    }

    @Override
    protected Object extractFieldValue(Object result, String fieldName) {
        return ((Map<String, Object>) result).get(fieldName);
    }

    @Override
    protected Object createObject(String className, List<String> genericClasses) {
        return new HashMap<String, Object>();
    }

    @Override
    protected void setField(Object toReturn, String fieldName, Object fieldValue) {
        Map<String, Object> returnMap = (Map<String, Object>) toReturn;
        returnMap.put(fieldName, fieldValue);
    }

    /**
     * In DMN only Lists are structured result while Maps are context so "plain" FEEL expressions
     * @param resultClass
     * @return
     */
    @Override
    protected boolean isStructuredResult(Class<?> resultClass) {
        return resultClass != null && ScenarioSimulationSharedUtils.isList(resultClass.getCanonicalName());
    }

    /**
     * In DMN only Lists are structured input while Maps are context so "plain" FEEL expressions
     * @param className
     * @return
     */
    @Override
    protected boolean isStructuredInput(String className) {
        return ScenarioSimulationSharedUtils.isList(className);
    }

    /**
     * This is not used for DMN
     * @param element
     * @param fieldName
     * @param className
     * @param genericClasses
     * @return
     */
    @Override
    protected Map.Entry<String, List<String>> getFieldClassNameAndGenerics(Object element, String fieldName, String className, List<String> genericClasses) {
        return new AbstractMap.SimpleEntry<>("", singletonList(""));
    }
}
