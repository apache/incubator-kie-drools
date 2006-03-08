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


import java.util.Map;

import org.drools.decisiontable.model.SnippetBuilder;

/**
 * Simple holder class identifying a condition or action column.
 * 
 * There are five types of columns relevant to a rule table.
 * @author <a href="mailto:Michael.Neale@gmail.com"> Michael Neale</a>
 */
public class ActionType
{

    public static final int CONDITION = 0;

    public static final int ACTION    = 1;

    // 08 - 16 - 2005 RIK: Define 3 new ActionType types
    // PRIORITY is used to set the salience parameter of a rule tag
    public static final int PRIORITY  = 2;

    // DURATION is used to set a duration tag inside a rule tag
    public static final int DURATION  = 3;

    // NAME is used to set the name parameter of a rule tag
    public static final int NAME      = 4;

    // 10 - 05 - 2005 RIK: Add 2 new AtcionType types
    // DESCRIPTION is used to set the description parameter of a rule tag
    public static final int DESCRIPTION      = 5;

    //  NOLOOP is used to set the no-loop parameter of a rule tag
    public static final int NOLOOP      = 6;

    //  XOR-GROUP is used to set the xor-group parameter of a rule tag
    public static final int XORGROUP      = 7;

    int                     type;

    String                  value;

    ActionType(int actionType,
               String cellValue)
    {
        type = actionType;
        value = cellValue;
    }

    String getSnippet(String cellValue)
    {
        SnippetBuilder builder = new SnippetBuilder( value );
        return builder.build( cellValue );
    }

    /**
     * Create a new action type that matches this cell, and add it to the map,
     * keyed on that column.
     */
    public static void addNewActionType(Map actionTypeMap,
                                 String value,
                                 int column,
                                 int row)
    {
        if ( value.toUpperCase( ).startsWith( "C" ) )
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.CONDITION,
                                               null ) );
        }
        else if ( value.toUpperCase( ).startsWith( "A" ) )
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.ACTION,
                                               null ) );
        }
        else if ( value.toUpperCase( ).startsWith( "P" ) ) // if the title cell
                                                            // value starts with
                                                            // "P" then put a
                                                            // ActionType.PRIORITY
                                                            // to the _actions
                                                            // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.PRIORITY,
                                               null ) );
        }
        else if ( value.toUpperCase( ).startsWith( "D" ) ) // if the title cell
                                                            // value starts with
                                                            // "D" then put a
                                                            // ActionType.DURATION
                                                            // to the _actions
                                                            // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.DURATION,
                                               null ) );
        }
        else if ( value.toUpperCase( ).startsWith( "N" ) ) // if the title cell
                                                            // value starts with
                                                            // "N" then put a
                                                            // ActionType.NAME
                                                            // to the _actions
                                                            // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.NAME,
                                               null ) );
        }
        else if ( value.toUpperCase( ).startsWith( "I" ) ) // if the title cell
												            // value starts with
												            // "I" then put a
												            // ActionType.DESCRIPTION
												            // to the _actions	
												            // list
		{
			actionTypeMap.put( new Integer( column ),
  							   new ActionType( ActionType.DESCRIPTION,
  									   		   null ) );
		}
        else if ( value.toUpperCase( ).startsWith( "U" ) ) // if the title cell
												            // value starts with
												            // "U" then put a
												            // ActionType.NOLOOP
												            // to the _actions	
												            // list
        {
			actionTypeMap.put( new Integer( column ),
							   new ActionType( ActionType.NOLOOP,
				                               null ) );
		}
        else if ( value.toUpperCase( ).startsWith( "X" ) ) // if the title cell
												            // value starts with
												            // "X" then put a
												            // ActionType.XORGROUP
												            // to the _actions	
												            // list
		{
			actionTypeMap.put( new Integer( column ),
			                   new ActionType( ActionType.XORGROUP,
					                           null ) );
		}
        else
        {
            throw new DecisionTableParseException( "Invalid column header (ACTION type), " +
                    "should be CONDITION or ACTION (etc..) row number:" + (row + 1) + " cell number:" + 
                    (column + 1) + " - does not contain a leading C or A identifer." );
        }
    }

}
