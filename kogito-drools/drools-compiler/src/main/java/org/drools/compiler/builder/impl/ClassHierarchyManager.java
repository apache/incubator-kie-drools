package org.drools.compiler.builder.impl;

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

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            QualifiedName name = tdescr.getType();

            cache.put(name, tdescr);

            if (taxonomy.get(name) == null) {
                taxonomy.put(name, new ArrayList<QualifiedName>());
            } else {
                kbuilder.addBuilderResult(new TypeDeclarationError(tdescr,
                                                                   "Found duplicate declaration for type " + tdescr.getType()));
            }

            Collection<QualifiedName> supers = taxonomy.get(name);

            boolean circular = false;
            for (QualifiedName sup : tdescr.getSuperTypes()) {
                if (!Object.class.getName().equals(name.getFullName())) {
                    if (!hasCircularDependency(tdescr.getType(), sup, taxonomy)) {
                        supers.add(sup);
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

            for (TypeFieldDescr field : tdescr.getFields().values()) {
                QualifiedName typeName = new QualifiedName(field.getPattern().getObjectType());
                if (!hasCircularDependency(name, typeName, taxonomy)) {
                    supers.add(typeName);
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
        if (taxonomy.containsKey(typeName)) {
            Collection<QualifiedName> parents = taxonomy.get(typeName);
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
            if ( mergeInheritedFields(tDescr, unresolvedTypes, unprocessableDescrs, pkgRegistry.getTypeResolver() ) ) {
                /*
                //descriptor also needs metadata from superclass - NO LONGER SINCE DROOLS 6.x
                for ( AbstractClassTypeDeclarationDescr descr : sortedTypeDescriptors ) {
                    // sortedTypeDescriptors are sorted by inheritance order, so we'll always find the superClass (if any) before the subclass
                    if ( qname.equals( descr.getType() ) ) {
                        typeDescr.getAnnotations().putAll( descr.getAnnotations() );
                        break;
                    } else if ( typeDescr.getType().equals( descr.getType() ) ) {
                        break;
                    }
                }
                */
            }
        }

        if ( inferFields ) {
            // not novel, but only an empty declaration was provided.
            // after inheriting the fields from supertypes, now we fill in the locally declared fields
            try {
                Class existingClass = TypeDeclarationUtils.getExistingDeclarationClass( typeDescr, pkgRegistry );
                ClassFieldInspector inspector = new ClassFieldInspector( existingClass );
                for (String name : inspector.getGetterMethods().keySet()) {
                    // classFieldAccessor requires both getter and setter
                    if (inspector.getSetterMethods().containsKey(name)) {
                        if (!inspector.isNonGetter(name) && !"class".equals(name)) {
                            TypeFieldDescr inheritedFlDescr = new TypeFieldDescr(
                                    name,
                                    new PatternDescr(
                                            inspector.getFieldTypes().get(name).getName()));
                            inheritedFlDescr.setInherited(!Modifier.isAbstract( inspector.getGetterMethods().get( name ).getModifiers() ));

                            if (!tDescr.getFields().containsKey(inheritedFlDescr.getFieldName()))
                                tDescr.getFields().put(inheritedFlDescr.getFieldName(),
                                                       inheritedFlDescr);
                        }
                    }
                }
            } catch ( Exception e ) {
                // can't happen as we know that the class is not novel - that is, it has been resolved before
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
    protected boolean mergeInheritedFields( TypeDeclarationDescr typeDescr,
                                            List<TypeDefinition> unresolvedTypes,
                                            Map<String,AbstractClassTypeDeclarationDescr> unprocessableDescrs,
                                            TypeResolver typeResolver ) {

        if (typeDescr.getSuperTypes().isEmpty())
            return false;
        boolean merge = false;

        for (int j = typeDescr.getSuperTypes().size() - 1; j >= 0; j--) {
            QualifiedName qname = typeDescr.getSuperTypes().get(j);
            String simpleSuperTypeName = qname.getName();
            String superTypePackageName = qname.getNamespace();
            String fullSuper = qname.getFullName();

            merge = mergeFields( simpleSuperTypeName,
                                 superTypePackageName,
                                 fullSuper,
                                 typeDescr,
                                 unresolvedTypes,
                                 unprocessableDescrs,
                                 typeResolver ) || merge;
        }

        return merge;
    }

    protected boolean mergeFields( String simpleSuperTypeName,
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
        } else {
            // If there is no regisrty the type isn't a DRL-declared type, which is forbidden.
            // Avoid NPE JIRA-3041 when trying to access the registry. Avoid subsequent problems.
            // DROOLS-536 At this point, the declarations might exist, but the package might not have been processed yet
            if ( isNovel ) {
                unprocessableDescrs.put( typeDescr.getType().getFullName(), typeDescr );
                return false;
            }
        }

        if ( unprocessableDescrs.containsKey( fullSuper ) ) {
            unprocessableDescrs.put( typeDescr.getType().getFullName(), typeDescr );
            return false;
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
            /*
            else {
                for ( TypeDefinition def : unresolvedTypes ) {
                    if ( def.getTypeClassName().equals( fullSuper ) ) {
                        TypeDeclarationDescr td = (TypeDeclarationDescr) def.typeDescr;
                        for ( TypeFieldDescr tf : td.getFields().values() ) {
                            fieldMap.put( tf.getFieldName(), tf.cloneAsInherited() );
                        }
                        isSuperClassDeclared = def.type.isNovel();
                        break;
                    }
                    isSuperClassDeclared = false;
                }
            }
            */
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
                ClassFieldInspector inspector = new ClassFieldInspector(superKlass);
                for (String name : inspector.getGetterMethods().keySet()) {
                    // classFieldAccessor requires both getter and setter
                    if (inspector.getSetterMethods().containsKey(name)) {
                        if (!inspector.isNonGetter(name) && !"class".equals(name)) {
                            TypeFieldDescr inheritedFlDescr = new TypeFieldDescr(
                                    name,
                                    new PatternDescr(
                                            inspector.getFieldTypes().get(name).getName()));
                            inheritedFlDescr.setInherited(!Modifier.isAbstract(inspector.getGetterMethods().get(name).getModifiers()));

                            if (!fieldMap.containsKey(inheritedFlDescr.getFieldName()))
                                fieldMap.put(inheritedFlDescr.getFieldName(),
                                             inheritedFlDescr);
                        }
                    }
                }

            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("Unable to resolve Type Declaration superclass '" + fullSuper + "'");
            } catch ( IOException e ) {
                e.printStackTrace();
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

                if (!type1.equals(type2)) {
                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                       "Cannot redeclare field '" + fieldName + " from " + type1 + " to " + type2));
                    typeDescr.setType(null,
                                      null);
                    return false;
                } else {
                    String initVal = fieldMap.get(fieldName).getInitExpr();
                    if (typeDescr.getFields().get(fieldName).getInitExpr() == null) {
                        typeDescr.getFields().get(fieldName).setInitExpr(initVal);
                    }
                    typeDescr.getFields().get(fieldName).setInherited(fieldMap.get(fieldName).isInherited());

                    for (String key : fieldMap.get(fieldName).getAnnotationNames()) {
                        if (typeDescr.getFields().get(fieldName).getAnnotation(key) == null) {
                            typeDescr.getFields().get(fieldName).addAnnotation(fieldMap.get(fieldName).getAnnotation(key));
                        }
                    }

                    if (typeDescr.getFields().get(fieldName).getIndex() < 0) {
                        typeDescr.getFields().get(fieldName).setIndex(fieldMap.get(fieldName).getIndex());
                    }
                }
            }
            fieldMap.put(fieldName,
                         typeDescr.getFields().get(fieldName));
        }

        typeDescr.setFields(fieldMap);

        return true;
    }

    protected TypeFieldDescr buildInheritedFieldDescrFromDefinition(org.kie.api.definition.type.FactField fld, TypeDeclarationDescr typeDescr) {
        PatternDescr fldType = new PatternDescr();
        TypeFieldDescr inheritedFldDescr = new TypeFieldDescr();
        inheritedFldDescr.setFieldName(fld.getName());
        fldType.setObjectType( ( (FieldDefinition) fld ).getTypeName() );
        inheritedFldDescr.setPattern(fldType);
        if (fld.isKey()) {
            AnnotationDescr keyAnnotation = new AnnotationDescr(Key.class.getCanonicalName());
            keyAnnotation.setFullyQualifiedName(Key.class.getCanonicalName());
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
