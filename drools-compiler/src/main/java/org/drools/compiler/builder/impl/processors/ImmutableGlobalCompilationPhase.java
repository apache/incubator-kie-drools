package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.GlobalError;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.ResourceChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ImmutableGlobalCompilationPhase extends AbstractPackageCompilationPhase {
    protected static final transient Logger logger = LoggerFactory.getLogger(ImmutableGlobalCompilationPhase.class);

    private final GlobalVariableContext globalVariableContext;

    public ImmutableGlobalCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, GlobalVariableContext globalVariableContext) {
        super(pkgRegistry, packageDescr);
        this.globalVariableContext = globalVariableContext;
    }

    public final void process() {
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        Set<String> existingGlobals = new HashSet<>(pkg.getGlobals().keySet());

        for (final GlobalDescr global : packageDescr.getGlobals()) {
            final String identifier = global.getIdentifier();
            existingGlobals.remove(identifier);
            String className = global.getType();

            // JBRULES-3039: can't handle type name with generic params
            while (className.indexOf('<') >= 0) {
                className = className.replaceAll("<[^<>]+?>", "");
            }

            try {
                Class<?> clazz = pkgRegistry.getTypeResolver().resolveType(className);
                if (clazz.isPrimitive()) {
                    this.results.add(new GlobalError(global, " Primitive types are not allowed in globals : " + className));
                    return;
                }
                addGlobal(pkg, identifier, clazz);
            } catch (final ClassNotFoundException e) {
                this.results.add(new GlobalError(global, e.getMessage()));
                logger.warn("ClassNotFoundException occured!", e);
            }
        }

        for (String toBeRemoved : existingGlobals) {
            removeGlobal(pkg, toBeRemoved);
        }
    }

    protected void addGlobal(InternalKnowledgePackage pkg, String identifier, Class<?> clazz) {
        pkg.addGlobal(identifier, clazz);
        globalVariableContext.addGlobal(identifier, clazz);
    }

    protected void removeGlobal(InternalKnowledgePackage pkg, String toBeRemoved) {
        pkg.removeGlobal(toBeRemoved);
    }
}
