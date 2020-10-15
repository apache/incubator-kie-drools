/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.mongodb.utils;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.util.JsonFormat;
import org.bson.Document;
import org.jbpm.marshalling.impl.KogitoProcessMarshallerWriteContext;
import org.jbpm.marshalling.impl.JBPMMessages;
import org.jbpm.marshalling.impl.JBPMMessages.ProcessInstance;
import org.kie.kogito.mongodb.marshalling.DocumentMarshallingException;
import org.kie.kogito.mongodb.model.ProcessInstanceDocument;

import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_INSTANCE_ID;
import static org.kie.kogito.mongodb.utils.DocumentConstants.VALUE;
import static org.kie.kogito.mongodb.utils.DocumentConstants.VARIABLE;
import static org.kie.kogito.mongodb.utils.DocumentUtils.getObjectMapper;

public class ProcessInstanceDocumentMapper implements BiFunction<KogitoProcessMarshallerWriteContext, JBPMMessages.ProcessInstance, ProcessInstanceDocument> {

    @Override
    public ProcessInstanceDocument apply(KogitoProcessMarshallerWriteContext context, ProcessInstance processInstance) {
        ProcessInstanceDocument doc = new ProcessInstanceDocument();
        try {
            JsonNode instanceNode = getObjectMapper().readTree(JsonFormat.printer().print(processInstance));
            doc.setId(instanceNode.get(PROCESS_INSTANCE_ID).asText());
            applyVariables(instanceNode, VARIABLE);
            doc.setProcessInstance(Optional.ofNullable(instanceNode).map(json -> Document.parse(json.toString())).orElse(null));
            if (context != null) {
                doc.setStrategies(context.getUsedStrategies().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue)));
            }
        } catch (Exception e) {
            throw new DocumentMarshallingException(e);
        }
        return doc;
    }

    private void applyVariables(JsonNode parent, String variable) {
        if (parent.has(variable) && parent.get(variable).isArray()) {
            parent.get(variable).forEach(node -> {
                if (node.get(VALUE) != null) {
                    try {
                        byte[] value = node.get(VALUE).binaryValue();
                        ((ObjectNode) node).set(VALUE, getObjectMapper().readTree(new String(value)));
                    } catch (Exception e) {
                        throw new DocumentMarshallingException(e);
                    }
                }
            });
        }
        parent.forEach(child -> applyVariables(child, variable));
    }

}
