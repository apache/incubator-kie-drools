package org.drools.workbench.models.commons.shared.oracle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.drools.workbench.models.datamodel.rule.DSLSentence;

/**
 * Default implementation of DataModelOracle
 */
public class PackageDataModelOracleImpl extends ProjectDataModelOracleImpl implements PackageDataModelOracle {

    //Package for which this DMO relates
    private String packageName = "";

    //Imports from the Project into this Package
    private Imports imports = new Imports();

    // Filtered (current package and imports) Fact Types and their corresponding fields
    private Map<String, ModelField[]> filteredModelFields = new HashMap<String, ModelField[]>();

    // Filtered (current package and imports) map of the field that contains the parametrized type of a collection
    // for example given "List<String> name", key = "name" value = "String"
    private Map<String, String> filteredFieldParametersType = new HashMap<String, String>();

    // Filtered (current package and imports) map of { TypeName.field : String[] } - where a list is valid values to display in a drop down for a given Type.field combination.
    private Map<String, String[]> filteredEnumLists = new HashMap<String, String[]>();

    // Filtered (current package and imports) Method information used (exclusively) by ExpressionWidget and ActionCallMethodWidget
    private Map<String, List<MethodInfo>> filteredMethodInformation = new HashMap<String, List<MethodInfo>>();

    // Filtered (current package and imports) Map {factType, isEvent} to determine which Fact Type can be treated as events.
    private Map<String, Boolean> filteredEventTypes = new HashMap<String, Boolean>();

    // Filtered (current package and imports) Map {factType, isCollection} to determine which Fact Types are Collections.
    private Map<String, Boolean> filteredCollectionTypes = new HashMap<String, Boolean>();

    // Filtered (current package and imports) Map {factType, TypeSource} to determine where a Fact Type as defined.
    private Map<String, TypeSource> filteredTypeSources = new HashMap<String, TypeSource>();

    // Filtered (current package and imports) Map {factType, superType} to determine the Super Type of a FactType.
    protected Map<String, String> filteredSuperTypes = new HashMap<String, String>();

    // Filtered (current package and imports) Map {factType, Set<Annotation>} containing the FactType's annotations.
    protected Map<String, Set<Annotation>> filteredTypeAnnotations = new HashMap<String, Set<Annotation>>();

    // Filtered (current package and imports) Map {factType, {fieldName, Set<Annotation>}} containing the FactType's Fields annotations.
    protected Map<String, Map<String, Set<Annotation>>> filteredTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();

    // Filtered (current package and imports) map of Globals {alias, class name}.
    private Map<String, String> filteredGlobalTypes = new HashMap<String, String>();

    // Package-level enumeration definitions derived from "Guvnor" enumerations.
    private Map<String, String[]> packageGuvnorEnumLists = new HashMap<String, String[]>();

    // Package-level DSL language extensions.
    private List<DSLSentence> packageDSLConditionSentences = new ArrayList<DSLSentence>();
    private List<DSLSentence> packageDSLActionSentences = new ArrayList<DSLSentence>();

    // Package-level map of Globals {alias, class name}.
    private Map<String, String> packageGlobalTypes = new HashMap<String, String>();

    // This is used to calculate what fields an enum list may depend on.
    private transient Map<String, Object> packageEnumLookupFields;

    //Public constructor is needed for Errai Marshaller :(
    public PackageDataModelOracleImpl() {
    }

    // ####################################
    // Fact Types
    // ####################################

    /**
     * Returns fact types available for rule authoring, i.e. those within the same package and those that have been imported.
     * @return
     */
    public String[] getFactTypes() {
        final String[] types = filteredModelFields.keySet().toArray( new String[ filteredModelFields.size() ] );
        Arrays.sort( types );
        return types;
    }

    /**
     * Return all fact types available to the project, i.e. everything type defined within the project or externally imported
     * @return
     */
    @Override
    public String[] getAllFactTypes() {
        final List<String> types = new ArrayList<String>();
        types.addAll( super.projectModelFields.keySet() );
        final String[] result = new String[ types.size() ];
        types.toArray( result );
        Arrays.sort( result );
        return result;
    }

