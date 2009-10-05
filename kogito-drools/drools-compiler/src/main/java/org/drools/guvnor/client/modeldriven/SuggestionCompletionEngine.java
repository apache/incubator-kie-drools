package org.drools.guvnor.client.modeldriven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class SuggestionCompletionEngine
    implements
    PortableObject {

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
     * A list of fact types (never fully qualified).
     */
    public String[]                       factTypes;

    /**
     * A map of types to the fields. key is type, value is (String[] of fields)
     * 
     */
    public Map<String, String[]>          fieldsForType;

    /**
     * A map of the Fields to their types, needed for operator completions, as
     * well as other things like rendering strings, dates etc. This is in the
     * format of: { 'Type.field' => 'typename' }. Should not be the exact type,
     * perhaps just a high level interface, eg "Comparable".
     * 
     */
    public Map<String, String>            fieldTypes;
    /**
    * A map of the field that containts the parametrized type of a collection
    * List<String> name
    * key = "name"
    * value = "Strint" 
    *
    */
    public Map<String, String>            fieldParametersType    = new HashMap<String, String>();

    /**
     * Contains a map of globals (name is key) and their type (value).
     */
    public Map<String, String>            globalTypes            = new HashMap();

    /**
     * A map of types to the modifying methods they expose. key is type, value
     * is (Sting[] of modifying methods)
     * 
     **/
    public Map<String, String>            modifiers;

    /**
     * Contains a map of { TypeName.field : String[] } - where a list is valid
     * values to display in a drop down for a given Type.field combination.
     */
    public Map<String, String[]>          dataEnumLists          = new HashMap<String, String[]>();                                                                            // TODO this is
    // a PROBLEM as
    // its not
    // always
    // String[]

    /**
     * This will show the names of globals that are a collection type.
     */
    public String[]                       globalCollections;

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
    private transient Map                 dataEnumLookupFields;

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
        final String type = (String) this.fieldTypes.get( factType + "." + fieldName );
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

    public String[] getFactTypes() {
        return this.factTypes;
    }

    public String[] getFieldCompletions(final String factType) {
        return (String[]) this.fieldsForType.get( factType );

    }

    public String[] getOperatorCompletions(final String factType,
                                           final String fieldName) {
        final String type = (String) this.fieldTypes.get( factType + "." + fieldName );
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

    /**
     * A map of the Fields to their types, needed for operator completions, as
     * well as other things like rendering strings, dates etc. This is in the
     * format of: { 'Type.field' => 'typename' }. Should not be the exact type,
     * perhaps just a high level interface, eg "Comparable", "Numeric",
     * "String".
     * 
     */
    public String getFieldType(final String factType,
                               final String fieldName) {
        return (String) this.fieldTypes.get( factType + "." + fieldName );
    }

    /*
     * returns the type of parametric class
     * List<String> a in a class called Toto
     * key =   "Toto.a"
     * value = "String"
     */
    public String getParametricFieldType(final String factType,
                                         final String fieldName) {
        return (String) this.fieldParametersType.get( factType + "." + fieldName );
    }

    public boolean isGlobalVariable(final String variable) {
        return this.globalTypes.containsKey( variable );
    }

    public String[] getFieldCompletionsForGlobalVariable(final String varName) {
        final String type = (String) this.globalTypes.get( varName );
        return (String[]) this.fieldsForType.get( type );
    }

    public List<MethodInfo> getMethodInfosForGlobalVariable(final String varName) {
        final String type = (String) this.globalTypes.get( varName );
        return this.methodInfos.get( type );
    }

    private String[] toStringArray(final Set set) {
        final String[] f = new String[set.size()];
        int i = 0;
        for ( final Iterator iter = set.iterator(); iter.hasNext(); ) {
            f[i] = (String) iter.next();
            i++;
        }
        return f;
    }

    public String[] getGlobalVariables() {
        return toStringArray( this.globalTypes.keySet() );
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

        Map dataEnumLookupFields = loadDataEnumLookupFields();

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

                return DropDownData.create( (String[]) this.dataEnumLists.get( key ) );

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
            Map dataEnumLookupFields = loadDataEnumLookupFields();
            Object _typeField = dataEnumLookupFields.get( type + "." + field );

            if ( _typeField instanceof String ) {
                String typeField = (String) dataEnumLookupFields.get( type + "." + field );
                for ( int i = 0; i < currentValues.length; i++ ) {
                    ActionFieldValue val = currentValues[i];
                    if ( val.field.equals( typeField ) ) {
                        String key = type + "." + field + "[" + typeField + "=" + val.value + "]";
                        return DropDownData.create( (String[]) this.dataEnumLists.get( key ) );
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

        String[] vals = (String[]) this.dataEnumLists.get( type + "." + field );
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
                          Map dataEnumLists) {
        for ( Iterator iterator = dataEnumLists.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
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

                String[] qry = (String[]) dataEnumLists.get( key );
                return qry[0];
            } else if ( key.startsWith( factType + "." + field ) && (fieldsNeeded == null || fieldsNeeded.length == 0) ) {
                String[] qry = (String[]) dataEnumLists.get( key );
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
        return (String[]) this.dataEnumLists.get( factType + "." + field );
    }

    /**
     * This is only used by enums that are like Fact.field[something=X] and so
     * on.
     */
    Map loadDataEnumLookupFields() {
        if ( this.dataEnumLookupFields == null ) {
            this.dataEnumLookupFields = new HashMap();
            Set keys = this.dataEnumLists.keySet();
            for ( Iterator iter = keys.iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
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

    public List<String> getMethodFields(String factName,
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
        List<String> methodList = new ArrayList<String>();;

        if ( infos != null ) {
            for ( MethodInfo info : infos ) {
                methodList.add( info.getName() );
            }
        }

        return methodList;
    }
}
