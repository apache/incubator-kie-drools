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

import java.util.Map;

import org.drools.template.parser.DecisionTableParseException;

/**
 * Simple holder class identifying a condition or action column etc.
 * This is stored in a map in the main listener class, to track what type of values
 * you can expect to see in the rows directly below.
 * 
 * There are five types of columns relevant to a rule table.
 * @author Michael Neale
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

    //  14 September 2006 SJW: Add new Agenda Group ActionType
    //  AGENDA-GROUP is used to set the agenda-group parameter of a rule tag
    public static final int AGENDAGROUP     = 8;

    public static final int RULEFLOWGROUP   = 9;

    int                     type;

    private SourceBuilder   sourceBuilder   = null;

    //    private String                  value;

    ActionType(final int actionType) {
        this.type = actionType;
    }

    /**
     * This is only set for LHS or RHS building. 
     */
    public void setSourceBuilder(SourceBuilder src) {
        this.sourceBuilder = src;
    }

    public SourceBuilder getSourceBuilder() {
        return this.sourceBuilder;
    }

    //    String getSnippet(final String cellValue) {
    //        final SnippetBuilder builder = new SnippetBuilder( this.value );
    //        return builder.build( cellValue );
    //    }

    /**
     * Create a new action type that matches this cell, and add it to the map,
     * keyed on that column.
     */
    public static void addNewActionType(final Map<Integer, ActionType> actionTypeMap,
                                        final String value,
                                        final int column,
                                        final int row) {

        if ( value.toUpperCase().startsWith( "U" ) || value.toUpperCase().equals( "NO-LOOP" ) ) // if the title cell
        // value starts with
        // "U" then put a
        // ActionType.NOLOOP
        // to the _actions  
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.NOLOOP ) );
        } else if ( value.toUpperCase().equals( "RULEFLOW-GROUP" ) || value.toUpperCase().startsWith( "R" ) ) {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.RULEFLOWGROUP ) );

        } else if ( value.toUpperCase().equals( "AGENDA-GROUP" ) ) // if the title cell
        // value equals "AGENDA-GROUP"
        // then put a
        // ActionType.AGENDAGROUP
        // to the _actions  
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.AGENDAGROUP ) );
        } else if ( value.toUpperCase().startsWith( "X" ) || value.toUpperCase().equals( "ACTIVATION-GROUP" ) ) // if the title cell
        // value starts with
        // "X" then put a
        // ActionType.XORGROUP
        // to the _actions  
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.ACTIVATIONGROUP ) );
        } else if ( value.toUpperCase().startsWith( "C" ) ) {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.CONDITION ) );
        } else if ( value.toUpperCase().startsWith( "A" ) ) {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.ACTION ) );
        } else if ( value.toUpperCase().startsWith( "P" ) ) // if the title cell
        // value starts with
        // "P" then put a
        // ActionType.PRIORITY
        // to the _actions
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.PRIORITY ) );
        } else if ( value.toUpperCase().startsWith( "D" ) ) // if the title cell
        // value starts with
        // "D" then put a
        // ActionType.DURATION
        // to the _actions
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.DURATION ) );
        } else if ( value.toUpperCase().startsWith( "N" ) ) // if the title cell
        // value starts with
        // "N" then put a
        // ActionType.NAME
        // to the _actions
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.NAME ) );
        } else if ( value.toUpperCase().startsWith( "I" ) ) // if the title cell
        // value starts with
        // "I" then put a
        // ActionType.DESCRIPTION
        // to the _actions	
        // list
        {
            actionTypeMap.put( new Integer( column ),
                               new ActionType( ActionType.DESCRIPTION ) );
        } else {
            throw new DecisionTableParseException( "Invalid column header (ACTION type), " + "should be CONDITION or ACTION (etc..) row number:" + (row + 1) + " cell number:" + (column + 1) + " - does not contain a leading C or A identifer." );
        }
    }

    /**
     * This is where a code snippet template is added.
     */
    public void addTemplate(int col,
                            String content) {
        this.sourceBuilder.addTemplate( col,
                                        content );
    }

    /**
     * Values are added to populate the template.
     * The source builder contained needs to be "cleared" when the resultant snippet is extracted.
     */
    public void addCellValue(int col,
                             String content) {
        //Michael Neale:
        //for single standard quotes we escape them - eg they may mean "inches" 
        // as in "I want a stone hence replica 19" tall"
        //for more info: http://www.imdb.com/title/tt0088258/
        int idx = content.indexOf("\"");
        if (idx > 0 && content.indexOf("\"", idx) > -1) {
            content = content.replace("\"", "\\\"");
        }
        this.sourceBuilder.addCellValue( col,
                                         content );
    }

}
