package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierMessage extends VerifierMessageBase {
    private static final long         serialVersionUID = 9190003495068712452L;

    protected final Collection<Cause> causes;

    public VerifierMessage(Map<String, String> impactedRules,
                           Severity severity,
                           MessageType messageType,
                           Cause faulty,
                           String message,
                           Collection<Cause> causes) {
        super( impactedRules,
               severity,
               messageType,
               faulty,
               message );

        this.causes = causes;
    }

    public VerifierMessage(Map<String, String> impactedRules,
                           Severity severity,
                           MessageType messageType,
                           Cause faulty,
                           String message,
                           Cause cause) {
        super( impactedRules,
               severity,
               messageType,
               faulty,
               message );

        Collection<Cause> causes = new ArrayList<Cause>();
        causes.add( cause );

        this.causes = causes;
    }

    public VerifierMessage(Map<String, String> impactedRules,
                           Severity severity,
                           MessageType messageType,
                           Cause faulty,
                           String message) {
        super( impactedRules,
               severity,
               messageType,
               faulty,
               message );

        this.causes = Collections.emptyList();
    }

    public Collection<Cause> getCauses() {
        return causes;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer( severity.getSingular() );

        str.append( " id = " );
        str.append( id );
        str.append( ":\n" );

        if ( !getImpactedRules().isEmpty() ) {
            str.append( "Impacted rules:\n" );
            for ( String ruleName : getImpactedRules().values() ) {
                str.append( "    -" + ruleName + "\n" );
            }
        }

        if ( faulty != null ) {
            str.append( "faulty : " );
            str.append( faulty );
            str.append( "\n" );
        }

        str.append( message );
        str.append( " \n\tCause trace: \n" );
        str.append( printCauses( 8,
                                 causes ) );

        return str.toString();
    }

    private StringBuffer printCauses(int spaces,
                                     Collection<Cause> causes) {

        StringBuffer buffer = new StringBuffer();

        for ( Cause cause : causes ) {
            for ( int i = 0; i < spaces; i++ ) {
                buffer.append( " " );
            }
            buffer.append( cause.toString() );
            buffer.append( "\n" );

            Collection<Cause> childCauses = cause.getCauses();
            if ( childCauses == null ) {
                System.out.println( cause );
            }
            if ( !childCauses.isEmpty() ) {
                buffer.append( printCauses( spaces * 2,
                                            childCauses ) );
            }
        }

        return buffer;
    }

}
