/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.event.correlation;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.correlation.SimpleCorrelation;

import static org.assertj.core.api.Assertions.assertThat;

class MD5CorrelationEncoderTest {

    private MD5CorrelationEncoder encoder = new MD5CorrelationEncoder();

    @Test
    public void testEncodeWithSimpleCorrelation() {
        Correlation<String> simpleCorrelation = new SimpleCorrelation<>("aaaa", "bbbb");
        String encode = encoder.encode(simpleCorrelation);
        assertThat(encode).isEqualTo("c818ccd6be2b10823eb7208d162879d0");//md5(aaaa|bbbb)
    }

    @Test
    public void testEncodeWithCompositeCorrelation() {
        Correlation<String> correlation1 = new SimpleCorrelation<>("aaaa", "bbbb");
        Correlation<String> correlation2 = new SimpleCorrelation<>("cccc", "dddd");
        Correlation<String> correlation3 = new SimpleCorrelation<>("eeee", "ffff");
        String encode = encoder.encode(new CompositeCorrelation(Set.of(correlation1, correlation2, correlation3)));
        assertThat(encode).isEqualTo("fc5428e42b043a7089d7a15d65558ca2");//md5(aaaa|bbbb|cccc|dddd|eeee|ffff)
    }
}
