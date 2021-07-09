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

package org.kie.dmn.core.typeref;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class DMNTyperefTest extends BaseInterpretedVsCompiledTest {

    public DMNTyperefTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNTyperefTest.class);

    @Test
    public void testCircular3() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("circular3.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_EEE7FA5B-AF9C-4937-8870-D612D4D8D860", "new-file");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("in1", mapOf(entry("name", "John Doe"), entry("patient age", mapOf(entry("age", 40), entry("extension", mapOf(entry("valueAge", mapOf(entry("age", 41), entry("extension", null)))))))));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        LOG.debug("{}", dmnResult);
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision-1")).isEqualTo("named John Doe age 40 ext age 41");
    }

    @Test
    public void testGenFn1() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("wireGenFnType1.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_10795E58-CD3F-4203-B4D7-C80D9D8BE7BD", "wireGenFnType1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("in1", "Hello, ");
        context.set("in2", "World!");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        LOG.debug("{}", dmnResult);
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision-1")).isEqualTo("Hello, World!");
    }

    @Test
    public void test_bkmWrongFnType() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("bkmWrongFnType.dmn", this.getClass());
        assertThat(messages).isNotNull().isNotEmpty();
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPEREF_MISMATCH))).isTrue();
    }

    @Test
    public void test_bkmWrongFnType2() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("bkmWrongFnType2.dmn", this.getClass());
        assertThat(messages).isNotNull().isNotEmpty();
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.PARAMETER_MISMATCH))).isTrue();
    }

    @Test
    public void test_bkmWrongFnType3() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("bkmWrongFnType3.dmn", this.getClass());
        assertThat(messages).isNotNull().isNotEmpty();
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.PARAMETER_MISMATCH))).isTrue();
    }

    @Test
    public void test_bkmWrongFnType4() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("bkmWrongFnType4.dmn", this.getClass());
        assertThat(messages).isNotNull().isNotEmpty();
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.PARAMETER_MISMATCH))).isTrue();
    }

    @Test
    public void testGenFn2() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("wireGenFnType2.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_10795E58-CD3F-4203-B4D7-C80D9D8BE7BD", "wireGenFnType2");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("in1", "Hello, ");
        context.set("in2", "World!");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DecisionService-1");
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        LOG.debug("{}", dmnResult);
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision-1")).isEqualTo("Hello, World!");
    }

    @Test
    public void test_dsWrongFnType() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("dsWrongFnType.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_10795E58-CD3F-4203-B4D7-C80D9D8BE7BD", "dsWrongFnType");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("in1", "Hello, ");
        context.set("in2", "World!");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DecisionService-1");
        assertThat(dmnResult.getMessages()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isNotEmpty();
    }

    @Test
    public void test_dsWrongFnType2() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("dsWrongFnType2.dmn", this.getClass());
        assertThat(messages).isNotNull().isNotEmpty();
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.PARAMETER_MISMATCH))).isTrue();
    }

    @Test
    public void test_dsWrongFnType3() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("dsWrongFnType3.dmn", this.getClass());
        assertThat(messages).isNotNull().isNotEmpty();
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.PARAMETER_MISMATCH))).isTrue();
    }

    @Test
    public void test_dsWrongFnType4() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("dsWrongFnType4.dmn", this.getClass());
        assertThat(messages).isNotNull().isNotEmpty();
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.PARAMETER_MISMATCH))).isTrue();
    }

    @Test
    public void test_dsWrongFnType5() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("dsWrongFnType5.dmn", this.getClass());
        assertThat(messages).isNotNull().isNotEmpty();
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPEREF_MISMATCH))).isTrue();
    }

    @Test
    public void test_dsWrongFnType6() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("dsWrongFnType6.dmn", this.getClass());
        assertThat(messages).isNotNull().isNotEmpty();
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPEREF_MISMATCH))).isTrue();
    }

    @Test
    public void test_dsWrongFnType7() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("dsWrongFnType7.dmn", this.getClass());
        assertThat(messages).isNotNull().isNotEmpty();
        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.TYPEREF_MISMATCH))).isTrue();
    }

    @Test
    public void testGenFn3() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("wireGenFnType3.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_10795E58-CD3F-4203-B4D7-C80D9D8BE7BD", "wireGenFnType3");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("in1", "Hello, ");
        context.set("in2", "World!");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DecisionService-1");
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        LOG.debug("{}", dmnResult);
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision-1")).isEqualTo("Hello, World!");
        assertThat(result.get("Decision-2")).isEqualTo(new BigDecimal(47));
    }
}
