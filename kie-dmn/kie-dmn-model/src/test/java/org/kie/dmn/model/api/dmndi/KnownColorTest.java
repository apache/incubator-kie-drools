/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.model.api.dmndi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class KnownColorTest {

    @Test
    void fromValue() {
        assertThat(KnownColor.fromValue("maroon")).isEqualTo(KnownColor.MAROON).hasFieldOrPropertyWithValue("value", "maroon");
        assertThat(KnownColor.fromValue("red")).isEqualTo(KnownColor.RED);
        assertThat(KnownColor.fromValue("orange")).isEqualTo(KnownColor.ORANGE);
        assertThat(KnownColor.fromValue("yellow")).isEqualTo(KnownColor.YELLOW);
        assertThat(KnownColor.fromValue("olive")).isEqualTo(KnownColor.OLIVE);
        assertThat(KnownColor.fromValue("purple")).isEqualTo(KnownColor.PURPLE);
        assertThat(KnownColor.fromValue("fuchsia")).isEqualTo(KnownColor.FUCHSIA);
        assertThat(KnownColor.fromValue("white")).isEqualTo(KnownColor.WHITE);
        assertThat(KnownColor.fromValue("lime")).isEqualTo(KnownColor.LIME);
        assertThat(KnownColor.fromValue("green")).isEqualTo(KnownColor.GREEN);
        assertThat(KnownColor.fromValue("navy")).isEqualTo(KnownColor.NAVY);
        assertThat(KnownColor.fromValue("blue")).isEqualTo(KnownColor.BLUE);
        assertThat(KnownColor.fromValue("aqua")).isEqualTo(KnownColor.AQUA);
        assertThat(KnownColor.fromValue("teal")).isEqualTo(KnownColor.TEAL);
        assertThat(KnownColor.fromValue("black")).isEqualTo(KnownColor.BLACK);
        assertThat(KnownColor.fromValue("silver")).isEqualTo(KnownColor.SILVER);
        assertThat(KnownColor.fromValue("gray")).isEqualTo(KnownColor.GRAY);
        assertThatIllegalArgumentException().isThrownBy(() -> KnownColor.fromValue("asd"));
    }
}
