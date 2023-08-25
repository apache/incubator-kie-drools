/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.actions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jbpm.workflow.core.WorkflowModelValidator;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;

public class JsonSchemaValidator implements WorkflowModelValidator, Externalizable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaValidator.class);

    @SuppressWarnings("squid:S1948") // sonar apparently does not realize that if a class implements externalizable, it is not mandatory for all each attributes to be serializable
    protected JsonNode jsonNode;
    protected boolean failOnValidationErrors;
    private final transient AtomicReference<JsonSchema> schemaObject = new AtomicReference<>();

    public JsonSchemaValidator() {
        // for serialization purposes
    }

    public JsonSchemaValidator(JsonNode jsonNode, boolean failOnValidationErrors) {
        this.jsonNode = jsonNode;
        this.failOnValidationErrors = failOnValidationErrors;
    }

    @Override
    public void validate(Map<String, Object> model) {
        Set<ValidationMessage> report =
                getSchema().validate((JsonNode) model.getOrDefault(SWFConstants.DEFAULT_WORKFLOW_VAR, NullNode.instance));
        if (!report.isEmpty()) {
            StringBuilder sb = new StringBuilder("There are JsonSchema validation errors:");
            report.forEach(m -> sb.append(System.lineSeparator()).append(m.getMessage()));
            final String validationMessage = sb.toString();
            logger.warn(validationMessage);
            if (failOnValidationErrors) {
                throw new IllegalArgumentException(validationMessage);
            }
        }
    }

    @Override
    public <T> Optional<T> schema(Class<T> clazz) {
        return JsonNode.class.isAssignableFrom(clazz) ? Optional.of(clazz.cast(getSchema().getSchemaNode())) : Optional.empty();
    }

    private JsonSchema getSchema() {
        JsonSchema result = schemaObject.get();
        if (result == null) {
            result = JsonSchemaFactory.getInstance(VersionFlag.V7).getSchema(jsonNode);
            schemaObject.set(result);
        }
        return result;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(failOnValidationErrors);
        out.writeUTF(ObjectMapperFactory.get().writeValueAsString(jsonNode));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.failOnValidationErrors = in.readBoolean();
        this.jsonNode = ObjectMapperFactory.get().readTree(in.readUTF());
    }
}
