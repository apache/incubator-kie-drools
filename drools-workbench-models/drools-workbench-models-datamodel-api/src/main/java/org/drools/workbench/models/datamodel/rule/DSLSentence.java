/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This represents a DSL sentence.
 */
public class DSLSentence
        implements
        IPattern,
        IAction {

    public static final String ENUM_TAG = "ENUM";
    public static final String DATE_TAG = "DATE";
    public static final String BOOLEAN_TAG = "BOOLEAN";
    public static final String CUSTOM_FORM_TAG = "CF";

    private String sentence;
    private String definition;
    private List<DSLVariableValue> values;

    /**
     * This will strip off any residual "{" stuff...
     */
    public String toString() {
        getDefinition();
        StringBuilder result = new StringBuilder( definition );
        int variableStart = definition.indexOf( "{" );
        while ( variableStart >= 0 ) {
            int variableEnd = getIndexForEndOfVariable( result.toString(),
                                                        variableStart );
            String variable = result.substring( variableStart + 1,
                                                variableEnd );
            int variableNameEnd = variable.indexOf( ":" );
            if ( variableNameEnd == -1 ) {
                variableNameEnd = variable.length();
            }
            String variableName = variable.substring( 0,
                                                      variableNameEnd );
            result.replace( variableStart + 1,
                            variableEnd,
                            variableName );
            variableEnd = variableEnd - variable.length() + variableName.length();
            variableStart = result.indexOf( "{",
                                            variableEnd );
        }
        return result.toString().replace( "\\n",
                                          "\n" );
    }

    /**
     * This will strip off any "{" stuff, substituting values accordingly
     */
    public String interpolate() {
        getValues();
        if ( definition == null ) {
            return "";
        }

        int variableStart = definition.indexOf( "{" );
        if ( variableStart < 0 ) {
            return definition;
        }

        int index = 0;
        int variableEnd = 0;
        StringBuilder sb = new StringBuilder();
        while ( variableStart >= 0 ) {
            sb.append( definition.substring( variableEnd,
                                             variableStart ) );
            variableEnd = getIndexForEndOfVariable( definition,
                                                    variableStart ) + 1;
            variableStart = definition.indexOf( "{",
                                                variableEnd );
            sb.append( values.get( index++ ).getValue() );
        }
        if ( variableEnd < definition.length() ) {
            sb.append( definition.substring( variableEnd ) );
        }
        return sb.toString();
    }

    /**
     * This is used by the GUI when adding a sentence to LHS or RHS.
     * @return
     */
    public DSLSentence copy() {
        final DSLSentence newOne = new DSLSentence();
        newOne.definition = getDefinition();
        List<DSLVariableValue> variableValues = getValues();
        if ( variableValues != null ) {
            for ( DSLVariableValue value : getValues() ) {
                newOne.getValues().add( value );
            }
        }
        return newOne;
    }

    public String getDefinition() {
        if ( definition == null ) {
            parseSentence();
        }
        return definition;
    }

    public void setDefinition( String definition ) {
        this.definition = definition;
    }

    public List<DSLVariableValue> getValues() {
        if ( this.values == null ) {
            parseDefinition();
        }
        return values;
    }

    public Map<String, String> getEnumFieldValueMap() {
        if ( this.values == null ) {
            parseDefinition();
        }
        Map<String, String> fieldValueMap = new HashMap<String, String>();
        if ( getValues().isEmpty() ) {
            return fieldValueMap;
        }

        int variableStart = definition.indexOf( "{" );
        int iVariable = 0;
        while ( variableStart >= 0 ) {
            int variableEnd = getIndexForEndOfVariable( definition,
                                                        variableStart );
            String variable = definition.substring( variableStart + 1,
                                                    variableEnd );

            //Extract field name for enumerations
            if ( variable.contains( ENUM_TAG ) ) {
                int lastIndex = variable.lastIndexOf( ":" );
                String factAndField = variable.substring( lastIndex + 1,
                                                          variable.length() );
                int dotIndex = factAndField.indexOf( "." );
                String field = factAndField.substring( dotIndex + 1,
                                                       factAndField.length() );
                fieldValueMap.put( field,
                                   values.get( iVariable ).getValue() );
            }
            iVariable++;
            variableStart = definition.indexOf( "{",
                                                variableEnd );
        }

        return fieldValueMap;
    }

    //Build the Definition and Values from a legacy Sentence. Legacy DSLSentence did not 
    //separate DSL definition from values, which led to complications when a user wanted 
    //to set the value of a DSL parameter to text including the special escaping used 
    //to differentiate value, from data-type, from restriction
    private void parseSentence() {
        if ( sentence == null ) {
            return;
        }
        definition = sentence;
        values = new ArrayList<DSLVariableValue>();
        sentence = null;

        int variableStart = definition.indexOf( "{" );
        while ( variableStart >= 0 ) {
            int variableEnd = getIndexForEndOfVariable( definition,
                                                        variableStart );
            String variable = definition.substring( variableStart + 1,
                                                    variableEnd );
            values.add( parseValue( variable ) );
            variableStart = definition.indexOf( "{",
                                                variableEnd );
        }
    }

    //Build the Values from the Definition.
    private void parseDefinition() {
        values = new ArrayList<DSLVariableValue>();
        if ( getDefinition() == null ) {
            return;
        }

        int variableStart = definition.indexOf( "{" );
        while ( variableStart >= 0 ) {
            int variableEnd = getIndexForEndOfVariable( definition,
                                                        variableStart );
            String variable = definition.substring( variableStart + 1,
                                                    variableEnd );
            values.add( parseValue( variable ) );
            variableStart = definition.indexOf( "{",
                                                variableEnd );
        }
    }

    private int getIndexForEndOfVariable( String dsl,
                                          int start ) {
        int end = -1;
        int bracketCount = 0;
        if ( start > dsl.length() ) {
            return end;
        }
        for ( int i = start; i < dsl.length(); i++ ) {
            char c = dsl.charAt( i );
            if ( c == '{' ) {
                bracketCount++;
            }
            if ( c == '}' ) {
                bracketCount--;
                if ( bracketCount == 0 ) {
                    end = i;
                    return end;
                }
            }
        }
        return -1;
    }

    private DSLVariableValue parseValue( String variable ) {
        //if the variable doesn't have a ':', then it is considered as a 
        //simple value
        if ( !variable.contains( ":" ) ) {
            return new DSLVariableValue( variable );
        }

        //if it does containt a ':', then the part before it is considered
        //as the real value (used to create the final drl) and the part
        //after it is considered as an id
        String value = variable.substring( 0,
                                           variable.indexOf( ":" ) );
        String id = variable.substring( variable.indexOf( ":" ) + 1 );

        return new DSLComplexVariableValue( id, value );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DSLSentence that = (DSLSentence) o;

        if (definition != null ? !definition.equals(that.definition) : that.definition != null) return false;
        if (sentence != null ? !sentence.equals(that.sentence) : that.sentence != null) return false;
        if (values != null ? !values.equals(that.values) : that.values != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sentence != null ? sentence.hashCode() : 0;
        result = 31 * result + (definition != null ? definition.hashCode() : 0);
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }
}
