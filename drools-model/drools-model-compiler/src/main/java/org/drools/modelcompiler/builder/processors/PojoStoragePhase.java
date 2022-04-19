package org.drools.modelcompiler.builder.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.ImportDeclaration;
import org.drools.modelcompiler.builder.CanonicalModelBuildContext;
import org.drools.modelcompiler.builder.GeneratedClassWithPackage;
import org.drools.util.TypeResolver;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.github.javaparser.StaticJavaParser.parseImport;

public class PojoStoragePhase implements CompilationPhase {


    CanonicalModelBuildContext buildContext;
    PackageRegistryManager pkgRegistryManager;
    private Collection<CompositePackageDescr> packages;

    public PojoStoragePhase(CanonicalModelBuildContext buildContext, PackageRegistryManager pkgRegistryManager, Collection<CompositePackageDescr> packages) {
        this.buildContext = buildContext;
        this.pkgRegistryManager = pkgRegistryManager;
        this.packages = packages;
    }

    public void process() {
        Collection<GeneratedClassWithPackage> allGeneratedPojos = buildContext.getAllGeneratedPojos();
        Map<String, Class<?>> allCompiledClasses = buildContext.getAllCompiledClasses();

        for (CompositePackageDescr packageDescr : packages) {
            InternalKnowledgePackage pkg = pkgRegistryManager.getPackageRegistry(packageDescr.getNamespace()).getPackage();
            allGeneratedPojos.stream()
                    .filter( pojo -> isInPackage(pkg, pojo) )
                    .forEach( pojo -> registerType(pkg.getTypeResolver(), allCompiledClasses.get(pojo.getFullyQualifiedName())) );
        }
    }

    private boolean isInPackage(InternalKnowledgePackage pkg, GeneratedClassWithPackage pojo) {
        return pkg.getName().equals( pojo.getPackageName() ) || pkg.getImports().values().stream().anyMatch( i -> hasImport( i, pojo ) );
    }

    private boolean hasImport(ImportDeclaration imp, GeneratedClassWithPackage pojo ) {
        com.github.javaparser.ast.ImportDeclaration impDec = parseImport("import " + imp.getTarget() + ";");
        return impDec.getNameAsString().equals( impDec.isAsterisk() ? pojo.getPackageName() : pojo.getFullyQualifiedName() );
    }

    public static void registerType(TypeResolver typeResolver, Class<?> clazz) {
        typeResolver.registerClass(clazz.getCanonicalName(), clazz);
        typeResolver.registerClass(clazz.getSimpleName(), clazz);
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return Collections.emptyList();
    }
}
