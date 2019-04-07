/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.remote.ejb.test.process.variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;

import org.jboss.qa.bpms.remote.ejb.domain.MyType;
import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.services.api.model.VariableDesc;
import org.junit.Test;

public class EObjectVariableTest extends RemoteEjbTest {

    @Test
    public void testLongProcessVariable() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("myobject", 10L);
        long pid = ejb.startProcess(ProcessDefinitions.OBJECT_VARIABLE, parameters);

        List<VariableDesc> typeVarHistory = ejb.getVariableHistory(pid, "type");
        Assertions.assertThat(typeVarHistory).isNotNull();
        Assertions.assertThat(typeVarHistory.size()).isEqualTo(1);

        VariableDesc typeVarLog = typeVarHistory.get(0);
        Assertions.assertThat(typeVarLog.getNewValue()).isEqualTo(Long.class.getName());

        List<VariableDesc> contentVarHistory = ejb.getVariableHistory(pid, "myobject");
        Assertions.assertThat(contentVarHistory).isNotNull();
        Assertions.assertThat(contentVarHistory.size()).isEqualTo(1);

        VariableDesc contentVarLog = contentVarHistory.get(0);
        Assertions.assertThat(contentVarLog.getNewValue()).isEqualTo("10");
    }

    @Test
    public void testMyTypeProcessVariableInside() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("myobject", new MyType("Hello World!"));
        long pid = ejb.startProcess(ProcessDefinitions.OBJECT_VARIABLE, parameters);

        List<VariableDesc> typeVarHistory = ejb.getVariableHistory(pid, "type");
        Assertions.assertThat(typeVarHistory).isNotNull();
        Assertions.assertThat(typeVarHistory.size()).isEqualTo(1);

        VariableDesc typeVarLog = typeVarHistory.get(0);
        Assertions.assertThat(typeVarLog.getNewValue()).isEqualTo(MyType.class.getName());

        List<VariableDesc> contentVarHistory = ejb.getVariableHistory(pid, "myobject");
        Assertions.assertThat(contentVarHistory).isNotNull();
        Assertions.assertThat(contentVarHistory.size()).isEqualTo(1);

        VariableDesc contentVarLog = contentVarHistory.get(0);
        Assertions.assertThat(contentVarLog.getNewValue()).isEqualTo("MyType{text=Hello World!}");
    }

    @Test
    public void testFloatArrayProcessVariable() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("myobject", new Float[]{10.3f, 5.6f});
        long pid = ejb.startProcess(ProcessDefinitions.OBJECT_VARIABLE, parameters);

        List<VariableDesc> typeVarHistory = ejb.getVariableHistory(pid, "type");
        Assertions.assertThat(typeVarHistory).isNotNull();
        Assertions.assertThat(typeVarHistory.size()).isEqualTo(1);

        VariableDesc typeVarLog = typeVarHistory.get(0);
        Assertions.assertThat(typeVarLog.getNewValue()).isEqualTo(Float[].class.getName());

        List<VariableDesc> contentVarHistory = ejb.getVariableHistory(pid, "myobject");
        Assertions.assertThat(contentVarHistory).isNotNull();
        Assertions.assertThat(contentVarHistory.size()).isEqualTo(1);

        VariableDesc contentVarLog = contentVarHistory.get(0);
        Assertions.assertThat(contentVarLog.getNewValue()).startsWith("[Ljava.lang.Float;@");
    }

}
