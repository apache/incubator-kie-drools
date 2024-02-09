/**
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
package org.drools.model.codegen.execmodel;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.model.codegen.execmodel.generator.DRLIdGenerator;
import org.kie.api.builder.ReleaseId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A delegate/container for {@link PackageModel}s, used in {@link ModelBuilderImpl}
 */
public class PackageModelManager {
    private final Map<String, PackageModel> packageModels;
    private final KnowledgeBuilderConfigurationImpl builderConfiguration;
    private final ReleaseId releaseId;
    private final DRLIdGenerator exprIdGenerator;

    public PackageModelManager(KnowledgeBuilderConfigurationImpl builderConfiguration, ReleaseId releaseId, DRLIdGenerator exprIdGenerator) {
        this.packageModels = new HashMap<>();
        this.builderConfiguration = builderConfiguration;
        this.releaseId = releaseId;
        this.exprIdGenerator = exprIdGenerator;
    }

    public PackageModel getPackageModel(PackageDescr packageDescr, PackageRegistry pkgRegistry, String pkgName) {
        return packageModels.computeIfAbsent(pkgName, s -> PackageModel.createPackageModel(builderConfiguration, packageDescr, pkgRegistry, pkgName, releaseId, exprIdGenerator));
    }

    public PackageModel remove(String name) {
        return packageModels.remove(name);
    }

    public Collection<PackageModel> values() {
        return packageModels.values();
    }
}
