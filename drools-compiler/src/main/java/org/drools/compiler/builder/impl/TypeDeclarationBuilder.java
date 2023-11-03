/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.builder.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.base.rule.TypeDeclaration;
import org.drools.compiler.builder.impl.classbuilder.ClassBuilder;
import org.drools.compiler.builder.impl.classbuilder.ClassBuilderFactory;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.EnumDeclarationDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.QualifiedName;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.kie.api.io.Resource;
import org.kie.internal.builder.ResourceChange;

public class TypeDeclarationBuilder {

    protected final TypeDeclarationContext context;

    protected final BuildResultCollector results;

    protected final Set<String> generatedTypes = new HashSet<>();

    protected TypeDeclarationCache classDeclarationExtractor;
    protected TypeDeclarationNameResolver typeDeclarationNameResolver;
    protected TypeDeclarationFactory typeDeclarationFactory;
    protected ClassDefinitionFactory classDefinitionFactory;
    protected TypeDeclarationConfigurator typeDeclarationConfigurator;
    protected DeclaredClassBuilder declaredClassBuilder;

    public TypeDeclarationBuilder(TypeDeclarationContext context, BuildResultCollector results) {
        this.context = context;
        this.results = results;
        this.classDeclarationExtractor = new TypeDeclarationCache(context, results);
        this.typeDeclarationNameResolver = new TypeDeclarationNameResolver(context, results);
        this.typeDeclarationFactory = new TypeDeclarationFactory(context, results);
        this.classDefinitionFactory = new ClassDefinitionFactory(context, results);
        this.typeDeclarationConfigurator = new TypeDeclarationConfigurator(context, results);
        this.declaredClassBuilder = new DeclaredClassBuilder(context, results);
    }

    public TypeDeclaration getAndRegisterTypeDeclaration( Class<?> cls, String packageName ) {
        return classDeclarationExtractor.getAndRegisterTypeDeclaration( cls, packageName );
    }

    public TypeDeclaration getExistingTypeDeclaration( String className ) {
        return classDeclarationExtractor.getCachedTypeDeclaration( className );
    }

    public TypeDeclaration getTypeDeclaration( Class<?> cls ) {
        return classDeclarationExtractor.getTypeDeclaration( cls );
    }

    public Collection<String> removeTypesGeneratedFromResource( Resource resource ) {
        Collection<String> removedTypes = classDeclarationExtractor.removeTypesGeneratedFromResource( resource );
        generatedTypes.removeAll( removedTypes );
        return removedTypes;
    }

    void registerGeneratedType(AbstractClassTypeDeclarationDescr typeDescr) {
        String fullName = typeDescr.getType().getFullName();
        generatedTypes.add(fullName);
    }



    /**********************************************************************************************************************************************************************
        1) Process the TypeDeclaration Descriptors
         Resolve names
         Normalize field descriptors
    **********************************************************************************************************************************************************************/



    public void processTypeDeclarations( Collection<? extends PackageDescr> packageDescrs,
                                         Collection<AbstractClassTypeDeclarationDescr> unsortedDescrs,
                                         List<TypeDefinition> unresolvedTypes,
                                         Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs ) {

        packageDescrs.forEach( context::getOrCreatePackageRegistry );
        packageDescrs.forEach(packageDescr -> setResourcesInDescriptors(packageDescr, null));

        // ensure all names are fully qualified before continuing
        typeDeclarationNameResolver.resolveTypes( packageDescrs, unresolvedTypes );

        // create "implicit" packages
        packageDescrs.forEach( this::normalizeForeignPackages );

        processUnresolvedTypes( null, null, unsortedDescrs, unresolvedTypes, unprocesseableDescrs );
    }

    public void processTypeDeclarations( PackageDescr packageDescr,
                                         PackageRegistry pkgRegistry,
                                         Resource currentResource,
                                         Collection<AbstractClassTypeDeclarationDescr> unsortedDescrs,
                                         List<TypeDefinition> unresolvedTypes,
                                         Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs ) {

        setResourcesInDescriptors( packageDescr, currentResource );

        // ensure all names are fully qualified before continuing
        typeDeclarationNameResolver.resolveTypes( packageDescr, unresolvedTypes, pkgRegistry.getTypeResolver() );

        // create "implicit" packages
        normalizeForeignPackages( packageDescr );

        processUnresolvedTypes( packageDescr, pkgRegistry, unsortedDescrs, unresolvedTypes, unprocesseableDescrs );
    }

