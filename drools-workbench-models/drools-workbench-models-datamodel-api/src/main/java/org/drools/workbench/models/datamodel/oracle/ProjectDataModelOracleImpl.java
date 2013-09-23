package org.drools.workbench.models.datamodel.oracle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.commons.shared.oracle.model.DataType;
import org.drools.workbench.models.commons.shared.oracle.OperatorsOracle;
import org.drools.workbench.models.commons.shared.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.commons.shared.oracle.model.TypeSource;
import org.drools.workbench.models.commons.shared.oracle.model.Annotation;
import org.drools.workbench.models.commons.shared.oracle.model.DropDownData;
import org.drools.workbench.models.commons.shared.oracle.model.FieldAccessorsAndMutators;
import org.drools.workbench.models.commons.shared.oracle.model.MethodInfo;
import org.drools.workbench.models.commons.shared.oracle.model.ModelField;

/**
 * Default implementation of DataModelOracle
 */
public class ProjectDataModelOracleImpl implements ProjectDataModelOracle {

    //Project name
    protected String projectName;

    //Fact Types and their corresponding fields
    protected Map<String, ModelField[]> projectModelFields = new HashMap<String, ModelField[]>();

    //Map of the field that contains the parametrized type of a collection
    //for example given "List<String> name", key = "name" value = "String"
    protected Map<String, String> projectFieldParametersType = new HashMap<String, String>();

    //Map {factType, isEvent} to determine which Fact Type can be treated as events.
    protected Map<String, Boolean> projectEventTypes = new HashMap<String, Boolean>();

    //Map {factType, TypeSource} to determine where a Fact Type as defined.
    protected Map<String, TypeSource> projectTypeSources = new HashMap<String, TypeSource>();

    //Map {factType, superType} to determine the Super Type of a FactType.
    protected Map<String, String> projectSuperTypes = new HashMap<String, String>();

    //Map {factType, Set<Annotation>} containing the FactType's annotations.
    protected Map<String, Set<Annotation>> projectTypeAnnotations = new HashMap<String, Set<Annotation>>();

    //Map {factType, Map<fieldName, Set<Annotation>>} containing the FactType's Field annotations.
    protected Map<String, Map<String, Set<Annotation>>> projectTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();

    // Scoped (current package and imports) map of { TypeName.field : String[] } - where a list is valid values to display in a drop down for a given Type.field combination.
    protected Map<String, String[]> projectJavaEnumLists = new HashMap<String, String[]>();

    //Method information used (exclusively) by ExpressionWidget and ActionCallMethodWidget
    protected Map<String, List<MethodInfo>> projectMethodInformation = new HashMap<String, List<MethodInfo>>();

    // A map of FactTypes {factType, isCollection} to determine which Fact Types are Collections.
    protected Map<String, Boolean> projectCollectionTypes = new HashMap<String, Boolean>();

    // List of available rule names
    private Map<String, Collection<String>> ruleNames = new HashMap<String, Collection<String>>();

    // List of available package names
    private List<String> packageNames = new ArrayList<String>();

    // This is used to calculate what fields an enum list may depend on.
    private transient Map<String, Object> enumLookupFields;

    //Public constructor is needed for Errai Marshaller :(
    public ProjectDataModelOracleImpl() {
    }

    // ####################################
    // Fact Types
    // ####################################

    /**
     * Returns fact types available for rule authoring, i.e. those within the same package and those that have been imported.
     * @return
     */
    @Override
    public String[] getFactTypes() {
        final String[] types = projectModelFields.keySet().toArray( new String[ projectModelFields.size() ] );
        Arrays.sort( types );
        return types;
    }

