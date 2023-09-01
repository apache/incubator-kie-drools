package org.drools.verifier.report.components;

public class MessageType {
    public static final MessageType NOT_SPECIFIED     = new MessageType( "NOT_SPECIFIED" );
    public static final MessageType RANGE_CHECK       = new MessageType( "RANGE_CHECK" );
    public static final MessageType MISSING_EQUALITY  = new MessageType( "MISSING_EQUALITY" );
    public static final MessageType REDUNDANCY        = new MessageType( "REDUNDANCY" );
    public static final MessageType SUBSUMPTION       = new MessageType( "SUBSUMPTION" );
    public static final MessageType MISSING_COMPONENT = new MessageType( "MISSING_COMPONENT" );
    public static final MessageType OPTIMISATION      = new MessageType( "OPTIMISATION" );
    public static final MessageType INCOHERENCE       = new MessageType( "INCOHERENCE" );
    public static final MessageType OVERLAP           = new MessageType( "OVERLAP" );
    public static final MessageType ALWAYS_FALSE      = new MessageType( "ALWAYS_FALSE" );
    public static final MessageType ALWAYS_TRUE       = new MessageType( "ALWAYS_TRUE" );
    public static final MessageType EQUIVALANCE       = new MessageType( "EQUIVALANCE" );

    public final String             type;

    public MessageType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
