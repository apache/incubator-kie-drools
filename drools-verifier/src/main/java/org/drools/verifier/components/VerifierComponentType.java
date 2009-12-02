package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierComponentType
    implements
    Comparable<VerifierComponentType> {
    public static final VerifierComponentType UNKNOWN                  = new VerifierComponentType( "UNKNOWN" );
    public static final VerifierComponentType FIELD                    = new VerifierComponentType( "FIELD" );
    public static final VerifierComponentType RULE                     = new VerifierComponentType( "RULE" );
    public static final VerifierComponentType CONSTRAINT               = new VerifierComponentType( "CONSTRAINT" );
    public static final VerifierComponentType VARIABLE                 = new VerifierComponentType( "VARIABLE" );
    public static final VerifierComponentType PATTERN                  = new VerifierComponentType( "PATTERN" );
    public static final VerifierComponentType SUB_PATTERN              = new VerifierComponentType( "SUB_PATTERN" );
    public static final VerifierComponentType SUB_RULE                 = new VerifierComponentType( "SUB_RULE" );
    public static final VerifierComponentType RESTRICTION              = new VerifierComponentType( "RESTRICTION" );
    public static final VerifierComponentType OPERATOR                 = new VerifierComponentType( "OPERATOR" );
    public static final VerifierComponentType FIELD_OBJECT_TYPE_LINK   = new VerifierComponentType( "FIELD_CLASS_LINK" );
    public static final VerifierComponentType COLLECT                  = new VerifierComponentType( "COLLECT" );
    public static final VerifierComponentType ACCUMULATE               = new VerifierComponentType( "ACCUMULATE" );
    public static final VerifierComponentType FROM                     = new VerifierComponentType( "FROM" );
    public static final VerifierComponentType EVAL                     = new VerifierComponentType( "EVAL" );
    public static final VerifierComponentType PREDICATE                = new VerifierComponentType( "PREDICATE" );
    public static final VerifierComponentType METHOD_ACCESSOR          = new VerifierComponentType( "METHOD_ACCESSOR" );
    public static final VerifierComponentType FIELD_ACCESSOR           = new VerifierComponentType( "FIELD_ACCESSOR" );
    public static final VerifierComponentType FUNCTION_CALL            = new VerifierComponentType( "FUNCTION_CALL" );
    public static final VerifierComponentType ACCESSOR                 = new VerifierComponentType( "ACCESSOR" );
    public static final VerifierComponentType RULE_PACKAGE             = new VerifierComponentType( "RULE_PACKAGE" );
    public static final VerifierComponentType CONSEQUENCE              = new VerifierComponentType( "CONSEQUENCE" );
    public static final VerifierComponentType OBJECT_TYPE              = new VerifierComponentType( "OBJECT_TYPE" );
    public static final VerifierComponentType INLINE_EVAL_DESCR        = new VerifierComponentType( "INLINE_EVAL_DESCR" );
    public static final VerifierComponentType RETURN_VALUE_FIELD_DESCR = new VerifierComponentType( "RETURN_VALUE_FIELD_DESCR" );
    public static final VerifierComponentType ENTRY_POINT_DESCR        = new VerifierComponentType( "ENTRY_POINT_DESCR" );
    public static final VerifierComponentType WORKING_MEMORY           = new VerifierComponentType( "WORKING_MEMORY" );

    private final String                      type;

    public VerifierComponentType(String t) {
        type = t;
    }

    public int compareTo(VerifierComponentType another) {
        return getType().compareTo( another.getType() );
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return "VerifierComponentType." + type;
    }
}
