package org.drools.guvnor.client.modeldriven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.modeldriven.ModelField.FIELD_CLASS_TYPE;
import org.drools.guvnor.client.modeldriven.brl.ActionFieldValue;
import org.drools.guvnor.client.modeldriven.brl.DSLSentence;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.PortableObject;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;

/**
 * An suggestion completion processor. This should be usable in both GWT/Web and
 * the IDE. The data for this can be loaded into this from simple string lists.
 * 
 * @author Michael Neale
 */
public class SuggestionCompletionEngine implements PortableObject {

    /** These are the explicit types supported */
    public static final String            TYPE_COLLECTION        = "Collection";
    public static final String            TYPE_COMPARABLE        = "Comparable";
    public static final String            TYPE_STRING            = "String";
    public static final String            TYPE_NUMERIC           = "Numeric";
    public static final String            TYPE_BOOLEAN           = "Boolean";
    public static final String            TYPE_DATE              = "Date";
    public static final String            TYPE_OBJECT            = "Object";                                                                                                   // for all other unknown
    // types

    /**
     * The operators that are used at different times (based on type).
     */
    private static final String[]         STANDARD_CONNECTIVES   = new String[]{"|| ==", "|| !=", "&& !="};
    private static final String[]         STRING_CONNECTIVES     = new String[]{"|| ==", "|| !=", "&& !=", "&& matches", "|| matches"};
    private static final String[]         COMPARABLE_CONNECTIVES = new String[]{"|| ==", "|| !=", "&& !=", "&& >", "&& <", "|| >", "|| <", "&& >=", "&& <=", "|| <=", "|| >="};
    private static final String[]         COLLECTION_CONNECTIVES = new String[]{"|| ==", "|| !=", "&& !=", "|| contains", "&& contains", "|| excludes", "&& excludes"};

    private static final String[]         STANDARD_OPERATORS     = new String[]{"==", "!="};
    private static final String[]         COMPARABLE_OPERATORS   = new String[]{"==", "!=", "<", ">", "<=", ">="};
    private static final String[]         STRING_OPERATORS       = new String[]{"==", "!=", "matches", "soundslike"};
    private static final String[]         COLLECTION_OPERATORS   = new String[]{"contains", "excludes", "==", "!="};

    /** The top level conditional elements (first order logic) */
    private static final String[]         CONDITIONAL_ELEMENTS   = new String[]{"not", "exists", "or"};

   
    /**
    * A map of the field that contains the parametrized type of a collection
    * List<String> name
    * key = "name"
    * value = "String"
    *
    */
    private Map<String, String>            fieldParametersType    = new HashMap<String, String>();

    /**
     * Contains a map of globals (name is key) and their type (value).
     */
    private Map<String, String>            globalTypes            = new HashMap<String, String>();

    /**
     * A map of types to the modifying methods they expose. key is type, value
     * is (Sting[] of modifying methods)
     * 
     **/
    private Map<String, String[]>            modifiers;

    /**
     * Contains a map of { TypeName.field : String[] } - where a list is valid
     * values to display in a drop down for a given Type.field combination.
     */
    private Map<String, String[]>          dataEnumLists          = new HashMap<String, String[]>();                                                                            // TODO this is
    // a PROBLEM as
    // its not
    // always
    // String[]

    /**
     * This will show the names of globals that are a collection type.
     */
    private String[]                       globalCollections;

    /** Operators (from the grammar):
         *      op=(    '=='
         |   '>'
         |   '>='
         |   '<'
         |   '<='
         |   '!='
         |   'contains'
         |   'matches'
         |       'excludes'
         )
         * Connectives add "&" and "|" to this.
         */

    /**
     * DSL language extensions, if needed, if provided by the package.
     */
    public DSLSentence[]                  conditionDSLSentences  = new DSLSentence[0];
    public DSLSentence[]                  actionDSLSentences     = new DSLSentence[0];
    public DSLSentence[]                  keywordDSLItems        = new DSLSentence[0];
    public DSLSentence[]                  anyScopeDSLItems       = new DSLSentence[0];

    /**
     * This is used to calculate what fields an enum list may depend on.
     * Optional.
     */
    private transient Map<String, Object>                 dataEnumLookupFields;

