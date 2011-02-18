package org.drools.decisiontable.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
 * @author Michael Neale
 *
 */
public class LhsBuilder implements SourceBuilder {

    private int headerRow;
    private int headerCol;
    private String colDefPrefix;
    private String colDefSuffix;
    private boolean multiple;
    private String andop;
    private Map<Integer, String> constraints;
    private List<String> values;
    private boolean hasValues;
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
    private static final Pattern patFrm    = Pattern.compile( "\\s+from\\s+" );
    private static final Pattern patPar    = Pattern.compile( "\\(\\s*\\)" );
    private static final Pattern patEval   = Pattern.compile( "\\beval\\s*(?:\\(\\s*\\)\\s*)?$" );
    
    
    /**
     * @param colDefinition The initial column definition that is shared via merged cells.
     */
    public LhsBuilder( int row, int column, String colDefinition ) {
        this.headerRow = row;
        this.headerCol = column;
        this.constraints = new HashMap<Integer, String>();
        this.values = new ArrayList<String>();

        String colDef = colDefinition == null ? "" : colDefinition;
        if( "".equals( colDef ) ){
            colDefPrefix = colDefSuffix = "";
            multiple = false;
            andop = "";
            return;
        }
        multiple = true;
        
        // ...eval
        Matcher matEval = patEval.matcher( colDef );
        if( matEval.find() ){
            colDefPrefix = colDef.substring( 0, matEval.start() ) + "eval(";
            colDefSuffix = ")";
            andop = " && ";
            return;
        }
        andop = ", ";

        // ...(<b> ) from...
        Matcher matParFrm = patParFrm.matcher( colDef );
        if( matParFrm.find() ){
            colDefPrefix = colDef.substring( 0, matParFrm.start() ) + '(';
            colDefSuffix = ") from" + colDef.substring( matParFrm.end() );
            return;
        }

        // ...from...
        Matcher matFrm = patFrm.matcher( colDef );
        if( matFrm.find() ){
            colDefPrefix = colDef.substring( 0, matFrm.start() ) + "(";
            colDefSuffix = ") from " + colDef.substring( matFrm.end() );
            return;
        }
        
        // ...(<b> )...
        Matcher matPar = patPar.matcher( colDef );
        if( matPar.find() ){
            colDefPrefix = colDef.substring( 0, matPar.start() ) + '(';
            colDefSuffix = ")" + colDef.substring( matPar.end() );
            return;
        }
        
        // <a>
        colDefPrefix = colDef + '(';
        colDefSuffix = ")";
    }

    public ActionType.Code getActionTypeCode(){
        return ActionType.Code.CONDITION;
    }

    public void addTemplate(int row, int column, String content) {
        Integer key = new Integer( column );
        content = content.trim();
        if ( constraints.containsKey( key ) ) {
            throw new IllegalArgumentException( "Internal error: Can't have a constraint added twice to one spreadsheet col." );
        }
        
        //we can wrap all values in quotes, it all works
        FieldType fieldType = calcFieldType( content );
        if (fieldType == FieldType.NORMAL_FIELD || ! isMultipleConstraints()) {
            constraints.put( key, content );
        } else if (fieldType == FieldType.SINGLE_FIELD) {
            constraints.put( key, content + " == \"" + SnippetBuilder.PARAM_STRING + "\"" );
        } else if (fieldType == FieldType.OPERATOR_FIELD) {
            constraints.put( key, content + " \"" + SnippetBuilder.PARAM_STRING + "\"" );
        }
    }
    
    public void clearValues() {
        this.hasValues = false;
        this.values.clear();
    }

    public void addCellValue(int row, int column, String value) {
        this.hasValues = true;
        Integer key = new Integer( column );
        String content = (String) this.constraints.get( key );
        if( content == null ){
            throw new DecisionTableParseException( "No code snippet for CONDITION in cell " +
            	RuleSheetParserUtil.rc2name( this.headerRow + 2, this.headerCol ) );
        }
        SnippetBuilder snip = new SnippetBuilder( content );
        String result = snip.build( value );
        this.values.add( result );
    }

    public String getResult() {
        StringBuffer buf = new StringBuffer();
        if ( ! isMultipleConstraints() ) {
            String nl = "";
            for( String content: values ){
                buf.append( nl ).append( content );
                nl = "\n";
            }
            return buf.toString();
        } else {
            buf.append( this.colDefPrefix );
            String sep = "";
            for( String constraint: values ) {
                buf.append( sep ).append( constraint );
                sep = this.andop;
            }
            buf.append( colDefSuffix );
            return buf.toString();
        }
    }

    /** Returns true if this is building up multiple constraints as in:
     * Foo(a ==b, c == d) etc...
     * If not, then it it really just like the "classic" style DTs.
     */
    boolean isMultipleConstraints() {
        return multiple;
    }

    /**
     * Work out the type of "field" that is being specified, 
     * as in :
     * age 
     * age <
     * age == $param
     * age == $1 || age == $2
     * forall{age < $}{,}
     * 
     * etc. as we treat them all differently.
     */
    public FieldType calcFieldType(String content) {
        if (!SnippetBuilder.getType(content).equals(
        		SnippetBuilder.SnippetType.SINGLE)) {
        	return FieldType.NORMAL_FIELD;
        }
       for ( String op : operators ) {
            if (content.endsWith( op )) {
                return FieldType.OPERATOR_FIELD;
            }
        }
        return FieldType.SINGLE_FIELD;
    }
    
    static class FieldType {
        public static final FieldType SINGLE_FIELD = new FieldType();
        public static final FieldType OPERATOR_FIELD = new FieldType();
        public static final FieldType NORMAL_FIELD = new FieldType();
    }

    public boolean hasValues() {
        return hasValues;
    }

}
