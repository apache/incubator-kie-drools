package org.drools.verifier.report.components;

public class CauseType {

    public static final CauseType RULE              = new CauseType( "RULE" );
    public static final CauseType FIELD             = new CauseType( "FIELD" );
    public static final CauseType GAP               = new CauseType( "GAP" );
    public static final CauseType PATTERN           = new CauseType( "PATTERN" );
    public static final CauseType RESTRICTION       = new CauseType( "RESTRICTION" );
    public static final CauseType SUB_PATTERN       = new CauseType( "SUB_PATTERN" );
    public static final CauseType SUB_RULE          = new CauseType( "SUB_RULE" );
    public static final CauseType SOURCE            = new CauseType( "SOURCE" );
    public static final CauseType RANGE_CHECK_CAUSE = new CauseType( "RANGE_CHECK_CAUSE" );
    public static final CauseType REDUNDANCY        = new CauseType( "REDUNDANCY" );
    public static final CauseType EVAL              = new CauseType( "EVAL" );
    public static final CauseType PREDICATE         = new CauseType( "PREDICATE" );
    public static final CauseType CONSTRAINT        = new CauseType( "CONSTRAINT" );
    public static final CauseType CONSEQUENCE       = new CauseType( "CONSEQUENCE" );
    public static final CauseType SUBSUMPTION       = new CauseType( "SUBSUMPTION" );
    public static final CauseType OPPOSITE          = new CauseType( "OPPOSITE" );
    public static final CauseType INCOMPATIBLE      = new CauseType( "INCOMPATIBLE" );
    public static final CauseType ALWAYS_TRUE       = new CauseType( "ALWAYS_TRUE" );

    public final String           type;

    public CauseType(String type) {
        this.type = type;
    }
}
