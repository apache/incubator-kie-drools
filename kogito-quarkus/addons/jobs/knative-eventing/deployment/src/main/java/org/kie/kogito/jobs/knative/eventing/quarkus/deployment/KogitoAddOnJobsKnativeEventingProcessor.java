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
package org.kie.kogito.jobs.knative.eventing.quarkus.deployment;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.OneOfCapabilityKogitoAddOnProcessor;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

public class KogitoAddOnJobsKnativeEventingProcessor extends OneOfCapabilityKogitoAddOnProcessor {

    static final String FEATURE = "kogito-addon-jobs-knative-eventing-extension";

    KogitoAddOnJobsKnativeEventingProcessor() {
        super(KogitoCapability.PROCESSES);
    }

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public ReflectiveClassBuildItem jobsApiReflection() {
        List<Class<?>> reflectiveClasses = new ArrayList<>();
        reflectiveClasses.addAll(org.kie.kogito.jobs.api.utils.ReflectionUtils.apiReflectiveClasses());
        reflectiveClasses.addAll(org.kie.kogito.jobs.service.api.utils.ReflectionUtils.apiReflectiveClasses());
        return ReflectiveClassBuildItem.builder(reflectiveClasses.toArray(new Class[] {}))
                .constructors()
                .fields()
                .build();
    }
}
