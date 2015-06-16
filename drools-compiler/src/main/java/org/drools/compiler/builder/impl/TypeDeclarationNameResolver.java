package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.QualifiedName;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.core.base.TypeResolver;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.util.ClassUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TypeDeclarationNameResolver {

    private KnowledgeBuilderImpl kbuilder;


    public TypeDeclarationNameResolver( KnowledgeBuilderImpl kbuilder ) {
        this.kbuilder = kbuilder;
    }


    public void resolveTypes( Collection<? extends PackageDescr> packageDescrs,
                              Collection<AbstractClassTypeDeclarationDescr> unsortedDescrs,
                              List<TypeDefinition> unresolvedTypes,
                              Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs ) {
        ensureQualifiedNames( packageDescrs, unresolvedTypes, unprocesseableDescrs );
    }

    protected void ensureQualifiedNames( Collection<? extends PackageDescr> packageDescrs,
                                         List<TypeDefinition> unresolvedTypes,
                                         Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs ) {
        for ( PackageDescr packageDescr : packageDescrs ) {
            for ( AbstractClassTypeDeclarationDescr descr : packageDescr.getClassAndEnumDeclarationDescrs() ) {
                ensureQualifiedDeclarationName( descr,
                                                packageDescr,
                                                kbuilder.getPackageRegistry( packageDescr.getName() ).getTypeResolver(),
                                                unresolvedTypes );
            }
        }

        for ( PackageDescr packageDescr : packageDescrs ) {
            for ( TypeDeclarationDescr declarationDescr : packageDescr.getTypeDeclarations() ) {
                qualifyNames( declarationDescr, packageDescr, unresolvedTypes, unprocesseableDescrs );
                discoverHierarchyForRedeclarations( declarationDescr, packageDescr );
            }
            for ( EnumDeclarationDescr enumDeclarationDescr : packageDescr.getEnumDeclarations() ) {
                qualifyNames( enumDeclarationDescr, packageDescr, unresolvedTypes, unprocesseableDescrs );
            }
        }
    }

    private void qualifyNames( AbstractClassTypeDeclarationDescr declarationDescr,
                               PackageDescr packageDescr,
                               List<TypeDefinition> unresolvedTypes,
                               Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs ) {
        ensureQualifiedSuperType( declarationDescr,
                                  packageDescr,
                                  kbuilder.getPackageRegistry( packageDescr.getName() ).getTypeResolver(),
                                  unresolvedTypes );
        ensureQualifiedFieldType( declarationDescr,
                                  packageDescr,
                                  kbuilder.getPackageRegistry( packageDescr.getName() ).getTypeResolver(),
                                  unresolvedTypes );
    }

    private void discoverHierarchyForRedeclarations( TypeDeclarationDescr typeDescr, PackageDescr packageDescr ) {
        PackageRegistry pkReg = kbuilder.getPackageRegistry( packageDescr.getName() );
        if ( ! TypeDeclarationUtils.isNovelClass( typeDescr, pkReg ) ) {
            Class typeClass = TypeDeclarationUtils.getExistingDeclarationClass( typeDescr, pkReg );
            if ( typeClass != null && typeDescr.isTrait() ) {
                fillStaticInterfaces( typeDescr, typeClass );
            } else {
                typeDescr.getSuperTypes().clear();
                typeDescr.addSuperType( typeClass.isInterface() || typeClass == Object.class ?
                                        Object.class.getName() :
                                        typeClass.getSuperclass().getName() );
            }
        }
    }

    private void ensureQualifiedDeclarationName( AbstractClassTypeDeclarationDescr declarationDescr,
                                                 PackageDescr packageDescr,
                                                 TypeResolver typeResolver,
                                                 List<TypeDefinition> unresolvedTypes ) {
        if ( ! declarationDescr.getType().isFullyQualified() ) {
            String resolved = resolveName( declarationDescr.getType().getFullName(),
                                           declarationDescr,
                                           packageDescr,
                                           typeResolver,
                                           unresolvedTypes,
                                           false );

            if ( resolved != null && ! alreadyDefinedInPackage( resolved, declarationDescr, packageDescr ) ) {
                declarationDescr.setTypeName( resolved );
            } else {
                // fall back to declaring package name - this should actually be the default in general
                declarationDescr.setNamespace( packageDescr.getNamespace() );
            }
        }
    }

    private boolean alreadyDefinedInPackage( String resolved, AbstractClassTypeDeclarationDescr current, PackageDescr pd ) {
        for ( AbstractClassTypeDeclarationDescr typeDeclaration : pd.getClassAndEnumDeclarationDescrs() ) {
            if ( typeDeclaration != current && typeDeclaration.getType().getFullName().equals( resolved ) ) {
                return true;
            }
        }
        return false;
    }

    private void ensureQualifiedSuperType( AbstractClassTypeDeclarationDescr typeDescr,
                                           PackageDescr packageDescr,
                                           TypeResolver typeResolver,
                                           List<TypeDefinition> unresolvedTypes ) {
        for ( QualifiedName qname : typeDescr.getSuperTypes() ) {
            String declaredSuperType = qname.getFullName();

            String resolved = resolveName( declaredSuperType,
                                           typeDescr,
                                           packageDescr,
                                           typeResolver,
                                           unresolvedTypes,
                                           true );

            if ( resolved != null ) {
                qname.setName( resolved );
            } else {
                kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr,
                                                                     "Cannot resolve supertype '" + declaredSuperType +
                                                                     " for declared type " + typeDescr.getTypeName() ) );
            }
        }
    }

    public void ensureQualifiedFieldType( AbstractClassTypeDeclarationDescr typeDescr,
                                          PackageDescr packageDescr,
                                          TypeResolver typeResolver,
                                          List<TypeDefinition> unresolvedTypes ) {

        for ( TypeFieldDescr field : typeDescr.getFields().values() ) {
            String declaredType = field.getPattern().getObjectType();
            String resolved = resolveName( declaredType,
                                           typeDescr,
                                           packageDescr,
                                           typeResolver,
                                           unresolvedTypes,
                                           true );

            if ( resolved != null ) {
                field.getPattern().setObjectType( resolved );
            } else {
                kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr,
                                                                     "Cannot resolve type '" + declaredType + " for field " + field.getFieldName() +
                                                                     " in declared type "  + typeDescr.getTypeName() ) );
            }
        }
    }

    private String resolveName( String type,
                                AbstractClassTypeDeclarationDescr typeDescr,
                                PackageDescr packageDescr,
                                TypeResolver typeResolver,
                                List<TypeDefinition> unresolvedTypes,
                                boolean forceResolution ) {
        boolean qualified = TypeDeclarationUtils.isQualified( type );

        if ( ! qualified ) {
            type = TypeDeclarationUtils.lookupSimpleNameByImports( type, typeDescr, packageDescr, kbuilder.getRootClassLoader() );
        }

        // if not qualified yet, it has to be resolved
        // DROOLS-677 : if qualified, it may be a partial name (e.g. an inner class)
        type = TypeDeclarationUtils.resolveType( type,
                                                 packageDescr,
                                                 kbuilder.getPackageRegistry( packageDescr.getNamespace() ) );
        qualified = TypeDeclarationUtils.isQualified( type );


        if ( ! qualified ) {
            try {
                Class klass = typeResolver.resolveType( type, TypeResolver.EXCLUDE_ANNOTATION_CLASS_FILTER );
                type = klass.getCanonicalName();
                return type;
            } catch ( ClassNotFoundException e ) {
                //e.printStackTrace();
            }
        } else {
            type = TypeDeclarationUtils.typeName2ClassName( type, kbuilder.getRootClassLoader() );
            qualified = TypeDeclarationUtils.isQualified( type );
        }

        if ( forceResolution && ! qualified ) {
            TypeDeclaration temp = new TypeDeclaration( type );
            temp.setTypeClassName( type );
            unresolvedTypes.add( new TypeDefinition( temp, null ) );
        }
        return qualified ? type : null;
    }


    private void fillStaticInterfaces( TypeDeclarationDescr typeDescr, Class<?> typeClass ) {
        for ( Class iKlass : ClassUtils.getMinimalImplementedInterfaceNames( typeClass ) ) {
            typeDescr.addSuperType( iKlass.getName() );
        }

    }


}
