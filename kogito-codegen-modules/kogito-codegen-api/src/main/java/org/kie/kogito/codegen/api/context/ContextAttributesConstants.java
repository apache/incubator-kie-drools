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
package org.kie.kogito.codegen.api.context;

public final class ContextAttributesConstants {

    /**
     * OpenAPI Generator Descriptors with information of every REST client generated indexed by the spec resource file.
     */
    public static final String OPENAPI_DESCRIPTORS = "openApiDescriptor";

    public static final String PROCESS_AUTO_SVG_MAPPING = "processAutoSVGMapping";

    public static final String KOGITO_FAULT_TOLERANCE_ENABLED = "kogito.faultToleranceEnabled";

    public static final String KOGITO_CODEGEN_BOOLEAN_OBJECT_ACCESSOR_BEHAVIOUR = "kogito.codegen.booleanObjectAccessorBehaviour";

    private ContextAttributesConstants() {
    }
}
