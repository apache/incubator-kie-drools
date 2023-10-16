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
package org.kie.kogito.addons.quarkus.data.index.persistence.deployment;

import org.kie.kogito.index.mongodb.model.ProcessDefinitionEntity;
import org.kie.kogito.index.mongodb.model.ProcessInstanceEntity;
import org.kie.kogito.index.mongodb.model.UserTaskInstanceEntity;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyBuildItem;
import io.quarkus.deployment.pkg.steps.NativeOrNativeSourcesBuild;

public class MongoDataIndexPersistenceProcessor extends AbstractKogitoAddonsQuarkusDataIndexPersistenceProcessor {

    private static final String FEATURE = "kogito-addons-quarkus-data-index-persistence-mongodb";

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep(onlyIf = NativeOrNativeSourcesBuild.class)
    public void mongoNativeResources(BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyClass) {
        reflectiveHierarchy(ProcessDefinitionEntity.class, reflectiveHierarchyClass);
        reflectiveHierarchy(ProcessInstanceEntity.class, reflectiveHierarchyClass);
        reflectiveHierarchy(UserTaskInstanceEntity.class, reflectiveHierarchyClass);
    }

}