    // /**
    // * For bulk loading up the data (from a previous rule save)
    // *
    // * @param factToFields A map of "FactType" (key - String) to String[]
    // (value)
    // * @param factFieldToOperator A map of "FactType.field" (key - String) to
    // String[] operators
    // * @param factFieldToConnectiveOperator A map of "FactType.field" (key
    // -String) to String[] operators
    // * that are valid CONNECTIVE operators.
    // *
    // * @param globals A map of global variable name to its fields (String[]).
    // * @param boundFacts A map of bound facts to types.
    // * @param conditionDSLs a list of DSLSentence suggestions for the LHS
    // * @param actionDSLs a list of DSLSentence suggestions for the RHS
    // *
    // */
    // public void load(
    // Map factToFields,
    // Map factFieldToOperator,
    // Map factFieldToConnectiveOperator,
    // Map globals,
    // List conditionDSLs,
    // List actionDSLs
    // ) {
    // this.factToFields = factToFields;
    // this.factFieldToOperator = factFieldToOperator;
    // this.factFieldToConnectiveOperator = factFieldToConnectiveOperator;
    // this.actionDSLSentences = actionDSLs;
    // this.conditionDSLSentences = conditionDSLs;
    // this.globals = globals;
    //
    // }

    private Map<String, List<MethodInfo>> methodInfos            = new HashMap<String, List<MethodInfo>>();

    private Map<String, ModelField[]> modelFields = new HashMap<String, ModelField[]>();
    private Map<String, ModelField[]> filterModelFields = null;

    private Map<String, FieldAccessorsAndMutators> accessorsAndMutators = new HashMap<String, FieldAccessorsAndMutators>();
	private FactTypeFilter factFilter = null;
	private boolean filteringFacts = true;
    
    public SuggestionCompletionEngine() {

    }

    public String[] getConditionalElements() {
        return CONDITIONAL_ELEMENTS;
    }

    public DSLSentence[] getDSLConditions() {
        return this.conditionDSLSentences;
    }

    public DSLSentence[] getDSLActions() {
        return this.actionDSLSentences;
    }

    public String[] getConnectiveOperatorCompletions(final String factType,
                                                     final String fieldName) {
        final String type = this.getFieldType( factType + "." + fieldName );
        if ( type == null ) {
            return STANDARD_CONNECTIVES;
        } else if ( type.equals( TYPE_STRING ) ) {
            return STRING_CONNECTIVES;
        } else if ( type.equals( TYPE_COMPARABLE ) || type.equals( TYPE_DATE ) || type.equals( TYPE_NUMERIC ) ) {
            return COMPARABLE_CONNECTIVES;
        } else if ( type.equals( TYPE_COLLECTION ) ) {
            return COLLECTION_CONNECTIVES;
        } else {
            return STANDARD_CONNECTIVES;
        }

    }

    public String[] getFieldCompletions(final String factType) {
        return this.getModelFields( factType );
    }

    public String[] getFieldCompletions(FieldAccessorsAndMutators accessorOrMutator,
                                        String factType) {
        return this.getModelFields( accessorOrMutator,
                                    factType );
    }
    
    public String[] getOperatorCompletions(final String factType,
                                           final String fieldName) {
        final String type = this.getFieldType( factType, fieldName );
        if ( type == null ) {
            return STANDARD_OPERATORS;
        } else if ( type.equals( TYPE_STRING ) ) {
            return STRING_OPERATORS;
        } else if ( type.equals( TYPE_COMPARABLE ) || type.equals( TYPE_DATE ) || type.equals( TYPE_NUMERIC ) ) {
            return COMPARABLE_OPERATORS;
        } else if ( type.equals( TYPE_COLLECTION ) ) {
            return COLLECTION_OPERATORS;
        } else {
            return STANDARD_OPERATORS;
        }

    }

    public String[] getFieldCompletionsForGlobalVariable(final String varName) {
        final String type = this.getGlobalVariable( varName );
        return this.getModelFields(type);
    }

    public List<MethodInfo> getMethodInfosForGlobalVariable(final String varName) {
        final String type = this.getGlobalVariable( varName );
        return this.methodInfos.get( type );
    }

