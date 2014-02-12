package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.base.ClassTypeResolver;
import org.drools.core.base.TypeResolver;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.util.ClassUtils;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.core.rule.DialectRuntimeRegistry;
import org.drools.core.rule.ImportDeclaration;
import org.drools.core.spi.Consequence;
import org.kie.api.io.Resource;

import java.util.HashSet;
import java.util.Map;

public class PackageRegistry {
    private final InternalKnowledgePackage pkg;
    private String                           dialect;

    private final DialectRuntimeRegistry     dialectRuntimeRegistry;
    private final DialectCompiletimeRegistry dialectCompiletimeRegistry;

    private final TypeResolver               typeResolver;

    public PackageRegistry(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, InternalKnowledgePackage pkg) {
        this.pkg = pkg;
        this.dialectCompiletimeRegistry = pkgConf.buildDialectRegistry(rootClassLoader, pkgConf, this, pkg);
        this.dialectRuntimeRegistry = pkg.getDialectRuntimeRegistry();

        this.typeResolver = new ClassTypeResolver( new HashSet<String>( this.pkg.getImports().keySet() ),
                                                   rootClassLoader,
                                                   this.pkg.getName() );

        this.typeResolver.addImport( pkg.getName() + ".*" );
        pkg.setTypeResolver(typeResolver);
    }

    private PackageRegistry(InternalKnowledgePackage pkg, DialectRuntimeRegistry runtimeRegistry, DialectCompiletimeRegistry compiletimeRegistry, TypeResolver typeResolver) {
        this.pkg = pkg;
        this.dialectRuntimeRegistry = runtimeRegistry;
        this.dialectCompiletimeRegistry = compiletimeRegistry;
        this.typeResolver = typeResolver;
    }

    PackageRegistry clonePackage(ClassLoader classLoader) {
        InternalKnowledgePackage clonedPkg = ClassUtils.deepClone(pkg, classLoader);
        clonedPkg.setDialectRuntimeRegistry(pkg.getDialectRuntimeRegistry());
        for (org.kie.api.definition.rule.Rule rule : pkg.getRules()) {
            RuleImpl clonedRule = clonedPkg.getRule(rule.getName());
            clonedRule.setConsequence(((RuleImpl)rule).getConsequence());
            if (((RuleImpl)rule).hasNamedConsequences()) {
                for (Map.Entry<String, Consequence> namedConsequence : ((RuleImpl)rule).getNamedConsequences().entrySet()) {
                    clonedRule.addNamedConsequence(namedConsequence.getKey(), namedConsequence.getValue());
                }
            }
        }

        PackageRegistry clone = new PackageRegistry(clonedPkg, dialectRuntimeRegistry, dialectCompiletimeRegistry, typeResolver);
        clone.setDialect(dialect);
        return clone;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public InternalKnowledgePackage getPackage() {
        return pkg;
    }

    public ClassLoader getPackageClassLoader() {
        return getPackage().getPackageClassLoader();
    }

    public DialectRuntimeRegistry getDialectRuntimeRegistry() {
        return dialectRuntimeRegistry;
    }

    public DialectCompiletimeRegistry getDialectCompiletimeRegistry() {
        return dialectCompiletimeRegistry;
    }

    public void addImport(ImportDescr importDescr) {
        registerImport( importDescr.getTarget() );
        this.dialectCompiletimeRegistry.addImport( importDescr );
    }

    public void registerImport(String importEntry) {
        this.pkg.addImport( new ImportDeclaration( importEntry ) );
        this.typeResolver.addImport( importEntry );
    }

    public void addStaticImport(ImportDescr importDescr) {
        this.dialectCompiletimeRegistry.addStaticImport( importDescr );
    }

    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    public void compileAll() {
        this.dialectCompiletimeRegistry.compileAll();
    }

    public boolean removeObjectsGeneratedFromResource(Resource resource) {
        return pkg.removeObjectsGeneratedFromResource(resource);
    }

    public TraitRegistry getTraitRegistry() {
        return pkg.getTraitRegistry();
    }
}
