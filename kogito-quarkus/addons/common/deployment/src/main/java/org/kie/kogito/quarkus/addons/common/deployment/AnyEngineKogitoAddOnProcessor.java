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
package org.kie.kogito.quarkus.addons.common.deployment;

import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Produce;
import io.quarkus.deployment.pkg.builditem.ArtifactResultBuildItem;

/**
 * You don't need to point to a particular engine if your add-on fits any set of it.
 * Just by extending this class, it will make any of {@link KogitoCapability#ENGINES} to be presented.
 * <p/>
 * If you need a particular set of {@link KogitoCapability} to be presented, please use {@link RequireCapabilityKogitoAddOnProcessor} instead.
 *
 * @see <a href="https://quarkus.io/guides/capabilities">Quarkus Extension Capabilities</a>
 */
public abstract class AnyEngineKogitoAddOnProcessor {

    /**
     * Verifies if one of the {@link KogitoCapability#ENGINES} are present in the classpath.
     * 
     * @param capabilities
     */
    @BuildStep
    @Produce(ArtifactResultBuildItem.class)
    void verifyCapabilities(final Capabilities capabilities) {
        if (KogitoCapability.ENGINES.stream().noneMatch(kc -> capabilities.isPresent(kc.getCapability()))) {
            throw this.exceptionForEngineNotPresent();
        }
    }

    private IllegalStateException exceptionForEngineNotPresent() {
        final StringBuilder sb = new StringBuilder();
        sb.append("This Kogito Quarkus Add-on requires at least one of the following Kogito Extensions: \n");
        KogitoCapability.ENGINES.forEach(c -> {
            sb.append("\t - ").append(c.getCapability()).append("\n");
            sb.append("\t\t offered by the artifact ")
                    .append(c.getOfferedBy().getGroupId())
                    .append(":")
                    .append(c.getOfferedBy().getArtifactId())
                    .append("\n");
        });
        sb.append("Add one of the above artifacts in your project's pom.xml file");
        return new IllegalStateException(sb.toString());
    }
}
