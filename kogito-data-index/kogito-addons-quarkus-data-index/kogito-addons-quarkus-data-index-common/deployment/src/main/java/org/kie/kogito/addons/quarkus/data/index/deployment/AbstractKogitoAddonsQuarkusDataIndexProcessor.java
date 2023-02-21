/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.addons.quarkus.data.index.deployment;

import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;
import org.kie.kogito.index.addon.vertx.VertxGraphiQLSetup;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.OneOfCapabilityKogitoAddOnProcessor;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyBuildItem;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import io.quarkus.deployment.pkg.steps.NativeOrNativeSourcesBuild;

public abstract class AbstractKogitoAddonsQuarkusDataIndexProcessor extends OneOfCapabilityKogitoAddOnProcessor {

    public static final String KOGITO_DATA_INDEX = "kogito.data-index.url";
    private static final String KOGITO_DATA_INDEX_HTTP_URL = "kogito.dataindex.http.url";

    AbstractKogitoAddonsQuarkusDataIndexProcessor() {
        super(KogitoCapability.SERVERLESS_WORKFLOW, KogitoCapability.PROCESSES);
    }

    @BuildStep
    public void startDataIndexNormal(BuildProducer<SystemPropertyBuildItem> systemProperties) {
        systemProperties.produce(new SystemPropertyBuildItem(KOGITO_DATA_INDEX_HTTP_URL, "http://localhost:${quarkus.http.port}"));
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public void processGraphiql(BuildProducer<AdditionalBeanBuildItem> additionalBean) {
        additionalBean.produce(AdditionalBeanBuildItem.builder().addBeanClass(VertxGraphiQLSetup.class).setUnremovable().setDefaultScope(DotNames.APPLICATION_SCOPED).build());
    }

    @BuildStep(onlyIf = { GlobalDevServicesConfig.Enabled.class, IsDevelopment.class })
    public void startDataIndexDevService(BuildProducer<SystemPropertyBuildItem> systemProperties) {
        systemProperties.produce(new SystemPropertyBuildItem(KOGITO_DATA_INDEX, "http://localhost:${quarkus.http.port}"));
    }

    @BuildStep(onlyIf = NativeOrNativeSourcesBuild.class)
    public void nativeResources(BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyClass) {
        resource.produce(new NativeImageResourceBuildItem("basic.schema.graphqls"));
        resource.produce(new NativeImageResourceBuildItem("io/vertx/ext/web/handler/graphiql/index.html"));
        reflectiveHierarchy(ProcessInstance.class, reflectiveHierarchyClass);
        reflectiveHierarchy(UserTaskInstance.class, reflectiveHierarchyClass);
    }

    protected void reflectiveHierarchy(Class<?> clazz, BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyClass) {
        DotName dotName = DotName.createSimple(clazz.getName());
        Type type = Type.create(dotName, Type.Kind.CLASS);
        reflectiveHierarchyClass.produce(new ReflectiveHierarchyBuildItem.Builder().type(type).build());
    }

}
