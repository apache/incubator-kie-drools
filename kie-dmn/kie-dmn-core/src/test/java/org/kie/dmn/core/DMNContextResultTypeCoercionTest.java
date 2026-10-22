/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.RuntimeModeOption;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.impl.VoidDMNRuntimeKB;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.Context;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Definitions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The result type of a decision (or BKM) must be applied to a <code>Context</code> only when the context produces the
 * result of the node itself. A context nested in another expression, e.g. the <code>return</code> of a
 * <code>for</code>, produces just a part of that result: coercing it to the (collection) type of the node wrapped each
 * element in an extra list. The implicit conversion of a single value to a singleton list must still be applied to the
 * result of the node.
 */
class DMNContextResultTypeCoercionTest extends BaseInterpretedVsCompiledTest {

    private static final String MODEL = "valid_models/DMNv1_6/ContextResultTypeCoercion.dmn";
    private static final String NAMESPACE = "https://github.com/kiegroup/drools/kie-dmn";
    private static final String NAME = "ContextResultTypeCoercion";

    private static final List<String> DOUBLED = List.of("aa", "bb", "cc");

    /**
     * @return all the combinations of: compiler (interpreted or exec model), runtime type check, runtime mode (lenient or strict)
     */
    static Stream<Arguments> modes() {
        Stream.Builder<Arguments> modes = Stream.builder();
        for (boolean execModel : new boolean[]{false, true}) {
            for (boolean typeCheck : new boolean[]{false, true}) {
                for (boolean strictMode : new boolean[]{false, true}) {
                    modes.add(Arguments.of(execModel, typeCheck, strictMode));
                }
            }
        }
        return modes.build();
    }

