/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.extensions;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.serverlessworkflow.api.datainputschema.DataInputSchema;
import io.serverlessworkflow.api.interfaces.Extension;

public class OutputSchema implements Extension {

    public static final String OUTPUT_SCHEMA = "workflow-output-schema";
    // this data structure will be removed when migrating to 0.9, but for now it makes sense to reuse it rather than creating a new one
    // see https://github.com/serverlessworkflow/specification/pull/696
    @SuppressWarnings("squid:S1700")
    private DataInputSchema outputSchema;
    // this is a bug in workflow sdk, see https://github.com/serverlessworkflow/sdk-java/pull/207
    @JsonProperty("extensionid")
    private String extensionId;

    @Override
    public String getExtensionId() {
        return extensionId;
    }

    public DataInputSchema getOutputSchema() {
        return outputSchema;
    }

}
