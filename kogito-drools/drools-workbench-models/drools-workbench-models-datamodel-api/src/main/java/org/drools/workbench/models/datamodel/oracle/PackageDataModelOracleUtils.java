package org.drools.workbench.models.datamodel.oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.commons.shared.imports.Import;
import org.drools.workbench.models.commons.shared.imports.Imports;
import org.drools.workbench.models.commons.shared.oracle.model.TypeSource;
import org.drools.workbench.models.commons.shared.oracle.model.Annotation;
import org.drools.workbench.models.commons.shared.oracle.model.MethodInfo;
import org.drools.workbench.models.commons.shared.oracle.model.ModelField;

/**
 * Utilities for PackageDataModelOracle
 */
public class PackageDataModelOracleUtils {

    //Filter and rename Model Fields based on package name and imports
    public static Map<String, ModelField[]> filterModelFields( final String packageName,
                                                               final Imports imports,
                                                               final Map<String, ModelField[]> projectModelFields ) {
        final Map<String, ModelField[]> scopedModelFields = new HashMap<String, ModelField[]>();
        for ( Map.Entry<String, ModelField[]> e : projectModelFields.entrySet() ) {
            final String mfQualifiedType = e.getKey();
            final String mfPackageName = getPackageName( mfQualifiedType );
            final String mfTypeName = getTypeName( mfQualifiedType );

            if ( mfPackageName.equals( packageName ) || isImported( mfQualifiedType,
                                                                    imports ) ) {
                scopedModelFields.put( mfTypeName,
                                       correctModelFields( packageName,
                                                           e.getValue(),
                                                           imports ) );
            }
        }
        return scopedModelFields;
    }

    //Filter and rename Collection Types based on package name and imports
    public static Map<String, Boolean> filterCollectionTypes( final String packageName,
                                                              final Imports imports,
                                                              final Map<String, Boolean> projectCollectionTypes ) {
        final Map<String, Boolean> scopedCollectionTypes = new HashMap<String, Boolean>();
        for ( Map.Entry<String, Boolean> e : projectCollectionTypes.entrySet() ) {
            final String collectionQualifiedType = e.getKey();
            final String collectionPackageName = getPackageName( collectionQualifiedType );
            final String collectionTypeName = getTypeName( collectionQualifiedType );

            if ( collectionPackageName.equals( packageName ) || isImported( collectionQualifiedType,
                                                                            imports ) ) {
                scopedCollectionTypes.put( collectionTypeName,
                                           e.getValue() );
            }
        }
        return scopedCollectionTypes;
    }

    //Filter and rename Global Types based on package name and imports
    public static Map<String, String> filterGlobalTypes( final String packageName,
                                                         final Imports imports,
                                                         final Map<String, String> packageGlobalTypes ) {
        final Map<String, String> scopedGlobalTypes = new HashMap<String, String>();
        for ( Map.Entry<String, String> e : packageGlobalTypes.entrySet() ) {
            final String globalQualifiedType = e.getValue();
            final String globalPackageName = getPackageName( globalQualifiedType );
            final String globalTypeName = getTypeName( globalQualifiedType );

            if ( globalPackageName.equals( packageName ) || isImported( globalQualifiedType,
                                                                        imports ) ) {
                scopedGlobalTypes.put( e.getKey(),
                                       globalTypeName );
            }
        }
        return scopedGlobalTypes;
    }

    //Filter and rename Event Types based on package name and imports
    public static Map<String, Boolean> filterEventTypes( final String packageName,
                                                         final Imports imports,
                                                         final Map<String, Boolean> projectEventTypes ) {
        final Map<String, Boolean> scopedEventTypes = new HashMap<String, Boolean>();
        for ( Map.Entry<String, Boolean> e : projectEventTypes.entrySet() ) {
            final String eventQualifiedType = e.getKey();
            final String eventPackageName = getPackageName( eventQualifiedType );
            final String eventTypeName = getTypeName( eventQualifiedType );

            if ( eventPackageName.equals( packageName ) || isImported( eventQualifiedType,
                                                                       imports ) ) {
                scopedEventTypes.put( eventTypeName,
                                      e.getValue() );
            }
        }
        return scopedEventTypes;
    }

