package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.GeneratedFact;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.util.asm.ClassFieldInspector;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.PropertyChangeSupport;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.TypeSafe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeDeclarationFactory {

    protected KnowledgeBuilderImpl kbuilder;

    public TypeDeclarationFactory( KnowledgeBuilderImpl kbuilder ) {
        this.kbuilder = kbuilder;
    }

    public TypeDeclaration processTypeDeclaration( PackageRegistry pkgRegistry,
                                                   AbstractClassTypeDeclarationDescr typeDescr ) {

        TypeDeclaration type = kbuilder.getTypeBuilder().getExistingTypeDeclaration( typeDescr.getFullTypeName() );
        if (type == null) {
            type = new TypeDeclaration( typeDescr.getTypeName() );
            type.setResource( typeDescr.getResource() );

            // if is not new, search the already existing declaration and
            // compare them o see if they are at least compatibles
            // check whether it is necessary to build the class or not
            type.setNovel( TypeDeclarationUtils.isNovelClass( typeDescr, pkgRegistry ) );
            type.setNature( type.isNovel() ? TypeDeclaration.Nature.DEFINITION : TypeDeclaration.Nature.DECLARATION );
        }

        processTypeAnnotations(typeDescr, type);
        return type;
    }

    private void processTypeAnnotations( AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type ) {
        try {
            Role role = typeDescr.getTypedAnnotation(Role.class);
            if (role != null) {
                type.setRole(role.value());
            }

            TypeSafe typeSafe = typeDescr.getTypedAnnotation(TypeSafe.class);
            if (typeSafe != null) {
                type.setTypesafe(typeSafe.value());
            }

            if (typeDescr instanceof EnumDeclarationDescr ) {
                type.setKind(TypeDeclaration.Kind.ENUM);
            } else if (typeDescr instanceof TypeDeclarationDescr && ((TypeDeclarationDescr)typeDescr).isTrait()) {
                type.setKind(TypeDeclaration.Kind.TRAIT);
            }

            type.setDynamic( typeDescr.hasAnnotation(PropertyChangeSupport.class) );
        } catch (Exception e) {
            kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, e.getMessage() ) );
        }

    }

    protected void checkRedeclaration( AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type, PackageRegistry pkgRegistry ) {
        TypeDeclaration previousTypeDeclaration = kbuilder.getPackageRegistry( typeDescr.getNamespace() ).getPackage().getTypeDeclaration( typeDescr.getTypeName() );

        try {
            // if there is no previous declaration, then the original declaration was a POJO
            // to the behavior previous these changes
            if ( previousTypeDeclaration == null ) {
                // new declarations of a POJO can't declare new fields,
                // except if the POJO was previously generated/compiled and saved into the kjar
                Class<?> existingDeclarationClass = TypeDeclarationUtils.getExistingDeclarationClass( typeDescr, pkgRegistry );
                if ( ! kbuilder.getBuilderConfiguration().isPreCompiled() &&
                     ! GeneratedFact.class.isAssignableFrom( existingDeclarationClass ) &&
                     ! type.getTypeClassDef().getFields().isEmpty()
                        ) {
                    try {
                        Class existingClass = pkgRegistry.getPackage().getTypeResolver().resolveType( typeDescr.getType().getFullName() );
                        ClassFieldInspector cfi = new ClassFieldInspector( existingClass );

                        int fieldCount = 0;
                        for ( String existingFieldName : cfi.getFieldTypesField().keySet() ) {
                            if ( ! cfi.isNonGetter( existingFieldName )
                                 && ! "class".equals( existingFieldName )
                                 && cfi.getSetterMethods().containsKey( existingFieldName )
                                 && cfi.getGetterMethods().containsKey( existingFieldName )
                                    ) {
                                if ( ! typeDescr.getFields().containsKey( existingFieldName ) ) {
                                    type.setValid(false);
                                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, "New declaration of "+typeDescr.getType().getFullName() +
                                                                                                  " does not include field " + existingFieldName ) );
                                } else {
                                    String fldType = cfi.getFieldTypes().get( existingFieldName ).getName();
                                    fldType = TypeDeclarationUtils.toBuildableType( fldType, kbuilder.getRootClassLoader() );
                                    TypeFieldDescr declaredField = typeDescr.getFields().get( existingFieldName );
                                    if ( ! fldType.equals( type.getTypeClassDef().getField( existingFieldName ).getTypeName() ) ) {
                                        type.setValid(false);
                                        kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, "New declaration of "+typeDescr.getType().getFullName() +
                                                                                                      " redeclared field " + existingFieldName + " : \n" +
                                                                                                      "existing : " + fldType + " vs declared : " + declaredField.getPattern().getObjectType() ) );
                                    } else {
                                        fieldCount++;
                                    }

                                }
                            }
                        }

                        if ( fieldCount != typeDescr.getFields().size() ) {
                            kbuilder.addBuilderResult( reportDeclarationDiff( cfi, typeDescr ) );
                        }
                    } catch ( IOException e ) {
                        e.printStackTrace();
                        type.setValid(false);
                        kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr, "Unable to redeclare " + typeDescr.getType().getFullName() + " : " + e.getMessage() ) );
                    } catch ( ClassNotFoundException e ) {
                        type.setValid(false);
                        kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr, "Unable to redeclare " + typeDescr.getType().getFullName() + " : " + e.getMessage() ) );
                    }
                }
            } else {

                int typeComparisonResult = this.compareTypeDeclarations(previousTypeDeclaration, type);

                if (typeComparisonResult < 0) {
                    //oldDeclaration is "less" than newDeclaration -> error
                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, typeDescr.getType().getFullName()
                                                                                  + " declares more fields than the already existing version"));
                    type.setValid(false);
                } else if (typeComparisonResult > 0 && !type.getTypeClassDef().getFields().isEmpty()) {
                    //oldDeclaration is "grater" than newDeclaration -> error
                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, typeDescr.getType().getFullName()
                                                                                  + " declares less fields than the already existing version"));
                    type.setValid(false);
                }

                //if they are "equal" -> no problem

                // in the case of a declaration, we need to copy all the
                // fields present in the previous declaration
                if (type.getNature() == TypeDeclaration.Nature.DECLARATION) {
                    mergeTypeDeclarations(previousTypeDeclaration, type);
                }
            }

        } catch (IncompatibleClassChangeError error) {
            //if the types are incompatible -> error
            kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, error.getMessage()));
        }

    }


    /**
     * Merges all the missing FactFields from oldDefinition into newDeclaration.
     */
    protected void mergeTypeDeclarations(TypeDeclaration oldDeclaration,
                                       TypeDeclaration newDeclaration) {
        if (oldDeclaration == null) {
            return;
        }

        //add the missing fields (if any) to newDeclaration
        for (FieldDefinition oldFactField : oldDeclaration.getTypeClassDef().getFieldsDefinitions()) {
            FieldDefinition newFactField = newDeclaration.getTypeClassDef().getField(oldFactField.getName());
            if (newFactField == null) {
                newDeclaration.getTypeClassDef().addField(oldFactField);
            }
        }

        //copy the defined class
        newDeclaration.setTypeClass( oldDeclaration.getTypeClass() );
    }

    protected int compareTypeDeclarations(TypeDeclaration oldDeclaration,
                                        TypeDeclaration newDeclaration) throws IncompatibleClassChangeError {

        //different formats -> incompatible
        if (!oldDeclaration.getFormat().equals(newDeclaration.getFormat())) {
            throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " has a different"
                                                   + " format that its previous definition: " + newDeclaration.getFormat() + "!=" + oldDeclaration.getFormat());
        }

        //different superclasses -> Incompatible (TODO: check for hierarchy)
        if (!oldDeclaration.getTypeClassDef().getSuperClass().equals(newDeclaration.getTypeClassDef().getSuperClass())) {
            if (oldDeclaration.getNature() == TypeDeclaration.Nature.DEFINITION
                && newDeclaration.getNature() == TypeDeclaration.Nature.DECLARATION
                && Object.class.getName().equals(newDeclaration.getTypeClassDef().getSuperClass())) {
                // actually do nothing. The new declaration just recalls the previous definition, probably to extend it.
            } else {
                throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " has a different"
                                                       + " superclass that its previous definition: " + newDeclaration.getTypeClassDef().getSuperClass()
                                                       + " != " + oldDeclaration.getTypeClassDef().getSuperClass());
            }
        }

        //different duration -> Incompatible
        if (!nullSafeEqualityComparison(oldDeclaration.getDurationAttribute(), newDeclaration.getDurationAttribute())) {
            throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " has a different"
                                                   + " duration: " + newDeclaration.getDurationAttribute()
                                                   + " != " + oldDeclaration.getDurationAttribute());
        }

        //        //different masks -> incompatible
        if (newDeclaration.getNature().equals(TypeDeclaration.Nature.DEFINITION)) {
            if (oldDeclaration.getSetMask() != newDeclaration.getSetMask()) {
                throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " is incompatible with"
                                                       + " the previous definition: " + newDeclaration
                                                       + " != " + oldDeclaration);
            }
        }

        //TODO: further comparison?

        //Field comparison
        List<FactField> oldFields = oldDeclaration.getTypeClassDef().getFields();
        Map<String, FactField> newFieldsMap = new HashMap<String, FactField>();
        for (FactField factField : newDeclaration.getTypeClassDef().getFields()) {
            newFieldsMap.put(factField.getName(), factField);
        }

        //each of the fields in the old definition that are also present in the
        //new definition must have the same type. If not -> Incompatible
        boolean allFieldsInOldDeclarationAreStillPresent = true;
        for (FactField oldFactField : oldFields) {
            FactField newFactField = newFieldsMap.get(oldFactField.getName());

            if (newFactField != null) {
                //we can't use newFactField.getType() since it throws a NPE at this point.
                String newFactType = ((FieldDefinition) newFactField).getTypeName();

                if (!newFactType.equals( ((FieldDefinition) oldFactField).getTypeName())) {
                    throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + "." + newFactField.getName() + " has a different"
                                                           + " type that its previous definition: " + newFactType
                                                           + " != " + oldFactField.getType().getCanonicalName());
                }
            } else {
                allFieldsInOldDeclarationAreStillPresent = false;
            }

        }

        //If the old declaration has less fields than the new declaration, oldDefinition < newDefinition
        if (oldFields.size() < newFieldsMap.size()) {
            return -1;
        }

        //If the old declaration has more fields than the new declaration, oldDefinition > newDefinition
        if (oldFields.size() > newFieldsMap.size()) {
            return 1;
        }

        //If the old declaration has the same fields as the new declaration,
        //and all the fieds present in the old declaration are also present in
        //the new declaration, then they are considered "equal", otherwise
        //they are incompatible
        if (allFieldsInOldDeclarationAreStillPresent) {
            return 0;
        }

        //Both declarations have the same number of fields, but not all the
        //fields in the old declaration are present in the new declaration.
        throw new IncompatibleClassChangeError(newDeclaration.getTypeName() + " introduces"
                                               + " fields that are not present in its previous version.");

    }

    protected boolean nullSafeEqualityComparison(Comparable c1,
                                               Comparable c2) {
        if (c1 == null) {
            return c2 == null;
        }
        return c2 != null && c1.compareTo(c2) == 0;
    }


    private TypeDeclarationError reportDeclarationDiff( ClassFieldInspector cfi, AbstractClassTypeDeclarationDescr typeDescr) {
        List<String> existing = new ArrayList<String>();
        for ( String existingFieldName : cfi.getFieldTypesField().keySet() ) {
            if ( ! cfi.isNonGetter( existingFieldName ) && ! "class".equals( existingFieldName ) && cfi.getSetterMethods().containsKey( existingFieldName ) ) {
                existing.add( existingFieldName );
            }
        }
        Collections.sort( existing );
        List<String> declared = new ArrayList<String>( typeDescr.getFields().keySet() );
        Collections.sort( declared );
        List<String> deltas = new ArrayList<String>();
        for ( String s : existing ) {
            if ( ! declared.contains( s ) ) {
                deltas.add( "--" + s );
            }
        }
        for ( String s : declared ) {
            if ( ! existing.contains( s ) ) {
                deltas.add( "++" + s );
            }
        }
        return new TypeDeclarationError( typeDescr, "New declaration of " + typeDescr.getType().getFullName() +
                                                    " can't declare a different set of fields \n" +
                                                    "existing : " + existing + "\n" +
                                                    "declared : " + declared + "\n" +
                                                    "diff : " + deltas );
    }

}
