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
package org.kie.dmn.api.core;

public enum DMNMessageType {
    KIE_API("Error calling Kie DMN API interface", Tag.RUNTIME, Tag.DMN_CORE),
    UNSUPPORTED_ELEMENT( "The referenced element is not supported by the implementation", Tag.COMPILATION, Tag.DMN_CORE ),
    REQ_NOT_FOUND( "The referenced node was not found", Tag.COMPILATION, Tag.DMN_CORE ),
    IMPORT_NOT_FOUND("The referenced import was not found", Tag.COMPILATION, Tag.DMN_CORE),
    TYPE_REF_NOT_FOUND( "The listed type reference could not be resolved", Tag.COMPILATION, Tag.DMN_CORE ),
    TYPE_DEF_NOT_FOUND( "The listed type definition was not found", Tag.COMPILATION, Tag.DMN_CORE ),
    INVALID_NAME( "The listed name is not a valid FEEL identifier", Tag.VALIDATION, Tag.DMN_VALIDATOR, Tag.COMPILATION, Tag.DMN_CORE ),
    INVALID_ATTRIBUTE_VALUE( "Invalid value for the listed attribute", Tag.VALIDATION, Tag.DMN_VALIDATOR, Tag.COMPILATION, Tag.DMN_CORE ),
    INVALID_SYNTAX( "Invalid FEEL syntax on the referenced expression", Tag.COMPILATION, Tag.RUNTIME, Tag.DMN_CORE ),
    MISSING_EXPRESSION( "No decision logic was defined for the node or variable", Tag.COMPILATION, Tag.VALIDATION, Tag.DMN_CORE, Tag.DMN_VALIDATOR ),
    MISSING_VARIABLE( "A variable declaration is missing", Tag.COMPILATION, Tag.VALIDATION, Tag.DMN_CORE, Tag.DMN_VALIDATOR ),
    VARIABLE_NAME_MISMATCH( "A variable name does not match the node it belongs to", Tag.COMPILATION, Tag.VALIDATION, Tag.DMN_CORE, Tag.DMN_VALIDATOR ),
    MISSING_TYPE_REF( "Type ref not defined", Tag.COMPILATION, Tag.VALIDATION, Tag.DMN_CORE, Tag.DMN_VALIDATOR ),
    DUPLICATE_NAME( "The referenced name is not unique with its scope", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    MISSING_NAME( "The referenced element requires a name", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    MISSING_OUTPUT_VALUES( "The referenced output elements are missing the list of output values", Tag.COMPILATION, Tag.VALIDATION, Tag.DMN_VALIDATOR, Tag.DMN_CORE ),
    ILLEGAL_USE_OF_NAME( "The referenced element should not have a name set", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    ILLEGAL_USE_OF_TYPEREF( "The referenced element should not have a typeref set", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    INVALID_HREF_SYNTAX( "The 'href' attribute requires the use of anchor syntax", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    DUPLICATED_PARAM( "The referenced param is duplicated", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    PARAMETER_MISMATCH( "The named parameter does not match", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    TYPEREF_MISMATCH( "The typeRef does not match", Tag.COMPILATION, Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    DUPLICATED_ITEM_DEF( "The referenced item definition or item component is duplicated", Tag.COMPILATION, Tag.VALIDATION, Tag.DMN_VALIDATOR, Tag.DMN_CORE ),
    DUPLICATED_RELATION_COLUMN( "The referenced relation column is duplicated", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    RELATION_CELL_NOT_LITERAL( "The referenced relation cell is not a literal expression", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    RELATION_CELL_COUNT_MISMATCH( "The referenced relation row cell count doesn't match the list of defined columns", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    DECISION_NOT_FOUND( "The referenced Decision was not found", Tag.RUNTIME, Tag.DMN_CORE ),
    ERROR_EVAL_NODE( "Error evaluating node", Tag.RUNTIME, Tag.DMN_CORE ),
    EXPR_TYPE_NOT_SUPPORTED_IN_NODE( "The referenced expression type is node supported by the ending", Tag.COMPILATION, Tag.DMN_CORE ),
    ERR_COMPILING_FEEL( "Error compiling the referenced FEEL expression", Tag.COMPILATION, Tag.RUNTIME, Tag.DMN_CORE ),
    ERR_EVAL_CTX( "Error evaluating context or context entry", Tag.RUNTIME, Tag.DMN_CORE ),
    FEEL_EVALUATION_ERROR( "FEEL expression evaluation error", Tag.COMPILATION, Tag.RUNTIME, Tag.DMN_CORE ),
    INVOCATION_ERROR( "Error invoking node or function", Tag.RUNTIME, Tag.DMN_CORE ),

    FAILED_VALIDATOR( "The DMN validator failed to load the validation rules. Impossible to proceed with validation.", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    FAILED_XML_VALIDATION( "DMN model failed XML schema validation", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    FAILED_VALIDATION("DMN Validation stopped on failed validation for some DMN Model", Tag.VALIDATION, Tag.DMN_VALIDATOR),

    DECISION_TABLE_ANALYSIS("DMN Validation, Decision Table Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_ANALYSIS_EMPTY("DMN Validation, Decision Table Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_ANALYSIS_ERROR("DMN Validation, Decision Table Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_GAP("DMN Validation, Decision Table Analysis, Gap Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_OVERLAP("DMN Validation, Decision Table Analysis, Overlap Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_OVERLAP_HITPOLICY_UNIQUE("DMN Validation, Decision Table Analysis, Overlap Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_OVERLAP_HITPOLICY_ANY("DMN Validation, Decision Table Analysis, Overlap Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_HITPOLICY_FIRST("DMN Validation, Decision Table Analysis, Hit Policy First considered bad practice", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_MASKED_RULE("DMN Validation, Decision Table Analysis, Masked Rule Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_MISLEADING_RULE("DMN Validation, Decision Table Analysis, Misleading Rule Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_SUBSUMPTION_RULE("DMN Validation, Decision Table Analysis, Subsumption Rule Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_CONTRACTION_RULE("DMN Validation, Decision Table Analysis, Contraction Rule Analysis", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_1STNFVIOLATION("DMN Validation, Decision Table Analysis, First Normal Form Violation", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_2NDNFVIOLATION("DMN Validation, Decision Table Analysis, Second Normal Form Violation", Tag.DECISION_TABLE_ANALYSIS, Tag.DMN_VALIDATOR),
    DECISION_TABLE_HITPOLICY_RECOMMENDER("DMN Validation, Decision Table Analysis, Hit Policy Recommender",Tag.DECISION_TABLE_ANALYSIS,Tag.DMN_VALIDATOR),

    DMNDI_MISSING_DIAGRAM( "No DMNDiagramElement is associated with the element", Tag.VALIDATION, Tag.DMN_VALIDATOR ),
    DMNDI_UNKNOWN_REF( "The referenced element could not be resolved", Tag.VALIDATION, Tag.DMN_VALIDATOR );

    private final Tag[]  tags;
    private final String description;

    DMNMessageType(String description, Tag... tags) {
        this.description = description;
        this.tags = tags;
    }

    public String getDescription() {
        return this.description;
    }

    public Tag[] getTags() {
        return this.tags;
    }

    public enum Tag {
        // message source
        DMN_CORE, DMN_VALIDATOR,
        // validation phase
        VALIDATION, COMPILATION, RUNTIME, DECISION_TABLE_ANALYSIS
    }

}