    private void processUnresolvedTypes( PackageDescr packageDescr,
                                         PackageRegistry pkgRegistry,
                                         Collection<AbstractClassTypeDeclarationDescr> unsortedDescrs,
                                         List<TypeDefinition> unresolvedTypes,
                                         Map<String, AbstractClassTypeDeclarationDescr> unprocesseableDescrs ) {
        // merge "duplicate" definitions and declarations
        unsortedDescrs = compactDefinitionsAndDeclarations( unsortedDescrs, unprocesseableDescrs );

        // now sort declarations by mutual dependencies
        ClassHierarchyManager classHierarchyManager = new ClassHierarchyManager( unsortedDescrs, context, results );

        for ( AbstractClassTypeDeclarationDescr typeDescr : classHierarchyManager.getSortedDescriptors() ) {
            PackageRegistry pkgReg = getPackageRegistry( pkgRegistry, packageDescr, typeDescr );
            createBean( typeDescr, results, pkgReg, classHierarchyManager, unresolvedTypes, unprocesseableDescrs );
        }

        for ( AbstractClassTypeDeclarationDescr typeDescr : classHierarchyManager.getSortedDescriptors() ) {
            if ( ! unprocesseableDescrs.containsKey( typeDescr.getType().getFullName() ) ) {
                PackageRegistry pkgReg = getPackageRegistry( pkgRegistry, packageDescr, typeDescr );
                InternalKnowledgePackage pkg = pkgReg.getPackage();
                TypeDeclaration type = pkg.getTypeDeclaration( typeDescr.getType().getName() );
                typeDeclarationConfigurator.wireFieldAccessors( pkgReg, typeDescr, type );

                if (context.getKnowledgeBase() != null) {
                    // in case of incremental compilatoin (re)register the new type declaration on the existing kbase
                    context.getKnowledgeBase().registerTypeDeclaration( type, pkg );
                }
            }
        }
    }

    private PackageRegistry getPackageRegistry( PackageRegistry pkgRegistry, PackageDescr packageDescr, AbstractClassTypeDeclarationDescr typeDescr ) {
        return pkgRegistry != null && typeDescr.getNamespace().equals( packageDescr.getName() ) ? pkgRegistry : context.getPackageRegistry( typeDescr.getNamespace() );
    }

    private Collection<AbstractClassTypeDeclarationDescr> compactDefinitionsAndDeclarations( Collection<AbstractClassTypeDeclarationDescr> unsortedDescrs, Map<String, AbstractClassTypeDeclarationDescr> unprocesseableDescrs ) {
        Map<String,AbstractClassTypeDeclarationDescr> compactedUnsorted = new HashMap<>( unsortedDescrs.size() );
        for ( AbstractClassTypeDeclarationDescr descr : unsortedDescrs ) {
            if ( compactedUnsorted.containsKey( descr.getType().getFullName() ) ) {
                AbstractClassTypeDeclarationDescr prev = compactedUnsorted.get( descr.getType().getFullName() );
                boolean res = mergeTypeDescriptors( prev, descr );
                if ( ! res ) {
                    unprocesseableDescrs.put( prev.getType().getFullName(), prev );
                    results.addBuilderResult( new TypeDeclarationError( prev,
                                                                         "Found duplicate declaration for type " + prev.getType().getFullName() + ", unable to reconcile " ) );
                }
            } else {
                compactedUnsorted.put( descr.getType().getFullName(), descr );
            }
        }
        return compactedUnsorted.values();
    }

    private boolean mergeTypeDescriptors( AbstractClassTypeDeclarationDescr prev, AbstractClassTypeDeclarationDescr descr ) {
        boolean isDef1 = isDefinition( prev );
        boolean isDef2 = isDefinition( descr );

        if ( isDef1 && isDef2 ) {
            return false;
        }
        if ( ! prev.getSuperTypes().isEmpty() && ! descr.getSuperTypes().isEmpty()
             && prev.getSuperTypes().size() != descr.getSuperTypes().size() ) {
            return false;
        }

        if ( prev.getSuperTypes().isEmpty() ) {
            for ( QualifiedName qn : descr.getSuperTypes() ) {
                ((TypeDeclarationDescr) prev).addSuperType( qn );
            }
        }
        if ( prev.getFields().isEmpty() ) {
            for ( String fieldName : descr.getFields().keySet() ) {
                prev.addField( descr.getFields().get( fieldName ) );
            }
        }
        for ( AnnotationDescr ad : descr.getAnnotations() ) {
            prev.addQualifiedAnnotation( ad );
        }
        for ( AnnotationDescr ad : prev.getAnnotations() ) {
            if ( ! descr.getAnnotations().contains( ad ) ) {
                descr.addQualifiedAnnotation( ad );
            }
        }
        return true;
    }

    private boolean isDefinition( AbstractClassTypeDeclarationDescr prev ) {
        return ! prev.getFields().isEmpty();
    }

    private void setResourcesInDescriptors( PackageDescr packageDescr, Resource currentResource ) {
        if (currentResource == null) {
            return;
        }
        for ( AbstractClassTypeDeclarationDescr typeDescr : packageDescr.getClassAndEnumDeclarationDescrs() ) {
            if ( typeDescr.getResource() == null ) {
                typeDescr.setResource( currentResource );
            }
        }
    }