    private String[] toStringArray(final Set<?> set) {
        final String[] f = new String[set.size()];
        int i = 0;
        for ( final Iterator<?> iter = set.iterator(); iter.hasNext(); i++) {
            f[i] = iter.next().toString();
        }
        return f;
    }

    /**
     * This returns a list of enums options (values) that can be used for the
     * given field of the given FactPattern.
     * 
     * This also takes into account enums that depend on other fields.
     * 
     */
    public DropDownData getEnums(FactPattern pat,
                                 String field) {

        Map<String, Object> dataEnumLookupFields = loadDataEnumLookupFields();

        if ( pat.constraintList != null && pat.constraintList.constraints != null ) {
            // we may need to check for data dependent enums
            Object _typeFields = dataEnumLookupFields.get( pat.factType + "." + field );

            if ( _typeFields instanceof String ) {
                String typeFields = (String) _typeFields;
                FieldConstraint[] cons = pat.constraintList.constraints;

                String key = pat.factType + "." + field;

                boolean addOpeninColumn = true;
                String[] splitTypeFields = typeFields.split( "," );
                for ( int j = 0; j < splitTypeFields.length; j++ ) {
                    String typeField = splitTypeFields[j];

                    for ( int i = 0; i < cons.length; i++ ) {
                        FieldConstraint con = cons[i];
                        if ( con instanceof SingleFieldConstraint ) {
                            SingleFieldConstraint sfc = (SingleFieldConstraint) con;

                            if ( sfc.fieldName.trim().equals( typeField.trim() ) ) {
                                if ( addOpeninColumn ) {
                                    key += "[";
                                    addOpeninColumn = false;
                                }
                                key += typeField + "=" + sfc.value;

                                if ( j != (splitTypeFields.length - 1) ) {
                                    key += ",";
                                }
                            }
                        }
                    }
                }

                if ( !addOpeninColumn ) {
                    key += "]";
                }

                DropDownData data = DropDownData.create( this.dataEnumLists.get( key ) );
                if ( data != null ) {
                    return DropDownData.create( this.dataEnumLists.get( key ) );
                }
            } else if ( _typeFields != null ) {
                // these enums are calculated on demand, server side...
                String[] fieldsNeeded = (String[]) _typeFields;

                String queryString = getQueryString( pat.factType,
                                                     field,
                                                     fieldsNeeded,
                                                     this.dataEnumLists );

                String[] valuePairs = new String[fieldsNeeded.length];

                // collect all the values of the fields needed, then return it
                // as a string...
                for ( int i = 0; i < fieldsNeeded.length; i++ ) {
                    for ( int j = 0; j < pat.constraintList.constraints.length; j++ ) {
                        FieldConstraint con = pat.constraintList.constraints[j];
                        if ( con instanceof SingleFieldConstraint ) {
                            SingleFieldConstraint sfc = (SingleFieldConstraint) con;
                            if ( sfc.fieldName.equals( fieldsNeeded[i] ) ) {
                                valuePairs[i] = fieldsNeeded[i] + "=" + sfc.value;
                            }
                        }
                    }
                }

                if ( valuePairs.length > 0 && valuePairs[0] != null ) {
                    return DropDownData.create( queryString,
                                                valuePairs );
                }
            }
        }
        return DropDownData.create( getEnumValues( pat.factType,
                                                   field ) );
    }

    /**
     * Similar to the one above - but this one is for RHS.
     */
    public DropDownData getEnums(String type,
                                 ActionFieldValue[] currentValues,
                                 String field) {

        if ( currentValues != null ) {
            Map<String, Object> dataEnumLookupFields = loadDataEnumLookupFields();
            Object _typeField = dataEnumLookupFields.get( type + "." + field );

            if ( _typeField instanceof String ) {
                String typeField = (String) dataEnumLookupFields.get( type + "." + field );
                for ( int i = 0; i < currentValues.length; i++ ) {
                    ActionFieldValue val = currentValues[i];
                    if ( val.field.equals( typeField ) ) {
                        String key = type + "." + field + "[" + typeField + "=" + val.value + "]";
                        return DropDownData.create( this.getDataEnumList( key ) );
                    }
                }
            } else if ( _typeField != null ) {
                String[] fieldsNeeded = (String[]) _typeField;
                String queryString = getQueryString( type,
                                                     field,
                                                     fieldsNeeded,
                                                     this.dataEnumLists );
                String[] valuePairs = new String[fieldsNeeded.length];

                // collect all the values of the fields needed, then return it
                // as a string...
                for ( int i = 0; i < fieldsNeeded.length; i++ ) {
                    for ( int j = 0; j < currentValues.length; j++ ) {
                        ActionFieldValue con = currentValues[j];
                        if ( con.field.equals( fieldsNeeded[i] ) ) {
                            valuePairs[i] = fieldsNeeded[i] + "=" + con.value;
                        }
                    }
                }
                return DropDownData.create( queryString,
                                            valuePairs );

            }
        }

        String[] vals = this.getDataEnumList( type + "." + field );
        return DropDownData.create( vals );

    }

