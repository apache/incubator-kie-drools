package org.drools.lang.descr;

import org.drools.io.Resource;

import java.util.List;
import java.util.Set;

public class CompositePackageDescr extends PackageDescr {

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
            }
        }

        List<FunctionImportDescr> currentFunctionImports = getFunctionImports();
        for (FunctionImportDescr descr : packageDescr.getFunctionImports()) {
            if (!currentFunctionImports.contains(descr)) {
                addFunctionImport(descr);
            }
        }

        List<AttributeDescr> currentAttributeDescrs = getAttributes();
        for (AttributeDescr descr : packageDescr.getAttributes()) {
            if (!currentAttributeDescrs.contains(descr)) {
                addAttribute(descr);
            }
        }

        List<GlobalDescr> currentGlobalDescrs = getGlobals();
        for (GlobalDescr descr : packageDescr.getGlobals()) {
            if (!currentGlobalDescrs.contains(descr)) {
                addGlobal(descr);
            }
        }

        List<FunctionDescr> currentFunctionDescrs = getFunctions();
        for (FunctionDescr descr : packageDescr.getFunctions()) {
            if (!currentFunctionDescrs.contains(descr)) {
                addFunction(descr);
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
            }
        }

        Set<WindowDeclarationDescr> windowDeclarationDescrs = getWindowDeclarations();
        for (WindowDeclarationDescr descr : packageDescr.getWindowDeclarations()) {
            if (!windowDeclarationDescrs.contains(descr)) {
                addWindowDeclaration(descr);
            }
        }
    }
}
