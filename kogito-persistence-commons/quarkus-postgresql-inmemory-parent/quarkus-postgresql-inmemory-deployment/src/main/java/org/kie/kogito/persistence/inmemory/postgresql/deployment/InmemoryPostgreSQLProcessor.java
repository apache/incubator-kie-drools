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

package org.kie.kogito.persistence.inmemory.postgresql.deployment;

import java.io.IOException;

import org.kie.kogito.persistence.inmemory.postgresql.runtime.InmemoryPostgreSQLRecorder;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigurationSourceValueBuildItem;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.runtime.RuntimeValue;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

class InmemoryPostgreSQLProcessor {

    private static final String FEATURE = "inmemory-postgres";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(RUNTIME_INIT)
    ServiceStartBuildItem startService(InmemoryPostgreSQLRecorder recorder,
            BuildProducer<RunTimeConfigurationSourceValueBuildItem> configSourceValueBuildItem) throws IOException {
        RuntimeValue<Integer> port = recorder.startPostgres();
        configSourceValueBuildItem.produce(new RunTimeConfigurationSourceValueBuildItem(recorder.configSources(port)));
        return new ServiceStartBuildItem(FEATURE);
    }

    @BuildStep
    void addDependencies(BuildProducer<IndexDependencyBuildItem> indexDependency) {
        indexDependency.produce(new IndexDependencyBuildItem("io.zonky.test",
                "embedded-postgres"));
    }
}
