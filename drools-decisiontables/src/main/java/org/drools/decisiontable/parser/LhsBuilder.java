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

package org.drools.decisiontable.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.template.model.SnippetBuilder;
import org.drools.template.parser.DecisionTableParseException;

/**
 * This utility will build up a list of constraints for a column.
 * For instance, the column has been spanned across multiple cells, and the cells below
 * contain the constraints.
 */
public class LhsBuilder implements SourceBuilder {

    private static final char QUOTE_DOUBLE = 0x22;
    private static final char QUOTE_LEFT = 0x201c;
    private static final char QUOTE_RIGHT = 0x201d;

    private int headerRow;
    private int headerCol;
    private String colDefPrefix;
    private String colDefSuffix;
    private boolean multiple;
    private boolean forAll;
    private String andop;
    private Map<Integer, String> constraints;
    private List<String> values;
    private boolean hasValues;
    private Map<Integer, FieldType> fieldTypes;

    private static Set<String> operators;

    static {
        operators = new HashSet<String>();
        operators.add( "==" );
        operators.add( "=" );
        operators.add( "!=" );
        operators.add( "<" );
        operators.add( ">" );
        operators.add( "<=" );
        operators.add( ">=" );
        operators.add( "contains" );
        operators.add( "matches" );
        operators.add( "memberOf" );
        operators.add( "str[startsWith]" );
        operators.add( "str[endsWith]" );
        operators.add( "str[length]" );
    }

    private static final Pattern patParFrm = Pattern.compile( "\\(\\s*\\)\\s*from\\b" );
    private static final Pattern patFrm = Pattern.compile( "\\s+from\\s+" );
    private static final Pattern patPar = Pattern.compile( "\\(\\s*\\)\\s*\\Z" );
    private static final Pattern patEval = Pattern.compile( "\\beval\\s*(?:\\(\\s*\\)\\s*)?$" );

    /**
     * @param colDefinition
     *         The initial column definition that is shared via merged cells.
     */
    public LhsBuilder( int row,
                       int column,
                       String colDefinition ) {
        this.headerRow = row;
        this.headerCol = column;
        this.constraints = new HashMap<Integer, String>();
        this.fieldTypes = new HashMap<>();
        this.values = new ArrayList<String>();
        this.forAll = false;

        String colDef = colDefinition == null ? "" : colDefinition;
        if ( "".equals( colDef ) ) {
            colDefPrefix = colDefSuffix = "";
            multiple = false;
            andop = "";
            return;
        }
        multiple = true;

        // ...eval
        Matcher matEval = patEval.matcher( colDef );
        if ( matEval.find() ) {
            colDefPrefix = colDef.substring( 0, matEval.start() ) + "eval(";
            colDefSuffix = ")";
            andop = " && ";
            return;
        }
        andop = ", ";

        // ...(<b> ) from...
        Matcher matParFrm = patParFrm.matcher( colDef );
        if ( matParFrm.find() ) {
            colDefPrefix = colDef.substring( 0, matParFrm.start() ) + '(';
            colDefSuffix = ") from" + colDef.substring( matParFrm.end() );
            return;
        }

        // ...from...
        Matcher matFrm = patFrm.matcher( colDef );
        if ( matFrm.find() ) {
            colDefPrefix = colDef.substring( 0, matFrm.start() ) + "(";
            colDefSuffix = ") from " + colDef.substring( matFrm.end() );
            return;
        }

        // ...(<b> )...
        Matcher matPar = patPar.matcher( colDef );
        if ( matPar.find() ) {
            colDefPrefix = colDef.substring( 0, matPar.start() ) + '(';
            colDefSuffix = ")" + colDef.substring( matPar.end() );
            return;
        }

        // <a>
        colDefPrefix = colDef + '(';
        colDefSuffix = ")";
    }

    public ActionType.Code getActionTypeCode() {
        return ActionType.Code.CONDITION;
    }

    public void addTemplate( int row,
                             int column,
                             String content ) {
        content = content.trim();
        if ( constraints.containsKey( column ) ) {
            throw new IllegalArgumentException( "Internal error: Can't have a constraint added twice to one spreadsheet col." );
        }
        if ( fieldTypes.containsKey( column ) ) {
            throw new IllegalArgumentException( "Internal error: Can't have a FieldType added twice to one spreadsheet col." );
        }

        //we can wrap all values in quotes, it all works
        final FieldType fieldType = calcFieldType( content );
        if ( !isMultipleConstraints() ) {
            constraints.put( column,
                             content );
        } else if ( fieldType == FieldType.FORALL_FIELD ) {
            forAll = true;
            constraints.put( column,
                             content );
        } else if ( fieldType == FieldType.NORMAL_FIELD ) {
            constraints.put( column,
                             content );
        } else if ( fieldType == FieldType.SINGLE_FIELD ) {
            constraints.put( column,
                             content + " == \"" + SnippetBuilder.PARAM_STRING + "\"" );
        } else if ( fieldType == FieldType.OPERATOR_FIELD ) {
            constraints.put( column,
                             content + " \"" + SnippetBuilder.PARAM_STRING + "\"" );
        }
        this.fieldTypes.put( column,
                             fieldType );
    }

