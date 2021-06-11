/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.lang.descr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.kie.api.io.Resource;
import org.kie.internal.builder.ResourceChange;

public class CompositePackageDescr extends PackageDescr {
    
    private CompositeAssetFilter filter;

    public CompositePackageDescr() { }

    public CompositePackageDescr(Resource resource, PackageDescr packageDescr) {
        super(packageDescr.getNamespace(), packageDescr.getDocumentation());
        internalAdd(resource, packageDescr);
    }

    public void addPackageDescr(Resource resource, PackageDescr packageDescr) {
        if (!getNamespace().equals(packageDescr.getNamespace())) {
            throw new RuntimeException("Composing PackageDescr (" + packageDescr.getName()
                + ") in different namespaces (namespace=" + getNamespace()
                + " packageDescr=" + packageDescr.getNamespace() + ")" );
        }
        internalAdd(resource, packageDescr);
    }

    private void internalAdd(Resource resource, PackageDescr packageDescr) {
        for (ImportDescr descr : packageDescr.getImports()) {
            addImport(descr);
            descr.setResource(resource);
        }

        for (FunctionImportDescr descr : packageDescr.getFunctionImports()) {
            addFunctionImport(descr);
            descr.setResource(resource);
        }

        for (AccumulateImportDescr descr : packageDescr.getAccumulateImports()) {
            addAccumulateImport(descr);
            descr.setResource(resource);
        }

        for (AttributeDescr descr : packageDescr.getAttributes()) {
            addAttribute(descr);
            descr.setResource(resource);
        }

        for (GlobalDescr descr : packageDescr.getGlobals()) {
            addGlobal(descr);
            descr.setResource(resource);
        }

        for (FunctionDescr descr : packageDescr.getFunctions()) {
            addFunction(descr);
            descr.setResource(resource);
        }

        for (RuleDescr descr : packageDescr.getRules()) {
            addRule(descr);
            descr.setResource(resource);
        }

        // Avoid adding the same type declaration twice, see
        // TypeDeclarationTest.testDuplicatedTypeDeclarationInDifferentResources
        // IncrementalCompilationTest.testIncrementalCompilationWithAmbiguousRedeclares
        // RHDM-1738
        Set<TypeDeclarationDescr> typeDeclarationDescrs = new HashSet<>(getTypeDeclarations());
        for (TypeDeclarationDescr descr : packageDescr.getTypeDeclarations()) {
            if (!typeDeclarationDescrs.contains(descr)) {
                addTypeDeclaration(descr);
                descr.setResource(resource);
            }
        }

        for (EnumDeclarationDescr enumDescr : packageDescr.getEnumDeclarations()) {
            addEnumDeclaration(enumDescr);
            enumDescr.setResource(resource);
        }

        for (EntryPointDeclarationDescr descr : packageDescr.getEntryPointDeclarations()) {
            addEntryPointDeclaration(descr);
            descr.setResource(resource);
        }

        for (WindowDeclarationDescr descr : packageDescr.getWindowDeclarations()) {
            addWindowDeclaration(descr);
            descr.setResource(resource);
        }
        packageDescr.getPreferredPkgUUID().ifPresent(pkgUUID -> {
            if (getPreferredPkgUUID().isPresent() && !pkgUUID.equals(getPreferredPkgUUID().get())) {
                throw new RuntimeException(String.format("Trying to overwrite preferredPkgUUID %s with a different value %s", getPreferredPkgUUID().get(), pkgUUID));
            }
            setPreferredPkgUUID(pkgUUID);
        });
    }
    
    public CompositeAssetFilter getFilter() {
        return filter;
    }
    
    public void addFilter( KnowledgeBuilderImpl.AssetFilter f ) {
        if( f != null ) {
            if( filter == null ) {
                this.filter = new CompositeAssetFilter();
            }
            this.filter.filters.add( f );
        }
    }
    
    public static class CompositeAssetFilter implements KnowledgeBuilderImpl.AssetFilter {
        public List<KnowledgeBuilderImpl.AssetFilter> filters = new ArrayList<KnowledgeBuilderImpl.AssetFilter>();

        @Override
        public Action accept(ResourceChange.Type type, String pkgName, String assetName) {
            for( KnowledgeBuilderImpl.AssetFilter filter : filters ) {
                Action result = filter.accept(type, pkgName, assetName);
                if( !Action.DO_NOTHING.equals( result ) ) {
                    return result;
                }
            }
            return Action.DO_NOTHING;
        }
    }
}
