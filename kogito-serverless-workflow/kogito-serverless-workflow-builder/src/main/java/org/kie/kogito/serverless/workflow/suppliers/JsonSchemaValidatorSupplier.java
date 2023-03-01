/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.suppliers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.jbpm.compiler.canonical.descriptors.ExpressionUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.actions.JsonSchemaValidator;
import org.kie.kogito.serverless.workflow.parser.SwaggerSchemaProvider;
import org.kie.kogito.serverless.workflow.parser.schema.JsonSchemaImpl;

import com.github.javaparser.ast.expr.Expression;

public class JsonSchemaValidatorSupplier extends JsonSchemaValidator implements Supplier<Expression>, SwaggerSchemaProvider {

    private static final long serialVersionUID = 1L;

    private transient Schema schema;

    public JsonSchemaValidatorSupplier(String schema, boolean failOnValidationErrors) {
        super(schema, failOnValidationErrors);
    }

    @Override
    public Expression get() {
        return ExpressionUtils.getObjectCreationExpr(JsonSchemaValidator.class, schemaRef, failOnValidationErrors);
    }

    @Override
    public Schema getSchema() {
        if (schema == null) {
            try {
                schema = ObjectMapperFactory.get().readValue(load().toString(), JsonSchemaImpl.class);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return schema;
    }
}
