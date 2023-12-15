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
package org.kie.kogito.codegen.api.utils;

public class KogitoCodeGenConstants {

    private KogitoCodeGenConstants() {

    }

    public static final String VALIDATION_CLASS = "jakarta.validation.constraints.NotNull";
    public static final String OPENAPI_SPEC_CLASS = "org.eclipse.microprofile.openapi.annotations.media.Schema";
    /**
     * Property that controls whether Kogito Codegen should ignore hidden files. Defaults to true.
     */
    public static final String IGNORE_HIDDEN_FILES_PROP = "kogito.codegen.ignoreHiddenFiles";
}
