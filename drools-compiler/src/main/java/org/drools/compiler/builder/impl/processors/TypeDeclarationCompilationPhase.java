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
package org.drools.compiler.builder.impl.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDefinition;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;

public class TypeDeclarationCompilationPhase extends AbstractPackageCompilationPhase {
    private final TypeDeclarationBuilder typeBuilder;
    private final Resource currentResource;

    public TypeDeclarationCompilationPhase(PackageDescr packageDescr, TypeDeclarationBuilder typeBuilder, PackageRegistry pkgRegistry, Resource resource) {
        super(pkgRegistry, packageDescr);
        this.typeBuilder = typeBuilder;
        this.currentResource = resource;
    }

    public void process() {
        Map<String, AbstractClassTypeDeclarationDescr> unprocesseableDescrs = new HashMap<>();
        List<TypeDefinition> unresolvedTypes = new ArrayList<>();
        List<AbstractClassTypeDeclarationDescr> unsortedDescrs = new ArrayList<>();
        unsortedDescrs.addAll(packageDescr.getTypeDeclarations());
        unsortedDescrs.addAll(packageDescr.getEnumDeclarations());
        typeBuilder.processTypeDeclarations(packageDescr, pkgRegistry, currentResource, unsortedDescrs, unresolvedTypes, unprocesseableDescrs);
        for (AbstractClassTypeDeclarationDescr descr : unprocesseableDescrs.values()) {
            this.results.add(new TypeDeclarationError(descr, "Unable to process type " + descr.getTypeName()));
        }
    }
}
