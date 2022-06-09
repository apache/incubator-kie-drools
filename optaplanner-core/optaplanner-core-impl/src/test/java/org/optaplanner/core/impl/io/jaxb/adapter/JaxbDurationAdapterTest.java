package org.optaplanner.core.impl.io.jaxb.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class JaxbDurationAdapterTest {

    private final JaxbDurationAdapter jaxbDurationAdapter = new JaxbDurationAdapter();

    @Test
    void unmarshall() {
        Duration duration = jaxbDurationAdapter.unmarshal("PT5M120S");
        assertThat(duration).isEqualTo(Duration.ofMinutes(7L));
    }

    @Test
    void nullOrEmpty_shouldUnmarshallAsNull() {
        assertThat(jaxbDurationAdapter.unmarshal(null)).isNull();
    }

    @Test
    void marshall() {
        String durationString = jaxbDurationAdapter.marshal(Duration.ofMinutes(5L));
        assertThat(durationString).isEqualTo("PT5M");
    }
}
