package org.kie.internal.jaxb;

import java.time.DateTimeException;
import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal( String localDateString ) throws Exception {
        if ( localDateString == null ) {
            return null;
        }
        try {
            return LocalDate.parse( localDateString );
        } catch ( DateTimeException e ) {
            throw new IllegalStateException( "Failed to convert string (" + localDateString + ") to type ("
                    + LocalDate.class.getName() + ")." );
        }
    }

    @Override
    public String marshal( LocalDate localDateObject ) throws Exception {
        if ( localDateObject == null ) {
            return null;
        }
        return localDateObject.toString();
    }

}
