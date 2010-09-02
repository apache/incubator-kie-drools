package org.drools.compiler;

import java.util.HashSet;

import org.drools.base.ClassTypeResolver;
import org.drools.base.TypeResolver;
import org.drools.rule.DialectRuntimeRegistry;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.Package;

public class PackageRegistry {
    private Package                    pkg;
    private String                     dialect;

    private DialectRuntimeRegistry     dialectRuntimeRegistry;
    private DialectCompiletimeRegistry dialectCompiletimeRegistry;

    private TypeResolver               typeResolver;

    public PackageRegistry(PackageBuilder packageBuilder,
                           Package pkg) {
        this.pkg = pkg;
        this.dialectCompiletimeRegistry = packageBuilder.getPackageBuilderConfiguration().buildDialectRegistry( packageBuilder,
                                                                                                                this,
                                                                                                                pkg );
        this.dialectRuntimeRegistry = pkg.getDialectRuntimeRegistry();

        this.typeResolver = new ClassTypeResolver( new HashSet<String>( this.pkg.getImports().keySet() ),
                                                   packageBuilder.getRootClassLoader() );

        this.typeResolver.addImport( pkg.getName() + ".*" );
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
