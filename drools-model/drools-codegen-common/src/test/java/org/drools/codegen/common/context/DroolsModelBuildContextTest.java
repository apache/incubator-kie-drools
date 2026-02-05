/*
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
package org.drools.codegen.common.context;

import java.util.Properties;

import org.drools.codegen.common.DroolsModelBuildContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DroolsModelBuildContextTest {

    @Test
    void quarkusHasRestDefaultTrue() {
        DroolsModelBuildContext context = QuarkusDroolsModelBuildContext.builder()
                .withClassAvailabilityResolver(className -> true)
                .build();
        assertThat(context.hasRest()).isTrue();
    }

    @Test
    void quarkusHasRestExplicitTrue() {
        Properties props = new Properties();
        props.setProperty(DroolsModelBuildContext.KOGITO_GENERATE_REST, "true");
        DroolsModelBuildContext context = QuarkusDroolsModelBuildContext.builder()
                .withClassAvailabilityResolver(className -> true)
                .withApplicationProperties(props)
                .build();
        assertThat(context.hasRest()).isTrue();
    }

    @Test
    void quarkusHasRestDisabledByProperty() {
        Properties props = new Properties();
        props.setProperty(DroolsModelBuildContext.KOGITO_GENERATE_REST, "false");
        DroolsModelBuildContext context = QuarkusDroolsModelBuildContext.builder()
                .withClassAvailabilityResolver(className -> true)
                .withApplicationProperties(props)
                .build();
        assertThat(context.hasRest()).isFalse();
    }

    @Test
    void quarkusHasRestClassNotAvailable() {
        DroolsModelBuildContext context = QuarkusDroolsModelBuildContext.builder()
                .withClassAvailabilityResolver(className -> false)
                .build();
        assertThat(context.hasRest()).isFalse();
    }

    @Test
    void springBootHasRestDefaultTrue() {
        DroolsModelBuildContext context = SpringBootDroolsModelBuildContext.builder()
                .withClassAvailabilityResolver(className -> true)
                .build();
        assertThat(context.hasRest()).isTrue();
    }

    @Test
    void springBootHasRestExplicitTrue() {
        Properties props = new Properties();
        props.setProperty(DroolsModelBuildContext.KOGITO_GENERATE_REST, "true");
        DroolsModelBuildContext context = SpringBootDroolsModelBuildContext.builder()
                .withClassAvailabilityResolver(className -> true)
                .withApplicationProperties(props)
                .build();
        assertThat(context.hasRest()).isTrue();
    }

    @Test
    void springBootHasRestDisabledByProperty() {
        Properties props = new Properties();
        props.setProperty(DroolsModelBuildContext.KOGITO_GENERATE_REST, "false");
        DroolsModelBuildContext context = SpringBootDroolsModelBuildContext.builder()
                .withClassAvailabilityResolver(className -> true)
                .withApplicationProperties(props)
                .build();
        assertThat(context.hasRest()).isFalse();
    }

    @Test
    void springBootHasRestClassNotAvailable() {
        DroolsModelBuildContext context = SpringBootDroolsModelBuildContext.builder()
                .withClassAvailabilityResolver(className -> false)
                .build();
        assertThat(context.hasRest()).isFalse();
    }
}
