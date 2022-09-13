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
package org.kie.kogito.quarkus.rules.deployment;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.kie.drl.engine.runtime.mapinput.service.KieRuntimeServiceDrlMapInput;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

/**
 * Main class of the Kogito rules extension
 */
public class RulesAssetsProcessor {

    private static final Logger LOGGER = Logger.getLogger(RulesAssetsProcessor.class);

    @BuildStep
    FeatureBuildItem featureBuildItem() {
        return new FeatureBuildItem("kogito-rules");
    }

    @BuildStep
    public List<ReflectiveClassBuildItem> reflectiveEfestoRules() {
        LOGGER.debug("reflectiveEfestoRules()");
        final List<ReflectiveClassBuildItem> toReturn = new ArrayList<>();
        toReturn.add(new ReflectiveClassBuildItem(true, true, KieRuntimeServiceDrlMapInput.class));
        LOGGER.debugf("toReturn {}", toReturn.size());
        return toReturn;
    }

}
