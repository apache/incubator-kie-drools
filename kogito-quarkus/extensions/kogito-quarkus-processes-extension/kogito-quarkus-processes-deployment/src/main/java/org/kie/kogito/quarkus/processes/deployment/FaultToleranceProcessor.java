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

package org.kie.kogito.quarkus.processes.deployment;

import java.util.function.BooleanSupplier;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;

import jakarta.interceptor.Interceptor;

import static org.kie.kogito.codegen.api.context.ContextAttributesConstants.KOGITO_FAULT_TOLERANCE_ENABLED;

public class FaultToleranceProcessor {

    public static class IsFaultToleranceEnabled implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return ConfigProvider.getConfig().getOptionalValue(KOGITO_FAULT_TOLERANCE_ENABLED, Boolean.class).orElse(true);
        }
    }

    @BuildStep(onlyIf = IsFaultToleranceEnabled.class)
    public void setupKogitoFaultTolerance(
            BuildProducer<SystemPropertyBuildItem> systemProperties,
            Capabilities capabilities) {

        if (capabilities.isPresent(Capability.SMALLRYE_FAULT_TOLERANCE)) {
            systemProperties.produce(new SystemPropertyBuildItem("mp.fault.tolerance.interceptor.priority", String.valueOf(Interceptor.Priority.PLATFORM_BEFORE + 100)));
        }
    }
}
