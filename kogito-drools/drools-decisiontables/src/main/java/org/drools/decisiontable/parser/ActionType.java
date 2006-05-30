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

import java.util.Map;

import org.drools.decisiontable.model.SnippetBuilder;

/**
 * Simple holder class identifying a condition or action column etc.
 * This is stored in a map in the main listener class, to track what type of values
 * you can expect to see in the rows directly below.
 * 
 * There are five types of columns relevant to a rule table.
 * @author <a href="mailto:Michael.Neale@gmail.com"> Michael Neale</a>
 */
public class ActionType {

    public static final int CONDITION       = 0;

    public static final int ACTION          = 1;

    // 08 - 16 - 2005 RIK: Define 3 new ActionType types
    // PRIORITY is used to set the salience parameter of a rule tag
    public static final int PRIORITY        = 2;

    // DURATION is used to set a duration tag inside a rule tag
    public static final int DURATION        = 3;

    // NAME is used to set the name parameter of a rule tag
    public static final int NAME            = 4;

    // 10 - 05 - 2005 RIK: Add 2 new AtcionType types
    // DESCRIPTION is used to set the description parameter of a rule tag
    public static final int DESCRIPTION     = 5;

    //  NOLOOP is used to set the no-loop parameter of a rule tag
    public static final int NOLOOP          = 6;

    //  XOR-GROUP is used to set the activation-group parameter of a rule tag
    public static final int ACTIVATIONGROUP = 7;

    int                     type;

    String                  value;

    ActionType(final int actionType,
               final String cellValue) {
        this.type = actionType;
        this.value = cellValue;
    }

    String getSnippet(final String cellValue) {
        final SnippetBuilder builder = new SnippetBuilder( this.value );
        return builder.build( cellValue );
    }

    /**
     * Create a new action type that matches this cell, and add it to the map,
     * keyed on that column.
     */
    public static void addNewActionType(final Map actionTypeMap,
                                        final String value,
                                        final int column,
                                        final int row) {
        if ( value.toUpperCase().startsWith( "C" ) ) {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.CONDITION,
                                               null ) );
        } else if ( value.toUpperCase().startsWith( "A" ) ) {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.ACTION,
                                               null ) );
        } else if ( value.toUpperCase().startsWith( "P" ) ) // if the title cell
        // value starts with
        // "P" then put a
        // ActionType.PRIORITY
        // to the _actions
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.PRIORITY,
                                               null ) );
        } else if ( value.toUpperCase().startsWith( "D" ) ) // if the title cell
        // value starts with
        // "D" then put a
        // ActionType.DURATION
        // to the _actions
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.DURATION,
                                               null ) );
        } else if ( value.toUpperCase().startsWith( "N" ) ) // if the title cell
        // value starts with
        // "N" then put a
        // ActionType.NAME
        // to the _actions
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.NAME,
                                               null ) );
        } else if ( value.toUpperCase().startsWith( "I" ) ) // if the title cell
        // value starts with
        // "I" then put a
        // ActionType.DESCRIPTION
        // to the _actions	
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.DESCRIPTION,
                                               null ) );
        } else if ( value.toUpperCase().startsWith( "U" ) ) // if the title cell
        // value starts with
        // "U" then put a
        // ActionType.NOLOOP
        // to the _actions	
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.NOLOOP,
                                               null ) );
        } else if ( value.toUpperCase().startsWith( "X" ) ) // if the title cell
        // value starts with
        // "X" then put a
        // ActionType.XORGROUP
        // to the _actions	
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.ACTIVATIONGROUP,
                                               null ) );
        } else {
            throw new DecisionTableParseException( "Invalid column header (ACTION type), " + "should be CONDITION or ACTION (etc..) row number:" + (row + 1) + " cell number:" + (column + 1) + " - does not contain a leading C or A identifer." );
        }
    }

}