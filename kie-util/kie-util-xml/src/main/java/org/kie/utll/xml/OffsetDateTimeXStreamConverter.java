package org.kie.utll.xml;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * TODO Remove when java.time converters are provided by XStream out of the box.
 *
 * @see <a href="https://github.com/x-stream/xstream/issues/75">XStream#75</a>
 */
public class OffsetDateTimeXStreamConverter implements Converter {

    private final DateTimeFormatter formatter;

    public OffsetDateTimeXStreamConverter() {
        formatter = new DateTimeFormatterBuilder()
                .appendPattern( "uuuu-MM-dd'T'HH:mm:ss" )
                .appendFraction( ChronoField.NANO_OF_SECOND, 0, 9, true )
                .appendOffsetId()
                .toFormatter();
    }

    @Override
    public void marshal( Object localTimeObject, HierarchicalStreamWriter writer, MarshallingContext context ) {
        OffsetDateTime offsetDateTime = (OffsetDateTime) localTimeObject;
        writer.setValue( formatter.format( offsetDateTime ) );
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        String offsetDateTimeString = reader.getValue();
        try {
            return OffsetDateTime.from( formatter.parse( offsetDateTimeString ) );
        } catch ( DateTimeException e ) {
            throw new IllegalStateException( "Failed to convert string (" + offsetDateTimeString + ") to type ("
                    + OffsetDateTime.class.getName() + ")." );
        }
    }

    @Override
    public boolean canConvert( Class type ) {
        return OffsetDateTime.class.isAssignableFrom( type );
    }

}
