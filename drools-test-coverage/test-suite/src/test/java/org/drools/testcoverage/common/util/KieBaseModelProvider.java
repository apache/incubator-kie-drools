/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common.util;

import java.util.Optional;

import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.KieBaseOption;

/**
 * Basic provider class for KieBaseModel instances.
 */
public interface KieBaseModelProvider {
    KieBaseModel getKieBaseModel(KieModuleModel kieModuleModel);
    KieBaseConfiguration getKieBaseConfiguration();
    void setAdditionalKieBaseOptions(KieBaseOption... options);
    boolean isIdentity();
    boolean isStreamMode();
    Optional<Class<? extends KieBuilder.ProjectType>> getExecutableModelProjectClass();
    boolean useAlphaNetworkCompiler();
}
