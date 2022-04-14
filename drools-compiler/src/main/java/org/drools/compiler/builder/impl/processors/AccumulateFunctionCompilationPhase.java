package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.util.TypeResolver;
import org.drools.drl.ast.descr.AccumulateImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.runtime.rule.AccumulateFunction;

import java.lang.reflect.InvocationTargetException;

public class AccumulateFunctionCompilationPhase extends AbstractPackageCompilationPhase {
    private final TypeResolver typeResolver;

    public AccumulateFunctionCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        super(pkgRegistry, packageDescr);
        this.typeResolver = pkgRegistry.getTypeResolver();
    }

    public void process() {
        for (final AccumulateImportDescr aid : packageDescr.getAccumulateImports()) {
            AccumulateFunction af = loadAccumulateFunction(
                    aid.getFunctionName(),
                    aid.getTarget());
            pkgRegistry.getPackage().addAccumulateFunction(aid.getFunctionName(), af);
        }
    }

    @SuppressWarnings("unchecked")
    private AccumulateFunction loadAccumulateFunction(
            String identifier,
            String className) {
        try {
            Class<? extends AccumulateFunction> clazz = (Class<? extends AccumulateFunction>) typeResolver.resolveType(className);
            return clazz.getConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Class " + className + " not found",
                    e);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Instantiation failed for class " + className,
                    e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Illegal access to class " + className,
                    e);
        }
    }
}