    //Filter and rename TypeSource based on package name and imports
    public static Map<String, TypeSource> filterTypeSources( final String packageName,
                                                             final Imports imports,
                                                             final Map<String, TypeSource> projectTypeSources ) {
        final Map<String, TypeSource> scopedTypeSources = new HashMap<String, TypeSource>();
        for ( Map.Entry<String, TypeSource> e : projectTypeSources.entrySet() ) {
            final String typeQualifiedType = e.getKey();
            final String typePackageName = getPackageName( typeQualifiedType );
            final String typeTypeName = getTypeName( typeQualifiedType );

            if ( typePackageName.equals( packageName ) || isImported( typeQualifiedType,
                                                                      imports ) ) {
                scopedTypeSources.put( typeTypeName,
                                       e.getValue() );
            }
        }
        return scopedTypeSources;
    }

    //Filter and rename Super Types based on package name and imports
    public static Map<String, String> filterSuperTypes( final String packageName,
                                                        final Imports imports,
                                                        final Map<String, String> projectSuperTypes ) {
        final Map<String, String> scopedSuperTypes = new HashMap<String, String>();
        for ( Map.Entry<String, String> e : projectSuperTypes.entrySet() ) {
            final String typeQualifiedType = e.getKey();
            final String typePackageName = getPackageName( typeQualifiedType );
            final String typeTypeName = getTypeName( typeQualifiedType );

            final String superTypeQualifiedType = e.getValue();

            if ( superTypeQualifiedType == null ) {
                //Doesn't have a Super Type
                if ( typePackageName.equals( packageName ) || isImported( typeQualifiedType,
                                                                          imports ) ) {
                    scopedSuperTypes.put( typeTypeName,
                                          superTypeQualifiedType );
                }
            } else {
                //Has a Super Type
                if ( typePackageName.equals( packageName ) || isImported( typeQualifiedType,
                                                                          imports ) ) {
                    final String superTypePackageName = getPackageName( superTypeQualifiedType );
                    final String superTypeTypeName = getTypeName( superTypeQualifiedType );
                    if ( superTypePackageName.equals( packageName ) || isImported( superTypeQualifiedType,
                                                                                   imports ) ) {
                        scopedSuperTypes.put( typeTypeName,
                                              superTypeTypeName );
                    } else {
                        scopedSuperTypes.put( typeTypeName,
                                              superTypeQualifiedType );
                    }
                }
            }
        }
        return scopedSuperTypes;
    }

    //Filter and rename Type Annotations based on package name and imports
    public static Map<String, Set<Annotation>> filterTypeAnnotations( final String packageName,
                                                                      final Imports imports,
                                                                      final Map<String, Set<Annotation>> projectTypeAnnotations ) {
        final Map<String, Set<Annotation>> scopedTypeAnnotations = new HashMap<String, Set<Annotation>>();
        for ( Map.Entry<String, Set<Annotation>> e : projectTypeAnnotations.entrySet() ) {
            final String typeAnnotationQualifiedType = e.getKey();
            final String typeAnnotationPackageName = getPackageName( typeAnnotationQualifiedType );
            final String typeAnnotationTypeName = getTypeName( typeAnnotationQualifiedType );

            if ( typeAnnotationPackageName.equals( packageName ) || isImported( typeAnnotationQualifiedType,
                                                                                imports ) ) {
                scopedTypeAnnotations.put( typeAnnotationTypeName,
                                           e.getValue() );
            }
        }
        return scopedTypeAnnotations;
    }

    //Filter and rename Type Fields Annotations based on package name and imports
    public static Map<String, Map<String, Set<Annotation>>> filterTypeFieldsAnnotations( final String packageName,
                                                                                         final Imports imports,
                                                                                         final Map<String, Map<String, Set<Annotation>>> projectTypeFieldsAnnotations ) {
        final Map<String, Map<String, Set<Annotation>>> scopedTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();
        for ( Map.Entry<String, Map<String, Set<Annotation>>> e : projectTypeFieldsAnnotations.entrySet() ) {
            final String typeAnnotationQualifiedType = e.getKey();
            final String typeAnnotationPackageName = getPackageName( typeAnnotationQualifiedType );
            final String typeAnnotationTypeName = getTypeName( typeAnnotationQualifiedType );

            if ( typeAnnotationPackageName.equals( packageName ) || isImported( typeAnnotationQualifiedType,
                                                                                imports ) ) {
                scopedTypeFieldsAnnotations.put( typeAnnotationTypeName,
                                                 e.getValue() );
            }
        }
        return scopedTypeFieldsAnnotations;
    }

