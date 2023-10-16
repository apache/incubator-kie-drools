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
package org.kie.kogito.addons.quarkus.jobs.service.embedded.deployment;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.DotName;
import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.OneOfCapabilityKogitoAddOnProcessor;

import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.ExcludedTypeBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourcePatternsBuildItem;
import io.quarkus.deployment.pkg.steps.NativeOrNativeSourcesBuild;
import io.quarkus.reactive.datasource.ReactiveDataSource;

import static org.kie.kogito.addons.quarkus.jobs.service.embedded.stream.EventPublisherJobStreams.DATA_INDEX_EVENT_PUBLISHER;

class KogitoAddonsQuarkusJobsServiceEmbeddedProcessor extends OneOfCapabilityKogitoAddOnProcessor {
    private static final String FEATURE = "kogito-addons-quarkus-jobs-service-embedded";
    private static final String JOBS_SERVICE_URL = "kogito.jobs-service.url";
    private static final String SERVICE_URL = "kogito.service.url";
    private static final String DATA_SOURCE_NAME = "jobs_service";
    private static final String DATA_SOURCE_NAME_KEY = "datasource.name";

    KogitoAddonsQuarkusJobsServiceEmbeddedProcessor() {
        super(KogitoCapability.SERVERLESS_WORKFLOW, KogitoCapability.PROCESSES);
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void buildConfiguration(BuildProducer<SystemPropertyBuildItem> systemProperties) {
        systemProperties.produce(new SystemPropertyBuildItem(SERVICE_URL, "http://${quarkus.http.host}:${quarkus.http.port}"));
        systemProperties.produce(new SystemPropertyBuildItem(JOBS_SERVICE_URL, "${" + SERVICE_URL + "}"));
        systemProperties.produce(new SystemPropertyBuildItem(DATA_SOURCE_NAME_KEY, DATA_SOURCE_NAME));
    }

    @BuildStep
    AnnotationsTransformerBuildItem transformDataSource(CombinedIndexBuildItem indexBuildItem) {
        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            public boolean appliesTo(org.jboss.jandex.AnnotationTarget.Kind kind) {
                return kind == AnnotationTarget.Kind.FIELD;
            }

            public void transform(TransformationContext context) {
                if (!(context.getTarget().kind() == AnnotationTarget.Kind.FIELD)) {
                    return;
                }
                DotName className = DotName.createSimple(context.getTarget().asField().type().name().toString());
                if (indexBuildItem.getIndex().getClassByName(className) == null) {
                    return;
                }

                if (context.getTarget().asField().type().name().equals(DotName.createSimple("io.vertx.mutiny.pgclient.PgPool"))) {
                    context.transform().add(ReactiveDataSource.class, AnnotationValue.createStringValue("value", DATA_SOURCE_NAME)).done();
                } else if (context.getTarget().asField().type().name().equals(DotName.createSimple("io.agroal.api.AgroalDataSource"))) {
                    context.transform().add(io.quarkus.agroal.DataSource.class, AnnotationValue.createStringValue("value", DATA_SOURCE_NAME)).done();
                }
            }

        });
    }

    @BuildStep(onlyIf = NativeOrNativeSourcesBuild.class)
    public void inMemoryNativeResources(BuildProducer<NativeImageResourcePatternsBuildItem> resource) {
        resource.produce(NativeImageResourcePatternsBuildItem.builder().includeGlob("postgres-*.txz").build());
    }

    @BuildStep
    public void excludeEventPublisherJobStreams(CombinedIndexBuildItem indexBuildItem, BuildProducer<ExcludedTypeBuildItem> excludedBeans) {
        if (indexBuildItem.getIndex().getClassByName(DotName.createSimple(DATA_INDEX_EVENT_PUBLISHER)) == null) {
            excludedBeans.produce(new ExcludedTypeBuildItem(DATA_INDEX_EVENT_PUBLISHER));
        }
    }
}
