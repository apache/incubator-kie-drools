package org.drools.model.codegen.execmodel.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.TypeDeclarationFactory;
import org.drools.compiler.builder.impl.processors.AbstractPackageCompilationPhase;
import org.drools.compiler.builder.impl.processors.AnnotationNormalizer;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.TypeDeclaration;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.drools.drl.ast.descr.AnnotatedBaseDescr;
import org.drools.drl.ast.descr.EnumDeclarationDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.model.codegen.execmodel.errors.UnsupportedFeatureError;
import org.drools.util.TypeResolver;

import static org.drools.compiler.builder.impl.ClassDefinitionFactory.createClassDefinition;
import static org.drools.base.util.Drools.hasMvel;

public class DeclaredTypeRegistrationPhase extends AbstractPackageCompilationPhase {

    private final PackageRegistryManager pkgRegistryManager;

    public DeclaredTypeRegistrationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, PackageRegistryManager pkgRegistryManager) {
        super(pkgRegistry, packageDescr);
        this.pkgRegistryManager = pkgRegistryManager;
    }

    @Override
    public void process() {
        for (TypeDeclarationDescr typeDescr : packageDescr.getTypeDeclarations()) {
            processTypeDeclarationDescr(pkgRegistry.getPackage(), typeDescr);
        }
        for (EnumDeclarationDescr enumDeclarationDescr : packageDescr.getEnumDeclarations()) {
            processTypeDeclarationDescr(pkgRegistry.getPackage(), enumDeclarationDescr);
        }

    }

    private void processTypeDeclarationDescr(InternalKnowledgePackage pkg, AbstractClassTypeDeclarationDescr typeDescr) {
        normalizeAnnotations(typeDescr, pkg.getTypeResolver(), false);
        try {
            Class<?> typeClass = pkg.getTypeResolver().resolveType( typeDescr.getTypeName() );
            String typePkg = typeClass.getPackage().getName();
            String typeName = typeClass.getName().substring( typePkg.length() + 1 );
            TypeDeclaration type = new TypeDeclaration(typeName );
            type.setTypeClass( typeClass );
            type.setResource( typeDescr.getResource() );
            if (hasMvel()) {
                type.setTypeClassDef( createClassDefinition( typeClass, typeDescr.getResource() ) );
            }
            TypeDeclarationFactory.processAnnotations(typeDescr, type);
            if (!type.isTypesafe()) {
                results.addBuilderResult(new UnsupportedFeatureError("@typesafe(false) is not supported in executable model : " + type));
            }
            pkgRegistryManager.getOrCreatePackageRegistry(
                    new PackageDescr(typePkg)).getPackage().addTypeDeclaration(type );
        } catch (ClassNotFoundException e) {
            TypeDeclaration type = new TypeDeclaration( typeDescr.getTypeName() );
            type.setResource( typeDescr.getResource() );
            TypeDeclarationFactory.processAnnotations(typeDescr, type);
            pkg.addTypeDeclaration( type );
        }
    }

    protected void normalizeAnnotations(AnnotatedBaseDescr annotationsContainer, TypeResolver typeResolver, boolean isStrict) {
        AnnotationNormalizer annotationNormalizer =
                AnnotationNormalizer.of(
                        typeResolver,
                        isStrict);

        annotationNormalizer.normalize(annotationsContainer);
        results.addAll(annotationNormalizer.getResults());
    }
}
