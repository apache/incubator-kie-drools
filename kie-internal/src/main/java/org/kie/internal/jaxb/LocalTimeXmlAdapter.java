package org.kie.internal.jaxb;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalTimeXmlAdapter extends XmlAdapter<String, LocalTime> {

    private final DateTimeFormatter formatter;

    public LocalTimeXmlAdapter() {
        formatter = new DateTimeFormatterBuilder()
                .appendPattern( "HH:mm:ss" )
                .appendFraction( ChronoField.NANO_OF_SECOND, 0, 9, true )
                .toFormatter();
    }

    @Override
    public LocalTime unmarshal( String localTimeString ) throws Exception {
        if ( localTimeString == null ) {
            return null;
        }
        try {
            return LocalTime.from( formatter.parse( localTimeString ) );
        } catch ( DateTimeException e ) {
            throw new IllegalStateException( "Failed to convert string (" + localTimeString + ") to type ("
                    + LocalTime.class.getName() + ")." );
        }
    }

    @Override
    public String marshal( LocalTime localTimeObject ) throws Exception {
        if ( localTimeObject == null ) {
            return null;
        }
        return formatter.format( localTimeObject );
    }

}
