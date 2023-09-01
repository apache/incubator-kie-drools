package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.compiler.DuplicateFunction;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.base.rule.Function;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

public class FunctionCompilationPhase extends AbstractPackageCompilationPhase {

    private final KnowledgeBuilderConfiguration configuration;

    public FunctionCompilationPhase(PackageRegistry pkgRegistry,
                                    PackageDescr packageDescr,
                                    KnowledgeBuilderConfiguration configuration) {
        super(pkgRegistry, packageDescr);
        this.configuration = configuration;
    }

    public void process() {
        for (FunctionDescr function : packageDescr.getFunctions()) {
            Function existingFunc = pkgRegistry.getPackage().getFunctions().get(function.getName());
            if (existingFunc != null && function.getNamespace().equals(existingFunc.getNamespace())) {
                this.results.add(
                        new DuplicateFunction(function,
                                this.configuration));
            }
        }

        for (final FunctionImportDescr functionImport : packageDescr.getFunctionImports()) {
            String importEntry = functionImport.getTarget();
            pkgRegistry.addStaticImport(functionImport);
            pkgRegistry.getPackage().addStaticImport(importEntry);
        }
    }
}
