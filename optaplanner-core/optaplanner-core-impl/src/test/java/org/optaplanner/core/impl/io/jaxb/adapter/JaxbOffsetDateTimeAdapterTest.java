package org.optaplanner.core.impl.io.jaxb.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class JaxbOffsetDateTimeAdapterTest {

    private final JaxbOffsetDateTimeAdapter jaxbOffsetDateTimeAdapter = new JaxbOffsetDateTimeAdapter();

    @Test
    void unmarshall() {
        OffsetDateTime offsetDateTime = jaxbOffsetDateTimeAdapter.unmarshal("2020-01-01T12:00:05.1+02:00");
        assertThat(offsetDateTime).isEqualTo(OffsetDateTime.of(2020, 1, 1, 12, 0, 05, 100000000, ZoneOffset.ofHours(2)));
    }

    @Test
    void nullOrEmpty_shouldUnmarshallAsNull() {
        assertThat(jaxbOffsetDateTimeAdapter.unmarshal(null)).isNull();
    }

    @Test
    void marshall() {
        String offsetDateTimeString =
                jaxbOffsetDateTimeAdapter.marshal(OffsetDateTime.of(2020, 1, 1, 12, 0, 05, 100000000, ZoneOffset.ofHours(2)));
        assertThat(offsetDateTimeString).isEqualTo("2020-01-01T12:00:05.1+02:00");
    }
}
