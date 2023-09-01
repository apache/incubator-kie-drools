package org.kie.internal.jaxb;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateTimeXmlAdapter extends XmlAdapter<String, LocalDateTime> {

    private final DateTimeFormatter formatter;

    public LocalDateTimeXmlAdapter() {
        formatter = new DateTimeFormatterBuilder()
                .appendPattern( "uuuu-MM-dd'T'HH:mm:ss" )
                .appendFraction( ChronoField.NANO_OF_SECOND, 0, 9, true )
                .toFormatter();
    }

    @Override
    public LocalDateTime unmarshal( String localDateTimeString ) throws Exception {
        if ( localDateTimeString == null ) {
            return null;
        }
        try {
            return LocalDateTime.from( formatter.parse( localDateTimeString ) );
        } catch ( DateTimeException e ) {
            throw new IllegalStateException( "Failed to convert string (" + localDateTimeString + ") to type ("
                    + LocalDateTime.class.getName() + ")." );
        }
    }

    @Override
    public String marshal( LocalDateTime localDateTimeObject ) throws Exception {
        if ( localDateTimeObject == null ) {
            return null;
        }
        return formatter.format( localDateTimeObject );
    }

}
