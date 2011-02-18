/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.List;
import java.util.StringTokenizer;

import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DecisionTableParseException;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * Parking lot for utility methods that don't belong anywhere else.
 */
public class RuleSheetParserUtil {

    private RuleSheetParserUtil() {
        // strictly util
    }

    public static String getRuleName(final String ruleRow) {
        String testVal = ruleRow.toLowerCase();
        final int left = testVal.indexOf( DefaultRuleSheetListener.RULE_TABLE_TAG );
        return ruleRow.substring( left + DefaultRuleSheetListener.RULE_TABLE_TAG.length() ).trim();
    }

    private static void invalidRuleTableDef(final String ruleRow) {
        throw new IllegalArgumentException( "Invalid rule table header cell. Should be in the format of 'RuleTable YourRuleName'. " + "It was: \n [" + ruleRow + "] \n" );
    }

    /**
     * Create a list of Import model objects from cell contents.
     * @param importCells The cells containing text for all the classes to import.
     * @return A list of Import classes, which can be added to the ruleset.
     */
    public static List<Import> getImportList(final List<String> importCells) {
        final List<Import> importList = new ArrayList<Import>();
        if ( importCells == null ) return importList;

        for( String importCell: importCells ){
            final StringTokenizer tokens = new StringTokenizer( importCell, "," );
            while ( tokens.hasMoreTokens() ) {
                final Import imp = new Import();
                imp.setClassName( tokens.nextToken().trim() );
                importList.add( imp );
            }
        }
        return importList;
    }

    /**
     * Create a list of Global model objects from cell contents.
     * @param variableCella The cells containing text for all the global variables to set.
     * @return A list of Variable classes, which can be added to the ruleset.
     */
    public static List<Global> getVariableList( final List<String> variableCells ){
        final List<Global> variableList = new ArrayList<Global>();
        if ( variableCells == null ) return variableList;

        for( String variableCell: variableCells ){
            final StringTokenizer tokens = new StringTokenizer( variableCell, "," );
            while ( tokens.hasMoreTokens() ) {
                final String token = tokens.nextToken();
                final Global vars = new Global();
                final StringTokenizer paramTokens = new StringTokenizer( token, " " );
                vars.setClassName( paramTokens.nextToken() );
                if ( !paramTokens.hasMoreTokens() ) {
                    throw new DecisionTableParseException( "The format for global variables is incorrect. " + "It should be: [Class name, Class otherName]. But it was: [" + variableCell + "]" );
                }
                vars.setIdentifier( paramTokens.nextToken() );
                variableList.add( vars );
            }
        }
        return variableList;
    }

    /**
     * @return true is the String could possibly mean true. False otherwise !
     */
    public static boolean isStringMeaningTrue(String property) {
        if ( property == null ) {
            return false;
        } else {
            property = property.trim();
            if ( property.equalsIgnoreCase( "true" ) ) {
                return true;
            } else if ( property.startsWith( "Y" ) ) {
                return true;
            } else if ( property.startsWith( "y" ) ) {
                return true;
            } else if ( property.equalsIgnoreCase( "on" ) ) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Convert spreadsheet row, column numbers to a cell name.
     * @param row  row number
     * @param col  the column number. Start with zero.
     * @return The spreadsheet name for this cell, "A" to "ZZZ".
     */
    public static String rc2name( int row, int col ){
        StringBuilder sb = new StringBuilder();
        int b = 26;
        int p = 1;
        if( col >= b  ){
            col -= b;
            p *= b;
        }
        if( col >= b*b ){
            col -= b*b;
            p *= b;
        }
        while( p > 0 ){
            sb.append( (char)(col/p + (int)'A') );
            col %= p;
            p /= b;
        }
        sb.append( row + 1 );
        return sb.toString();
    }
}