    /**
     * Return all fact types that are external to the package, i.e. they need to be imported to be used
     * @return
     */
    @Override
    public String[] getExternalFactTypes() {
        final String[] allTypes = getAllFactTypes();
        final List<String> externalTypes = new ArrayList<String>();
        for ( String type : allTypes ) {
            final String packageName = PackageDataModelOracleUtils.getPackageName( type );
            if ( !packageName.equals( this.packageName ) ) {
                externalTypes.add( type );
            }
        }
        final String[] result = new String[ externalTypes.size() ];
        externalTypes.toArray( result );
        Arrays.sort( result );
        return result;
    }

    /**
     * Returns fact's name from class type
     * @param type
     * @return
     */
    public String getFactNameFromType( final String type ) {
        if ( type == null ) {
            return null;
        }
        if ( filteredModelFields.containsKey( type ) ) {
            return type;
        }
        for ( Map.Entry<String, ModelField[]> entry : filteredModelFields.entrySet() ) {
            for ( ModelField mf : entry.getValue() ) {
                if ( DataType.TYPE_THIS.equals( mf.getName() ) && type.equals( mf.getClassName() ) ) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Is the Fact Type known to the DataModelOracle
     * @param factType
     * @return
     */
    public boolean isFactTypeRecognized( final String factType ) {
        return filteredModelFields.containsKey( factType );
    }

    /**
     * Check whether a given FactType is an Event for CEP purposes
     * @param factType
     * @return
     */
    public boolean isFactTypeAnEvent( final String factType ) {
        if ( !filteredEventTypes.containsKey( factType ) ) {
            return false;
        }
        return filteredEventTypes.get( factType );
    }

    /**
     * Return where a given FactType was defined
     * @param factType
     * @return
     */
    @Override
    public TypeSource getTypeSource( final String factType ) {
        return filteredTypeSources.get( factType );
    }

    /**
     * Get the Super Type for a given FactType
     * @param factType
     * @return null if no Super Type
     */
    @Override
    public String getSuperType( final String factType ) {
        return filteredSuperTypes.get( factType );
    }

    /**
     * Get the Annotations for a given FactType
     * @param factType
     * @return Empty set if no annotations exist for the type
     */
    @Override
    public Set<Annotation> getTypeAnnotations( final String factType ) {
        if ( !filteredTypeAnnotations.containsKey( factType ) ) {
            return Collections.EMPTY_SET;
        }
        return filteredTypeAnnotations.get( factType );
    }

    /**
     * Get the Fields Annotations for a given FactType
     * @param factType
     * @return Empty Map if no annotations exist for the type
     */
    @Override
    public Map<String, Set<Annotation>> getTypeFieldsAnnotations( final String factType ) {
        if ( !filteredTypeFieldsAnnotations.containsKey( factType ) ) {
            return Collections.EMPTY_MAP;
        }
        return filteredTypeFieldsAnnotations.get( factType );
    }

    // ####################################
    // Fact Types' Fields
    // ####################################

    public String[] getFieldCompletions( final String factType ) {
        return getModelFields( factType );
    }

    private String[] getModelFields( final String modelClassName ) {
        final String shortName = getFactNameFromType( modelClassName );
        if ( !filteredModelFields.containsKey( shortName ) ) {
            return new String[ 0 ];
        }

        final ModelField[] fields = filteredModelFields.get( shortName );
        final String[] fieldNames = new String[ fields.length ];
        for ( int i = 0; i < fields.length; i++ ) {
            fieldNames[ i ] = fields[ i ].getName();
        }
        return fieldNames;
    }

    public String[] getFieldCompletions( final FieldAccessorsAndMutators accessorOrMutator,
                                         final String factType ) {
        final String shortName = getFactNameFromType( factType );
        if ( !filteredModelFields.containsKey( shortName ) ) {
            return new String[ 0 ];
        }

        final ModelField[] fields = filteredModelFields.get( shortName );
        final List<String> fieldNames = new ArrayList<String>();
        for ( int i = 0; i < fields.length; i++ ) {
            final ModelField field = fields[ i ];
            if ( FieldAccessorsAndMutators.compare( accessorOrMutator,
                                                    field.getAccessorsAndMutators() ) ) {
                fieldNames.add( field.getName() );
            }
        }
        return fieldNames.toArray( new String[ fieldNames.size() ] );
    }

    public String getFieldClassName( final String modelClassName,
                                     final String fieldName ) {
        final ModelField field = getField( modelClassName,
                                           fieldName );
        return field == null ? null : field.getClassName();
    }

    private ModelField getField( final String modelClassName,
                                 final String fieldName ) {
        final String shortName = getFactNameFromType( modelClassName );
        final ModelField[] fields = filteredModelFields.get( shortName );
        if ( fields == null ) {
            return null;
        }
        for ( ModelField modelField : fields ) {
            if ( modelField.getName().equals( fieldName ) ) {
                return modelField;
            }
        }
        return null;
    }

    public String getFieldType( final String modelClassName,
                                final String fieldName ) {
        final ModelField field = getField( modelClassName,
                                           fieldName );
        return field == null ? null : field.getType();
    }

    public Map<String, ModelField[]> getModelFields() {
        return filteredModelFields;
    }

    // ####################################
    // Operators
    // ####################################

    /**
     * Get the Operators applicable Base Constraints
     * @param factType
     * @param fieldName
     * @return
     */
    public String[] getOperatorCompletions( final String factType,
                                            final String fieldName ) {

        final String fieldType = getFieldType( factType,
                                               fieldName );

        if ( fieldType == null ) {
            return OperatorsOracle.STANDARD_OPERATORS;
        } else if ( fieldName.equals( DataType.TYPE_THIS ) ) {
            if ( isFactTypeAnEvent( factType ) ) {
                return OracleUtils.joinArrays( OperatorsOracle.STANDARD_OPERATORS,
                                               OperatorsOracle.SIMPLE_CEP_OPERATORS,
                                               OperatorsOracle.COMPLEX_CEP_OPERATORS );
            } else {
                return OperatorsOracle.STANDARD_OPERATORS;
            }
        } else if ( fieldType.equals( DataType.TYPE_STRING ) ) {
            return OracleUtils.joinArrays( OperatorsOracle.STRING_OPERATORS,
                                           OperatorsOracle.EXPLICIT_LIST_OPERATORS );
        } else if ( DataType.isNumeric( fieldType ) ) {
            return OracleUtils.joinArrays( OperatorsOracle.COMPARABLE_OPERATORS,
                                           OperatorsOracle.EXPLICIT_LIST_OPERATORS );
        } else if ( fieldType.equals( DataType.TYPE_DATE ) ) {
            return OracleUtils.joinArrays( OperatorsOracle.COMPARABLE_OPERATORS,
                                           OperatorsOracle.EXPLICIT_LIST_OPERATORS,
                                           OperatorsOracle.SIMPLE_CEP_OPERATORS );
        } else if ( fieldType.equals( DataType.TYPE_COMPARABLE ) ) {
            return OperatorsOracle.COMPARABLE_OPERATORS;
        } else if ( fieldType.equals( DataType.TYPE_COLLECTION ) ) {
            return OperatorsOracle.COLLECTION_OPERATORS;
        } else {
            return OperatorsOracle.STANDARD_OPERATORS;
        }
    }

    /**
     * Get the Operators applicable for Connective Constraints
     * @param factType
     * @param fieldName
     * @return
     */
    public String[] getConnectiveOperatorCompletions( final String factType,
                                                      final String fieldName ) {
        final String fieldType = getFieldType( factType,
                                               fieldName );

        if ( fieldType == null ) {
            return OperatorsOracle.STANDARD_CONNECTIVES;
        } else if ( fieldName.equals( DataType.TYPE_THIS ) ) {
            if ( isFactTypeAnEvent( factType ) ) {
                return OracleUtils.joinArrays( OperatorsOracle.STANDARD_CONNECTIVES,
                                               OperatorsOracle.SIMPLE_CEP_CONNECTIVES,
                                               OperatorsOracle.COMPLEX_CEP_CONNECTIVES );
            } else {
                return OperatorsOracle.STANDARD_CONNECTIVES;
            }
        } else if ( fieldType.equals( DataType.TYPE_STRING ) ) {
            return OperatorsOracle.STRING_CONNECTIVES;
        } else if ( DataType.isNumeric( fieldType ) ) {
            return OperatorsOracle.COMPARABLE_CONNECTIVES;
        } else if ( fieldType.equals( DataType.TYPE_DATE ) ) {
            return OracleUtils.joinArrays( OperatorsOracle.COMPARABLE_CONNECTIVES,
                                           OperatorsOracle.SIMPLE_CEP_CONNECTIVES );
        } else if ( fieldType.equals( DataType.TYPE_COMPARABLE ) ) {
            return OperatorsOracle.COMPARABLE_CONNECTIVES;
        } else if ( fieldType.equals( DataType.TYPE_COLLECTION ) ) {
            return OperatorsOracle.COLLECTION_CONNECTIVES;
        } else {
            return OperatorsOracle.STANDARD_CONNECTIVES;
        }

    }

    // ####################################
    // Globals
    // ####################################

    public String[] getFieldCompletionsForGlobalVariable( final String varName ) {
        final String type = getGlobalVariable( varName );
        return getModelFields( type );
    }

    public List<MethodInfo> getMethodInfosForGlobalVariable( final String varName ) {
        final String type = getGlobalVariable( varName );
        return filteredMethodInformation.get( type );
    }

    public String getGlobalVariable( final String name ) {
        return filteredGlobalTypes.get( name );
    }

    public boolean isGlobalVariable( final String name ) {
        return filteredGlobalTypes.containsKey( name );
    }

    public String[] getGlobalVariables() {
        return OracleUtils.toStringArray( filteredGlobalTypes.keySet() );
    }

    public String[] getGlobalCollections() {
        final List<String> globalCollections = new ArrayList<String>();
        for ( Map.Entry<String, String> e : filteredGlobalTypes.entrySet() ) {
            if ( filteredCollectionTypes.containsKey( e.getValue() ) ) {
                if ( Boolean.TRUE.equals( filteredCollectionTypes.get( e.getValue() ) ) ) {
                    globalCollections.add( e.getKey() );
                }
            }
        }
        return OracleUtils.toStringArray( globalCollections );
    }

    // ####################################
    // DSLs
    // ####################################

    public List<DSLSentence> getDSLConditions() {
        return Collections.unmodifiableList( packageDSLConditionSentences );
    }

    public List<DSLSentence> getDSLActions() {
        return Collections.unmodifiableList( packageDSLActionSentences );
    }

    // ####################################
    // Enums
    // ####################################

    /**
     * Get enums for a Type and Field.
     */
    public DropDownData getEnums( final String type,
                                  final String field ) {
        return getEnums( type,
                         field,
                         new HashMap<String, String>() );
    }

    /**
     * Get enums for a Type and Field where the enum list may depend upon the values of other fields.
     */
    public DropDownData getEnums( final String type,
                                  final String field,
                                  final Map<String, String> currentValueMap ) {

        final Map<String, Object> dataEnumLookupFields = loadDataEnumLookupFields();

        if ( !currentValueMap.isEmpty() ) {
            // we may need to check for data dependent enums
            final Object _typeFields = dataEnumLookupFields.get( type + "#" + field );

            if ( _typeFields instanceof String ) {
                final String typeFields = (String) _typeFields;
                final StringBuilder dataEnumListsKeyBuilder = new StringBuilder( type );
                dataEnumListsKeyBuilder.append( "#" ).append( field );

                boolean addOpeninColumn = true;
                final String[] splitTypeFields = typeFields.split( "," );
                for ( int j = 0; j < splitTypeFields.length; j++ ) {
                    final String typeField = splitTypeFields[ j ];

                    for ( Map.Entry<String, String> currentValueEntry : currentValueMap.entrySet() ) {
                        final String fieldName = currentValueEntry.getKey();
                        final String fieldValue = currentValueEntry.getValue();
                        if ( fieldName.trim().equals( typeField.trim() ) ) {
                            if ( addOpeninColumn ) {
                                dataEnumListsKeyBuilder.append( "[" );
                                addOpeninColumn = false;
                            }
                            dataEnumListsKeyBuilder.append( typeField ).append( "=" ).append( fieldValue );

                            if ( j != ( splitTypeFields.length - 1 ) ) {
                                dataEnumListsKeyBuilder.append( "," );
                            }
                        }
                    }
                }

                if ( !addOpeninColumn ) {
                    dataEnumListsKeyBuilder.append( "]" );
                }

                final DropDownData data = DropDownData.create( filteredEnumLists.get( dataEnumListsKeyBuilder.toString() ) );
                if ( data != null ) {
                    return data;
                }
            } else if ( _typeFields != null ) {
                // these enums are calculated on demand, server side...
                final String[] fieldsNeeded = (String[]) _typeFields;
                final String queryString = getQueryString( type,
                                                           field,
                                                           fieldsNeeded,
                                                           filteredEnumLists );
                final String[] valuePairs = new String[ fieldsNeeded.length ];

                // collect all the values of the fields needed, then return it as a string...
                for ( int i = 0; i < fieldsNeeded.length; i++ ) {
                    for ( Map.Entry<String, String> currentValueEntry : currentValueMap.entrySet() ) {
                        final String fieldName = currentValueEntry.getKey();
                        final String fieldValue = currentValueEntry.getValue();
                        if ( fieldName.equals( fieldsNeeded[ i ] ) ) {
                            valuePairs[ i ] = fieldsNeeded[ i ] + "=" + fieldValue;
                        }
                    }
                }

                if ( valuePairs.length > 0 && valuePairs[ 0 ] != null ) {
                    return DropDownData.create( queryString,
                                                valuePairs );
                }
            }
        }
        return DropDownData.create( getEnumValues( type,
                                                   field ) );
    }

    /**
     * Get the query string for a fact.field It will ignore any specified field,
     * and just look for the string - as there should only be one Fact.field of
     * this type (it is all determined server side).
     * @param fieldsNeeded
     */
    private String getQueryString( final String factType,
                                   final String field,
                                   final String[] fieldsNeeded,
                                   final Map<String, String[]> dataEnumLists ) {
        for ( Iterator<String> iterator = dataEnumLists.keySet().iterator(); iterator.hasNext(); ) {
            final String key = iterator.next();
            if ( key.startsWith( factType + "#" + field ) && fieldsNeeded != null && key.contains( "[" ) ) {

                final String[] values = key.substring( key.indexOf( '[' ) + 1,
                                                       key.lastIndexOf( ']' ) ).split( "," );

                if ( values.length != fieldsNeeded.length ) {
                    continue;
                }

                boolean fail = false;
                for ( int i = 0; i < values.length; i++ ) {
                    final String a = values[ i ].trim();
                    final String b = fieldsNeeded[ i ].trim();
                    if ( !a.equals( b ) ) {
                        fail = true;
                        break;
                    }
                }
                if ( fail ) {
                    continue;
                }

                final String[] qry = dataEnumLists.get( key );
                return qry[ 0 ];
            } else if ( key.startsWith( factType + "#" + field ) && ( fieldsNeeded == null || fieldsNeeded.length == 0 ) ) {
                final String[] qry = dataEnumLists.get( key );
                return qry[ 0 ];
            }
        }
        throw new IllegalStateException();
    }

    /**
     * For simple cases - where a list of values are known based on a field.
     */
    public String[] getEnumValues( final String factType,
                                   final String field ) {
        return filteredEnumLists.get( factType + "#" + field );
    }

    public boolean hasEnums( final String factType,
                             final String field ) {
        return hasEnums( factType + "#" + field );
    }

    public boolean hasEnums( final String qualifiedFactField ) {
        boolean hasEnums = false;
        final String key = qualifiedFactField.replace( ".",
                                                       "#" );
        final String dependentType = key + "[";
        for ( String e : filteredEnumLists.keySet() ) {
            //e.g. Fact.field1
            if ( e.equals( key ) ) {
                return true;
            }
            //e.g. Fact.field2[field1=val2]
            if ( e.startsWith( dependentType ) ) {
                return true;
            }
        }
        return hasEnums;
    }

    /**
     * Check whether the childField is related to the parentField through a
     * chain of enumeration dependencies. Both fields belong to the same Fact
     * Type. Furthermore code consuming this function should ensure both
     * parentField and childField relate to the same Fact Pattern
     * @param factType
     * @param parentField
     * @param childField
     * @return
     */
    public boolean isDependentEnum( final String factType,
                                    final String parentField,
                                    final String childField ) {
        final Map<String, Object> enums = loadDataEnumLookupFields();
        if ( enums.isEmpty() ) {
            return false;
        }
        //Check if the childField is a direct descendant of the parentField
        final String key = factType + "#" + childField;
        if ( !enums.containsKey( key ) ) {
            return false;
        }

        //Otherwise follow the dependency chain...
        final Object _parent = enums.get( key );
        if ( _parent instanceof String ) {
            final String _parentField = (String) _parent;
            if ( _parentField.equals( parentField ) ) {
                return true;
            } else {
                return isDependentEnum( factType,
                                        parentField,
                                        _parentField );
            }
        }
        return false;
    }

    /**
     * This is only used by enums that are like Fact.field[something=X] and so on.
     */
    private Map<String, Object> loadDataEnumLookupFields() {
        if ( packageEnumLookupFields == null ) {
            packageEnumLookupFields = new HashMap<String, Object>();
            final Set<String> keys = filteredEnumLists.keySet();
            for ( String key : keys ) {
                if ( key.indexOf( '[' ) != -1 ) {
                    int ix = key.indexOf( '[' );
                    final String factField = key.substring( 0,
                                                            ix );
                    final String predicate = key.substring( ix + 1,
                                                            key.indexOf( ']' ) );
                    if ( predicate.indexOf( '=' ) > -1 ) {

                        final String[] bits = predicate.split( "," );
                        final StringBuilder typeFieldBuilder = new StringBuilder();

                        for ( int i = 0; i < bits.length; i++ ) {
                            typeFieldBuilder.append( bits[ i ].substring( 0,
                                                                          bits[ i ].indexOf( '=' ) ) );
                            if ( i != ( bits.length - 1 ) ) {
                                typeFieldBuilder.append( "," );
                            }
                        }

                        packageEnumLookupFields.put( factField,
                                                     typeFieldBuilder.toString() );
                    } else {
                        final String[] fields = predicate.split( "," );
                        for ( int i = 0; i < fields.length; i++ ) {
                            fields[ i ] = fields[ i ].trim();
                        }
                        packageEnumLookupFields.put( factField,
                                                     fields );
                    }
                }
            }
        }

        return packageEnumLookupFields;
    }

    // ####################################
    // Methods
    // ####################################

    /**
     * Get a list of Methods for a Fact Type
     * @param factType
     * @return
     */
    public List<String> getMethodNames( final String factType ) {
        return getMethodNames( factType,
                               -1 );
    }

    /**
     * Get a list of Methods for a Fact Type that have at least the specified number of parameters
     * @param factType
     * @param paramCount
     * @return
     */
    public List<String> getMethodNames( final String factType,
                                        final int paramCount ) {
        final List<MethodInfo> infos = filteredMethodInformation.get( factType );
        final List<String> methodList = new ArrayList<String>();
        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if ( paramCount == -1 || info.getParams().size() <= paramCount ) {
                    methodList.add( info.getNameWithParameters() );
                }
            }
        }
        return methodList;
    }

    /**
     * Get a list of parameters for a Method of a Fact Type
     * @param factType
     * @param methodNameWithParams
     * @return
     */
    public List<String> getMethodParams( final String factType,
                                         final String methodNameWithParams ) {
        final List<MethodInfo> infos = filteredMethodInformation.get( factType );
        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if ( info.getNameWithParameters().startsWith( methodNameWithParams ) ) {
                    return info.getParams();
                }
            }
        }
        return null;
    }

    /**
     * Get information on a Method of a Fact Type
     * @param factType
     * @param methodFullName
     * @return
     */
    public MethodInfo getMethodInfo( final String factType,
                                     final String methodFullName ) {
        final List<MethodInfo> infos = filteredMethodInformation.get( factType );
        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if ( info.getNameWithParameters().equals( methodFullName ) ) {
                    return info;
                }
            }
        }
        return null;
    }

    // ####################################
    // Parametric Types
    // ####################################

    /**
     * Get the parametric type of a Field.
     * @param factType
     * @param fieldName
     * @return
     */
    public String getParametricFieldType( final String factType,
                                          final String fieldName ) {
        final String qualifiedFactFieldName = factType + "#" + fieldName;
        return filteredFieldParametersType.get( qualifiedFactFieldName );
    }

    public void filter( final Imports imports ) {
        this.imports = imports;
        filter();
    }

    public void filter() {
        //Filter and rename Model Fields based on package name and imports
        filteredModelFields = new HashMap<String, ModelField[]>();
        filteredModelFields.putAll( PackageDataModelOracleUtils.filterModelFields( packageName,
                                                                                   imports,
                                                                                   projectModelFields ) );

        //Filter and rename Global Types based on package name and imports
        filteredGlobalTypes = new HashMap<String, String>();
        filteredGlobalTypes.putAll( PackageDataModelOracleUtils.filterGlobalTypes( packageName,
                                                                                   imports,
                                                                                   packageGlobalTypes ) );

        //Filter and rename Collection Types based on package name and imports
        filteredCollectionTypes = new HashMap<String, Boolean>();
        filteredCollectionTypes.putAll( PackageDataModelOracleUtils.filterCollectionTypes( packageName,
                                                                                           imports,
                                                                                           projectCollectionTypes ) );

        //Filter and rename Event Types based on package name and imports
        filteredEventTypes = new HashMap<String, Boolean>();
        filteredEventTypes.putAll( PackageDataModelOracleUtils.filterEventTypes( packageName,
                                                                                 imports,
                                                                                 projectEventTypes ) );

        //Filter and rename TypeSources based on package name and imports
        filteredTypeSources = new HashMap<String, TypeSource>();
        filteredTypeSources.putAll( PackageDataModelOracleUtils.filterTypeSources( packageName,
                                                                                   imports,
                                                                                   projectTypeSources ) );

        //Filter and rename Declared Types based on package name and imports
        filteredSuperTypes = new HashMap<String, String>();
        filteredSuperTypes.putAll( PackageDataModelOracleUtils.filterSuperTypes( packageName,
                                                                                 imports,
                                                                                 projectSuperTypes ) );

        //Filter and rename Type Annotations based on package name and imports
        filteredTypeAnnotations = new HashMap<String, Set<Annotation>>();
        filteredTypeAnnotations.putAll( PackageDataModelOracleUtils.filterTypeAnnotations( packageName,
                                                                                           imports,
                                                                                           projectTypeAnnotations ) );

        //Filter and rename Type Field Annotations based on package name and imports
        filteredTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();
        filteredTypeFieldsAnnotations.putAll( PackageDataModelOracleUtils.filterTypeFieldsAnnotations( packageName,
                                                                                                       imports,
                                                                                                       projectTypeFieldsAnnotations ) );

        //Filter and rename Enum definitions based on package name and imports
        filteredEnumLists = new HashMap<String, String[]>();
        filteredEnumLists.putAll( packageGuvnorEnumLists );
        filteredEnumLists.putAll( PackageDataModelOracleUtils.filterEnumDefinitions( packageName,
                                                                                     imports,
                                                                                     projectJavaEnumLists ) );

        //Filter and rename based on package name and imports
        filteredMethodInformation = new HashMap<String, List<MethodInfo>>();
        filteredMethodInformation.putAll( PackageDataModelOracleUtils.filterMethodInformation( packageName,
                                                                                               imports,
                                                                                               projectMethodInformation ) );

        //Filter and rename based on package name and imports
        filteredFieldParametersType = new HashMap<String, String>();
        filteredFieldParametersType.putAll( PackageDataModelOracleUtils.filterFieldParametersTypes( packageName,
                                                                                                    imports,
                                                                                                    projectFieldParametersType ) );
    }

    // ##############################################################################################
    // Non-interface methods for the Builder to use.
    // Ideally these should be package-protected but Errai Marshaller doesn't like non-public methods
    // ##############################################################################################

    public void setPackageName( final String packageName ) {
        this.packageName = packageName;
    }

    public void addPackageGuvnorEnums( final Map<String, String[]> dataEnumLists ) {
        this.packageGuvnorEnumLists.putAll( dataEnumLists );
    }

    public void addPackageDslConditionSentences( final List<DSLSentence> dslConditionSentences ) {
        this.packageDSLConditionSentences.addAll( dslConditionSentences );
    }

    public void addPackageDslActionSentences( final List<DSLSentence> dslActionSentences ) {
        this.packageDSLActionSentences.addAll( dslActionSentences );
    }

    public void addPackageGlobals( final Map<String, String> packageGlobalTypes ) {
        this.packageGlobalTypes.putAll( packageGlobalTypes );
    }

}

