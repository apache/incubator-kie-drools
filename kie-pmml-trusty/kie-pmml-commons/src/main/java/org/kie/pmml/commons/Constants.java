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
package org.kie.pmml.commons;

public class Constants {

    public static final String UNEXPECTED_OP_TYPE = "Unexpected opType %s";
    public static final String EXPECTED_TWO_ENTRIES_RETRIEVED = "Expected two entries, retrieved %d";
    public static final String UNEXPECTED_OPERATION_TYPE = "Unexpected Operation Type %s";
    public static final String UNEXPECTED_NORMALIZATION_METHOD = "Unexpected Normalization Method %s";
    public static final String DONE = "DONE";
    public static final String MISSING_BODY_TEMPLATE = "Missing body in %s";
    public static final String MISSING_DEFAULT_CONSTRUCTOR = "Missing default constructor in ClassOrInterfaceDeclaration %s ";
    public static final String EXPECTING_EXACTLY_ONE_METHOD = "Expecting exactly one %s method in ClassOrInterfaceDeclaration %s, found %s ";
    public static final String MISSING_VARIABLE_IN_BODY = "Missing expected variable '%s' in body %s";
    public static final String MISSING_METHOD_IN_CLASS = "Missing expected method '%s' in class %s";
    public static final String MISSING_BODY_IN_METHOD = "Missing expected body in method %s";
    public static final String MISSING_RETURN_IN_METHOD = "Missing expected return in method %s";
    public static final String MISSING_EXPRESSION_IN_RETURN = "Missing expected expression in return %s";
    public static final String MISSING_PARAMETER_IN_CONSTRUCTOR_INVOCATION = "Missing expected parameter %s in constructor invocation %s";
    public static final String MISSING_CONSTRUCTOR_IN_BODY = "Missing constructor invocation in body %s";
    public static final String UNCHANGED_VARIABLE_IN_CONSTRUCTOR = "Unchanged variable {} in constructor {} ";
    public static final String MISSING_STATIC_INITIALIZER = "Missing expected static initializer in class %s";
    public static final String MISSING_METHOD_TEMPLATE = "Missing method '%s' in %s";
    public static final String MISSING_VARIABLE_INITIALIZER_TEMPLATE = "Missing '%s' initializer in %s";
    public static final String MISSING_CHAINED_METHOD_DECLARATION_TEMPLATE = "Missing '%s' MethodDeclaration in %s";
    public static final String MISSING_PARENT_NODE_TEMPLATE = "Missing parent node in %s";
    public static final String MISSING_METHOD_REFERENCE_TEMPLATE = "Missing method reference '%s' in %s";
    public static final String PACKAGE_CLASS_TEMPLATE = "%s.%s";
    public static final String WRONG_EXPRESSION_TEMPLATE = "Retrieved %s (%s) while a %s was expected from \n%s";
    public static final String EXPECTING_HAS_KNOWLEDGEBUILDER_TEMPLATE = "Expecting HasKnowledgeBuilder, received %s";
    public static final String VARIABLE_NAME_TEMPLATE = "%s_%s";
    public static final String EXPRESSION_NOT_MANAGED = "Expression %s not managed";

    //
    public static final String GET_MODEL = "getModel";
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    public static final String EVALUATE_PREDICATE = "evaluatePredicate";
    public static final String PREDICATE_FUNCTION = "predicateFunction";
    public static final String STRING_OBJECT_MAP = "stringObjectMap";
    public static final String INITIAL_SCORE = "initialScore";
    public static final String SCORE = "score";
    public static final String REASON_CODE = "reasonCode";
    public static final String REASON_CODE_ALGORITHM = "reasonCodeAlgorithm";
    public static final String EMPTY_LIST = "emptyList";
    public static final String AS_LIST = "asList";
    public static final String TO_RETURN = "toReturn";

    public static final String PMML_STRING = "pmml";
    public static final String PMML_SUFFIX = "." + PMML_STRING;

    public static final String PMML_FILE_NAME = "_pmml_file_name_";

    public static final String PMML_MODEL_NAME = "_model_name_";

    private Constants() {
        // Avoid instantiation
    }
}
