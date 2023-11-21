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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDefinition;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class TypeDeclarationCompositeCompilationPhase implements CompilationPhase {
    private Collection<CompositePackageDescr> packages;
    private final TypeDeclarationBuilder typeBuilder;

    public TypeDeclarationCompositeCompilationPhase(Collection<CompositePackageDescr> packages, TypeDeclarationBuilder typeBuilder) {
        this.packages = packages;
        this.typeBuilder = typeBuilder;
    }

    public void process() {
        Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs = new HashMap<>();
        List<TypeDefinition> unresolvedTypes = new ArrayList<>();
        List<AbstractClassTypeDeclarationDescr> unsortedDescrs = new ArrayList<>();
        for (CompositePackageDescr packageDescr : packages) {
            unsortedDescrs.addAll(packageDescr.getTypeDeclarations());
            unsortedDescrs.addAll(packageDescr.getEnumDeclarations());
        }

        typeBuilder.processTypeDeclarations( packages, unsortedDescrs, unresolvedTypes, unprocesseableDescrs );
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return Collections.emptyList();
    }
}
