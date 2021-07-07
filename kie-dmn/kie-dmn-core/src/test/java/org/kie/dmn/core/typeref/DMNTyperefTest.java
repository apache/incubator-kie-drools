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

import org.junit.Test;
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

public class DMNTyperefTest extends BaseInterpretedVsCompiledTest {

    public DMNTyperefTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNTyperefTest.class);

    @Test
    public void testSimpleItemDefinition() {
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

}
