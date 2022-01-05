/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quarkus.common.deployment;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;

import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KogitoAssetsProcessorTest {

    @Test
    public void validateAvailableCapabilitiesWithOptaPlannerNoRest() {
        KogitoBuildContext context = QuarkusKogitoBuildContext.builder().build();
        Capabilities capabilities = capabilities("org.optaplanner.optaplanner-quarkus");
        KogitoAssetsProcessor processor = new KogitoAssetsProcessor();

        assertThat(context.getApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST)).isEmpty();
        processor.validateAvailableCapabilities(context, capabilities);
        assertThat(context.getApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST)).contains("false");
    }

    @Test
    public void validateAvailableCapabilitiesWithOptaPlannerWithRest() {
        KogitoBuildContext context = QuarkusKogitoBuildContext.builder().build();
        Capabilities capabilities = capabilities(Capability.RESTEASY, Capability.RESTEASY_JSON_JACKSON, "org.optaplanner.optaplanner-quarkus");
        KogitoAssetsProcessor processor = new KogitoAssetsProcessor();

        assertThat(context.getApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST)).isEmpty();
        processor.validateAvailableCapabilities(context, capabilities);
        assertThat(context.getApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST)).isEmpty();
    }

    @Test
    public void validateAvailableCapabilitiesWithoutOptaPlanner() {
        KogitoBuildContext context = QuarkusKogitoBuildContext.builder().build();
        Capabilities capabilities = capabilities(Capability.RESTEASY, Capability.RESTEASY_JSON_JACKSON);
        KogitoAssetsProcessor processor = new KogitoAssetsProcessor();

        assertThat(context.getApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST)).isEmpty();
        processor.validateAvailableCapabilities(context, capabilities);
        assertThat(context.getApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST)).isEmpty();
    }

    @Test
    public void validateAvailableCapabilitiesRest() {
        KogitoBuildContext context = QuarkusKogitoBuildContext.builder().build();
        KogitoAssetsProcessor processor = new KogitoAssetsProcessor();

        assertThat(context.getApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST)).isEmpty();
        assertThatThrownBy(() -> processor.validateAvailableCapabilities(context, capabilities()))
                .isInstanceOf(MissingRestCapabilityException.class);

        assertThatThrownBy(() -> processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY)))
                .isInstanceOf(MissingRestCapabilityException.class);

        assertThatThrownBy(() -> processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY_JSON_JACKSON)))
                .isInstanceOf(MissingRestCapabilityException.class);

        assertThatThrownBy(() -> processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY_REACTIVE)))
                .isInstanceOf(MissingRestCapabilityException.class);

        assertThatThrownBy(() -> processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY_REACTIVE_JSON_JACKSON)))
                .isInstanceOf(MissingRestCapabilityException.class);

        processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY, Capability.RESTEASY_JSON_JACKSON));
        processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY_REACTIVE, Capability.RESTEASY_REACTIVE_JSON_JACKSON));

        assertThat(context.getApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST)).isEmpty();
    }

    @Test
    public void validateAvailableCapabilitiesRestDisabled() {
        KogitoBuildContext context = QuarkusKogitoBuildContext.builder().build();
        context.setApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST, "false");
        KogitoAssetsProcessor processor = new KogitoAssetsProcessor();

        processor.validateAvailableCapabilities(context, capabilities());
        processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY));
        processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY_JSON_JACKSON));
        processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY, Capability.RESTEASY_JSON_JACKSON));
        processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY_REACTIVE));
        processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY_REACTIVE_JSON_JACKSON));
        processor.validateAvailableCapabilities(context, capabilities(Capability.RESTEASY_REACTIVE, Capability.RESTEASY_REACTIVE_JSON_JACKSON));

        assertThat(context.getApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST)).contains("false");
    }

    private Capabilities capabilities(String... values) {
        return new Capabilities(Set.of(values));
    }
}