    public void clearValues() {
        this.hasValues = false;
        this.values.clear();
    }

    public void addCellValue( int row,
                              int column,
                              String value ) {
        this.hasValues = true;
        Integer key = new Integer( column );
        String content = this.constraints.get( key );
        if ( content == null ) {
            throw new DecisionTableParseException( "No code snippet for CONDITION in cell " +
                                                           RuleSheetParserUtil.rc2name( this.headerRow + 2, this.headerCol ) );
        }
        SnippetBuilder snip = new SnippetBuilder( content );
        String result = snip.build( fixValue( column,
                                              value ) );
        this.values.add( result );
    }

    /**
     * If the type of the column is either FieldType.SINGLE_FIELD or FieldType.OPERATOR_FIELD we have
     * added quotation-marks around the template parameter. Consequentially if a cell value included the
     * quotation-marks (i.e. for an empty-string or white-space) we need to remove the additional
     * quotation-marks.
     * @param value
     * @return
     */
    private String fixValue( final int column,
                             final String value ) {
        String _value = value;
        final FieldType fieldType = this.fieldTypes.get( column );
        if ( fieldType == FieldType.NORMAL_FIELD || !isMultipleConstraints() || isForAll() ) {
            return value;
        }
        if ( isDelimitedString( _value ) ) {
            _value = _value.substring( 1,
                                       _value.length() - 1 );
        }
        return _value;
    }

    public String getResult() {
        StringBuffer buf = new StringBuffer();
        if ( !isMultipleConstraints() ) {
            String nl = "";
            for ( String content : values ) {
                buf.append( nl ).append( content );
                nl = "\n";
            }
            return buf.toString();
        } else {
            buf.append( this.colDefPrefix );
            String sep = "";
            for ( String constraint : values ) {
                buf.append( sep ).append( constraint );
                sep = this.andop;
            }
            buf.append( colDefSuffix );
            return buf.toString();
        }
    }

    /**
     * Returns true if this is building up multiple constraints as in:
     * Foo(a ==b, c == d) etc...
     * If not, then it it really just like the "classic" style DTs.
     */
    boolean isMultipleConstraints() {
        return multiple;
    }

    /**
     * Check whether the column definition is a 'forall' construct. In these
     * situations we do not attempt to strip quotation marks from field values.
     * @return true if the column definition is 'forall'
     */
    boolean isForAll() {
        return forAll;
    }

    /**
     * Work out the type of "field" that is being specified,
     * as in :
     * age
     * age <
     * age == $param
     * age == $1 || age == $2
     * forall{age < $}{,}
     * <p/>
     * etc. as we treat them all differently.
     */
    public FieldType calcFieldType( String content ) {
        final SnippetBuilder.SnippetType snippetType = SnippetBuilder.getType( content );
        if ( snippetType.equals( SnippetBuilder.SnippetType.FORALL ) ) {
            return FieldType.FORALL_FIELD;
        } else if ( !snippetType.equals(
                SnippetBuilder.SnippetType.SINGLE ) ) {
            return FieldType.NORMAL_FIELD;
        }
        for ( String op : operators ) {
            if ( content.endsWith( op ) ) {
                return FieldType.OPERATOR_FIELD;
            }
        }
        return FieldType.SINGLE_FIELD;
    }

    static class FieldType {

        //This is only used to aid debugging
        @SuppressWarnings("unused")
        private String fieldType;

        private FieldType( final String fieldType ) {
            this.fieldType = fieldType;
        }

        public static final FieldType SINGLE_FIELD = new FieldType( "single" );
        public static final FieldType OPERATOR_FIELD = new FieldType( "operator" );
        public static final FieldType NORMAL_FIELD = new FieldType( "normal" );
        public static final FieldType FORALL_FIELD = new FieldType( "forall" );

    }

    public boolean hasValues() {
        return hasValues;
    }

    private boolean isDelimitedString( final String content ) {
        return isDelimitedString( content,
                                  QUOTE_DOUBLE,
                                  QUOTE_DOUBLE ) ||
                isDelimitedString( content,
                                   QUOTE_LEFT,
                                   QUOTE_RIGHT ) ||
                isDelimitedString( content,
                                   QUOTE_LEFT,
                                   QUOTE_LEFT ) ||
                isDelimitedString( content,
                                   QUOTE_RIGHT,
                                   QUOTE_RIGHT );
    }

    private boolean isDelimitedString( final String content,
                                       final char openQuote,
                                       final char closeQuote ) {
        return ( content.indexOf( openQuote ) == 0 && content.indexOf( closeQuote,
                                                                       1 ) == content.length() - 1 );
    }

}
