package org.drools.verifier.report.components;

/**
 * 
 * @author trikkola
 *
 */
public class ReasonType {

    public static final ReasonType MISSING_VALUE = new ReasonType( "MISSING_VALUE" );
    public static final ReasonType REDUNDANT     = new ReasonType( "REDUNDANT" );
    public static final ReasonType SUBSUMPTANT   = new ReasonType( "SUBSUMPTANT" );
    public static final ReasonType ALWAYS_TRUE   = new ReasonType( "ALWAYS_TRUE" );
    public static final ReasonType ALWAYS_FALSE  = new ReasonType( "ALWAYS_FALSE" );
    public static final ReasonType INCOMPATIBLE  = new ReasonType( "INCOMPATIBLE" );
    public static final ReasonType OPPOSITY      = new ReasonType( "OPPOSITY" );

    public final String            type;

    public ReasonType(String type) {
        this.type = type;
    }
}
