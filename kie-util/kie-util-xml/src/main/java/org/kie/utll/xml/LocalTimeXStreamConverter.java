package org.kie.utll.xml;

import java.time.DateTimeException;
import java.time.LocalTime;
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
public class LocalTimeXStreamConverter implements Converter {

    private final DateTimeFormatter formatter;

    public LocalTimeXStreamConverter() {
        formatter = new DateTimeFormatterBuilder()
                .appendPattern( "HH:mm:ss" )
                .appendFraction( ChronoField.NANO_OF_SECOND, 0, 9, true )
                .toFormatter();
    }

    @Override
    public void marshal( Object localTimeObject, HierarchicalStreamWriter writer, MarshallingContext context ) {
        LocalTime localTime = (LocalTime) localTimeObject;
        writer.setValue( formatter.format( localTime ) );
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        String localTimeString = reader.getValue();
        try {
            return LocalTime.from( formatter.parse( localTimeString ) );
        } catch ( DateTimeException e ) {
            throw new IllegalStateException( "Failed to convert string (" + localTimeString + ") to type ("
                    + LocalTime.class.getName() + ")." );
        }
    }

    @Override
    public boolean canConvert( Class type ) {
        return LocalTime.class.isAssignableFrom( type );
    }

}
