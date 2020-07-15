/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.io.jaxb;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

public class JaxbDurationAdapterTest {

    private final JaxbDurationAdapter jaxbDurationAdapter = new JaxbDurationAdapter();

    @Test
    public void unmarshall() {
        Duration duration = jaxbDurationAdapter.unmarshal("PT5M120S");
        assertThat(duration).isEqualTo(Duration.ofMinutes(7L));
    }

    @Test
    public void nullOrEmpty_shouldUnmarshallAsNull() {
        assertThat(jaxbDurationAdapter.unmarshal(null)).isNull();
    }

    @Test
    public void marshall() {
        String durationString = jaxbDurationAdapter.marshal(Duration.ofMinutes(5L));
        assertThat(durationString).isEqualTo("PT5M");
    }
}
