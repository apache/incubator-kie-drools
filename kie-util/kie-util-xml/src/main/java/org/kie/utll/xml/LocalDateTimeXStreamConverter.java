package org.kie.utll.xml;

import java.time.DateTimeException;
import java.time.LocalDateTime;
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
public class LocalDateTimeXStreamConverter implements Converter {

    private final DateTimeFormatter formatter;

    public LocalDateTimeXStreamConverter() {
        formatter = new DateTimeFormatterBuilder()
                .appendPattern( "uuuu-MM-dd'T'HH:mm:ss" )
                .appendFraction( ChronoField.NANO_OF_SECOND, 0, 9, true )
                .toFormatter();
    }

    @Override
    public void marshal( Object localDateTimeObject, HierarchicalStreamWriter writer, MarshallingContext context ) {
        LocalDateTime localDateTime = (LocalDateTime) localDateTimeObject;
        writer.setValue( formatter.format( localDateTime ) );
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        String localDateTimeString = reader.getValue();
        try {
            return LocalDateTime.from( formatter.parse( localDateTimeString ) );
        } catch ( DateTimeException e ) {
            throw new IllegalStateException( "Failed to convert string (" + localDateTimeString + ") to type ("
                    + LocalDateTime.class.getName() + ")." );
        }
    }

    @Override
    public boolean canConvert( Class type ) {
        return LocalDateTime.class.isAssignableFrom( type );
    }

}