    //Filter and rename Enum definitions based on package name and imports
    public static Map<String, String[]> filterEnumDefinitions( final String packageName,
                                                               final Imports imports,
                                                               final Map<String, String[]> enumDefinitions ) {
        final Map<String, String[]> scopedEnumLists = new HashMap<String, String[]>();
        for ( Map.Entry<String, String[]> e : enumDefinitions.entrySet() ) {
            final String enumQualifiedType = getQualifiedTypeFromEnumeration( e.getKey() );
            final String enumFieldName = getFieldNameFromEnumeration( e.getKey() );
            final String enumPackageName = getPackageName( enumQualifiedType );
            final String enumTypeName = getTypeName( enumQualifiedType );

            if ( enumPackageName.equals( packageName ) || isImported( enumQualifiedType,
                                                                      imports ) ) {
                scopedEnumLists.put( enumTypeName + "#" + enumFieldName,
                                     e.getValue() );
            }
        }
        return scopedEnumLists;
    }

    //Filter and rename Method Information (used by ActionCallXXX and ExpressionBuilder) based on package name and imports
    public static Map<String, List<MethodInfo>> filterMethodInformation( final String packageName,
                                                                         final Imports imports,
                                                                         final Map<String, List<MethodInfo>> projectMethodInformation ) {
        final Map<String, List<MethodInfo>> scopedMethodInformation = new HashMap<String, List<MethodInfo>>();
        for ( Map.Entry<String, List<MethodInfo>> e : projectMethodInformation.entrySet() ) {
            final String miQualifiedType = e.getKey();
            final String miPackageName = getPackageName( miQualifiedType );
            final String miTypeName = getTypeName( miQualifiedType );

            if ( miPackageName.equals( packageName ) || isImported( miQualifiedType,
                                                                    imports ) ) {
                scopedMethodInformation.put( miTypeName,
                                             correctMethodInformation( packageName,
                                                                       e.getValue(),
                                                                       imports ) );
            }
        }
        return scopedMethodInformation;
    }

    //Filter and rename based on package name and imports
    public static Map<String, String> filterFieldParametersTypes( final String packageName,
                                                                  final Imports imports,
                                                                  final Map<String, String> projectFieldParametersTypes ) {
        final Map<String, String> scopedFieldParametersType = new HashMap<String, String>();
        for ( Map.Entry<String, String> e : projectFieldParametersTypes.entrySet() ) {
            String fieldName = e.getKey();
            String fieldType = e.getValue();
            final String fFieldName = getFieldNameFromEnumeration( fieldName );

            final String fFieldName_QualifiedType = getQualifiedTypeFromEnumeration( fieldName );
            final String fFieldName_PackageName = getPackageName( fFieldName_QualifiedType );
            final String fFieldName_TypeName = getTypeName( fFieldName_QualifiedType );
            if ( fFieldName_PackageName.equals( packageName ) || isImported( fFieldName_QualifiedType,
                                                                             imports ) ) {
                fieldName = fFieldName_TypeName;
            }

            final String fFieldType_QualifiedType = getQualifiedTypeFromEnumeration( fieldType );
            final String fFieldType_PackageName = getPackageName( fFieldType_QualifiedType );
            final String fFieldType_TypeName = getTypeName( fFieldType_QualifiedType );
            if ( fFieldType_PackageName.equals( packageName ) || isImported( fFieldType_QualifiedType,
                                                                             imports ) ) {
                fieldType = fFieldType_TypeName;
            }

            scopedFieldParametersType.put( fieldName + "#" + fFieldName,
                                           fieldType );
        }
        return scopedFieldParametersType;
    }

    public static String getPackageName( final String qualifiedType ) {
        String packageName = qualifiedType;
        int dotIndex = packageName.lastIndexOf( "." );
        if ( dotIndex != -1 ) {
            return packageName.substring( 0,
                                          dotIndex );
        }
        return "";
    }

    public static String getTypeName( final String qualifiedType ) {
        String typeName = qualifiedType;
        int dotIndex = typeName.lastIndexOf( "." );
        if ( dotIndex != -1 ) {
            typeName = typeName.substring( dotIndex + 1 );
        }
        return typeName.replace( "$",
                                 "." );
    }