    private DMNResult evaluate(String decisionName, boolean execModel, boolean typeCheck, boolean strictMode) {
        init(execModel, strictMode);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(MODEL, this.getClass());
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(typeCheck));
        final DMNModel dmnModel = runtime.getModel(NAMESPACE, NAME);
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        return runtime.evaluateByName(dmnModel, DMNFactory.newContext(), decisionName);
    }

    private Object resultOf(String decisionName, boolean execModel, boolean typeCheck, boolean strictMode) {
        final DMNResult dmnResult = evaluate(decisionName, execModel, typeCheck, strictMode);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        return dmnResult.getDecisionResultByName(decisionName).getResult();
    }

    // a for returning a context, with a decision typed as a collection: each element must not be wrapped in a list

    @ParameterizedTest
    @MethodSource("modes")
    void forReturnContextEntry(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("For Return Context Entry", execModel, typeCheck, strictMode)).isEqualTo(DOUBLED);
    }

    @ParameterizedTest
    @MethodSource("modes")
    void forReturnContextGetValue(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("For Return Context Get Value", execModel, typeCheck, strictMode)).isEqualTo(DOUBLED);
    }

    @ParameterizedTest
    @MethodSource("modes")
    void forReturnContextPath(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("For Return Context Path", execModel, typeCheck, strictMode)).isEqualTo(DOUBLED);
    }

    @ParameterizedTest
    @MethodSource("modes")
    void forReturnContextStructEntry(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("For Return Context Struct Entry", execModel, typeCheck, strictMode))
                .isEqualTo(List.of(Map.of("name", "a"), Map.of("name", "b"), Map.of("name", "c")));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void plainFor(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Plain For", execModel, typeCheck, strictMode)).isEqualTo(DOUBLED);
    }

    @ParameterizedTest
    @MethodSource("modes")
    void forContextInTypedEntry(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("For Context In Typed Entry", execModel, typeCheck, strictMode)).isEqualTo(DOUBLED);
    }

    @ParameterizedTest
    @MethodSource("modes")
    void forReturnContextWrongType(boolean execModel, boolean typeCheck, boolean strictMode) {
        // the decision is typed as a collection of numbers, while the elements are strings
        final DMNResult dmnResult = evaluate("For Return Context Wrong Type", execModel, typeCheck, strictMode);
        if (typeCheck) {
            // the wrong type is still reported, on the decision
            assertThat(dmnResult.hasErrors()).isTrue();
            assertThat(dmnResult.getMessages(DMNMessage.Severity.ERROR))
                    .anyMatch(m -> m.getMessageType() == DMNMessageType.ERROR_EVAL_NODE);
            assertThat(dmnResult.getDecisionResultByName("For Return Context Wrong Type").getResult()).isNull();
        } else {
            assertThat(dmnResult.hasErrors()).isFalse();
            assertThat(dmnResult.getDecisionResultByName("For Return Context Wrong Type").getResult()).isEqualTo(List.of("a", "b"));
        }
    }

    // other nested contexts

    @ParameterizedTest
    @MethodSource("modes")
    void listOfContexts(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("List Of Contexts", execModel, typeCheck, strictMode)).isEqualTo(List.of("a", "b"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void contextAsInvocationBinding(boolean execModel, boolean typeCheck, boolean strictMode) {
        // the context is the argument of the BKM, that is not a list: the result of the decision is converted to a singleton list
        assertThat(resultOf("Context As Invocation Binding", execModel, typeCheck, strictMode)).isEqualTo(List.of("a!"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void contextInNestedFunctionBody(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Context In Nested Function Body", execModel, typeCheck, strictMode)).isEqualTo(List.of("a!"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void contextAsFilterSource(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Context As Filter Source", execModel, typeCheck, strictMode)).isEqualTo(List.of("a"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void contextInSatisfies(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Context In Satisfies", execModel, typeCheck, strictMode)).isEqualTo(Boolean.TRUE);
    }

    // the context produces the result of the node: the implicit conversion to a singleton list is preserved

    @ParameterizedTest
    @MethodSource("modes")
    void rootContext(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Root Context", execModel, typeCheck, strictMode)).isEqualTo(List.of("a"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void rootConditionalOfContexts(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Root Conditional Of Contexts", execModel, typeCheck, strictMode)).isEqualTo(List.of("a"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void rootConditionalElseContext(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Root Conditional Else Context", execModel, typeCheck, strictMode)).isEqualTo(List.of("a"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void rootContextResultEntryContext(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Root Context Result Entry Context", execModel, typeCheck, strictMode)).isEqualTo(List.of("a"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void typedEntryContext(boolean execModel, boolean typeCheck, boolean strictMode) {
        // the type of the entry holding the context is applied to the context
        assertThat(resultOf("Typed Entry Context", execModel, typeCheck, strictMode)).isEqualTo(List.of("b"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void untypedRootContext(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Untyped Root Context", execModel, typeCheck, strictMode)).isEqualTo("a");
    }

    // BKM: the result of a function is not coerced by the invocation, only the result type of the root context is applied

    @ParameterizedTest
    @MethodSource("modes")
    void bkmRootContext(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Call Root Context BKM", execModel, typeCheck, strictMode)).isEqualTo(List.of("a"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void bkmRootConditionalOfContexts(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Call Root Conditional BKM", execModel, typeCheck, strictMode)).isEqualTo(List.of("a"));
    }

    @ParameterizedTest
    @MethodSource("modes")
    void bkmForReturnContext(boolean execModel, boolean typeCheck, boolean strictMode) {
        assertThat(resultOf("Call For Context BKM", execModel, typeCheck, strictMode)).isEqualTo(DOUBLED);
    }

    // the parent link of a model element is populated when a model is unmarshalled:
    // the result type of a context must not be deduced from the parent link of the context

    @ParameterizedTest
    @MethodSource("modes")
    void contextsWithoutParentLink(boolean execModel, boolean typeCheck, boolean strictMode) {
        init(execModel, strictMode);
        final Definitions definitions = DMNMarshallerFactory.newDefaultMarshaller()
                .unmarshal(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(MODEL), StandardCharsets.UTF_8));
        definitions.getDrgElement().forEach(DMNContextResultTypeCoercionTest::removeParentLinkOfContexts);
        final DMNModel dmnModel = new DMNCompilerImpl().compile(definitions, Collections.emptyList());
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        final DMNRuntimeImpl runtime = new DMNRuntimeImpl(new VoidDMNRuntimeKB());
        runtime.setOption(new RuntimeTypeCheckOption(typeCheck));
        runtime.setOption(new RuntimeModeOption((strictMode ? RuntimeModeOption.MODE.STRICT : RuntimeModeOption.MODE.LENIENT).getMode()));

        final Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("For Return Context Entry", DOUBLED);
        expected.put("List Of Contexts", List.of("a", "b"));
        expected.put("Root Context", List.of("a"));
        expected.put("Root Conditional Of Contexts", List.of("a"));
        expected.put("Call Root Context BKM", List.of("a"));
        expected.put("Call Root Conditional BKM", List.of("a"));
        expected.put("Call For Context BKM", DOUBLED);
        expected.forEach((decisionName, expectedResult) -> {
            final DMNResult dmnResult = runtime.evaluateByName(dmnModel, DMNFactory.newContext(), decisionName);
            assertThat(dmnResult.hasErrors()).as(decisionName + " " + DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
            assertThat(dmnResult.getDecisionResultByName(decisionName).getResult()).as(decisionName).isEqualTo(expectedResult);
        });
    }

    private static void removeParentLinkOfContexts(DMNModelInstrumentedBase element) {
        element.getChildren().forEach(child -> {
            removeParentLinkOfContexts(child);
            if (child instanceof Context) {
                child.setParent(null);
            }
        });
    }
}
