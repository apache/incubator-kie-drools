/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.ResourceChange;

public class GlobalCompilationPhase extends ImmutableGlobalCompilationPhase {

    private final InternalKnowledgeBase kBase;
    private final AssetFilter assetFilter;

    public GlobalCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, InternalKnowledgeBase kBase, GlobalVariableContext globalVariableContext, AssetFilter filterAcceptsRemoval) {
        super(pkgRegistry, packageDescr, globalVariableContext);
        this.kBase = kBase;
        this.assetFilter = filterAcceptsRemoval;
    }

    @Override
    protected void addGlobal(InternalKnowledgePackage pkg, String identifier, Class<?> clazz) {
        super.addGlobal(pkg, identifier, clazz);
        if (kBase != null) {
            kBase.addGlobal(identifier, clazz);
        }
    }

    protected void removeGlobal(InternalKnowledgePackage pkg, String toBeRemoved) {
        if (assetFilter != null && AssetFilter.Action.REMOVE.equals(assetFilter.accept(ResourceChange.Type.GLOBAL, pkg.getName(), toBeRemoved))) {
            pkg.removeGlobal(toBeRemoved);
            if (kBase != null) {
                kBase.removeGlobal(toBeRemoved);
            }
        }
    }

}
