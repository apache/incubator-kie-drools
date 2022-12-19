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
package org.kie.kogito.quarkus.addons.common.deployment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.quarkus.deployment.Capabilities;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KogitoAddOnProcessorTest {

    @Test
    void verifyRequiredCapabilitiesWhenNotPresent() {
        final RequireEngineAddonProcessor requireEngineAddonProcessor = new RequireEngineAddonProcessor();
        final Capabilities capabilities = new Capabilities(Collections.emptySet());
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> requireEngineAddonProcessor.verifyCapabilities(capabilities));
        assertTrue(exception.getMessage().contains(KogitoCapability.DECISIONS.getCapability()));
        assertTrue(exception.getMessage().contains(KogitoCapability.PREDICTIONS.getCapability()));
    }

    @Test
    void verifyAtLeastOneRequiredCapabilitiesWhenNotPresent() {
        final RequireOneEngineAddonProcessor requireEngineAddonProcessor = new RequireOneEngineAddonProcessor();
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class,
                        () -> requireEngineAddonProcessor.verifyCapabilities(new Capabilities(Collections.emptySet())));
        assertTrue(exception.getMessage().contains(KogitoCapability.SERVERLESS_WORKFLOW.getCapability()));
    }

    @Test
    void verifyAnyEngineRequiredButNotPresent() {
        final AnyEngineAddonProcessorImpl anyEngineAddonProcessorImpl = new AnyEngineAddonProcessorImpl();
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class,
                        () -> anyEngineAddonProcessorImpl.verifyCapabilities(new Capabilities(Collections.emptySet())));
        assertTrue(exception.getMessage().contains(KogitoCapability.DECISIONS.getCapability()));
        assertTrue(exception.getMessage().contains(KogitoCapability.PREDICTIONS.getCapability()));
        assertTrue(exception.getMessage().contains(KogitoCapability.PROCESSES.getCapability()));
        assertTrue(exception.getMessage().contains(KogitoCapability.SERVERLESS_WORKFLOW.getCapability()));
        assertTrue(exception.getMessage().contains(KogitoCapability.RULES.getCapability()));
    }

    @Test
    void verifyRequiredCapabilitiesWhenPresent() {
        final RequireEngineAddonProcessor requireEngineAddonProcessor = new RequireEngineAddonProcessor();
        final Set<String> capabilities = new HashSet<>();
        capabilities.add(KogitoCapability.DECISIONS.getCapability());
        capabilities.add(KogitoCapability.PREDICTIONS.getCapability());
        assertDoesNotThrow(() -> requireEngineAddonProcessor.verifyCapabilities(new Capabilities(capabilities)));
    }

    @Test
    void verifyOneRequiredCapabilitiesWhenPresent() {
        final RequireOneEngineAddonProcessor requireEngineAddonProcessor = new RequireOneEngineAddonProcessor();
        final Set<String> capabilities = new HashSet<>();
        capabilities.add(KogitoCapability.SERVERLESS_WORKFLOW.getCapability());
        assertDoesNotThrow(() -> requireEngineAddonProcessor.verifyCapabilities(new Capabilities(capabilities)));
    }

}
