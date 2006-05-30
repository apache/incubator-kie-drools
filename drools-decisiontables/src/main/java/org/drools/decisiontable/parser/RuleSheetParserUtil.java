package org.drools.decisiontable.parser;

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

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.decisiontable.model.Global;
import org.drools.decisiontable.model.Import;

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
        final int left = ruleRow.indexOf( RuleSheetListener.RULE_TABLE_TAG );

        if ( ruleRow.indexOf( '(' ) > -1 || ruleRow.indexOf( ')' ) > -1 ) {
            invalidRuleTableDef( ruleRow );
        }
        return ruleRow.substring( left + RuleSheetListener.RULE_TABLE_TAG.length() ).trim();
    }

    private static void invalidRuleTableDef(final String ruleRow) {
        throw new IllegalArgumentException( "Invalid rule table header cell. Should be in the format of 'RuleTable YourRuleName'. " + "It was: \n [" + ruleRow + "] \n" );
    }

    /**
     * 
     * @param importCell
     *            The cell text for all the classes to import.
     * @return A list of Import classes, which can be added to the ruleset.
     */
    public static List getImportList(final String importCell) {
        final List importList = new LinkedList();
        if ( importCell == null ) {
            return importList;
        }
        final StringTokenizer tokens = new StringTokenizer( importCell,
                                                      "," );
        while ( tokens.hasMoreTokens() ) {
            final Import imp = new Import();
            imp.setClassName( tokens.nextToken().trim() );
            importList.add( imp );
        }
        return importList;
    }

    /**
     * 08 - 18 - 2005
     * Ricardo Rojas
     * @param variableCell
     *            The cell text for all the application data variables to set.
     * @return A list of Variable classes, which can be added to the ruleset.
     */
    public static List getVariableList(final String variableCell) {
        final List variableList = new LinkedList();
        if ( variableCell == null ) {
            return variableList;
        }
        final StringTokenizer tokens = new StringTokenizer( variableCell,
                                                      "," );
        while ( tokens.hasMoreTokens() ) {
            final String token = tokens.nextToken();
            final Global vars = new Global();
            final StringTokenizer paramTokens = new StringTokenizer( token,
                                                               " " );
            vars.setClassName( paramTokens.nextToken() );
            if ( !paramTokens.hasMoreTokens() ) {
                throw new DecisionTableParseException( "The format for global variables is incorrect. " + "It should be: [Class name, Class otherName]. But it was: [" + variableCell + "]" );
            }
            vars.setIdentifier( paramTokens.nextToken() );
            variableList.add( vars );
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

}