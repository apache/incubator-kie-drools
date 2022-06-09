package org.optaplanner.core.impl.io.jaxb.adapter;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import javax.xml.bind.annotation.adapters.XmlAdapter;

// TODO: Move the code to the jaxb-ri
public class JaxbOffsetDateTimeAdapter extends XmlAdapter<String, OffsetDateTime> {
    private final DateTimeFormatter formatter;

    public JaxbOffsetDateTimeAdapter() {
        formatter = new DateTimeFormatterBuilder()
                .appendPattern("uuuu-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .appendOffsetId()
                .toFormatter();
    }

    @Override
    public OffsetDateTime unmarshal(String offsetDateTimeString) {
        if (offsetDateTimeString == null) {
            return null;
        }
        try {
            return OffsetDateTime.from(formatter.parse(offsetDateTimeString));
        } catch (DateTimeException e) {
            throw new IllegalStateException("Failed to convert string (" + offsetDateTimeString + ") to type ("
                    + OffsetDateTime.class.getName() + ").");
        }
    }

    @Override
    public String marshal(OffsetDateTime offsetDateTimeObject) {
        if (offsetDateTimeObject == null) {
            return null;
        }
        return formatter.format(offsetDateTimeObject);
    }
}
