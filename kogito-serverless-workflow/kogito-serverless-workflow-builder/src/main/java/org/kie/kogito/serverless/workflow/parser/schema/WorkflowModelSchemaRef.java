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

package org.kie.kogito.serverless.workflow.parser.schema;

import org.eclipse.microprofile.openapi.models.media.Schema;

/**
 * Structure to hold references for the generated input/output model schema for a given workflow definition
 */
public class WorkflowModelSchemaRef {

    private String inputModelRef;
    private String outputModelRef;
    private Schema inputModel;
    private Schema outputModel;

    WorkflowModelSchemaRef() {

    }

    void setInputModelRef(String inputModelRef) {
        this.inputModelRef = inputModelRef;
    }

    void setOutputModelRef(String outputModelRef) {
        this.outputModelRef = outputModelRef;
    }

    public String getInputModelRef() {
        return inputModelRef;
    }

    public String getOutputModelRef() {
        return outputModelRef;
    }

    public boolean hasModel() {
        return inputModelRef != null || this.outputModelRef != null;
    }

    public boolean hasInputModel() {
        return inputModelRef != null;
    }

    public boolean hasOutputModel() {
        return outputModelRef != null;
    }

    Schema getInputModel() {
        return inputModel;
    }

    void setInputModel(Schema inputModel) {
        this.inputModel = inputModel;
    }

    Schema getOutputModel() {
        return outputModel;
    }

    void setOutputModel(Schema outputModel) {
        this.outputModel = outputModel;
    }

}