    /**
     * Get the query string for a fact.field It will ignore any specified field,
     * and just look for the string - as there should only be one Fact.field of
     * this type (it is all determined server side).
     * @param fieldsNeeded 
     */
    String getQueryString(String factType,
                          String field,
                          String[] fieldsNeeded,
                          Map<String, String[]> dataEnumLists) {
        for ( Iterator<String> iterator = dataEnumLists.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            if ( key.startsWith( factType + "." + field ) && fieldsNeeded != null && key.contains( "[" ) ) {

                String[] values = key.substring( key.indexOf( '[' ) + 1,
                                                 key.lastIndexOf( ']' ) ).split( "," );

                if ( values.length != fieldsNeeded.length ) {
                    continue;
                }

                boolean fail = false;
                for ( int i = 0; i < values.length; i++ ) {
                    String a = values[i].trim();
                    String b = fieldsNeeded[i].trim();
                    if ( !a.equals( b ) ) {
                        fail = true;
                        break;
                    }
                }
                if ( fail ) {
                    continue;
                }

                String[] qry = getDataEnumList( key );
                return qry[0];
            } else if ( key.startsWith( factType + "." + field ) && (fieldsNeeded == null || fieldsNeeded.length == 0) ) {
                String[] qry = getDataEnumList( key );
                return qry[0];
            }
        }
        throw new IllegalStateException();
    }

    /**
     * For simple cases - where a list of values are known based on a field.
     */
    public String[] getEnumValues(String factType,
                                  String field) {
        return this.getDataEnumList( factType + "." + field );
    }

    /**
     * This is only used by enums that are like Fact.field[something=X] and so
     * on.
     */
    Map<String, Object> loadDataEnumLookupFields() {
        if ( this.dataEnumLookupFields == null ) {
            this.dataEnumLookupFields = new HashMap<String, Object>();
            Set<String> keys = this.dataEnumLists.keySet();
            for ( Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
                String key = iter.next();
                if ( key.indexOf( '[' ) != -1 ) {
                    int ix = key.indexOf( '[' );
                    String factField = key.substring( 0,
                                                      ix );
                    String predicate = key.substring( ix + 1,
                                                      key.indexOf( ']' ) );
                    if ( predicate.indexOf( '=' ) > -1 ) {

                        String[] bits = predicate.split( "," );
                        String typeField = "";

                        for ( int i = 0; i < bits.length; i++ ) {
                            typeField += bits[i].substring( 0,
                                                            bits[i].indexOf( '=' ) );
                            if ( i != (bits.length - 1) ) {
                                typeField += ",";
                            }
                        }

                        dataEnumLookupFields.put( factField,
                                                  typeField );
                    } else {
                        String[] fields = predicate.split( "," );
                        for ( int i = 0; i < fields.length; i++ ) {
                            fields[i] = fields[i].trim();
                        }
                        dataEnumLookupFields.put( factField,
                                                  fields );
                    }
                }
            }
        }

        return dataEnumLookupFields;
    }

    public void addMethodInfo(String factName,
                              List<MethodInfo> methodInfos) {
        this.methodInfos.put( factName,
                              methodInfos );
    }
    
    public List<String> getMethodParams(String factName,
                                        String methodNameWithParams) {
        if ( methodInfos.get( factName ) != null ) {
            List<MethodInfo> infos = methodInfos.get( factName );

            for ( MethodInfo info : infos ) {
                if ( info.getNameWithParameters().startsWith( methodNameWithParams ) ) {
                    return info.getParams();
                }
            }
        }

        return null;
    }

