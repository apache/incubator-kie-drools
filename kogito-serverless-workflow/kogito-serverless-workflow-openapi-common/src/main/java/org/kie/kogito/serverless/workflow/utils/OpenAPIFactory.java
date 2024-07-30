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
package org.kie.kogito.serverless.workflow.utils;

import java.util.Optional;

import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.buildLoader;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.readString;

public class OpenAPIFactory {

    private static final Logger logger = LoggerFactory.getLogger(OpenAPIFactory.class);

    private OpenAPIFactory() {
    }

    public static OpenAPI getOpenAPI(String uri, Workflow workflow, FunctionDefinition function, Optional<ParserContext> context) {
        SwaggerParseResult result =
                new OpenAPIParser().readContents(readString(buildLoader(uri, workflow, context, function.getAuthRef())), null, null);
        OpenAPI openAPI = result.getOpenAPI();
        if (openAPI == null) {
            throw new IllegalArgumentException("Problem parsing uri " + uri + " Messages" + result.getMessages());
        }
        logger.debug("OpenAPI parser messages {}", result.getMessages());
        return openAPI;
    }
}
