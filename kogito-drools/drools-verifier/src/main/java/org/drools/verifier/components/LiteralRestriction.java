package org.drools.verifier.components;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.drools.verifier.report.components.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public abstract class LiteralRestriction extends Restriction
    implements
    Cause {

    public LiteralRestriction(Pattern pattern) {
        super( pattern );
    }

    public RestrictionType getRestrictionType() {
        return Restriction.RestrictionType.LITERAL;
    }

    public abstract String getValueAsString();

    public abstract String getValueType();

    public static LiteralRestriction createRestriction(Pattern pattern,
                                                       String value) {

        if ( value == null ) {
            return new StringRestriction( pattern );
        }

        if ( "true".equals( value ) || "false".equals( value ) ) {
            BooleanRestriction booleanRestriction = new BooleanRestriction( pattern );
            booleanRestriction.setValue( value.equals( "true" ) );

            return booleanRestriction;
        }

        try {
            NumberRestriction numberRestriction = new NumberRestriction( pattern );
            numberRestriction.setValue( Integer.parseInt( value ) );
            return numberRestriction;
        } catch ( NumberFormatException e ) {
            // Not int.
        }

        try {
            NumberRestriction numberRestriction = new NumberRestriction( pattern );
            numberRestriction.setValue( Double.parseDouble( value ) );
            return numberRestriction;
        } catch ( NumberFormatException e ) {
            // Not double.
        }

        try {
            // TODO: Get this from config.
            String fmt = System.getProperty( "drools.dateformat" );
            if ( fmt == null ) {
                fmt = "dd-MMM-yyyy";
            }

            DateRestriction dateRestriction = new DateRestriction( pattern );
            dateRestriction.setValue( new SimpleDateFormat( fmt,
                                                            Locale.ENGLISH ).parse( value ) );

            return dateRestriction;
        } catch ( Exception e ) {
            // Not a date.
        }

        StringRestriction stringRestriction = new StringRestriction( pattern );
        stringRestriction.setValue( value );
        return stringRestriction;
    }

    @Override
    public String toString() {
        return "LiteralRestriction from rule [" + getRuleName() + "] value '" + operator.getOperatorString() + " " + getValueAsString() + "'";
    }
}
