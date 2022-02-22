package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.GlobalError;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.ResourceChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

class GlobalProcessor extends AbstractPackageProcessor {
    protected static final transient Logger logger = LoggerFactory.getLogger(GlobalProcessor.class);

    private final KnowledgeBaseImpl kBase;
    private final KnowledgeBuilderImpl knowledgeBuilder;
    private final BiConsumer<InternalKnowledgePackage, String> cleanupCallback;

    public GlobalProcessor(PackageRegistry pkgRegistry, PackageDescr packageDescr, KnowledgeBaseImpl kBase, KnowledgeBuilderImpl knowledgeBuilder, BiConsumer<InternalKnowledgePackage, String> cleanupCallback) {
        super(pkgRegistry, packageDescr);
        this.kBase = kBase;
        this.knowledgeBuilder = knowledgeBuilder;
        this.cleanupCallback = cleanupCallback;
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
            cleanupCallback.accept(pkg, toBeRemoved);
        }
    }

}
