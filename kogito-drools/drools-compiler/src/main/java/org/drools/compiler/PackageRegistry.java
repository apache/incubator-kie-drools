package org.drools.compiler;

import java.util.HashSet;
import java.util.Map;

import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.core.util.ClassUtils;
import org.drools.rule.DialectRuntimeRegistry;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;

public class PackageRegistry {
    private final Package              pkg;
    private String                     dialect;

    private final DialectRuntimeRegistry     dialectRuntimeRegistry;
    private final DialectCompiletimeRegistry dialectCompiletimeRegistry;

    private final TypeResolver         typeResolver;

    public PackageRegistry(PackageBuilder packageBuilder, Package pkg) {
        this.pkg = pkg;
        this.dialectCompiletimeRegistry = packageBuilder.getPackageBuilderConfiguration().buildDialectRegistry( packageBuilder,
                                                                                                                this,
                                                                                                                pkg );
        this.dialectRuntimeRegistry = pkg.getDialectRuntimeRegistry();

        this.typeResolver = new ClassTypeResolver( new HashSet<String>( this.pkg.getImports().keySet() ),
                                                   packageBuilder.getRootClassLoader(),
                                                   this.pkg.getName() );

        this.typeResolver.addImport( pkg.getName() + ".*" );
        pkg.setTypeResolver(typeResolver);
    }

    private PackageRegistry(Package pkg, DialectRuntimeRegistry runtimeRegistry, DialectCompiletimeRegistry compiletimeRegistry, TypeResolver typeResolver) {
        this.pkg = pkg;
        this.dialectRuntimeRegistry = runtimeRegistry;
        this.dialectCompiletimeRegistry = compiletimeRegistry;
        this.typeResolver = typeResolver;
    }

    PackageRegistry clonePackage(ClassLoader classLoader) {
        Package clonedPkg = ClassUtils.deepClone(pkg, classLoader);
        clonedPkg.setDialectRuntimeRegistry(pkg.getDialectRuntimeRegistry());
        for (Rule rule : pkg.getRules()) {
            Rule clonedRule = clonedPkg.getRule(rule.getName());
            clonedRule.setConsequence(rule.getConsequence());
            if (rule.hasNamedConsequences()) {
                for (Map.Entry<String, Consequence> namedConsequence : rule.getNamedConsequences().entrySet()) {
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

    public Package getPackage() {
        return pkg;
    }

    public DialectRuntimeRegistry getDialectRuntimeRegistry() {
        return dialectRuntimeRegistry;
    }

    public DialectCompiletimeRegistry getDialectCompiletimeRegistry() {
        return dialectCompiletimeRegistry;
    }

    public void addImport(String importEntry) {
        this.pkg.addImport( new ImportDeclaration( importEntry ) );
        this.typeResolver.addImport( importEntry );
        this.dialectCompiletimeRegistry.addImport( importEntry );
    }

    public void addStaticImport(String staticImport) {
        this.dialectCompiletimeRegistry.addStaticImport( staticImport );
    }

    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    public void compileAll() {
        this.dialectCompiletimeRegistry.compileAll();
    }

}
