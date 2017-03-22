/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.builder.impl;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.QualifiedName;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.core.base.TypeResolver;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.traits.Alias;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.util.HierarchySorter;
import org.drools.core.util.asm.ClassFieldInspector;
import org.kie.api.definition.type.Key;
import org.kie.api.io.Resource;

public class ClassHierarchyManager {

    protected KnowledgeBuilderImpl kbuilder;

    protected List<AbstractClassTypeDeclarationDescr> sortedDescriptors;
    protected Map<QualifiedName, Collection<QualifiedName>> taxonomy;

    public ClassHierarchyManager( Collection<AbstractClassTypeDeclarationDescr> unsortedDescrs, KnowledgeBuilderImpl kbuilder ) {
        this.kbuilder = kbuilder;
        this.sortedDescriptors = sortByHierarchy( unsortedDescrs, kbuilder );
    }

    public List<AbstractClassTypeDeclarationDescr> getSortedDescriptors() {
        return sortedDescriptors;
    }

    /**
     * Utility method to sort declared beans. Linearizes the hierarchy,
     * i.e.generates a sequence of declaration such that, if Sub is subclass of
     * Sup, then the index of Sub will be > than the index of Sup in the
     * resulting collection. This ensures that superclasses are processed before
     * their subclasses
     */
    protected List<AbstractClassTypeDeclarationDescr> sortByHierarchy( Collection<AbstractClassTypeDeclarationDescr> unsortedDescrs, KnowledgeBuilderImpl kbuilder ) {

        taxonomy = new HashMap<QualifiedName, Collection<QualifiedName>>();
        Map<QualifiedName, AbstractClassTypeDeclarationDescr> cache = new HashMap<QualifiedName, AbstractClassTypeDeclarationDescr>();

        for (AbstractClassTypeDeclarationDescr tdescr : unsortedDescrs) {
            cache.put(tdescr.getType(), tdescr);
        }

        for (AbstractClassTypeDeclarationDescr tdescr : unsortedDescrs) {
            QualifiedName name = tdescr.getType();

            Collection<QualifiedName> supers = taxonomy.get(name);
            if (supers == null) {
                supers = new ArrayList<QualifiedName>();
                taxonomy.put(name, supers);
            } else {
                kbuilder.addBuilderResult(new TypeDeclarationError(tdescr,
                                                                   "Found duplicate declaration for type " + tdescr.getType()));
            }

            boolean circular = false;
            for (QualifiedName sup : tdescr.getSuperTypes()) {
                if (!Object.class.getName().equals(name.getFullName())) {
                    if (!hasCircularDependency(tdescr.getType(), sup, taxonomy)) {
                        if ( cache.containsKey( sup ) ) {
                            supers.add( sup );
                        }
                    } else {
                        circular = true;
                        kbuilder.addBuilderResult(new TypeDeclarationError(tdescr,
                                                                           "Found circular dependency for type " + tdescr.getTypeName()));
                        break;
                    }
                }
            }
            if (circular) {
                tdescr.getSuperTypes().clear();
            }
        }

        for (AbstractClassTypeDeclarationDescr tdescr : unsortedDescrs) {
            for (TypeFieldDescr field : tdescr.getFields().values()) {
                QualifiedName name = tdescr.getType();
                QualifiedName typeName = new QualifiedName(field.getPattern().getObjectType());
                if (!hasCircularDependency(name, typeName, taxonomy)) {
                    if ( cache.containsKey( typeName ) ) {
                        taxonomy.get( name ).add( typeName );
                    }
                } else {
                    field.setRecursive( true );
                }
            }
        }

        List<QualifiedName> sorted = new HierarchySorter<QualifiedName>().sort(taxonomy);
        ArrayList list = new ArrayList( sorted.size() );
        for ( QualifiedName name : sorted ) {
            list.add( cache.get( name ) );
        }

        return list;
    }

