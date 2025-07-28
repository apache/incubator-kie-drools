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
package org.kie.kogito.addons.quarkus.data.index.deployment;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;
import org.kie.kogito.index.addon.vertx.VertxGraphiQLSetup;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.ProcessDefinition;
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
import io.quarkus.deployment.pkg.steps.NativeOrNativeSourcesBuild;

public abstract class AbstractKogitoAddonsQuarkusDataIndexProcessor extends OneOfCapabilityKogitoAddOnProcessor {
    private static final String QUARKUS_HTTP_PORT = "quarkus.http.port";
    private static final String KOGITO_SERVICE_URL_PROP = "kogito.service.url";
    private static final String KOGITO_DATA_INDEX_PROP = "kogito.data-index.url";
    /**
     * Skips the setting of the default kogito.data-index.url from the runtime url.
     * This is convenient in scenarios like the dev-ui executing in k8s where want it to set this url using window
     * location instead. Must be explicitly set to true to take effect.
     */
    private static final String SKIP_DEFAULT_DATA_INDEX_URL_PROP = "kogito.data-index-addons.skip-default-data-index-url";

    AbstractKogitoAddonsQuarkusDataIndexProcessor() {
        super(KogitoCapability.SERVERLESS_WORKFLOW, KogitoCapability.PROCESSES);
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public void buildDefaultDataIndexURLSystemProperty(BuildProducer<SystemPropertyBuildItem> systemProperties) {
        boolean skipDefaultDataIndexUrl = ConfigProvider.getConfig().getOptionalValue(SKIP_DEFAULT_DATA_INDEX_URL_PROP, Boolean.class).orElse(false);
        if (!skipDefaultDataIndexUrl) {
            // Setting a default `kogito.data-index.url` accordingly to the runtime url.
            String dataIndexUrl = ConfigProvider.getConfig().getOptionalValue(KOGITO_SERVICE_URL_PROP, String.class).orElseGet(() -> {
                Integer port = ConfigProvider.getConfig().getOptionalValue(QUARKUS_HTTP_PORT, Integer.class).orElse(8080);
                return "http://localhost:" + port;
            });
            systemProperties.produce(new SystemPropertyBuildItem(KOGITO_DATA_INDEX_PROP, dataIndexUrl));
        }
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public void processGraphiql(BuildProducer<AdditionalBeanBuildItem> additionalBean) {
        additionalBean.produce(AdditionalBeanBuildItem.builder().addBeanClass(VertxGraphiQLSetup.class).setUnremovable().setDefaultScope(DotNames.APPLICATION_SCOPED).build());
    }

    @BuildStep(onlyIf = NativeOrNativeSourcesBuild.class)
    public void nativeResources(BuildProducer<NativeImageResourceBuildItem> resource,
            BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyClass) {
        resource.produce(new NativeImageResourceBuildItem("graphql/basic.schema.graphqls"));
        resource.produce(new NativeImageResourceBuildItem("io/vertx/ext/web/handler/graphiql/index.html"));
        reflectiveHierarchy(Node.class, reflectiveHierarchyClass);
        reflectiveHierarchy(ProcessDefinition.class, reflectiveHierarchyClass);
        reflectiveHierarchy(ProcessInstance.class, reflectiveHierarchyClass);
        reflectiveHierarchy(UserTaskInstance.class, reflectiveHierarchyClass);
    }

    protected void reflectiveHierarchy(Class<?> clazz, BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyClass) {
        DotName dotName = DotName.createSimple(clazz.getName());
        Type type = Type.create(dotName, Type.Kind.CLASS);
        reflectiveHierarchyClass.produce(new ReflectiveHierarchyBuildItem.Builder().type(type).build());
    }

}
