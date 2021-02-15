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
package org.kie.kogito.pmml.openapi.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PMMLOASResult {

    String DEFINITIONS = "definitions";
    String INPUT_SET = "InputSet";
    String RESULT_SET = "ResultSet";
    String OUTPUT_SET = "OutputSet";
    String REQUIRED = "required";
    String PROPERTIES = "properties";
    String TYPE = "type";
    String FORMAT = "format";
    String DEFAULT = "default";
    String ENUM = "enum";
    String INTERVALS = "intervals";
    String OBJECT = "object";
    String STRING = "string";
    String BOOLEAN = "boolean";
    String INTEGER = "integer";
    String NUMBER = "number";
    String DOUBLE = "double";
    String FLOAT = "float";
    String CORRELATION_ID = "correlationId";
    String SEGMENTATION_ID = "segmentationId";
    String SEGMENT_ID = "segmentId";
    String SEGMENT_INDEX = "segmentIndex";
    String RESULT_CODE = "resultCode";
    String RESULT_OBJECT_NAME = "resultObjectName";
    String RESULT_VARIABLES = "resultVariables";
    String MINIMUM = "minimum";
    String MAXIMUM = "maximum";

    ObjectNode jsonSchemaNode();
}
