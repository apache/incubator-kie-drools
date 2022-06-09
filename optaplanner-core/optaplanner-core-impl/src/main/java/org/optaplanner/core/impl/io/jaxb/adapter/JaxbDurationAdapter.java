package org.optaplanner.core.impl.io.jaxb.adapter;

import java.time.Duration;

import javax.xml.bind.annotation.adapters.XmlAdapter;

// TODO: Move the code to the jaxb-ri
public class JaxbDurationAdapter extends XmlAdapter<String, Duration> {

    @Override
    public Duration unmarshal(String durationString) {
        if (durationString == null) {
            return null;
        }
        return Duration.parse(durationString);
    }

    @Override
    public String marshal(Duration duration) {
        if (duration == null) {
            return null;
        }
        return duration.toString();
    }
}
