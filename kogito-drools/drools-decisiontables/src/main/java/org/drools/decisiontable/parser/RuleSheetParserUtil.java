package org.drools.decisiontable.parser;


/*
 * Copyright 2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.decisiontable.model.Import;
import org.drools.decisiontable.model.Global;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * Parking lot for utility methods that don't belong anywhere else.
 */
public class RuleSheetParserUtil
{

    private RuleSheetParserUtil()
    {
        // strictly util
    }

    public static String getRuleName(String ruleRow)
    {
        int left = ruleRow.indexOf( RuleSheetListener.RULE_TABLE_TAG );
        
        if ( ruleRow.indexOf('(') > -1 || ruleRow.indexOf(')') > -1 )
        {
            invalidRuleTableDef( ruleRow );
        }
        return ruleRow.substring( left + RuleSheetListener.RULE_TABLE_TAG.length( )).trim( );
    }

    private static void invalidRuleTableDef(String ruleRow)
    {
        throw new IllegalArgumentException( "Invalid rule table header cell. Should be in the format of 'RuleTable YourRuleName'. " + 
        		"It was: \n [" + ruleRow + "] \n" );
    }


    /**
     * 
     * @param importCell
     *            The cell text for all the classes to import.
     * @return A list of Import classes, which can be added to the ruleset.
     */
    public static List getImportList(String importCell)
    {
        List importList = new LinkedList( );
        if ( importCell == null )
        {
            return importList;
        }
        StringTokenizer tokens = new StringTokenizer( importCell,
                                                      "," );
        while ( tokens.hasMoreTokens( ) )
        {
            Import imp = new Import( );
            imp.setClassName( tokens.nextToken( ).trim( ) );
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
    public static List getVariableList(String variableCell)
    {
        List variableList = new LinkedList( );
        if ( variableCell == null )
        {
            return variableList;
        }
        StringTokenizer tokens = new StringTokenizer( variableCell, "," );
		while ( tokens.hasMoreTokens( ) )
		{
			String token = tokens.nextToken( );
			Global vars = new Global( );
			StringTokenizer paramTokens = new StringTokenizer( token, " " );
			vars.setClassName( paramTokens.nextToken( ) );
            if (!paramTokens.hasMoreTokens()) {
                throw new DecisionTableParseException("The format for global variables is incorrect. " +
                        "It should be: [Class name, Class otherName]. But it was: [" + variableCell + "]");
            }
			vars.setIdentifier( paramTokens.nextToken( ) );
			variableList.add( vars );
		}
        return variableList;
    }

    /**
     * @return true is the String could possibly mean true. False otherwise !
     */
    public static boolean isStringMeaningTrue(String property)
    {
        if ( property == null )
        {
            return false;
        }
        else
        {
            property = property.trim( );
            if ( property.equalsIgnoreCase( "true" ) )
            {
                return true;
            }
            else if ( property.startsWith( "Y" ) )
            {
                return true;
            }
            else if ( property.startsWith( "y" ) )
            {
                return true;
            }
            else if ( property.equalsIgnoreCase( "on" ) )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

}

