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

import java.io.IOException;
import java.io.UncheckedIOException;
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

import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.readAllBytes;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.runtimeLoader;

public class JsonSchemaValidator implements WorkflowModelValidator {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaValidator.class);

    protected final String schemaRef;
    protected final boolean failOnValidationErrors;
    private final AtomicReference<JsonSchema> schemaObject = new AtomicReference<>();

    public JsonSchemaValidator(String schema, boolean failOnValidationErrors) {
        this.schemaRef = schema;
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
        return JsonNode.class.isAssignableFrom(clazz) ? Optional.of((T) getSchema().getSchemaNode()) : Optional.empty();
    }

    private JsonSchema getSchema() {
        try {
            JsonSchema result = schemaObject.get();
            if (result == null) {
                result = JsonSchemaFactory.getInstance(VersionFlag.V7).getSchema(ObjectMapperFactory.get().readTree(readAllBytes(runtimeLoader(schemaRef))));
                schemaObject.set(result);
            }
            return result;
        } catch (IOException io) {
            throw new UncheckedIOException(io);
        }
    }
}
