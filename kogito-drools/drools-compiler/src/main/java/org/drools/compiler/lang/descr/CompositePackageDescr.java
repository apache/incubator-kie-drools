package org.drools.compiler.lang.descr;

import org.drools.compiler.compiler.PackageBuilder;
import org.kie.api.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompositePackageDescr extends PackageDescr {
    
    private CompositeAssetFilter filter;

    public CompositePackageDescr() { }

    public CompositePackageDescr(Resource resource, PackageDescr packageDescr) {
        super(packageDescr.getNamespace(), packageDescr.getDocumentation());
        internalAdd(resource, packageDescr);
    }

    public void addPackageDescr(Resource resource, PackageDescr packageDescr) {
        if (!getNamespace().equals(packageDescr.getNamespace())) {
            throw new RuntimeException("Composing PackageDescr in different namespaces");
        }
        internalAdd(resource, packageDescr);
    }

    private void internalAdd(Resource resource, PackageDescr packageDescr) {
        List<ImportDescr> currentImports = getImports();
        for (ImportDescr descr : packageDescr.getImports()) {
            if (!currentImports.contains(descr)) {
                addImport(descr);
                descr.setResource(resource);
            }
        }

        List<FunctionImportDescr> currentFunctionImports = getFunctionImports();
        for (FunctionImportDescr descr : packageDescr.getFunctionImports()) {
            if (!currentFunctionImports.contains(descr)) {
                addFunctionImport(descr);
                descr.setResource(resource);
            }
        }
        
        List<AccumulateImportDescr> accumulateImports = getAccumulateImports();
        for (AccumulateImportDescr descr : packageDescr.getAccumulateImports()) {
            if (!currentFunctionImports.contains(descr)) {
                addAccumulateImport(descr);
                descr.setResource(resource);
            }
        }

        List<AttributeDescr> currentAttributeDescrs = getAttributes();
        for (AttributeDescr descr : packageDescr.getAttributes()) {
            if (!currentAttributeDescrs.contains(descr)) {
                addAttribute(descr);
                descr.setResource(resource);
            }
        }

        List<GlobalDescr> currentGlobalDescrs = getGlobals();
        for (GlobalDescr descr : packageDescr.getGlobals()) {
            if (!currentGlobalDescrs.contains(descr)) {
                addGlobal(descr);
                descr.setResource(resource);
            }
        }

        List<FunctionDescr> currentFunctionDescrs = getFunctions();
        for (FunctionDescr descr : packageDescr.getFunctions()) {
            if (!currentFunctionDescrs.contains(descr)) {
                addFunction(descr);
                descr.setResource(resource);
            }
        }

        List<RuleDescr> ruleDescrs = getRules();
        for (RuleDescr descr : packageDescr.getRules()) {
            if (!ruleDescrs.contains(descr)) {
                addRule(descr);
                descr.setResource(resource);
            }
        }

        List<TypeDeclarationDescr> typeDeclarationDescrs = getTypeDeclarations();
        for (TypeDeclarationDescr descr : packageDescr.getTypeDeclarations()) {
            if (!typeDeclarationDescrs.contains(descr)) {
                addTypeDeclaration(descr);
                descr.setResource(resource);
            }
        }

        Set<EntryPointDeclarationDescr> entryPointDeclarationDescrs = getEntryPointDeclarations();
        for (EntryPointDeclarationDescr descr : packageDescr.getEntryPointDeclarations()) {
            if (!entryPointDeclarationDescrs.contains(descr)) {
                addEntryPointDeclaration(descr);
                descr.setResource(resource);
            }
        }

        Set<WindowDeclarationDescr> windowDeclarationDescrs = getWindowDeclarations();
        for (WindowDeclarationDescr descr : packageDescr.getWindowDeclarations()) {
            if (!windowDeclarationDescrs.contains(descr)) {
                addWindowDeclaration(descr);
                descr.setResource(resource);
            }
        }
    }
    
    public CompositeAssetFilter getFilter() {
        return filter;
    }
    
    public void addFilter( PackageBuilder.AssetFilter f ) {
        if( f != null ) {
            if( filter == null ) {
                this.filter = new CompositeAssetFilter();
            }
            this.filter.filters.add( f );
        }
    }
    
    public static class CompositeAssetFilter implements PackageBuilder.AssetFilter {
        public List<PackageBuilder.AssetFilter> filters = new ArrayList<PackageBuilder.AssetFilter>();

        @Override
        public Action accept(String pkgName, String assetName) {
            for( PackageBuilder.AssetFilter filter : filters ) {
                Action result = filter.accept(pkgName, assetName);
                if( !Action.DO_NOTHING.equals( result ) ) {
                    return result;
                }
            }
            return Action.DO_NOTHING;
        }
    }
}