    private static String getQualifiedTypeFromEnumeration( final String qualifiedType ) {
        String typeName = qualifiedType;
        int hashIndex = typeName.lastIndexOf( "#" );
        if ( hashIndex != -1 ) {
            typeName = typeName.substring( 0,
                                           hashIndex );
        }
        return typeName;
    }

    private static String getFieldNameFromEnumeration( final String qualifiedType ) {
        String fieldName = qualifiedType;
        int hashIndex = fieldName.lastIndexOf( "#" );
        if ( hashIndex != -1 ) {
            return fieldName.substring( hashIndex + 1 );
        }
        return "";
    }

    private static ModelField[] correctModelFields( final String packageName,
                                                    final ModelField[] originalModelFields,
                                                    final Imports imports ) {
        final List<ModelField> correctedModelFields = new ArrayList<ModelField>();
        for ( final ModelField mf : originalModelFields ) {
            String mfType = mf.getType();
            String mfClassName = mf.getClassName();

            final String mfClassName_QualifiedType = mfClassName;
            final String mfClassName_PackageName = getPackageName( mfClassName_QualifiedType );
            final String mfClassName_TypeName = getTypeName( mfClassName_QualifiedType );
            if ( mfClassName_PackageName.equals( packageName ) || isImported( mfClassName_QualifiedType,
                                                                              imports ) ) {
                mfClassName = mfClassName_TypeName;
            }

            final String mfType_QualifiedType = mfType;
            final String mfType_PackageName = getPackageName( mfType_QualifiedType );
            final String mfType_TypeName = getTypeName( mfType_QualifiedType );
            if ( mfType_PackageName.equals( packageName ) || isImported( mfType_QualifiedType,
                                                                         imports ) ) {
                mfType = mfType_TypeName;
            }
            correctedModelFields.add( new ModelField( mf.getName(),
                                                      mfClassName,
                                                      mf.getClassType(),
                                                      mf.getOrigin(),
                                                      mf.getAccessorsAndMutators(),
                                                      mfType ) );
        }
        final ModelField[] result = new ModelField[ correctedModelFields.size() ];
        return correctedModelFields.toArray( result );
    }

    private static List<MethodInfo> correctMethodInformation( final String packageName,
                                                              final List<MethodInfo> originalMethodInformation,
                                                              final Imports imports ) {
        final List<MethodInfo> correctedMethodInformation = new ArrayList<MethodInfo>();
        for ( final MethodInfo mi : originalMethodInformation ) {
            String miReturnType = mi.getReturnClassType();
            String miGenericReturnType = mi.getGenericType();
            String miParametricReturnType = mi.getParametricReturnType();

            final String miReturnType_QualifiedType = miReturnType;
            final String miReturnType_PackageName = getPackageName( miReturnType_QualifiedType );
            final String miReturnType_TypeName = getTypeName( miReturnType_QualifiedType );
            if ( miReturnType_PackageName.equals( packageName ) || isImported( miReturnType_QualifiedType,
                                                                               imports ) ) {
                miReturnType = miReturnType_TypeName;
            }

            final String miGenericReturnType_QualifiedType = miGenericReturnType;
            final String miGenericReturnType_PackageName = getPackageName( miGenericReturnType_QualifiedType );
            final String miGenericReturnType_TypeName = getTypeName( miGenericReturnType_QualifiedType );
            if ( miGenericReturnType_PackageName.equals( packageName ) || isImported( miGenericReturnType_QualifiedType,
                                                                                      imports ) ) {
                miGenericReturnType = miGenericReturnType_TypeName;
            }

            if ( miParametricReturnType != null ) {
                final String miParametricReturnType_QualifiedType = miParametricReturnType;
                final String miParametricReturnType_PackageName = getPackageName( miParametricReturnType_QualifiedType );
                final String miParametricReturnType_TypeName = getTypeName( miParametricReturnType_QualifiedType );
                if ( miParametricReturnType_PackageName.equals( packageName ) || isImported( miParametricReturnType_QualifiedType,
                                                                                             imports ) ) {
                    miParametricReturnType = miParametricReturnType_TypeName;
                }
            }

            correctedMethodInformation.add( new MethodInfo( mi.getName(),
                                                            mi.getParams(),
                                                            miReturnType,
                                                            miParametricReturnType,
                                                            miGenericReturnType ) );
        }
        return correctedMethodInformation;
    }

    private static boolean isImported( final String qualifiedType,
                                       final Imports imports ) {
        final Import item = new Import( qualifiedType );
        return imports.contains( item );
    }

}