    /**
     * Returns fact's name from class type
     * @param type
     * @return
     */
    @Override
    public String getFactNameFromType( final String type ) {
        if ( type == null ) {
            return null;
        }
        if ( projectModelFields.containsKey( type ) ) {
            return type;
        }
        for ( Map.Entry<String, ModelField[]> entry : projectModelFields.entrySet() ) {
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
    @Override
    public boolean isFactTypeRecognized( final String factType ) {
        return projectModelFields.containsKey( factType );
    }

    /**
     * Check whether a given FactType is an Event for CEP purposes
     * @param factType
     * @return
     */
    @Override
    public boolean isFactTypeAnEvent( final String factType ) {
        if ( !projectEventTypes.containsKey( factType ) ) {
            return false;
        }
        return projectEventTypes.get( factType );
    }

    /**
     * Return where a given FactType was defined
     * @param factType
     * @return
     */
    @Override
    public TypeSource getTypeSource( final String factType ) {
        return projectTypeSources.get( factType );
    }

    /**
     * Get the Super Type for a given FactType
     * @param factType
     * @return null if no Super Type
     */
    @Override
    public String getSuperType( final String factType ) {
        return projectSuperTypes.get( factType );
    }

    /**
     * Get the Annotations for a given FactType
     * @param factType
     * @return Empty Set if no annotations exist for the type
     */
    @Override
    public Set<Annotation> getTypeAnnotations( final String factType ) {
        if ( !projectTypeAnnotations.containsKey( factType ) ) {
            return Collections.EMPTY_SET;
        }
        return projectTypeAnnotations.get( factType );
    }

    /**
     * Get the Fields Annotations for a given FactType
     * @param factType
     * @return Empty Map if no annotations exist for the type
     */
    @Override
    public Map<String, Set<Annotation>> getTypeFieldsAnnotations( final String factType ) {
        if ( !projectTypeFieldsAnnotations.containsKey( factType ) ) {
            return Collections.EMPTY_MAP;
        }
        return projectTypeFieldsAnnotations.get( factType );
    }

    // ####################################
    // Fact Types' Fields
    // ####################################

    @Override
    public Map<String, ModelField[]> getModelFields() {
        return projectModelFields;
    }

    @Override
    public String[] getFieldCompletions( final String factType ) {
        return getModelFields( factType );
    }

    private String[] getModelFields( final String modelClassName ) {
        final String shortName = getFactNameFromType( modelClassName );
        if ( !projectModelFields.containsKey( shortName ) ) {
            return new String[ 0 ];
        }

        final ModelField[] fields = projectModelFields.get( shortName );
        final String[] fieldNames = new String[ fields.length ];
        for ( int i = 0; i < fields.length; i++ ) {
            fieldNames[ i ] = fields[ i ].getName();
        }
        return fieldNames;
    }

    @Override
    public String[] getFieldCompletions( final FieldAccessorsAndMutators accessorOrMutator,
                                         final String factType ) {
        final String shortName = getFactNameFromType( factType );
        if ( !projectModelFields.containsKey( shortName ) ) {
            return new String[ 0 ];
        }

        final ModelField[] fields = projectModelFields.get( shortName );
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

    @Override
    public String getFieldType( final String modelClassName,
                                final String fieldName ) {
        final ModelField field = getField( modelClassName,
                                           fieldName );
        return field == null ? null : field.getType();
    }

    @Override
    public String getFieldClassName( final String modelClassName,
                                     final String fieldName ) {
        final ModelField field = getField( modelClassName,
                                           fieldName );
        return field == null ? null : field.getClassName();
    }

    private ModelField getField( final String modelClassName,
                                 final String fieldName ) {
        final String shortName = getFactNameFromType( modelClassName );
        final ModelField[] fields = projectModelFields.get( shortName );
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

    // ####################################
    // Parametric Types
    // ####################################

    /**
     * Get the parametric type of a Field.
     * @param factType
     * @param fieldName
     * @return
     */
    @Override
    public String getParametricFieldType( final String factType,
                                          final String fieldName ) {
        final String qualifiedFactFieldName = factType + "#" + fieldName;
        return projectFieldParametersType.get( qualifiedFactFieldName );
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
    @Override
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
    @Override
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
    // Enums
    // ####################################

    /**
     * Get enums for a Type and Field.
     */
    @Override
    public DropDownData getEnums( final String type,
                                  final String field ) {
        return getEnums( type,
                         field,
                         new HashMap<String, String>() );
    }

    /**
     * Get enums for a Type and Field where the enum list may depend upon the values of other fields.
     */
    @Override
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

                final DropDownData data = DropDownData.create( projectJavaEnumLists.get( dataEnumListsKeyBuilder.toString() ) );
                if ( data != null ) {
                    return data;
                }
            } else if ( _typeFields != null ) {
                // these enums are calculated on demand, server side...
                final String[] fieldsNeeded = (String[]) _typeFields;
                final String queryString = getQueryString( type,
                                                           field,
                                                           fieldsNeeded,
                                                           projectJavaEnumLists );
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
    @Override
    public String[] getEnumValues( final String factType,
                                   final String field ) {
        return projectJavaEnumLists.get( factType + "#" + field );
    }

    @Override
    public boolean hasEnums( final String factType,
                             final String field ) {
        return hasEnums( factType + "#" + field );
    }

    @Override
    public boolean hasEnums( final String qualifiedFactField ) {
        boolean hasEnums = false;
        final String key = qualifiedFactField.replace( ".",
                                                       "#" );
        final String dependentType = key + "[";
        for ( String e : projectJavaEnumLists.keySet() ) {
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
    @Override
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
        if ( enumLookupFields == null ) {
            enumLookupFields = new HashMap<String, Object>();
            final Set<String> keys = projectJavaEnumLists.keySet();
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

                        enumLookupFields.put( factField,
                                              typeFieldBuilder.toString() );
                    } else {
                        final String[] fields = predicate.split( "," );
                        for ( int i = 0; i < fields.length; i++ ) {
                            fields[ i ] = fields[ i ].trim();
                        }
                        enumLookupFields.put( factField,
                                              fields );
                    }
                }
            }
        }

        return enumLookupFields;
    }

    // ####################################
    // Methods
    // ####################################

    /**
     * Get a list of Methods for a Fact Type
     * @param factType
     * @return
     */
    @Override
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
    @Override
    public List<String> getMethodNames( final String factType,
                                        final int paramCount ) {
        final List<MethodInfo> infos = projectMethodInformation.get( factType );
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
    @Override
    public List<String> getMethodParams( final String factType,
                                         final String methodNameWithParams ) {
        final List<MethodInfo> infos = projectMethodInformation.get( factType );
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
    @Override
    public MethodInfo getMethodInfo( final String factType,
                                     final String methodFullName ) {
        final List<MethodInfo> infos = projectMethodInformation.get( factType );
        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if ( info.getNameWithParameters().equals( methodFullName ) ) {
                    return info;
                }
            }
        }
        return null;
    }

    // ##############################################################################################
    // Non-interface methods for the Builder to use.
    // Ideally these should be package-protected but Errai Marshaller doesn't like non-public methods
    // ##############################################################################################

    public void setProjectName( final String projectName ) {
        this.projectName = projectName;
    }

    public void addFactsAndFields( final Map<String, ModelField[]> modelFields ) {
        this.projectModelFields.putAll( modelFields );
    }

    public void addFieldParametersType( final Map<String, String> fieldParametersType ) {
        this.projectFieldParametersType.putAll( fieldParametersType );
    }

    public void addEventTypes( final Map<String, Boolean> eventTypes ) {
        this.projectEventTypes.putAll( eventTypes );
    }

    public void addTypeSources( final Map<String, TypeSource> typeSources ) {
        this.projectTypeSources.putAll( typeSources );
    }

    public void addSuperTypes( final Map<String, String> superTypes ) {
        this.projectSuperTypes.putAll( superTypes );
    }

    public void addTypeAnnotations( final Map<String, Set<Annotation>> annotations ) {
        this.projectTypeAnnotations.putAll( annotations );
    }

    public void addTypeFieldsAnnotations( final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations ) {
        this.projectTypeFieldsAnnotations.putAll( typeFieldsAnnotations );
    }

    public void addEnumDefinitions( final Map<String, String[]> dataEnumLists ) {
        this.projectJavaEnumLists.putAll( dataEnumLists );
    }

    public void addMethodInformation( final Map<String, List<MethodInfo>> methodInformation ) {
        this.projectMethodInformation.putAll( methodInformation );
    }

    public void addCollectionTypes( final Map<String, Boolean> collectionTypes ) {
        this.projectCollectionTypes.putAll( collectionTypes );
    }

    public Map<String, String> getProjectFieldParametersType() {
        return this.projectFieldParametersType;
    }

    public Map<String, Boolean> getProjectEventTypes() {
        return this.projectEventTypes;
    }

    public Map<String, TypeSource> getProjectTypeSources() {
        return this.projectTypeSources;
    }

    public Map<String, String> getProjectSuperTypes() {
        return this.projectSuperTypes;
    }

    public Map<String, Set<Annotation>> getTypeAnnotations() {
        return this.projectTypeAnnotations;
    }

    public Map<String, Map<String, Set<Annotation>>> getTypeFieldsAnnotations() {
        return this.projectTypeFieldsAnnotations;
    }

    public Map<String, String[]> getProjectJavaEnumLists() {
        return this.projectJavaEnumLists;
    }

    public Map<String, List<MethodInfo>> getProjectMethodInformation() {
        return this.projectMethodInformation;
    }

    public Map<String, Boolean> getProjectCollectionTypes() {
        return this.projectCollectionTypes;
    }

    public void addRuleNames(String packageName, Collection<String> ruleNames) {
        this.ruleNames.put(packageName, ruleNames);
    }

    @Override
    public Map<String, Collection<String>> getRuleNamesMap() {
        return ruleNames;
    }

    @Override
    public List<String> getRuleNames() {
        List<String> allTheRuleNames = new ArrayList<String>();
        for (String packageName : ruleNames.keySet()) {
            allTheRuleNames.addAll(ruleNames.get(packageName));
        }
        return allTheRuleNames;
    }

    @Override
    public Collection<String> getRuleNamesForPackage(String packageName) {
        return ruleNames.get(packageName);
    }

    public void addPackageNames(List<String> packageNames) {
        this.packageNames.addAll(packageNames);
    }

    @Override
    public List<String> getPackageNames() {
        return packageNames;
    }
}

