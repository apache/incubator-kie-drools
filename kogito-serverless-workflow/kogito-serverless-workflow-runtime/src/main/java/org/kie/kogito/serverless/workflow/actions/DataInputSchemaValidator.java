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
import java.util.Map;

import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.jbpm.workflow.core.WorkflowInputModelValidator;
import org.json.JSONObject;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.readAllBytes;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.runtimeLoader;

public class DataInputSchemaValidator implements WorkflowInputModelValidator {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DataInputSchemaValidator.class);

    protected final String schema;
    protected final boolean failOnValidationErrors;

    public DataInputSchemaValidator(String schema, boolean failOnValidationErrors) {
        this.schema = schema;
        this.failOnValidationErrors = failOnValidationErrors;
    }

    @Override
    public void validate(Map<String, Object> model) {
        ObjectMapper mapper = ObjectMapperFactory.get();
        try {
            SchemaLoader.load(mapper.readValue(readAllBytes(runtimeLoader(schema)), JSONObject.class))
                    .validate(mapper.convertValue(model.getOrDefault(SWFConstants.DEFAULT_WORKFLOW_VAR, NullNode.instance), JSONObject.class));
        } catch (ValidationException ex) {
            handleException(ex, ex.getCausingExceptions().isEmpty() ? ex : ex.getCausingExceptions());
        } catch (IOException ex) {
            handleException(ex, ex);
        }
    }

    private void handleException(Throwable ex, Object toAppend) {
        String validationError = String.format("Error validating input schema: %s", toAppend);
        logger.warn(validationError, ex);
        if (failOnValidationErrors) {
            throw new IllegalArgumentException(validationError);
        }
    }
}
