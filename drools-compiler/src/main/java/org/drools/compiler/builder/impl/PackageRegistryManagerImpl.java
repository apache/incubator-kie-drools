package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static org.drools.core.util.StringUtils.isEmpty;

public class PackageRegistryManagerImpl implements PackageRegistryManager {
    private final RootClassLoaderProvider classLoaderProvider;
    private final KnowledgeBuilderConfigurationImpl configuration;
    private final InternalKnowledgeBaseProvider kBaseProvider;

    private final Map<String, PackageRegistry> pkgRegistryMap = new ConcurrentHashMap<>();

    //This list of package level attributes is initialised with the PackageDescr's attributes added to the assembler.
    //The package level attributes are inherited by individual rules not containing explicit overriding parameters.
    //The map is keyed on the PackageDescr's namespace and contains a map of AttributeDescr's keyed on the
    //AttributeDescr's name.
    private final Map<String, Map<String, AttributeDescr>> packageAttributes = new HashMap<>();

    //PackageDescrs' list of ImportDescrs are kept identical as subsequent PackageDescrs are added.
    private final Map<String, List<PackageDescr>> packages = new ConcurrentHashMap<>();

    public PackageRegistryManagerImpl(
            KnowledgeBuilderConfigurationImpl configuration,
            RootClassLoaderProvider classLoaderProvider,
            InternalKnowledgeBaseProvider kBaseProvider) {
        this.configuration = configuration;
        this.classLoaderProvider = classLoaderProvider;
        this.kBaseProvider = kBaseProvider;
    }

    @Override
    public PackageRegistry getPackageRegistry(String packageName) {
        return pkgRegistryMap.get(packageName);
    }

    public PackageRegistry getOrCreatePackageRegistry(PackageDescr packageDescr) {
        if (packageDescr == null) {
            return null;
        }
        if (isEmpty(packageDescr.getNamespace())) {
            packageDescr.setNamespace(this.configuration.getDefaultPackageName());
        }
        return pkgRegistryMap.computeIfAbsent(packageDescr.getName(), name -> createPackageRegistry(packageDescr));
    }

    @Override
    public Map<String, PackageRegistry> getPackageRegistry() {
        return null;
    }

    private PackageRegistry createPackageRegistry(PackageDescr packageDescr) {
        initPackage(packageDescr);
        ClassLoader rootClassLoader = this.classLoaderProvider.getRootClassLoader();

        InternalKnowledgePackage pkg;
        InternalKnowledgeBase kBase = kBaseProvider.getKnowledgeBase();
        if (kBase == null || (pkg = kBase.getPackage(packageDescr.getName())) == null) {
            // there is no rulebase or it does not define this package so define it
            pkg = CoreComponentFactory.get().createKnowledgePackage((packageDescr.getName()));
            pkg.setClassFieldAccessorCache(new ClassFieldAccessorCache(rootClassLoader));

            // if there is a rulebase then add the package.
            if (kBase != null) {
                try {
                    pkg = (InternalKnowledgePackage) kBase.addPackage(pkg).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // the RuleBase will also initialise the
                pkg.getDialectRuntimeRegistry().onAdd(rootClassLoader);
            }
        }

        PackageRegistry pkgRegistry = new PackageRegistry(rootClassLoader, configuration, pkg);

        // add default import for this namespace
        pkgRegistry.addImport(new ImportDescr(packageDescr.getNamespace() + ".*"));

        for (ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.registerImport(importDescr.getTarget());
        }

        return pkgRegistry;
    }


    private void initPackage(PackageDescr packageDescr) {
        //Gather all imports for all PackageDescrs for the current package and replicate into
        //all PackageDescrs for the current package, thus maintaining a complete list of
        //ImportDescrs for all PackageDescrs for the current package.
        List<PackageDescr> packageDescrsForPackage = packages.computeIfAbsent(packageDescr.getName(), k -> new ArrayList<>());
        packageDescrsForPackage.add(packageDescr);
        Set<ImportDescr> imports = new HashSet<>();
        for (PackageDescr pd : packageDescrsForPackage) {
            imports.addAll(pd.getImports());
        }
        for (PackageDescr pd : packageDescrsForPackage) {
            pd.getImports().clear();
            pd.addAllImports(imports);
        }

        //Copy package level attributes for inclusion on individual rules
        if (!packageDescr.getAttributes().isEmpty()) {
            Map<String, AttributeDescr> pkgAttributes = packageAttributes.get(packageDescr.getNamespace());
            if (pkgAttributes == null) {
                pkgAttributes = new HashMap<>();
                this.packageAttributes.put(packageDescr.getNamespace(),
                        pkgAttributes);
            }
            for (AttributeDescr attr : packageDescr.getAttributes()) {
                pkgAttributes.put(attr.getName(),
                        attr);
            }
        }
    }

}
