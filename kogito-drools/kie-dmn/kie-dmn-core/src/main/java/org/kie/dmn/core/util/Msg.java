package org.kie.dmn.core.util;

import org.kie.dmn.api.core.DMNMessageType;

public class Msg {
    // consolidated
    public static final Message2 UNSUPPORTED_ELEMENT                                 = new Message2( DMNMessageType.UNSUPPORTED_ELEMENT, "Element %s with type='%s' is not supported." );
    public static final Message2 REQ_INPUT_NOT_FOUND_FOR_NODE                        = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required input '%s' not found on node '%s'" );
    public static final Message2 REQ_DECISION_NOT_FOUND_FOR_NODE                     = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required Decision '%s' not found on node '%s'" );
    public static final Message2 REQ_BKM_NOT_FOUND_FOR_NODE                          = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required Business Knowledge Model '%s' not found on node '%s'" );
    public static final Message2 REQ_DEP_NOT_FOUND_FOR_NODE                          = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required dependency '%s' not found on node '%s'" );
    public static final Message2 REQ_DEP_INVALID_TYPE                                = new Message2( DMNMessageType.REQ_NOT_FOUND, "Required dependency '%s' on node '%s' does not match the node type" );
    public static final Message2 UNABLE_TO_EVALUATE_DECISION_REQ_DEP                 = new Message2( DMNMessageType.REQ_NOT_FOUND, "Unable to evaluate decision '%s' as it depends on decision '%s'" );
    public static final Message2 FUNCTION_NOT_FOUND                                  = new Message2( DMNMessageType.REQ_NOT_FOUND, "Function '%s' not found. Invocation failed on node '%s'" );
    public static final Message2 UNKNOWN_TYPE_REF_ON_NODE                            = new Message2( DMNMessageType.TYPE_DEF_NOT_FOUND, "Unable to resolve type reference '%s' on node '%s'" );
    public static final Message2 UNKNOWN_FEEL_TYPE_REF_ON_NODE                       = new Message2( DMNMessageType.TYPE_REF_NOT_FOUND, "Type reference '%s' is not a valid FEEL type reference on node '%s'" );
    public static final Message1 UNKNOWN_OUTPUT_TYPE_FOR_DT_ON_NODE                  = new Message1( DMNMessageType.TYPE_REF_NOT_FOUND, "Unknown output type for decision table on node '%s'" );
    public static final Message2 INVALID_NAME                                        = new Message2( DMNMessageType.INVALID_NAME, "Invalid name '%s': %s" );
    public static final Message1 INVALID_SYNTAX                                      = new Message1( DMNMessageType.INVALID_SYNTAX, "%s: invalid syntax" );
    public static final Message2 INVALID_SYNTAX2                                     = new Message2( DMNMessageType.INVALID_SYNTAX, "%s: %s" );
    public static final Message1 MISSING_EXPRESSION_FOR_BKM                          = new Message1( DMNMessageType.MISSING_EXPRESSION, "Missing expression for Business Knowledge Model node '%s'" );
    public static final Message1 MISSING_EXPRESSION_FOR_DECISION                     = new Message1( DMNMessageType.MISSING_EXPRESSION, "Missing expression for Decision Node '%s'" );
    public static final Message1 MISSING_EXPRESSION_FOR_NODE                         = new Message1( DMNMessageType.MISSING_EXPRESSION, "Missing expression for Node '%s'" );
    public static final Message2 MISSING_EXPRESSION_FOR_INVOCATION                   = new Message2( DMNMessageType.MISSING_EXPRESSION, "Missing expression for parameter %s on node '%s'" );
    public static final Message1 MISSING_PARAMETER_FOR_INVOCATION                    = new Message1( DMNMessageType.MISSING_EXPRESSION, "Missing parameter for invocation node '%s'" );
    public static final Message2 MISSING_EXPRESSION_FOR_NAME                         = new Message2( DMNMessageType.MISSING_EXPRESSION, "No expression defined for name '%s' on node '%s'" );
    public static final Message1 MISSING_ENTRIES_ON_CONTEXT                          = new Message1( DMNMessageType.MISSING_EXPRESSION, "Context expression has no entries on node '%s'" );
    public static final Message1 MISSING_VARIABLE_FOR_BKM                            = new Message1( DMNMessageType.MISSING_VARIABLE, "Business Knowledge Model node '%s' is missing the variable declaration" );
    public static final Message1 MISSING_VARIABLE_FOR_INPUT                          = new Message1( DMNMessageType.MISSING_VARIABLE, "Input Data node '%s' is missing the variable declaration" );
    public static final Message1 MISSING_VARIABLE_FOR_DECISION                       = new Message1( DMNMessageType.MISSING_VARIABLE, "Decision node '%s' is missing the variable declaration" );
    public static final Message1 MISSING_VARIABLE_ON_CONTEXT                         = new Message1( DMNMessageType.MISSING_VARIABLE, "Context entry is missing variable declaration on node '%s'" );
    public static final Message2 VARIABLE_NAME_MISMATCH_FOR_BKM                      = new Message2( DMNMessageType.VARIABLE_NAME_MISMATCH, "Variable name '%s' does not match the Business Knowledge Model node name '%s'" );
    public static final Message2 VARIABLE_NAME_MISMATCH_FOR_DECISION                 = new Message2( DMNMessageType.VARIABLE_NAME_MISMATCH, "Variable name '%s' does not match the Decision node name '%s'" );
    public static final Message2 VARIABLE_NAME_MISMATCH_FOR_INPUT                    = new Message2( DMNMessageType.VARIABLE_NAME_MISMATCH, "Variable name '%s' does not match the Input Data node name '%s'" );
    public static final Message1 DUPLICATE_CONTEXT_ENTRY                             = new Message1( DMNMessageType.DUPLICATE_NAME, "Duplicate context entry with variables named '%s'" );
    public static final Message2 MISSING_TYPEREF_FOR_VARIABLE                        = new Message2( DMNMessageType.MISSING_TYPE_REF, "Variable named '%s' is missing typeRef on node '%s'" );
    public static final Message2 VARIABLE_LEADING_TRAILING_SPACES                    = new Message2( DMNMessageType.INVALID_NAME, "Variable name contains leading or traling spaces '%s' on node '%s'" );
    public static final Message2 MISSING_TYPEREF_FOR_PARAMETER                       = new Message2( DMNMessageType.MISSING_TYPE_REF, "Parameter named '%s' is missing typeRef on node '%s'" );
    public static final Message2 MISSING_TYPEREF_FOR_COLUMN                          = new Message2( DMNMessageType.MISSING_TYPE_REF, "Column named '%s' is missing typeRef on node '%s'" );
    public static final Message1 DUPLICATE_DRG_ELEMENT                               = new Message1( DMNMessageType.DUPLICATE_NAME, "Duplicate node name '%s' in the model" );
    public static final Message1 MISSING_NAME_FOR_DT_OUTPUT                          = new Message1( DMNMessageType.MISSING_NAME, "Decision table with multiple outputs on node '%s' requires a name for each output" );
    public static final Message1 MISSING_TYPEREF_FOR_DT_OUTPUT                       = new Message1( DMNMessageType.MISSING_TYPE_REF, "Decision table with multiple outputs on node '%s' requires a typeref for each output" );
    public static final Message1 MISSING_OUTPUT_VALUES                               = new Message1( DMNMessageType.MISSING_OUTPUT_VALUES, "Decision table with hit policy Priority on node '%s' requires output elements to specify the output values list" );
    public static final Message1 DTABLE_SINGLEOUT_NONAME                             = new Message1( DMNMessageType.ILLEGAL_USE_OF_NAME, "Decision table with single output on node '%s' should not have output name" );
    public static final Message1 DTABLE_SINGLEOUT_NOTYPEREF                          = new Message1( DMNMessageType.ILLEGAL_USE_OF_TYPEREF, "Decision table with single output on node '%s' should not have output typeRef" );
    public static final Message1 ELEMREF_NOHASH                                      = new Message1( DMNMessageType.INVALID_HREF_SYNTAX, "The 'href' reference on node '%s' requires the use of the anchor syntax" );
    public static final Message2 DUPLICATE_FORMAL_PARAM                              = new Message2( DMNMessageType.DUPLICATED_PARAM, "The formal parameter '%s' on function definition on node '%s' is duplicated" );
    public static final Message3 UNKNOWN_PARAMETER                                   = new Message3( DMNMessageType.PARAMETER_MISMATCH, "Unknown parameter '%s' invoking function '%s' on node '%s'" );
    public static final Message2 PARAMETER_COUNT_MISMATCH                            = new Message2( DMNMessageType.PARAMETER_MISMATCH, "Parameter count mismatch invoking function '%s' on node '%s'" );
    public static final Message2 DUPLICATED_ITEM_COMPONENT                           = new Message2( DMNMessageType.DUPLICATED_ITEM_DEF, "Item Component '%s' is duplicated on Item Definition '%s'" );
    public static final Message1 DUPLICATED_ITEM_DEFINITION                          = new Message1( DMNMessageType.DUPLICATED_ITEM_DEF, "Item Definition '%s' is duplicated in the model" );
    public static final Message2 DUPLICATED_RELATION_COLUMN                          = new Message2( DMNMessageType.DUPLICATED_RELATION_COLUMN, "Relation column '%s' is duplicated on node '%s'" );
    public static final Message2 RELATION_CELL_NOT_LITERAL                           = new Message2( DMNMessageType.RELATION_CELL_NOT_LITERAL, "Relation row '%d' contains a cell that is not a literal expression on node '%s'" );
    public static final Message2 RELATION_CELL_COUNT_MISMATCH                        = new Message2( DMNMessageType.RELATION_CELL_COUNT_MISMATCH, "Relation row '%d' contains the wrong number of cells on node '%s'" );
    public static final Message2 ERROR_EVAL_BKM_NODE                                 = new Message2( DMNMessageType.ERROR_EVAL_NODE, "Error evaluating Business Knowledge Model node '%s': %s" );
    public static final Message2 ERROR_EVAL_DECISION_NODE                            = new Message2( DMNMessageType.ERROR_EVAL_NODE, "Error evaluating Decision node '%s': %s" );
    public static final Message3 ERROR_EVAL_NODE_DEP_WRONG_TYPE                      = new Message3( DMNMessageType.ERROR_EVAL_NODE, "Error while evaluating node '%s' for dependency '%s': the dependency value '%s' is inconsistent with the declared type" );
    public static final Message2 EXPR_TYPE_NOT_SUPPORTED_IN_NODE                     = new Message2( DMNMessageType.EXPR_TYPE_NOT_SUPPORTED_IN_NODE, "Expression type '%s' not supported in node '%s'" );
    public static final Message3 ERR_COMPILING_FEEL_EXPR_ON_DT_INPUT_CLAUSE_IDX      = new Message3( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s', input clause #%s" );
    public static final Message3 ERR_COMPILING_FEEL_EXPR_ON_DT_OUTPUT_CLAUSE_IDX     = new Message3( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s', output clause #%s" );
    public static final Message3 ERR_COMPILING_FEEL_EXPR_ON_DT_RULE_IDX              = new Message3( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s', rule #%s" );
    public static final Message2 ERR_COMPILING_FEEL_EXPR_ON_DT                       = new Message2( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s'" );
    public static final Message2 ERR_COMPILING_ALLOWED_VALUES_LIST_ON_ITEM_DEF       = new Message2( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling allowed values list '%s' on item definition '%s'" );
    public static final Message3 ERR_COMPILING_FEEL_EXPR_FOR_NAME_ON_NODE            = new Message3( DMNMessageType.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' for name '%s' on node '%s'" );
    public static final Message2 ERR_EVAL_CTX_ENTRY_ON_CTX                           = new Message2( DMNMessageType.ERR_EVAL_CTX, "Error evaluating context extry '%s' on context '%s'" );
    public static final Message1 DECISION_NOT_FOUND_FOR_NAME                         = new Message1( DMNMessageType.DECISION_NOT_FOUND, "Decision not found for name '%s'" );
    public static final Message1 DECISION_NOT_FOUND_FOR_ID                           = new Message1( DMNMessageType.DECISION_NOT_FOUND, "Decision not found for type '%s'" );
    public static final Message1 FEEL_ERROR                                          = new Message1( DMNMessageType.FEEL_EVALUATION_ERROR, "%s" );
    public static final Message1 FEEL_WARN                                           = new Message1( DMNMessageType.FEEL_EVALUATION_ERROR, "%s" );
    public static final Message3 ERR_EVAL_PARAM_FOR_INVOCATION_ON_NODE               = new Message3( DMNMessageType.FEEL_EVALUATION_ERROR, "Error evaluating parameter '%s' for invocation '%s' on node '%s'" );
    public static final Message2 ERR_EVAL_LIST_ELEMENT_ON_POSITION_ON_LIST           = new Message2( DMNMessageType.FEEL_EVALUATION_ERROR, "Error evaluating list element on position '%s' on list '%s'" );
    public static final Message3 ERR_EVAL_ROW_ELEMENT_ON_POSITION_ON_ROW_OF_RELATION = new Message3( DMNMessageType.FEEL_EVALUATION_ERROR, "Error evaluating row element on position '%s' on row '%s' of relation '%s'" );
    public static final Message2 ERR_INVOKING_PARAM_EXPR_FOR_PARAM_ON_NODE           = new Message2( DMNMessageType.INVOCATION_ERROR, "Error invoking parameter expression for parameter '%s' on node '%s'." );
    public static final Message2 ERR_INVOKING_FUNCTION_ON_NODE                       = new Message2( DMNMessageType.INVOCATION_ERROR, "Error invoking function '%s' on node '%s'" );
    public static final Message0 FAILED_VALIDATOR                                    = new Message0( DMNMessageType.FAILED_VALIDATOR, "The validator was unable to compile the embedded DMN validation rules. Validation of the DMN Model cannot be performed." );
    public static final Message0 FAILED_NO_XML_SOURCE                                = new Message0( DMNMessageType.FAILED_VALIDATOR, "Schema validation not supported for in memory object. Please use the validate method with the file or reader signature." );
    public static final Message1 FAILED_XML_VALIDATION                               = new Message1( DMNMessageType.FAILED_XML_VALIDATION, "Failed XML validation of DMN file: %s" );

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
}
