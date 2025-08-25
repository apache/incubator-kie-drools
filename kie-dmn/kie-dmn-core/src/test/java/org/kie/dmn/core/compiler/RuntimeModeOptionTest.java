/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.compiler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeModeOptionTest {

    @Test
    void getRuntimeModeOption() {
        RuntimeModeOption runtimeModeOption = new RuntimeModeOption("strict");
        assertThat(runtimeModeOption).isNotNull();
        assertThat(runtimeModeOption.getRuntimeMode()).isEqualTo(RuntimeModeOption.MODE.STRICT);
        runtimeModeOption = new RuntimeModeOption("lenient");
        assertThat(runtimeModeOption).isNotNull();
        assertThat(runtimeModeOption.getRuntimeMode()).isEqualTo(RuntimeModeOption.MODE.LENIENT);
        runtimeModeOption = new RuntimeModeOption("test");
        assertThat(runtimeModeOption).isNotNull();
        assertThat(runtimeModeOption.getRuntimeMode()).isEqualTo(RuntimeModeOption.MODE.LENIENT);

    }

    @Test
    void getModeFromString() {
        String modeName = "strict";
        assertThat(RuntimeModeOption.MODE.getModeFromString(modeName)).isEqualTo(RuntimeModeOption.MODE.STRICT);
        modeName = "lenient";
        assertThat(RuntimeModeOption.MODE.getModeFromString(modeName)).isEqualTo(RuntimeModeOption.MODE.LENIENT);
        modeName = "test";
        assertThat(RuntimeModeOption.MODE.getModeFromString(modeName)).isEqualTo(RuntimeModeOption.MODE.LENIENT);

    }
}