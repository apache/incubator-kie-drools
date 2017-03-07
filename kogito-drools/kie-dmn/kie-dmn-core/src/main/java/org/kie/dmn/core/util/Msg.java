package org.kie.dmn.core.util;

import org.kie.dmn.core.impl.DMNMessageTypeId;
import org.kie.dmn.core.impl.DMNMessageTypeImpl;
    
public class Msg {
    public static final Message2 ELEMENT_WITH_ID_NOT_SUPPORTED = new Message2(DMNMessageTypeId.ELEMENT_WITH_ID_NOT_SUPPORTED, "Element %s with id='%s' not supported.");
    public static final Message2 REQ_INPUT_NOT_FOUND_FOR_NODE = new Message2(DMNMessageTypeId.REQ_NOT_FOUND, "Required input '%s' not found for node '%s'");
    public static final Message2 REQ_DECISION_NOT_FOUND_FOR_NODE = new Message2(DMNMessageTypeId.REQ_NOT_FOUND, "Required Decision '%s' not found for node '%s'");
    public static final Message2 REQ_BKM_NOT_FOUND_FOR_NODE = new Message2(DMNMessageTypeId.REQ_NOT_FOUND, "Required Business Knowledge Model '%s' not found for node '%s'");
    public static final Message2 UNKNOWN_TYPE_REF_ON_NODE = new Message2(DMNMessageTypeId.NO_TYPE_REF_FOUND, "Unknown type reference '%s' on node '%s'");
    public static final Message2 NO_TYPE_DEF_FOUND_FOR_NODE = new Message2(DMNMessageTypeId.NO_TYPE_DEF_FOUND, "No '%s' type definition found for node '%s'");
    public static final Message3 NO_TYPE_DEF_FOUND_FOR_ELEMENT_ON_NODE = new Message3(DMNMessageTypeId.NO_TYPE_DEF_FOUND, "No '%s' type definition found for element '%s' on node '%s'");
    public static final Message2 INVALID_NAME_VARIABLENAME = new Message2(DMNMessageTypeId.INVALID_NAME, "Invalid name '%s': %s");
    public static final Message1 INVALID_SYNTAX = new Message1(DMNMessageTypeId.INVALID_SYNTAX, "%s: invalid syntax");
    public static final Message2 INVALID_SYNTAX2 = new Message2(DMNMessageTypeId.INVALID_SYNTAX, "%s: %s");
    public static final Message1 DECISION_NOT_FOUND_FOR_NAME = new Message1(DMNMessageTypeId.DECISION_NOT_FOUND, "Decision not found for name '%s'");
    public static final Message1 DECISION_NOT_FOUND_FOR_ID = new Message1(DMNMessageTypeId.DECISION_NOT_FOUND, "Decision not found for id '%s'");
    public static final Message1 MISSING_EXPRESSION_FOR_BKM_NODE_SKIP_EVAL = new Message1(DMNMessageTypeId.MISSING_EXPRESSION, "Missing expression for Business Knowledge Model node '%s'. Skipping evaluation.");
    public static final Message2 MISSING_DEP_FOR_BKM = new Message2(DMNMessageTypeId.MISSING_DEP, "Missing dependency for Business Knowledge Model node '%s': dependency='%s'");
    public static final Message2 ERROR_EVAL_BKM_NODE = new Message2(DMNMessageTypeId.ERROR_EVAL_NODE, "Error evaluating Business Knowledge Model node '%s': %s" );
    public static final Message1 MISSING_EXPRESSION_FOR_DECISION_NODE_SKIP_EVAL = new Message1(DMNMessageTypeId.MISSING_EXPRESSION, "Missing expression for Decision node '%s'. Skipping evaluation.");
    public static final Message2 MISSING_DEP_FOR_DECISION = new Message2(DMNMessageTypeId.MISSING_DEP, "Missing dependency for Decision node '%s': dependency='%s'");
    public static final Message2 ERROR_EVAL_DECISION_NODE = new Message2(DMNMessageTypeId.ERROR_EVAL_NODE, "Error evaluating Decision node '%s': %s" );
    public static final Message2 UNABLE_TO_EVALUATE_DECISION_AS_IT_DEPS = new Message2(DMNMessageTypeId.MISSING_DEP, "Unable to evaluate decision '%s' as it depends on decision '%s'");
    public static final Message2 EXPR_TYPE_NOT_SUPPORTED_IN_NODE = new Message2(DMNMessageTypeId.EXPR_TYPE_NOT_SUPPORTED_IN_NODE, "Expression type '%s' not supported in node '%s'");
    public static final Message1 NO_EXPR_DEF_FOR_NODE = new Message1(DMNMessageTypeId.NO_EXPR_DEF_FOR_NODE, "No expression defined for node '%s'");
    public static final Message2 NO_EXPR_DEF_FOR_NAME_ON_NODE = new Message2(DMNMessageTypeId.NO_EXPR_DEF_FOR_NODE, "No expression defined for name '%s' node '%s'");
    public static final Message3 ERR_COMPILING_FEEL_EXPR_ON_DT_INPUT_CLAUSE_IDX = new Message3(DMNMessageTypeId.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s', input clause #%s");
    public static final Message3 ERR_COMPILING_FEEL_EXPR_ON_DT_OUTPUT_CLAUSE_IDX = new Message3(DMNMessageTypeId.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s', output clause #%s");
    public static final Message3 ERR_COMPILING_FEEL_EXPR_ON_DT_RULE_IDX = new Message3(DMNMessageTypeId.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s', rule #%s");
    public static final Message2 ERR_COMPILING_FEEL_EXPR_ON_DT = new Message2(DMNMessageTypeId.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' on decision table '%s'");
    public static final Message2 ERR_COMPILING_ALLOWED_VALUES_LIST_ON_ITEM_DEF = new Message2(DMNMessageTypeId.ERR_COMPILING_FEEL, "Error compiling allowed values list '%s' on item definition '%s'");
    public static final Message3 ERR_COMPILING_FEEL_EXPR_FOR_NAME_ON_NODE = new Message3(DMNMessageTypeId.ERR_COMPILING_FEEL, "Error compiling FEEL expression '%s' for name '%s' on node '%s'");
    public static final Message2 ERR_EVAL_CTX_ENTRY_ON_CTX = new Message2(DMNMessageTypeId.ERR_EVAL_CTX, "Error evaluating context extry '%s' on context '%s'");
    public static final Message1 FEEL_ERROR = new Message1(DMNMessageTypeId.FEEL_PROBLEM, "%s");
    public static final Message1 FEEL_WARN = new Message1(DMNMessageTypeId.FEEL_PROBLEM, "%s");
    public static final Message2 FUNCTION_NOT_FOUND_INVOCATION_FAILED_ON_NODE = new Message2(DMNMessageTypeId.MISSING_DEP, "Function '%s' not found. Invocation failed on node '%s'");
    public static final Message3 ERR_EVAL_PARAM_FOR_INVOCATION_ON_NODE = new Message3(DMNMessageTypeId.ERR_EVAL, "Error evaluating parameter '%s' for invocation '%s' on node '%s'");
    public static final Message2 ERR_INVOKING_PARAM_EXPR_FOR_PARAM_ON_NODE = new Message2(DMNMessageTypeId.ERR_INVOKE, "Error invoking parameter expression for parameter '%s' on node '%s'.");
    public static final Message2 ERR_INVOKING_FUNCTION_ON_NODE = new Message2(DMNMessageTypeId.ERR_INVOKE, "Error invoking function '%s' on node '%s'");
    public static final Message2 ERR_EVAL_LIST_ELEMENT_ON_POSITION_ON_LIST = new Message2(DMNMessageTypeId.ERR_EVAL, "Error evaluating list element on position '%s' on list '%s'");
    public static final Message3 ERR_EVAL_ROW_ELEMENT_ON_POSITION_ON_ROW_OF_RELATION = new Message3(DMNMessageTypeId.ERR_EVAL, "Error evaluating row element on position '%s' on row '%s' of relation '%s'");
    
    public static DMNMessageTypeImpl createMessage( Message0 message) {
        return Msg.buildMessage(message);
    }
    public static DMNMessageTypeImpl createMessage( Message1 message, Object p1) {
        return Msg.buildMessage(message, p1);
    }
    public static DMNMessageTypeImpl createMessage( Message2 message, Object p1, Object p2) {
        return Msg.buildMessage(message, p1, p2);
    }
    public static DMNMessageTypeImpl createMessage( Message3 message, Object p1, Object p2, Object p3) {
        return Msg.buildMessage(message, p1, p2, p3);
    }
    public static DMNMessageTypeImpl createMessage( Message4 message, Object p1, Object p2, Object p3, Object p4) {
        return Msg.buildMessage(message, p1, p2, p3, p4);
    }
    
    private static DMNMessageTypeImpl buildMessage( Message message, Object... params ) {
        return new DMNMessageTypeImpl(String.format( message.getMask(), params ), message.getId());
    }
    
    public static interface Message {
        String getMask();
        DMNMessageTypeId getId();
    }
    public abstract static class AbstractMessage implements Message {
        private final String mask;
        private final DMNMessageTypeId id;
        public AbstractMessage(DMNMessageTypeId id, String mask) {
            this.id = id;
            this.mask = mask;
        }
        public String getMask() {
            return this.mask;
        }
        public DMNMessageTypeId getId() {
            return id;
        }
    }
    public static class Message0 extends AbstractMessage {
        public Message0(DMNMessageTypeId id, String mask) {
            super(id, mask);
        }
    }
    public static class Message1 extends AbstractMessage {
        public Message1(DMNMessageTypeId id, String mask) {
            super(id, mask);
        }
    }
    public static class Message2 extends AbstractMessage {
        public Message2(DMNMessageTypeId id, String mask) {
            super(id, mask);
        }
    }
    public static class Message3 extends AbstractMessage {
        public Message3(DMNMessageTypeId id, String mask) {
            super(id, mask);
        }
    }
    public static class Message4 extends AbstractMessage {
        public Message4(DMNMessageTypeId id, String mask) {
            super(id, mask);
        }
    }
}
