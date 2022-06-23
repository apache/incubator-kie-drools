package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.ResourceChange;

public class FunctionCompiler extends SimpleFunctionCompiler {

    private final AssetFilter assetFilter;

    public FunctionCompiler(PackageRegistry pkgRegistry, PackageDescr packageDescr, AssetFilter assetFilter, ClassLoader rootClassLoader) {
        super(pkgRegistry, packageDescr, rootClassLoader);
        this.assetFilter = assetFilter;
    }


    @Override
    protected void postCompileAddFunction(FunctionDescr functionDescr) {
        if (filterAccepts(functionDescr)) {
            super.postCompileAddFunction(functionDescr);
        }
    }

    @Override
    protected void addFunction(FunctionDescr functionDescr) {
        if (filterAccepts(functionDescr)) {
            super.addFunction(functionDescr);
        }
    }

    private boolean filterAccepts(FunctionDescr functionDescr) {
        return assetFilter == null ||
                !AssetFilter.Action.DO_NOTHING.equals(
                        assetFilter.accept(
                                ResourceChange.Type.FUNCTION,
                                functionDescr.getNamespace(),
                                functionDescr.getName()));
    }

}
