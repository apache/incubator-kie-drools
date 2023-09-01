package org.kie.internal.jaxb;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OffsetDateTimeXmlAdapter extends XmlAdapter<String, OffsetDateTime> {

    private final DateTimeFormatter formatter;

    public OffsetDateTimeXmlAdapter() {
        formatter = new DateTimeFormatterBuilder()
                .appendPattern("uuuu-MM-dd'T'HH:mm:ss")
                .appendFraction( ChronoField.NANO_OF_SECOND, 0, 9, true)
                .appendOffsetId()
                .toFormatter();
    }

    @Override
    public OffsetDateTime unmarshal( String offsetDateTimeString ) throws Exception {
        if ( offsetDateTimeString == null ) {
            return null;
        }
        try {
            return OffsetDateTime.from( formatter.parse( offsetDateTimeString ) );
        } catch ( DateTimeException e ) {
            throw new IllegalStateException( "Failed to convert string (" + offsetDateTimeString + ") to type ("
                    + OffsetDateTime.class.getName() + ")." );
        }
    }

    @Override
    public String marshal( OffsetDateTime offsetDateTimeObject ) throws Exception {
        if ( offsetDateTimeObject == null ) {
            return null;
        }
        return formatter.format( offsetDateTimeObject );
    }

}
