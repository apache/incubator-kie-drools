package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.core.base.TypeResolver;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.factmodel.ClassBuilder;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.rule.TypeDeclaration;

import static org.drools.core.util.ClassUtils.convertClassToResourcePath;

public class DeclaredClassBuilder {


    protected final KnowledgeBuilderImpl kbuilder;

    public DeclaredClassBuilder( KnowledgeBuilderImpl kbuilder ) {
        this.kbuilder = kbuilder;
    }

    public void generateBeanFromDefinition( AbstractClassTypeDeclarationDescr typeDescr,
                                            TypeDeclaration type,
                                            PackageRegistry pkgRegistry,
                                            ClassDefinition def ) {

        if ( type.isNovel() ) {
            String fullName = typeDescr.getType().getFullName();
            JavaDialectRuntimeData dialect = (JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData("java");
            if ( ensureJavaTypeConsistency( typeDescr, def, pkgRegistry.getTypeResolver() ) ) {
                switch ( type.getKind() ) {
                    case TRAIT:
                        try {
                            buildClass( def, fullName, dialect, this.kbuilder.getBuilderConfiguration().getClassBuilderFactory().getTraitBuilder() );
                        } catch ( Exception e ) {
                            e.printStackTrace();
                            this.kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr,
                                                                                      "Unable to compile declared trait " + fullName +
                                                                                      ": " + e.getMessage() + ";" ) );
                        }
                        break;
                    case ENUM:
                        try {
                            buildClass( def, fullName, dialect, this.kbuilder.getBuilderConfiguration().getClassBuilderFactory().getEnumClassBuilder() );
                        } catch ( Exception e ) {
                            e.printStackTrace();
                            this.kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr,
                                                                                      "Unable to compile declared enum " + fullName +
                                                                                      ": " + e.getMessage() + ";" ) );
                        }
                        break;
                    case CLASS:
                    default:
                        try {
                            buildClass( def, fullName, dialect, this.kbuilder.getBuilderConfiguration().getClassBuilderFactory().getBeanClassBuilder() );
                        } catch ( Exception e ) {
                            e.printStackTrace();
                            this.kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr,
                                                                                      "Unable to create a class for declared type " + fullName +
                                                                                      ": " + e.getMessage() + ";" ) );
                        }
                        break;
                }
            }
        }
    }

    private boolean ensureJavaTypeConsistency( AbstractClassTypeDeclarationDescr typeDescr, ClassDefinition def, TypeResolver typeResolver ) {
        try {
            if ( typeDescr instanceof TypeDeclarationDescr &&  ! ( (TypeDeclarationDescr) typeDescr ).isTrait()
                && typeResolver.resolveType( def.getSuperClass() ).isInterface() ) {
                kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr, "Interfaces cannot be used as super types of normal classes: " + def.getSuperClass() ) );
                return false;
            }
            for ( String sup : def.getInterfaces() ) {
                if ( !typeResolver.resolveType( sup ).isInterface() ) {
                    kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr, "Non-interface type used as super interface : " + sup ) );
                    return false;
                }
            }
        } catch ( ClassNotFoundException cnfe ) {
            kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr, "Unable to resolve parent type :"  + cnfe.getMessage() ) );
            return false;
        }
        return true;
    }

    protected void buildClass( ClassDefinition def, String fullName, JavaDialectRuntimeData dialect, ClassBuilder cb ) throws Exception {
        byte[] bytecode = cb.buildClass(def, kbuilder.getRootClassLoader());
        String resourceName = convertClassToResourcePath(fullName);
        dialect.putClassDefinition( resourceName, bytecode );
        if (kbuilder.getKnowledgeBase() != null) {
            kbuilder.getKnowledgeBase().registerAndLoadTypeDefinition(fullName, bytecode);
        } else {
            if (kbuilder.getRootClassLoader() instanceof ProjectClassLoader ) {
                ((ProjectClassLoader) kbuilder.getRootClassLoader()).defineClass(fullName, resourceName, bytecode);
            } else {
                dialect.write(resourceName, bytecode);
            }
        }
    }


}
