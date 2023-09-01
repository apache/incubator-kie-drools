package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.ResourceChange;

public class FunctionCompiler extends ImmutableFunctionCompiler {

    public static CompilationPhase of(PackageRegistry pkgRegistry, PackageDescr packageDescr, AssetFilter assetFilter, ClassLoader rootClassLoader) {
        if (assetFilter == null) {
            return new ImmutableFunctionCompiler(pkgRegistry, packageDescr, rootClassLoader);
        } else {
            return new FunctionCompiler(pkgRegistry, packageDescr, assetFilter, rootClassLoader);
        }
    }

    private final AssetFilter assetFilter;

    private FunctionCompiler(PackageRegistry pkgRegistry, PackageDescr packageDescr, AssetFilter assetFilter, ClassLoader rootClassLoader) {
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
