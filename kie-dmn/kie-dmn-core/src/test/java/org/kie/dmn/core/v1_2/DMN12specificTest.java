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
package org.kie.dmn.core.v1_2;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
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

public class DMN12specificTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMN12specificTest.class);

    @ParameterizedTest
    @MethodSource("params")
    void dmn12typeAliases(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("typeAliases.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_9f6be450-17c0-49d9-a67f-960ad04b046f", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a date and time", LocalDateTime.of(2018, 9, 28, 16, 7));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a decision")).isEqualTo(LocalDateTime.of(2018, 9, 28, 16, 7).plusDays(1));
    }

    @ParameterizedTest
    @MethodSource("params")
    void itemDefCollection(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-filter.dmn", getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_f52ca843-504b-4c3b-a6bc-4d377bffef7a", "filter01");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final Object[][] data = new Object[][]{{7792, 10, "Clark"},
                                               {7934, 10, "Miller"},
                                               {7976, 20, "Adams"},
                                               {7902, 20, "Ford"},
                                               {7900, 30, "James"}};
        final List<Map<String, Object>> employees = new ArrayList<>();
        for (Object[] aData : data) {
            final Map<String, Object> e = new HashMap<>();
            e.put("id", aData[0]);
            e.put("dept", aData[1]);
            e.put("name", aData[2]);
            employees.add(e);
        }
        final DMNContext context = DMNFactory.newContext();
        context.set("Employees", employees);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getContext().get("filter01")).asList().containsExactly("Adams", "Ford");
    }

    @ParameterizedTest
    @MethodSource("params")
    void dmn12typeRefInformationItem(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-3544
        check_testDMN12typeRefInformationItem("typeRefInformationItem_original.dmn");
    }

    @ParameterizedTest
    @MethodSource("params")
    void dmn12typeRefInformationItemModified(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-3544
        check_testDMN12typeRefInformationItem("typeRefInformationItem_modified.dmn");
    }

    private void check_testDMN12typeRefInformationItem(String filename) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(filename, this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_fe2fd9ea-5928-4a35-b218-036de5798776", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("invoke the a function")).isEqualTo("Hello World");
        assertThat(result.get("the list of vowels")).asList().containsExactly("a", "e", "i", "o", "u");
        assertThat(result.get("a Person")).isEqualTo(mapOf(entry("name", "John"),
                                                    entry("surname", "Doe"),
                                                    entry("age", BigDecimal.valueOf(47))));
    }
}