    protected void createBean( AbstractClassTypeDeclarationDescr typeDescr,
                             BuildResultCollector results,
                             PackageRegistry pkgRegistry,
                             ClassHierarchyManager hierarchyManager,
                             List<TypeDefinition> unresolvedTypes,
                             Map<String,AbstractClassTypeDeclarationDescr> unprocesseableDescrs ) {

        //descriptor needs fields inherited from superclass
        if ( typeDescr instanceof TypeDeclarationDescr ) {
            hierarchyManager.inheritFields( pkgRegistry, typeDescr, results, unprocesseableDescrs );
        }

        TypeDeclaration type = typeDeclarationFactory.processTypeDeclaration( pkgRegistry, typeDescr );
        boolean success = ! results.hasErrors();

        try {
            // the type declaration is generated in any case (to be used by subclasses, if any)
            // the actual class will be generated only if needed
            ClassDefinition def = null;
            if ( success ) {
                def = classDefinitionFactory.generateDeclaredBean( typeDescr,
                                                                   type,
                                                                   pkgRegistry,
                                                                   unresolvedTypes,
                                                                   unprocesseableDescrs );

                // now use the definition to compare redeclarations, if any
                // this has to be done after the classDef has been generated
                if ( ! type.isNovel() ) {
                    typeDeclarationFactory.checkRedeclaration( typeDescr, type, pkgRegistry );
                }

            }
            success = ( def != null ) && ( ! results.hasErrors() );

            if(success) {
                this.postGenerateDeclaredBean(typeDescr, type, def, pkgRegistry);
            }
            success = ! results.hasErrors();

            if ( success ) {
                ClassBuilder classBuilder = ClassBuilderFactory.get().getClassBuilder(type);
                declaredClassBuilder.generateBeanFromDefinition(typeDescr,
                                                                type,
                                                                pkgRegistry,
                                                                def, classBuilder);
            }
            success = ! results.hasErrors();

            if ( success ) {
                Class<?> clazz = pkgRegistry.getTypeResolver().resolveType( typeDescr.getType().getFullName() );
                type.setTypeClass( clazz );
                type.setValid( true );
            } else {
                unprocesseableDescrs.put( typeDescr.getType().getFullName(), typeDescr );
                type.setValid( false );
            }

            typeDeclarationConfigurator.finalizeConfigurator(type, typeDescr, pkgRegistry, context.getPackageRegistry(), hierarchyManager );

        } catch ( final ClassNotFoundException e ) {
            unprocesseableDescrs.put( typeDescr.getType().getFullName(), typeDescr );
            results.addBuilderResult(new TypeDeclarationError( typeDescr,
                                                                "Class '" + type.getTypeClassName() +
                                                                "' not found for type declaration of '" +
                                                                type.getTypeName() + "'" ) );
        }

        if ( ! success  ) {
            unresolvedTypes.add( new TypeDefinition( type, typeDescr ) );
        } else {
            registerGeneratedType( typeDescr );
        }
    }

    protected void postGenerateDeclaredBean(AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type, ClassDefinition def, PackageRegistry pkgRegistry) {
        // currently used only in drools-traits module
    }

    protected void normalizeForeignPackages( PackageDescr packageDescr ) {
        Map<String, PackageDescr> foreignPackages = null;

        for ( AbstractClassTypeDeclarationDescr typeDescr : packageDescr.getClassAndEnumDeclarationDescrs() ) {
            if ( context.filterAccepts( ResourceChange.Type.DECLARATION, typeDescr.getNamespace(), typeDescr.getTypeName()) ) {

                if ( ! typeDescr.getNamespace().equals( packageDescr.getNamespace() ) ) {
                    // If the type declaration is for a different namespace, process that separately.
                    PackageDescr altDescr;

                    if ( foreignPackages == null ) {
                        foreignPackages = new HashMap<>(  );
                    }

                    if ( foreignPackages.containsKey( typeDescr.getNamespace() ) ) {
                        altDescr = foreignPackages.get( typeDescr.getNamespace() );
                    } else {
                        altDescr = new PackageDescr(typeDescr.getNamespace());
                        altDescr.setResource(packageDescr.getResource());
                        foreignPackages.put( typeDescr.getNamespace(), altDescr );
                    }

                    if (typeDescr instanceof TypeDeclarationDescr) {
                        altDescr.addTypeDeclaration((TypeDeclarationDescr) typeDescr);
                    } else if (typeDescr instanceof EnumDeclarationDescr) {
                        altDescr.addEnumDeclaration((EnumDeclarationDescr) typeDescr);
                    }

                    for (ImportDescr imp : packageDescr.getImports()) {
                        altDescr.addImport(imp);
                    }

                    context.getOrCreatePackageRegistry( altDescr );
                }
            }
        }
    }
}
