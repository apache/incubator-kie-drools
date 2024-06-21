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
package org.kie.dmn.core.util;

import org.kie.dmn.api.core.DMNMessageType;

public final class Msg {
    // consolidated
    public static final Message1 PARAM_CANNOT_BE_NULL                                = new Message1( DMNMessageType.KIE_API, "Kie DMN API parameter '%s' cannot be null." );
    public static final Message1 PARAM_CANNOT_BE_EMPTY                               = new Message1( DMNMessageType.KIE_API, "Kie DMN API parameter '%s' cannot be empty." );
    public static final Message2 UNSUPPORTED_ELEMENT                                 = new Message2( DMNMessageType.UNSUPPORTED_ELEMENT, "Element %s with type='%s' is not supported." );
    public static final Message1 IMPORT_TYPE_UNKNOWN                                 = new Message1( DMNMessageType.INVALID_SYNTAX, "Import type unknown: '%s'." );
    public static final Message2 IMPORT_NOT_FOUND_FOR_NODE                           = new Message2( DMNMessageType.IMPORT_NOT_FOUND, "Required import not found: %s for node '%s' " );
    public static final Message2 IMPORT_NOT_FOUND_FOR_NODE_MISSING_ALIAS             = new Message2( DMNMessageType.IMPORT_NOT_FOUND, "Required import not found: %s for node '%s'; missing DMN Import name alias." );
    public static final Message2 REQ_INPUT_NOT_FOUND_FOR_DS                          = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required input '%s' not found for Decision Service '%s', invoking using null.");
    public static final Message2 REFERENCE_NOT_FOUND_FOR_DS                          = new Message2( DMNMessageType.REQ_NOT_FOUND, "Element reference '%s' not resolved for Decision Service '%s'.");
    public static final Message2 REQ_INPUT_NOT_FOUND_FOR_NODE                        = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required input '%s' not found on node '%s'" );
    public static final Message3 OUTPUT_NOT_FOUND_FOR_DS                             = new Message3( DMNMessageType.REQ_NOT_FOUND, "Decision service '%s' does not define any output decisions in top segment (outputDecision count: %s, encapsulatedDecision count: %s)" );
    public static final Message2 REQ_DECISION_NOT_FOUND_FOR_NODE                     = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required Decision '%s' not found on node '%s'" );
    public static final Message2 REQ_BKM_NOT_FOUND_FOR_NODE                          = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required Business Knowledge Model '%s' not found on node '%s'" );
    public static final Message1 CYCLIC_DEP_FOR_NODE                                 = new Message1( DMNMessageType.REQ_NOT_FOUND, "Cyclic dependency detected for node '%s'");
    public static final Message2 REQ_DEP_NOT_FOUND_FOR_NODE                          = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required dependency '%s' not found on node '%s'" );
    public static final Message2 REQ_DEP_INVALID_TYPE                                = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required dependency '%s' on node '%s' does not match the node type" );
    public static final Message2 UNABLE_TO_EVALUATE_DECISION_REQ_DEP                 = new Message2( DMNMessageType.REQ_NOT_FOUND, "Unable to evaluate decision '%s' as it depends on decision '%s'" );
    public static final Message2 FUNCTION_NOT_FOUND                                  = new Message2( DMNMessageType.REQ_NOT_FOUND, "Function '%s' not found. Invocation failed on node '%s'" );
    public static final Message2 UNKNOWN_TYPE_REF_ON_NODE                            = new Message2( DMNMessageType.TYPE_DEF_NOT_FOUND, "Unable to resolve type reference '%s' on node '%s'" );
    public static final Message2 UNKNOWN_FEEL_TYPE_REF_ON_NODE                       = new Message2( DMNMessageType.TYPE_REF_NOT_FOUND, "Type reference '%s' is not a valid FEEL type reference on node '%s'" );
    public static final Message1 UNKNOWN_OUTPUT_TYPE_FOR_DT_ON_NODE                  = new Message1( DMNMessageType.TYPE_REF_NOT_FOUND, "Unknown output type for decision table on node '%s'" );
    public static final Message2 INVALID_NAME                                        = new Message2( DMNMessageType.INVALID_NAME, "Invalid name '%s': %s" );
    public static final Message3 NAME_NOT_NORMALIZED                                 = new Message3( DMNMessageType.INVALID_NAME, "Name '%s' contains whitespace which is not normalized for element %s with id '%s'" );
    public static final Message3 NAME_NOT_TRIMMED                                    = new Message3( DMNMessageType.INVALID_NAME, "Name '%s' is not trimmed for element %s with id '%s'" );
    public static final Message1 INVALID_SYNTAX                                      = new Message1( DMNMessageType.INVALID_SYNTAX, "%s: invalid syntax" );
    public static final Message2 INVALID_SYNTAX2                                     = new Message2( DMNMessageType.INVALID_SYNTAX, "%s: %s" );
    public static final Message1 MISSING_EXPRESSION_FOR_BKM                          = new Message1( DMNMessageType.MISSING_EXPRESSION, "Missing expression for Business Knowledge Model node '%s'" );
    public static final Message1 MISSING_EXPRESSION_FOR_FUNCTION                     = new Message1( DMNMessageType.MISSING_EXPRESSION, "Missing expression for function '%s'. Skipping evaluation and returning null" );
    public static final Message1 MISSING_EXPRESSION_FOR_DECISION                     = new Message1( DMNMessageType.MISSING_EXPRESSION, "Missing expression for Decision Node '%s'" );
    public static final Message1 MISSING_EXPRESSION_FOR_NODE                         = new Message1( DMNMessageType.MISSING_EXPRESSION, "Missing expression for Node '%s'" );
    public static final Message1 MISSING_EXPRESSION_FOR_INVOCATION                   = new Message1( DMNMessageType.MISSING_EXPRESSION, "Missing expression for invocation node '%s'" );
    public static final Message3 EXPRESSION_FOR_INVOCATION_NOT_RESOLVED              = new Message3( DMNMessageType.REQ_NOT_FOUND, "The expression '%s' for invocation node '%s' did not resolve during compile time. In this DMN scope: %s" );
    public static final Message2 MISSING_EXPRESSION_FOR_PARAM_OF_INVOCATION          = new Message2( DMNMessageType.MISSING_EXPRESSION, "Missing expression for parameter %s on node '%s'");
    public static final Message1 MISSING_PARAMETER_FOR_INVOCATION                    = new Message1( DMNMessageType.MISSING_EXPRESSION, "Missing parameter for invocation node '%s'" );
    public static final Message2 MISSING_EXPRESSION_FOR_NAME                         = new Message2( DMNMessageType.MISSING_EXPRESSION, "No expression defined for name '%s' on node '%s'" );
    public static final Message1 MISSING_ENTRIES_ON_CONTEXT                          = new Message1( DMNMessageType.MISSING_EXPRESSION, "Context expression has no entries on node '%s'" );
    public static final Message1 MISSING_VARIABLE_FOR_DS                             = new Message1( DMNMessageType.MISSING_VARIABLE, "Decision Service node '%s' is missing the variable declaration");
    public static final Message1 MISSING_VARIABLE_FOR_BKM                            = new Message1( DMNMessageType.MISSING_VARIABLE, "Business Knowledge Model node '%s' is missing the variable declaration" );
    public static final Message1 MISSING_VARIABLE_FOR_INPUT                          = new Message1( DMNMessageType.MISSING_VARIABLE, "Input Data node '%s' is missing the variable declaration" );
    public static final Message1 MISSING_VARIABLE_FOR_DECISION                       = new Message1( DMNMessageType.MISSING_VARIABLE, "Decision node '%s' is missing the variable declaration" );
    public static final Message1 MISSING_VARIABLE_ON_CONTEXT                         = new Message1( DMNMessageType.MISSING_VARIABLE, "Context entry is missing variable declaration on node '%s'" );
    public static final Message2 VARIABLE_NAME_MISMATCH_FOR_BKM                      = new Message2( DMNMessageType.VARIABLE_NAME_MISMATCH, "Variable name '%s' does not match the Business Knowledge Model node name '%s'" );
    public static final Message2 VARIABLE_NAME_MISMATCH_FOR_DECISION                 = new Message2( DMNMessageType.VARIABLE_NAME_MISMATCH, "Variable name '%s' does not match the Decision node name '%s'" );
    public static final Message2 VARIABLE_NAME_MISMATCH_FOR_INPUT                    = new Message2( DMNMessageType.VARIABLE_NAME_MISMATCH, "Variable name '%s' does not match the Input Data node name '%s'" );
    public static final Message2 VARIABLE_TYPE_MISMATCH_FOR_BKM_EL                   = new Message2( DMNMessageType.TYPEREF_MISMATCH, "Encapsulated logic type '%s' does not match the Business Knowledge Model variable type '%s'. Compilation will consider only the variable type." );
    public static final Message2 VARIABLE_TYPE_MISMATCH_FOR_BKM_EL_BODY              = new Message2( DMNMessageType.TYPEREF_MISMATCH, "Encapsulated logic's expression type '%s' does not match the Business Knowledge Model variable's return type (was expecting '%s'). Compilation will refer only the BKM variable type." );
    public static final Message1 DUPLICATE_CONTEXT_ENTRY                             = new Message1( DMNMessageType.DUPLICATE_NAME, "Duplicate context entry with variables named '%s'" );
    public static final Message2 MISSING_TYPEREF_FOR_VARIABLE                        = new Message2( DMNMessageType.MISSING_TYPE_REF, "Variable named '%s' is missing its type reference on node '%s'" );
    public static final Message2 VARIABLE_LEADING_TRAILING_SPACES                    = new Message2( DMNMessageType.INVALID_NAME, "Variable name contains leading or traling spaces '%s' on node '%s'" );
    public static final Message2 MISSING_TYPEREF_FOR_PARAMETER                       = new Message2( DMNMessageType.MISSING_TYPE_REF, "Parameter named '%s' is missing its type reference on node '%s'" );
    public static final Message2 MISSING_TYPEREF_FOR_COLUMN                          = new Message2( DMNMessageType.MISSING_TYPE_REF, "Column named '%s' is missing its type reference on node '%s'" );
    public static final Message3 WRONG_TYPEREF_FOR_COLUMN                            = new Message3( DMNMessageType.MISSING_TYPE_REF, "Column #%s named '%s' defines a type reference '%s' which does not exists");
    public static final Message1 DUPLICATE_DRG_ELEMENT                               = new Message1( DMNMessageType.DUPLICATE_NAME, "Duplicate node name '%s' in the model" );
    public static final Message1 IMPORT_NAME_NOT_UNIQUE                              = new Message1( DMNMessageType.DUPLICATE_NAME, "Import name '%s' is not unique in the model, the DMN Spec mandates the Import's name must be distinct from the names of other imports, decisions, input data, business knowledge models, decision services, and item definitions within the importing model only." );
    public static final Message1 MISSING_NAME_FOR_DT_OUTPUT                          = new Message1( DMNMessageType.MISSING_NAME, "Decision table with multiple outputs on node '%s' requires a name for each output" );
    public static final Message1 MISSING_TYPEREF_FOR_DT_OUTPUT                       = new Message1( DMNMessageType.MISSING_TYPE_REF, "Decision table with multiple outputs on node '%s' requires a type reference for each output" );
    public static final Message1 MISSING_OUTPUT_VALUES                               = new Message1( DMNMessageType.MISSING_OUTPUT_VALUES, "Decision table '%s' with hit policy Priority requires output elements to specify the output values list" );
    public static final Message1 DTABLE_SINGLEOUT_NONAME                             = new Message1( DMNMessageType.ILLEGAL_USE_OF_NAME, "Decision table with single output on node '%s' should not have output name" );
    public static final Message1 DTABLE_SINGLEOUT_NOTYPEREF                          = new Message1( DMNMessageType.ILLEGAL_USE_OF_TYPEREF, "Decision table with single output on node '%s' should not have an output type reference" );
    public static final Message3 DTABLE_EMPTY_ENTRY                                  = new Message3( DMNMessageType.MISSING_EXPRESSION, "Missing test on decision table input entry (row %d, column %d) on node '%s'" );
    public static final Message1 ELEMREF_NOHASH                                      = new Message1( DMNMessageType.INVALID_HREF_SYNTAX, "The 'href' reference on node '%s' requires the use of the anchor syntax" );
    public static final Message2 DUPLICATE_FORMAL_PARAM                              = new Message2( DMNMessageType.DUPLICATED_PARAM, "The formal parameter '%s' on function definition on node '%s' is duplicated" );
    public static final Message3 UNKNOWN_PARAMETER                                   = new Message3( DMNMessageType.PARAMETER_MISMATCH, "Unknown parameter '%s' invoking function '%s' on node '%s'" );
    public static final Message3 PARAMETER_COUNT_MISMATCH_COMPILING                  = new Message3( DMNMessageType.PARAMETER_MISMATCH, "Parameter count mismatch while compiling node '%s'; the type FunctionItem has '%s' parameters, the defined decision logic has '%s' parameters." );
    public static final Message3 PARAMETER_NAMES_MISMATCH_COMPILING                  = new Message3( DMNMessageType.PARAMETER_MISMATCH, "Parameter names mismatch while compiling node '%s'; the type FunctionItem defines '%s' as parameters, the defined decision logic defines '%s' as parameters." );
    public static final Message4 PARAMETER_TYPEREF_MISMATCH_COMPILING                = new Message4( DMNMessageType.PARAMETER_MISMATCH, "Parameter typeRef mismatch while compiling node '%s'; for the parameter '%s', FunctionItem defines typeRef '%s', the defined decision logic defines typeRef '%s'." );
    public static final Message3 RETURNTYPE_TYPEREF_MISMATCH_COMPILING               = new Message3( DMNMessageType.TYPEREF_MISMATCH, "Return type typeRef mismatch while compiling node '%s'; FunctionItem defines typeRef '%s', the defined decision logic defines typeRef '%s'." );
    public static final Message2 PARAMETER_COUNT_MISMATCH                            = new Message2( DMNMessageType.PARAMETER_MISMATCH, "Parameter count mismatch invoking function '%s' on node '%s'" );
    public static final Message1 PARAMETER_COUNT_MISMATCH_DS                         = new Message1( DMNMessageType.PARAMETER_MISMATCH, "Parameter count mismatch invoking decision service function '%s' " );
    public static final Message3 PARAMETER_TYPE_MISMATCH                             = new Message3( DMNMessageType.PARAMETER_MISMATCH, "Parameter '%s' is of type '%s' but the actual value '%s' is not an instance of that type; setting as null" );
    public static final Message3 PARAMETER_TYPE_MISMATCH_DS                          = new Message3( DMNMessageType.PARAMETER_MISMATCH, "Decision Service parameter '%s' is of type '%s' but the actual value '%s' is not an instance of that type; setting as null" );
    public static final Message2 DUPLICATED_ITEM_COMPONENT                           = new Message2( DMNMessageType.DUPLICATED_ITEM_DEF, "Item Component '%s' is duplicated on Item Definition '%s'" );
    public static final Message1 DUPLICATED_ITEM_DEFINITION                          = new Message1( DMNMessageType.DUPLICATED_ITEM_DEF, "Item Definition '%s' is duplicated in the model" );
    public static final Message2 DUPLICATED_RELATION_COLUMN                          = new Message2( DMNMessageType.DUPLICATED_RELATION_COLUMN, "Relation column '%s' is duplicated on node '%s'" );
    public static final Message2 RELATION_CELL_NOT_LITERAL                           = new Message2( DMNMessageType.RELATION_CELL_NOT_LITERAL, "Relation row '%d' contains a cell that is not a literal expression on node '%s'" );
    public static final Message2 RELATION_CELL_COUNT_MISMATCH                        = new Message2( DMNMessageType.RELATION_CELL_COUNT_MISMATCH, "Relation row '%d' contains the wrong number of cells on node '%s'" );
    public static final Message1 ERRORS_EVAL_DS_NODE                                 = new Message1( DMNMessageType.ERROR_EVAL_NODE, "Errors occured while evaluating Decision Service node '%s'.");
    public static final Message2 ERROR_EVAL_DS_NODE                                  = new Message2( DMNMessageType.ERROR_EVAL_NODE, "Error evaluating Decision Service node '%s': %s" );
    public static final Message2 ERROR_EVAL_BKM_NODE                                 = new Message2( DMNMessageType.ERROR_EVAL_NODE, "Error evaluating Business Knowledge Model node '%s': %s" );
    public static final Message2 ERROR_EVAL_DECISION_NODE                            = new Message2( DMNMessageType.ERROR_EVAL_NODE, "Error evaluating Decision node '%s': %s" );
    public static final Message4 ERROR_EVAL_NODE_DEP_WRONG_TYPE                      = new Message4( DMNMessageType.ERROR_EVAL_NODE, "Error while evaluating node '%s' for dependency '%s': the dependency value '%s' is not allowed by the declared type (%s)" );
    public static final Message3 ERROR_EVAL_NODE_RESULT_WRONG_TYPE                   = new Message3( DMNMessageType.ERROR_EVAL_NODE, "Error while evaluating node '%s': the declared result type is '%s' but the actual value '%s' is not an instance of that type" );
    public static final Message2 EXPR_TYPE_NOT_SUPPORTED_IN_NODE                     = new Message2( DMNMessageType.EXPR_TYPE_NOT_SUPPORTED_IN_NODE, "Expression type '%s' not supported in node '%s'" );
    public static final Message4 ERR_COMPILING_FEEL_EXPR_ON_DT_INPUT_CLAUSE_IDX      = new Message4( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s', input clause #%s: %s" );
    public static final Message4 ERR_COMPILING_FEEL_EXPR_ON_DT_OUTPUT_CLAUSE_IDX     = new Message4( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s', output clause #%s: %s" );
    public static final Message4 ERR_COMPILING_FEEL_EXPR_ON_DT_RULE_IDX              = new Message4( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s', rule #%s: %s" );
    public static final Message2 ERR_COMPILING_FEEL_EXPR_ON_DT_PARAM                 = new Message2( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' parameter of invocation of decision table '%s'");
    public static final Message2 ERR_COMPILING_FEEL_EXPR_ON_DT                       = new Message2( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s'" );
    public static final Message2 ERR_COMPILING_ALLOWED_VALUES_LIST_ON_ITEM_DEF       = new Message2( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling allowed values list '%s' on item definition '%s'" );
    public static final Message2 ERR_COMPILING_TYPE_CONSTRAINT_LIST_ON_ITEM_DEF       = new Message2( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling type constraint list '%s' on item definition '%s'" );
    public static final Message4 ERR_COMPILING_FEEL_EXPR_FOR_NAME_ON_NODE            = new Message4( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' for name '%s' on node '%s': %s" );
    public static final Message2 ERR_EVAL_CTX_ENTRY_ON_CTX                           = new Message2( DMNMessageType.ERR_EVAL_CTX, "Error evaluating context extry '%s' on context '%s'" );
    public static final Message3 ERR_EVAL_CTX_ENTRY_ON_CTX_MSG                       = new Message3( DMNMessageType.ERR_EVAL_CTX, "Unrecoverable error evaluating context extry '%s' on context '%s': %s" );
    public static final Message1 DECISION_NOT_FOUND_FOR_NAME                         = new Message1( DMNMessageType.DECISION_NOT_FOUND, "Decision not found for name '%s'" );
    public static final Message1 DECISION_NOT_FOUND_FOR_ID                           = new Message1( DMNMessageType.DECISION_NOT_FOUND, "Decision not found for type '%s'" );
    public static final Message1 DECISION_SERVICE_NOT_FOUND_FOR_NAME                 = new Message1( DMNMessageType.DECISION_NOT_FOUND, "Decision Service not found for name '%s'");
    public static final Message1 FEEL_ERROR                                          = new Message1( DMNMessageType.FEEL_EVALUATION_ERROR, "%s" );
    public static final Message1 FEEL_WARN                                           = new Message1( DMNMessageType.FEEL_EVALUATION_ERROR, "%s" );
    public static final Message3 FEEL_EVENT_EVAL_LITERAL_EXPRESSION                  = new Message3( DMNMessageType.FEEL_EVALUATION_ERROR, "FEEL %s while evaluating literal expression '%s': %s");
    public static final Message3 ERR_EVAL_PARAM_FOR_INVOCATION_ON_NODE               = new Message3( DMNMessageType.FEEL_EVALUATION_ERROR, "Error evaluating parameter '%s' for invocation '%s' on node '%s'" );
    public static final Message2 ERR_EVAL_LIST_ELEMENT_ON_POSITION_ON_LIST           = new Message2( DMNMessageType.FEEL_EVALUATION_ERROR, "Error evaluating list element on position '%s' on list '%s'" );
    public static final Message3 ERR_EVAL_ROW_ELEMENT_ON_POSITION_ON_ROW_OF_RELATION = new Message3( DMNMessageType.FEEL_EVALUATION_ERROR, "Error evaluating row element on position '%s' on row '%s' of relation '%s'" );
    public static final Message2 ERR_INVOKING_PARAM_EXPR_FOR_PARAM_ON_NODE           = new Message2( DMNMessageType.INVOCATION_ERROR, "Error invoking parameter expression for parameter '%s' on node '%s'." );
    public static final Message2 ERR_INVOKING_FUNCTION_ON_NODE                       = new Message2( DMNMessageType.INVOCATION_ERROR, "Error invoking function '%s' on node '%s'" );
    public static final Message0 FAILED_VALIDATOR                                    = new Message0( DMNMessageType.FAILED_VALIDATOR, "The validator was unable to compile the embedded DMN validation rules. Validation of the DMN Model cannot be performed." );
    public static final Message1 VALIDATION_RUNTIME_PROBLEM                          = new Message1( DMNMessageType.FAILED_VALIDATION, "Validation of the DMN Model cannot be performed because of some runtime exception '%s'." );
    public static final Message0 VALIDATION_STOPPED                                  = new Message0( DMNMessageType.FAILED_VALIDATION, "One of the supplied DMN Models has failed validation; cannot proceed to validation of the remaining DMN Models." );
    public static final Message0 FAILED_NO_XML_SOURCE                                = new Message0( DMNMessageType.FAILED_VALIDATOR, "Schema validation not supported for in memory object. Please use the validate method with the file or reader signature." );
    public static final Message1 FAILED_XML_VALIDATION                               = new Message1( DMNMessageType.FAILED_XML_VALIDATION, "Failed XML validation of DMN file: %s" );
    public static final Message2 FUNC_DEF_INVALID_KIND                               = new Message2( DMNMessageType.INVALID_ATTRIBUTE_VALUE, "Invalid 'kind' value '%s' on function definition in node '%s'" );
    public static final Message1 FUNC_DEF_PMML_NOT_SUPPORTED                         = new Message1( DMNMessageType.INVALID_ATTRIBUTE_VALUE, "No PMML runtime found to support PMML function definitions, node '%s'. Function evaluation will be skipped." );
    public static final Message1 FUNC_DEF_BODY_NOT_CONTEXT                           = new Message1( DMNMessageType.INVALID_SYNTAX, "A non-FEEL function definition requires a context as its body in node '%s'" );
    public static final Message3 FUNC_DEF_COMPILATION_ERR                            = new Message3( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling Java function '%s' on node '%s': %s" );
    public static final Message2 FUNC_DEF_MISSING_ENTRY                              = new Message2( DMNMessageType.INVALID_SYNTAX, "A Java function definition requires both the 'class' and the 'method signature' attributes. Invalid definition for function '%s' on node '%s'" );
    public static final Message2 FUNC_DEF_PMML_MISSING_ENTRY                         = new Message2( DMNMessageType.INVALID_SYNTAX, "A PMML function definition requires both the 'document' and the 'model' attributes. Invalid definition for function '%s' on node '%s'" );
    public static final Message1 FUNC_DEF_PMML_MISSING_MODEL_NAME                    = new Message1( DMNMessageType.INVALID_SYNTAX, "The PMML function definition did not provide 'model' attribute, while the PMML resource defines the following model names: %s" );
    public static final Message1 FUNC_DEF_PMML_ERR_LOCATIONURI                       = new Message1( DMNMessageType.IMPORT_NOT_FOUND, "Unable to locate pmml model from locationURI '%s'");
    public static final Message2 ERROR_CHECKING_ALLOWED_VALUES                       = new Message2( DMNMessageType.FEEL_EVALUATION_ERROR, "Error checking allowed values for node '%s': %s" );
    public static final Message1 DTMULTIPLEOUTPUTCOLLECTOPERATOR                     = new Message1( DMNMessageType.INVALID_SYNTAX, "Decision Table '%s' is using Collect with Operator for compound outputs; this is partially supported beyond the specification requirements.");
    public static final Message1 DTANALYSISRESULT                                    = new Message1( DMNMessageType.DECISION_TABLE_ANALYSIS, "Decision Table Analysis results: %s");
    public static final Message1 DTANALYSIS_EMPTY                                    = new Message1( DMNMessageType.DECISION_TABLE_ANALYSIS_EMPTY, "Decision Table Analysis of table '%s' finished with no messages to be reported.");
    public static final Message2 DTANALYSIS_ERROR_ANALYSIS_SKIPPED                   = new Message2( DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR, "Skipped Decision Table Analysis of table '%s' because: %s");
    public static final Message3 DTANALYSIS_HITPOLICY_PRIORITY_ANALYSIS_SKIPPED      = new Message3( DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR, "Skipped Decision Table Analysis of table '%s' hit policy Priority mask rules for rules: %s %s as they define multiple inputentries");
    public static final Message4 DTANALYSIS_ERROR_RULE_OUTSIDE_DOMAIN                = new Message4( DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR, "Rule %s defines '%s' which is outside the domain min/max %s of column %s");
    public static final Message4 DTANALYSIS_ERROR_RULE_OUTPUT_OUTSIDE_LOV            = new Message4( DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR, "Rule %s defines output '%s' which is outside the column's %s allowed values %s");
    public static final Message1 DTANALYSIS_GAP                                      = new Message1( DMNMessageType.DECISION_TABLE_GAP, "Gap detected: %s");
    public static final Message1 DTANALYSIS_GAP_SKIPPED_BECAUSE_FREE_STRING          = new Message1( DMNMessageType.DECISION_TABLE_GAP, "Columns: %s relate to FEEL string values which can be enumerated for the inputs; Gap analysis skipped.");
    public static final Message1 DTANALYSIS_OVERLAP                                  = new Message1( DMNMessageType.DECISION_TABLE_OVERLAP, "Overlap observed: %s");
    public static final Message1 DTANALYSIS_OVERLAP_HITPOLICY_UNIQUE                 = new Message1( DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_UNIQUE, "Overlap detected: %s. UNIQUE hit policy decision tables can only have one matching rule.");
    public static final Message1 DTANALYSIS_OVERLAP_HITPOLICY_ANY                    = new Message1( DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_ANY   , "Overlap detected: %s. ANY hit policy decision tables allows multiple rules to match, but they [must] all have the same output");
    public static final Message2 DTANALYSIS_HITPOLICY_PRIORITY_MASKED_RULE           = new Message2( DMNMessageType.DECISION_TABLE_MASKED_RULE, "Rule %s is masked by rule: %s");
    public static final Message2 DTANALYSIS_HITPOLICY_PRIORITY_MISLEADING_RULE       = new Message2( DMNMessageType.DECISION_TABLE_MISLEADING_RULE, "Rule %s is a misleading rule. It could be misleading over other rules, such as rule: %s");
    public static final Message4 DTANALYSIS_SUBSUMPTION_RULE                         = new Message4( DMNMessageType.DECISION_TABLE_SUBSUMPTION_RULE, "Subsumption: Rule %s contains rule: %s; rules can be contracted, by keeping rule %s and erasing rule %s");
    public static final Message2 DTANALYSIS_CONTRACTION_RULE                         = new Message2( DMNMessageType.DECISION_TABLE_CONTRACTION_RULE, "Table is not fully contracted. Combine rules %s by joining input %s.");
    public static final Message0 DTANALYSIS_1STNFVIOLATION_FIRST                     = new Message0( DMNMessageType.DECISION_TABLE_1STNFVIOLATION, "First Normal Form Violation: hit policy First is a violation of First Normal Form; consider changing for example to Priority");
    public static final Message0 DTANALYSIS_1STNFVIOLATION_RULE_ORDER                = new Message0( DMNMessageType.DECISION_TABLE_1STNFVIOLATION, "First Normal Form Violation: hit policy Rule Order is a violation of First Normal Form; consider changing for example to Output Order or Collect");
    public static final Message1 DTANALYSIS_1STNFVIOLATION_DUPLICATE_RULES           = new Message1( DMNMessageType.DECISION_TABLE_1STNFVIOLATION, "First Normal Form Violation: Rules %s are duplicates");
    public static final Message2 DTANALYSIS_2NDNFVIOLATION                           = new Message2( DMNMessageType.DECISION_TABLE_2NDNFVIOLATION, "Second Normal Form Violation: Input %s is irrelevant for rules %s. Consider combining these rules over the irrelevant input");
    public static final Message3 DTANALYSIS_2NDNFVIOLATION_WAS_DASH                  = new Message3( DMNMessageType.DECISION_TABLE_2NDNFVIOLATION, "Second Normal Form Violation: input entry '%s' in rule %s column %s covers the entire domain. The input entry should be written as -.");
    public static final Message1 DTANALYSIS_HITPOLICY_RECOMMENDER_UNIQUE             = new Message1( DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER, "The HitPolicy for decision table '%s' should be UNIQUE");
    public static final Message1 DTANALYSIS_HITPOLICY_RECOMMENDER_ANY                = new Message1( DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER, "Overlapping rules have the same output value, so the HitPolicy for decision table '%s' should be ANY");
    public static final Message1 DTANALYSIS_HITPOLICY_RECOMMENDER_PRIORITY           = new Message1( DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER, "Overlapping rules have different output value, so the HitPolicy for decision table '%s' should be PRIORITY");
    public static final Message1 DMNDI_MISSING_SHAPE                                 = new Message1( DMNMessageType.DMNDI_MISSING_DIAGRAM, "Missing DMNShape for '%s'" );
    public static final Message1 DMNDI_MISSING_EDGE                                  = new Message1( DMNMessageType.DMNDI_MISSING_DIAGRAM, "Missing DMNEdge for '%s'" );
    public static final Message2 DMNDI_UNKNOWN_REF                                   = new Message2( DMNMessageType.DMNDI_UNKNOWN_REF, "Unable to resolve dmnElementRef '%s' on '%s'" );
    public static final Message1 UNABLE_TO_RETRIEVE_PMML_RESULT                      = new Message1( DMNMessageType.INVOCATION_ERROR, "Unable to retrieve result from PMML model '%s'" );
    public static final Message2 MISSING_EXPRESSION_FOR_CONDITION                    = new Message2( DMNMessageType.MISSING_EXPRESSION, "Missing %s expression for Conditional node '%s'" );
    public static final Message2 MISSING_EXPRESSION_FOR_ITERATOR                     = new Message2( DMNMessageType.MISSING_EXPRESSION, "Missing %s expression for Iterator node '%s'" );
    public static final Message2 MISSING_EXPRESSION_FOR_FILTER                       = new Message2( DMNMessageType.MISSING_EXPRESSION, "Missing %s expression for Filter node '%s'" );
    public static final Message2 CONDITION_RESULT_NOT_BOOLEAN                        = new Message2( DMNMessageType.ERROR_EVAL_NODE, "The if condition on node %s returned a non boolean result: '%s'" );
    public static final Message1 IN_RESULT_NULL                                      = new Message1( DMNMessageType.ERROR_EVAL_NODE, "The in condition on node %s returned null.");
    public static final Message1 FILTER_EXPRESSION_RESULT_NOT_BOOLEAN                = new Message1( DMNMessageType.ERROR_EVAL_NODE, "The filter expression on node %s returned a non boolean result");
    public static final Message1 ITERATOR_EXPRESSION_RESULT_NOT_BOOLEAN              = new Message1( DMNMessageType.ERROR_EVAL_NODE, "The satisfy expression on node %s returned a non boolean result");
    public static final Message2 INDEX_OUT_OF_BOUND                                  = new Message2( DMNMessageType.ERROR_EVAL_NODE, "Index out of bound: list of %s elements, index %s; will evaluate as FEEL null");


    public static interface Message {
        String getMask();

        DMNMessageType getType();
    }
    public abstract static class AbstractMessage
            implements Message {
        private final String         mask;
        private final DMNMessageType type;

        public AbstractMessage(DMNMessageType type, String mask) {
            this.type = type;
            this.mask = mask;
        }

        public String getMask() {
            return this.mask;
        }

        public DMNMessageType getType() {
            return type;
        }
    }
    public static class Message0
            extends AbstractMessage {
        public Message0(DMNMessageType id, String mask) {
            super( id, mask );
        }
    }
    public static class Message1
            extends AbstractMessage {
        public Message1(DMNMessageType id, String mask) {
            super( id, mask );
        }
    }
    public static class Message2
            extends AbstractMessage {
        public Message2(DMNMessageType id, String mask) {
            super( id, mask );
        }
    }
    public static class Message3
            extends AbstractMessage {
        public Message3(DMNMessageType id, String mask) {
            super( id, mask );
        }
    }
    public static class Message4
            extends AbstractMessage {
        public Message4(DMNMessageType id, String mask) {
            super( id, mask );
        }
    }

    private Msg() {
        // Constructing instances is not allowed for this class
    }
}
