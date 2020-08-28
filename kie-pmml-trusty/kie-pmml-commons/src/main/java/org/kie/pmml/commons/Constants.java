/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.commons;

public class Constants {

    public static final String UNEXPECTED_OP_TYPE = "Unexpected opType %s";
    public static final String EXPECTED_TWO_ENTRIES_RETRIEVED = "Expected two entries, retrieved %d";
    public static final String UNEXPECTED_OPERATION_TYPE = "Unexpected Operation Type %s";
    public static final String UNEXPECTED_NORMALIZATION_METHOD = "Unexpected Normalization Method %s";
    public static final String DONE = "DONE";
    public static final String MISSING_BODY_TEMPLATE = "Missing body in %s";
    public static final String MISSING_DEFAULT_CONSTRUCTOR = "Missing default constructor in ClassOrInterfaceDeclaration %s ";
    public static final String MISSING_VARIABLE_IN_BODY = "Missing expected variable '%s' in body %s";
    public static final String MISSING_METHOD_IN_CLASS = "Missing expected method '%s' in class %s";
    public static final String MISSING_BODY_IN_METHOD = "Missing expected body in method %s";
    public static final String MISSING_RETURN_IN_METHOD = "Missing expected return in method %s";
    public static final String MISSING_EXPRESSION_IN_RETURN = "Missing expected expression in return %s";
    public static final String MISSING_PARAMETER_IN_CONSTRUCTOR_INVOCATION = "Missing expected parameter %s in constructor invocation %s";
    public static final String MISSING_CONSTRUCTOR_IN_BODY = "Missing constructor invocation in body %s";
    public static final String UNCHANGED_VARIABLE_IN_CONSTRUCTOR = "Unchanged variable {} in constructor {} ";

    private Constants() {
        // Avoid instantiation
    }
}
