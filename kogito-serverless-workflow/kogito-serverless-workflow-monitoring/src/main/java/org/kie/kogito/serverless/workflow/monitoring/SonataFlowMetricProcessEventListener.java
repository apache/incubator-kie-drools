/*
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
package org.kie.kogito.serverless.workflow.monitoring;

import java.util.Iterator;

import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.monitoring.core.common.process.MetricsProcessEventListener;
import org.kie.kogito.serverless.workflow.SWFConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

public class SonataFlowMetricProcessEventListener extends MetricsProcessEventListener {

    public enum ArrayStoreMode {
        STRING,
        JSON_STRING,
        MULTI_PARAM
    }

    static final String INPUT_PARAMS_COUNTER_NAME = "sonataflow_input_parameters_counter";

    private ArrayStoreMode arrayStoreMode;

    public SonataFlowMetricProcessEventListener(KogitoGAV gav, MeterRegistry meterRegistry, ArrayStoreMode arrayStoreMode) {
        super("sonataflow-process-monitoring-listener", gav, meterRegistry);
        this.arrayStoreMode = arrayStoreMode;
    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
        final KogitoProcessInstance processInstance = (KogitoProcessInstance) event.getProcessInstance();
        Object node = processInstance.getVariables().get(SWFConstants.DEFAULT_WORKFLOW_VAR);
        if (node instanceof ObjectNode) {
            registerObject(processInstance.getProcessId(), null, (ObjectNode) node);
        }
    }

    final void registerObject(String processId, String key, ObjectNode node) {
        node.fields().forEachRemaining(e -> registerInputParam(processId, concat(key, e.getKey(), '.'), e.getValue()));
    }

    private void registerInputParam(String processId, String key, JsonNode value) {
        if (value instanceof ObjectNode) {
            registerObject(processId, key, (ObjectNode) value);
        } else if (value instanceof ArrayNode) {
            registerArray(processId, key, (ArrayNode) value);
        } else {
            registerValue(processId, key, value.asText());
        }
    }

    private void registerArray(String processId, String key, ArrayNode node) {
        if (arrayStoreMode == ArrayStoreMode.MULTI_PARAM) {
            Iterator<JsonNode> iter = node.elements();
            for (int i = 0; iter.hasNext(); i++) {
                registerInputParam(processId, concat(key, "[" + i + "]"), iter.next());
            }
        } else if (arrayStoreMode == ArrayStoreMode.JSON_STRING) {
            registerValue(processId, key, node.toString());
        } else if (arrayStoreMode == ArrayStoreMode.STRING) {
            registerValue(processId, key, JsonObjectUtils.toJavaValue(node).toString());
        }
    }

    private void registerValue(String processId, String key, String value) {
        buildCounter(INPUT_PARAMS_COUNTER_NAME, "Input parameters", processId, Tag.of("param_name", key), Tag.of("param_value", value)).increment();
    }

    private String concat(String prefix, String key, char prefixChar) {
        return prefix == null ? key : prefix + prefixChar + key;
    }

    private String concat(String prefix, String key) {
        return prefix == null ? key : prefix + key;
    }
}