    protected static boolean hasCircularDependency(QualifiedName name,
                                                   QualifiedName typeName,
                                                   Map<QualifiedName, Collection<QualifiedName>> taxonomy) {
        if (name.equals( typeName )) {
            return true;
        }
        Collection<QualifiedName> parents = taxonomy.get(typeName);
        if (parents != null) {
            if (parents.contains(name)) {
                return true;
            } else {
                for (QualifiedName ancestor : parents) {
                    if (hasCircularDependency(name, ancestor, taxonomy)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void inheritFields( PackageRegistry pkgRegistry,
                               AbstractClassTypeDeclarationDescr typeDescr,
                               Collection<AbstractClassTypeDeclarationDescr> sortedTypeDescriptors,
                               List<TypeDefinition> unresolvedTypes,
                               Map<String,AbstractClassTypeDeclarationDescr> unprocessableDescrs ) {
        TypeDeclarationDescr tDescr = (TypeDeclarationDescr) typeDescr;
        boolean isNovel = TypeDeclarationUtils.isNovelClass( typeDescr, pkgRegistry );
        boolean inferFields = ! isNovel && typeDescr.getFields().isEmpty();

        for ( QualifiedName qname : tDescr.getSuperTypes() ) {
            //descriptor needs fields inherited from superclass
            mergeInheritedFields(tDescr, unresolvedTypes, unprocessableDescrs, pkgRegistry.getTypeResolver() );
        }

        if ( inferFields ) {
            // not novel, but only an empty declaration was provided.
            // after inheriting the fields from supertypes, now we fill in the locally declared fields
            Class existingClass = TypeDeclarationUtils.getExistingDeclarationClass( typeDescr, pkgRegistry );
            buildDescrsFromFields( existingClass, tDescr, pkgRegistry, tDescr.getFields());
        }
    }

    private static void buildDescrsFromFields( Class klass, TypeDeclarationDescr typeDescr,
                                               PackageRegistry pkgRegistry, Map<String, TypeFieldDescr> fieldMap ) {
        ClassFieldInspector inspector = null;
        try {
            inspector = new ClassFieldInspector( klass );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        for (String name : inspector.getGetterMethods().keySet()) {
            // classFieldAccessor requires both getter and setter
            if (inspector.getSetterMethods().containsKey(name)) {
                if (!inspector.isNonGetter(name) && !"class".equals(name)) {
                    Resource resource = typeDescr.getResource();
                    PatternDescr patternDescr = new PatternDescr( inspector.getFieldType(name).getName());
                    patternDescr.setResource(resource);
                    TypeFieldDescr inheritedFlDescr = new TypeFieldDescr( name, patternDescr);
                    inheritedFlDescr.setResource(resource);
                    inheritedFlDescr.setInherited(!Modifier.isAbstract( inspector.getGetterMethods().get( name ).getModifiers() ));

                    if (!fieldMap.containsKey(inheritedFlDescr.getFieldName()))
                        fieldMap.put(inheritedFlDescr.getFieldName(),
                                               inheritedFlDescr);
                }
            }
        }
    }

    /**
     * In order to build a declared class, the fields inherited from its
     * superclass(es) are added to its declaration. Inherited descriptors are
     * marked as such to distinguish them from native ones. Various scenarioes
     * are possible. (i) The superclass has been declared in the DRL as well :
     * the fields are cloned as inherited (ii) The superclass is imported
     * (external), but some of its fields have been tagged with metadata (iii)
     * The superclass is imported.
     *
     * The search for field descriptors is carried out in the order. (i) and
     * (ii+iii) are mutually exclusive. The search is as such: (i) The
     * superclass' declared fields are used to build the base class additional
     * fields (iii) The superclass is inspected to discover its (public) fields,
     * from which descriptors are generated (ii) Both (i) and (iii) are applied,
     * but the declared fields override the inspected ones
     *
     *
     *
     * @param typeDescr
     *            The base class descriptor, to be completed with the inherited
     *            fields descriptors
     * @return true if all went well
     */
    protected void mergeInheritedFields( TypeDeclarationDescr typeDescr,
                                            List<TypeDefinition> unresolvedTypes,
                                            Map<String,AbstractClassTypeDeclarationDescr> unprocessableDescrs,
                                            TypeResolver typeResolver ) {

        if (typeDescr.getSuperTypes().isEmpty()) {
            return;
        }

        for (int j = typeDescr.getSuperTypes().size() - 1; j >= 0; j--) {
            QualifiedName qname = typeDescr.getSuperTypes().get(j);
            String simpleSuperTypeName = qname.getName();
            String superTypePackageName = qname.getNamespace();
            String fullSuper = qname.getFullName();

            mergeFields( simpleSuperTypeName,
                         superTypePackageName,
                         fullSuper,
                         typeDescr,
                         unresolvedTypes,
                         unprocessableDescrs,
                         typeResolver );
        }
    }

    protected void mergeFields( String simpleSuperTypeName,
                                String superTypePackageName,
                                String fullSuper,
                                TypeDeclarationDescr typeDescr,
                                List<TypeDefinition> unresolvedTypes,
                                Map<String,AbstractClassTypeDeclarationDescr> unprocessableDescrs,
                                TypeResolver resolver ) {

        Map<String, TypeFieldDescr> fieldMap = new LinkedHashMap<String, TypeFieldDescr>();
        boolean isNovel = TypeDeclarationUtils.isNovelClass( typeDescr, kbuilder.getPackageRegistry( typeDescr.getNamespace() ) );

        PackageRegistry registry = kbuilder.getPackageRegistry( superTypePackageName );
        InternalKnowledgePackage pack = null;
        if ( registry != null ) {
            pack = registry.getPackage();
        }

        if ( unprocessableDescrs.containsKey( fullSuper ) ) {
            unprocessableDescrs.put( typeDescr.getType().getFullName(), typeDescr );
            return;
        }

        // if a class is declared in DRL, its package can't be null? The default package is replaced by "defaultpkg"
        boolean isSuperClassTagged = false;
        boolean isSuperClassDeclared = true; //in the same package, or in a previous one

        if ( pack != null ) {

            // look for the supertype declaration in available packages
            TypeDeclaration superTypeDeclaration = pack.getTypeDeclaration( simpleSuperTypeName );

            if (superTypeDeclaration != null && superTypeDeclaration.getTypeClassDef() != null ) {
                ClassDefinition classDef = superTypeDeclaration.getTypeClassDef();
                // inherit fields
                for (org.kie.api.definition.type.FactField fld : classDef.getFields()) {
                    TypeFieldDescr inheritedFlDescr = buildInheritedFieldDescrFromDefinition(fld, typeDescr);
                    fieldMap.put(inheritedFlDescr.getFieldName(),
                                 inheritedFlDescr);
                }

                // new classes are already distinguished from tagged external classes
                isSuperClassTagged = !superTypeDeclaration.isNovel();
            }
        } else {
            isSuperClassDeclared = false;
        }

        // look for the class externally
        if ( !isSuperClassDeclared || isSuperClassTagged ) {
            try {
                Class superKlass;
                if ( registry != null ) {
                    superKlass = registry.getTypeResolver().resolveType(fullSuper);
                } else {
                    // if the supertype has not been declared, and we have got so far, it means that this class is not novel
                    superKlass = resolver.resolveType( fullSuper );
                }
                buildDescrsFromFields( superKlass, typeDescr, registry, fieldMap);
            } catch (ClassNotFoundException cnfe) {
                unprocessableDescrs.put( typeDescr.getType().getFullName(), typeDescr );
                return;
            }
        }

        // finally, locally declared fields are merged. The map swap ensures that super-fields are added in order, before the subclass' ones
        // notice that it is not possible to override a field changing its type
        for ( String fieldName : typeDescr.getFields().keySet() ) {
            if ( fieldMap.containsKey( fieldName ) ) {
                String type1 = fieldMap.get( fieldName ).getPattern().getObjectType();
                String type2 = typeDescr.getFields().get( fieldName ).getPattern().getObjectType();
                if (type2.lastIndexOf(".") < 0) {
                    try {
                        TypeResolver typeResolver = kbuilder.getPackageRegistry( typeDescr.getNamespace() ).getTypeResolver();
                        type1 = typeResolver.resolveType( type1 ).getName();
                        type2 = typeResolver.resolveType( type2 ).getName();
                        // now that we are at it... this will be needed later anyway
                        fieldMap.get( fieldName ).getPattern().setObjectType( type1 );
                        typeDescr.getFields().get( fieldName ).getPattern().setObjectType( type2 );
                    } catch (ClassNotFoundException cnfe) {
                        // will fail later
                    }
                }

                boolean clash = ! type1.equals(type2);
                TypeFieldDescr overriding = null;
                if ( clash ) {
                    // this may still be an override using a subclass of the original type
                    try {
                        Class<?> sup = resolver.resolveType( type1 );
                        Class<?> loc = resolver.resolveType( type2 );
                        if ( sup.isAssignableFrom( loc ) ) {
                            clash = false;
                            // mark as non inherited so that a new field is actually built
                            overriding = fieldMap.get( fieldName );
                        }
                    } catch ( ClassNotFoundException cnfe ) {
                        // not much to do
                    }
                }
                if ( clash ) {
                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                       "Cannot redeclare field '" + fieldName + " from " + type1 + " to " + type2));
                    typeDescr.setType(null,null);
                    return;
                } else {
                    String initVal = fieldMap.get(fieldName).getInitExpr();
                    TypeFieldDescr fd = typeDescr.getFields().get(fieldName);
                    if (fd.getInitExpr() == null) {
                        fd.setInitExpr( initVal );
                    }

                    fd.setInherited( fieldMap.get( fieldName ).isInherited() );
                    fd.setOverriding( overriding );


                    for (String key : fieldMap.get(fieldName).getAnnotationNames()) {
                        if (fd.getAnnotation( key ) == null) {
                            fd.addAnnotation( fieldMap.get( fieldName ).getAnnotation( key ) );
                        }
                    }

                    if (fd.getIndex() < 0) {
                        fd.setIndex( fieldMap.get( fieldName ).getIndex() );
                    }
                }
            }
            fieldMap.put( fieldName,
                          typeDescr.getFields().get( fieldName ) );
        }

        typeDescr.setFields(fieldMap);
    }

    protected TypeFieldDescr buildInheritedFieldDescrFromDefinition(org.kie.api.definition.type.FactField fld, TypeDeclarationDescr typeDescr) {
        TypeFieldDescr inheritedFldDescr = new TypeFieldDescr();
        inheritedFldDescr.setFieldName(fld.getName());
        inheritedFldDescr.setResource(typeDescr.getResource());
        PatternDescr fldType = new PatternDescr();
        fldType.setObjectType( ( (FieldDefinition) fld ).getTypeName() );
        inheritedFldDescr.setPattern(fldType); // also sets resource for PatternDescr fldType
        if (fld.isKey()) {
            AnnotationDescr keyAnnotation = new AnnotationDescr(Key.class.getCanonicalName());
            keyAnnotation.setFullyQualifiedName(Key.class.getCanonicalName());
            keyAnnotation.setResource(typeDescr.getResource());
            inheritedFldDescr.addAnnotation(keyAnnotation);
        }
        inheritedFldDescr.setIndex(((FieldDefinition) fld).getDeclIndex());
        inheritedFldDescr.setInherited(true);

        String initExprOverride = ((FieldDefinition) fld).getInitExpr();
        int overrideCount = 0;
        // only @aliasing local fields may override defaults.
        for (TypeFieldDescr localField : typeDescr.getFields().values()) {
            Alias alias = localField.getTypedAnnotation(Alias.class);
            if (alias != null && fld.getName().equals(alias.value().replaceAll("\"", "")) && localField.getInitExpr() != null) {
                overrideCount++;
                initExprOverride = localField.getInitExpr();
            }
        }
        if (overrideCount > 1) {
            // however, only one is allowed
            initExprOverride = null;
        }
        inheritedFldDescr.setInitExpr(initExprOverride);
        return inheritedFldDescr;
    }


    public void addDeclarationToPackagePreservingOrder( TypeDeclaration type,
                                                        AbstractClassTypeDeclarationDescr typeDescr,
                                                        InternalKnowledgePackage tgtPackage,
                                                        Map<String, PackageRegistry> pkgRegistryMap ) {
        Collection<QualifiedName> parents = taxonomy.get( new QualifiedName( type.getFullName() ) );
        int index = getSortedDescriptors().indexOf( typeDescr );

        if ( parents != null && ! parents.isEmpty() ) {
            for ( QualifiedName parentName : parents ) {
                String nameSpace = parentName.getNamespace();
                String name = parentName.getName();

                PackageRegistry parentPkgRegistry = pkgRegistryMap.get( nameSpace );
                if ( parentPkgRegistry != null ) {
                    TypeDeclaration parentDeclaration = parentPkgRegistry.getPackage().getTypeDeclaration( name );
                    if ( parentDeclaration != null && parentDeclaration.getNature() == TypeDeclaration.Nature.DEFINITION ) {
                        index = Math.max( index, parentDeclaration.getOrder() );
                    }
                }
            }
        }

        type.setOrder( index + 1 );
        tgtPackage.addTypeDeclaration( type );
    }
}