    public List<String> getMethodNames(String factName) {
        List<MethodInfo> infos = methodInfos.get( factName );
        List<String> methodList = new ArrayList<String>();

        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                methodList.add( info.getName() );
            }
        }

        return methodList;
    }

    public MethodInfo getMethodinfo(String factName, String methodFullName) {
    	List<MethodInfo> infos = methodInfos.get( factName );

        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if (info.getNameWithParameters().equals(methodFullName)) {
                	return info;
                }
            }
        }

        return null;
    }
    
    public String getMethodClassType(String factName, String methodFullName) {
    	List<MethodInfo> infos = methodInfos.get( factName );

        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                if (info.getNameWithParameters().equals(methodFullName)) {
                	return info.getReturnClassType();
                }
            }
        }

        return null;
    }
    
    public List<String> getMethodFullNames(String factName) {
        return getMethodFullNames(factName, -1);
    }    

    public List<String> getMethodFullNames(String factName, int paramCount) {
        List<MethodInfo> infos = methodInfos.get( factName );
        List<String> methodList = new ArrayList<String>();

        if ( infos != null ) {
			for (MethodInfo info : infos) {
				if (paramCount == -1 || info.getParams().size() <= paramCount) {
					methodList.add(info.getNameWithParameters());
				}
			}
        }

        return methodList;
    }
    
    /**
     * Returns fact's name from class type
     *  
     * @param type
     * @return
     */
    public String getFactNameFromType(String type) {
    	if (type == null) {
    		return null;
    	}
    	if (getModelFields().containsKey(type)) {
    		return type;
    	} 
    	for (Map.Entry<String, ModelField[]> entry : getModelFields().entrySet()) {
			for (ModelField mf : entry.getValue()) {
				if ("this".equals(mf.getName()) && type.equals(mf.getClassName())) {
					return entry.getKey();
				}
			}
		}
    	return null;
    }
    
    /**
     * returns the type of parametric class
     * List<String> a in a class called Toto
     * key =   "Toto.a"
     * value = "String"
     */
    public String getParametricFieldType(final String factType,
                                         final String fieldName) {
        return this.getParametricFieldType( factType + "." + fieldName );
    }

    public String getParametricFieldType(String fieldName){
        return this.fieldParametersType.get(fieldName);
    }

    public void putParametricFieldType(String fieldName, String type){
        this.fieldParametersType.put(fieldName, type);
    }
    
    public String getGlobalVariable(String name){
        return this.globalTypes.get(name);
    }

    public boolean isGlobalVariable(String name){
        return this.globalTypes.containsKey(name);
    }

    public void setGlobalVariables(Map<String, String> globalTypes){
         this.globalTypes = globalTypes;
    }

    public String[] getGlobalVariables() {
        return toStringArray( this.globalTypes.keySet() );
    }

    public void setModifiers(Map<String,String[]> map){
        this.modifiers = map;
    }

    public String[] getModifiers(String name){
        return this.modifiers.get(name);
    }

    public void setGlobalCollections(String[] globalCollections){
        this.globalCollections = globalCollections;
    }

    public String[] getGlobalCollections() {
        return this.globalCollections;
    }

    public String[] getDataEnumList(String type){
        return this.dataEnumLists.get(type);
    }

    public void setDataEnumLists(Map<String,String[]> data){
        this.dataEnumLists = data;
    }

    public void putDataEnumList(String name,String[] value){
        this.dataEnumLists.put(name, value);
    }

    public void putAllDataEnumLists(Map<String,String[]> value){
        this.dataEnumLists.putAll(value);
    }

    public int getDataEnumListsSize(){
        return this.dataEnumLists.size();
    }

    public boolean hasDataEnumLists(){
        return this.dataEnumLists != null && this.dataEnumLists.size() > 0;
    }


    ////

    public void setFactTypes(String[] factTypes) {
        for (String factType : factTypes) {
            //adds the fact type with no fields.
            this.getModelFields().put(factType, new ModelField[0]);
        }
    }

    public void setFactTypeFilter(FactTypeFilter filter){
    	this.factFilter = filter;
    	filterModelFields();
    }

    public void setFieldsForTypes(Map<String,ModelField[]> fieldsForType){
    	this.getModelFields().clear();
        this.getModelFields().putAll(fieldsForType);
    }

    /**
     * Returns all the fact types.
     * @return
     */
    public String[] getFactTypes() {
        String[] types = this.getModelFields().keySet().toArray(new String[this.getModelFields().size()]);
        Arrays.sort(types);
		return types;
    }

    public boolean containsFactType(String modelClassName){
        return this.getModelFields().containsKey(modelClassName);
    }

    private ModelField getField(String modelClassName, String fieldName){
        ModelField[] fields = this.getModelFields().get(modelClassName);

        if (fields == null){
            return null;
        }

        for (ModelField modelField : fields) {
            if (modelField.getName().equals(fieldName)){
                return modelField;
            }
        }

        return null;
    }

    public String[] getModelFields(FieldAccessorsAndMutators accessorOrMutator,
                                   String modelClassName) {

        if ( !this.getModelFields().containsKey( modelClassName ) ) {
            return new String[0];
        }

        ModelField[] fields = this.getModelFields().get( modelClassName );

        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add( "this" );

        for ( int i = 0; i < fields.length; i++ ) {
            String fieldName = fields[i].getName();
            if ( fields[i].getClassType() == FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS ) {
                fieldNames.add( fieldName );
            } else if ( FieldAccessorsAndMutators.compare( accessorOrMutator,
                                                           this.accessorsAndMutators.get( modelClassName + "." + fieldName ) ) ) {
                fieldNames.add( fieldName );
            }
        }

        return fieldNames.toArray( new String[fieldNames.size()] );
    }

    public String[] getModelFields(String modelClassName){

        if (!this.getModelFields().containsKey(modelClassName)){
            return new String[0];
        }

        ModelField[] fields = this.getModelFields().get(modelClassName);

        String[] fieldNames = new String[fields.length];

        for (int i=0;i<fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }

        return fieldNames;
    }

    /**
     *
     * @param propertyName of the type class.field
     * @return
     */
    public String getFieldClassName(String propertyName){
        String[] split = propertyName.split("\\.");
        if (split.length!=2){
            throw new IllegalArgumentException("Invalid format '"+propertyName+"'. It must be of type className.propertyName");
        }
        return this.getFieldClassName(split[0], split[1]);
    }

    public String getFieldClassName(String modelClassName, String fieldName){
        ModelField field = this.getField(modelClassName, fieldName);
        return field==null?null:field.getClassName();
    }

    public ModelField.FIELD_CLASS_TYPE getFieldClassType(String modelClassName, String fieldName){
        ModelField field = this.getField(modelClassName, fieldName);
        return field==null?null:field.getClassType();
    }

    public String getFieldType(String propertyName){
        String[] split = propertyName.split("\\.", 3);
        if (split.length!=2){
            throw new IllegalArgumentException("Invalid format '"+propertyName+"'. It must be of type className.propertyName");
        }
        return this.getFieldType(split[0], split[1]);
    }

    public String getFieldType(String modelClassName, String fieldName){
        ModelField field = this.getField(modelClassName, fieldName);
        return field==null?null:field.getType();
    }

    public void setAccessorsAndMutators(Map<String, FieldAccessorsAndMutators> accessorsAndMutators) {
        this.accessorsAndMutators=accessorsAndMutators;
    }

    
    
	public void setModelFields(Map<String, ModelField[]> modelFields) {
		this.modelFields = modelFields;
		filterModelFields();
	}

	private void filterModelFields() {
		if (factFilter != null) {
			filterModelFields = new HashMap<String, ModelField[]>();
			for (Map.Entry<String, ModelField[]> entry : modelFields.entrySet()) {
				if (!factFilter.filter(entry.getKey())) {
					filterModelFields.put(entry.getKey(), entry.getValue());
				}
			}
		}
	}
	
	public Map<String, ModelField[]> getModelFields() {
		if (factFilter != null && isFilteringFacts()) {
			return filterModelFields;
		}
		return modelFields;
	}

	public boolean isFilteringFacts() {
		return filteringFacts;
	}

	public void setFilteringFacts(boolean filterFacts) {
		this.filteringFacts = filterFacts;
	}
}
