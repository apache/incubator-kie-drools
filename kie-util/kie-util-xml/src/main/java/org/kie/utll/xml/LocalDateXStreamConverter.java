package org.kie.utll.xml;

import java.time.DateTimeException;
import java.time.LocalDate;

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
public class LocalDateXStreamConverter implements Converter {

    @Override
    public void marshal( Object localDateObject, HierarchicalStreamWriter writer, MarshallingContext context ) {
        LocalDate localDate = (LocalDate) localDateObject;
        writer.setValue( localDate.toString() );
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        String localDateString = reader.getValue();
        try {
            return LocalDate.parse( localDateString );
        } catch ( DateTimeException e ) {
            throw new IllegalStateException( "Failed to convert string (" + localDateString + ") to type ("
                    + LocalDate.class.getName() + ")." );
        }
    }

    @Override
    public boolean canConvert( Class type ) {
        return LocalDate.class.isAssignableFrom( type );
    }

}
