/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.tests;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

public class SplitGatewayTest extends AbstractCodegenTest {

    @Test
    public void testMultilineGateway() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("gateway/SplitMultilineExpression.bpmn2"));
        Application app = generateCode(resourcesTypeMap, false);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.processes().processById("SplitMultilineExpression");

        Model m = p.createModel();
        m.fromMap(singletonMap("valid", false));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(p.instances().size()).isZero();
    }
}
