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
package org.kie.kogito.quarkus.common.deployment;

import org.kie.internal.services.KieRuntimesImpl;
import org.kie.internal.services.KieWeaversImpl;
import org.kie.kogito.quarkus.config.KogitoSmallryeConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.StaticInitConfigBuilderBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

public class KogitoCommonProcessor {

    private static final Logger logger = LoggerFactory.getLogger(KogitoCommonProcessor.class);

    @BuildStep
    public NativeImageResourceBuildItem kieRuntimesSPIResource() {
        logger.debug("kieRuntimesSPIResource()");
        return new NativeImageResourceBuildItem("META-INF/services/org.kie.api.internal.runtime.KieRuntimes");
    }

    @BuildStep
    public NativeImageResourceBuildItem kieRuntimeServiceSPIResource() {
        logger.debug("kieRuntimeServiceSPIResource()");
        return new NativeImageResourceBuildItem("META-INF/services/org.kie.api.internal.runtime.KieRuntimeService");
    }

    @BuildStep
    public NativeImageResourceBuildItem kieWeaversSPIResource() {
        logger.debug("kieWeaversSPIResource()");
        return new NativeImageResourceBuildItem("META-INF/services/org.kie.api.internal.weaver.KieWeavers");
    }

    @BuildStep
    public NativeImageResourceBuildItem kieWeaverServiceSPIResource() {
        logger.debug("kieWeaverServiceSPIResource()");
        return new NativeImageResourceBuildItem("META-INF/services/org.kie.api.internal.weaver.KieWeaverService");
    }

    @BuildStep
    public ReflectiveClassBuildItem kieRuntimesImplReflectiveClass() {
        logger.debug("kieRuntimesImplReflectiveClass()");
        return new ReflectiveClassBuildItem(true, true, KieRuntimesImpl.class);
    }

    @BuildStep
    public ReflectiveClassBuildItem kieWeaversImplReflectiveClass() {
        logger.debug("kieWeaversImplReflectiveClass()");
        return new ReflectiveClassBuildItem(true, true, KieWeaversImpl.class);
    }

    @BuildStep
    public void kogitoConfig(BuildProducer<StaticInitConfigBuilderBuildItem> staticInitConfigBuilder) {
        staticInitConfigBuilder.produce(new StaticInitConfigBuilderBuildItem(KogitoSmallryeConfigBuilder.class));
    }
}
