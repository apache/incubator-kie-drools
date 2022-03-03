package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.GlobalError;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.ResourceChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class GlobalCompilationPhase extends AbstractPackageCompilationPhase {
    protected static final transient Logger logger = LoggerFactory.getLogger(GlobalCompilationPhase.class);

    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderImpl knowledgeBuilder;
    private final FilterCondition filterAcceptsRemoval;

    public GlobalCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, InternalKnowledgeBase kBase, KnowledgeBuilderImpl knowledgeBuilder, FilterCondition filterAcceptsRemoval) {
        super(pkgRegistry, packageDescr);
        this.kBase = kBase;
        this.knowledgeBuilder = knowledgeBuilder;
        this.filterAcceptsRemoval = filterAcceptsRemoval;
    }

    public void process() {
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
                pkg.addGlobal(identifier, clazz);
                knowledgeBuilder.addGlobal(identifier, clazz);

                if (kBase != null) {
                    kBase.addGlobal(identifier, clazz);
                }
            } catch (final ClassNotFoundException e) {
                this.results.add(new GlobalError(global, e.getMessage()));
                logger.warn("ClassNotFoundException occured!", e);
            }
        }

        for (String toBeRemoved : existingGlobals) {
            if (filterAcceptsRemoval.accepts(ResourceChange.Type.GLOBAL, pkg.getName(), toBeRemoved)) {
                pkg.removeGlobal(toBeRemoved);
                if (kBase != null) {
                    kBase.removeGlobal(toBeRemoved);
                }
            }
        }
    }

}
