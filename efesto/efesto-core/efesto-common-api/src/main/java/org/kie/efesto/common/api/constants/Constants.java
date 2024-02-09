/**
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
package org.kie.efesto.common.api.constants;

public class Constants {

    public static final String MISSING_BODY_TEMPLATE = "Missing body in %s";
    public static final String MISSING_VARIABLE_IN_BODY = "Missing expected variable '%s' in body %s";
    public static final String MISSING_BODY_IN_METHOD = "Missing expected body in method %s";
    public static final String MISSING_PARAMETER_IN_CONSTRUCTOR_INVOCATION = "Missing expected parameter %s in constructor invocation %s";
    public static final String MISSING_CONSTRUCTOR_IN_BODY = "Missing constructor invocation in body %s";
    public static final String MISSING_STATIC_INITIALIZER = "Missing expected static initializer in class %s";

    public static final String MISSING_VARIABLE_INITIALIZER_TEMPLATE = "Missing '%s' initializer in %s";
    public static final String MISSING_CHAINED_METHOD_DECLARATION_TEMPLATE = "Missing '%s' MethodDeclaration in %s";

    public static final String PACKAGE_CLASS_TEMPLATE = "%s.%s";

    //
    public static final String OUTPUTFIELDS_MAP_IDENTIFIER = "$outputFieldsMap";

    //
    public static final String INDEXFILE_DIRECTORY_PROPERTY = "indexfile.directory";

    private Constants() {
        // Avoid instantiation
    }
}
