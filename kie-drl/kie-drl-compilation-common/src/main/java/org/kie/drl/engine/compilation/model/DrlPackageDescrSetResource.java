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
package org.kie.drl.engine.compilation.model;

import java.util.Set;

import org.drools.drl.ast.descr.PackageDescr;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoSetResource;

public class DrlPackageDescrSetResource extends EfestoSetResource<PackageDescr> implements EfestoCompilationOutput {

    public DrlPackageDescrSetResource(Set<PackageDescr> packageDescrs, String basePath) {
        super(packageDescrs, "drl", basePath);
    }


}
