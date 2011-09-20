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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.drools.template.parser.DecisionTableParseException;

/**
 * Simple holder class identifying a condition, action or attribute column, also
 * including the rule name and a comment (called "description").
 * Its objects are stored in a map in the main listener class, to track what type of values
 * you can expect to see in the rows directly below the column header, identified
 * by an ActionType.Code.
 */
public class ActionType {

    public enum Code {
        CONDITION(       "CONDITION",        "C" ),
        ACTION(          "ACTION",           "A" ),
        NAME(            "NAME",             "N", 1 ),
        DESCRIPTION(     "DESCRIPTION",      "I" ),
        SALIENCE(        "PRIORITY",         "P", 1 ),
        DURATION(        "DURATION",         "D", 1 ),
        NOLOOP(          "NO-LOOP",          "U", 1 ),
        LOCKONACTIVE(    "LOCK-ON-ACTIVE",   "L", 1 ),
        AUTOFOCUS(       "AUTO-FOCUS",       "F", 1 ),
        ACTIVATIONGROUP( "ACTIVATION-GROUP", "X", 1 ),
        AGENDAGROUP(     "AGENDA-GROUP",     "G", 1 ),
        RULEFLOWGROUP(   "RULEFLOW-GROUP",   "R", 1 ),
        METADATA(        "METADATA",         "@" );
                
        private String colHeader;
        private String colShort;
        private int    maxCount;
        
        /**
         * Constructor.
         * @param colHeader the column header
         * @param colShort  a single letter, recognized as initial
         * @param maxCount  maximum number of permitted columns
         */
        Code( String colHeader, String colShort, int maxCount ){
            this.colHeader = colHeader;
            this.colShort = colShort;
            this.maxCount = maxCount;
        }
        
        
        Code( String colHeader, String colShort ){
            this( colHeader, colShort, Integer.MAX_VALUE );
        }
        
        public String getColHeader(){
            return colHeader;
        }
        public String getColShort(){
            return colShort;
        }
        public int getMaxCount() {
            return maxCount;
        }
    }

    public static final EnumSet<Code> ATTRIBUTE_CODE_SET = EnumSet.range( Code.SALIENCE, Code.RULEFLOWGROUP );

    private static final Map<String,Code> tag2code = new HashMap<String,Code>();
    static {
        for( Code code: EnumSet.allOf( Code.class ) ){
            tag2code.put( code.colHeader, code );
            tag2code.put( code.colShort, code );
        }
    }

    private Code code;
    private SourceBuilder sourceBuilder  = null;

    /**
     * Constructor.
     * @param actionTypeCode code identifying the column
     */
    ActionType( Code actionTypeCode) {
        this.code = actionTypeCode;
    }

    public static EnumSet<Code> getAttributeCodeSet() {
        return ATTRIBUTE_CODE_SET;
    }

    public static Map<String, Code> getTag2code() {
        return tag2code;
    }

    /**
     * Retrieves the code.
     * @return an enum Code value
     */
    public Code getCode(){
        return this.code;
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

    /**
     * Create a new action type that matches this cell, and add it to the map,
     * keyed on that column.
     */
    public static void addNewActionType(final Map<Integer, ActionType> actionTypeMap,
                                        final String value,
                                        final int column, final int row) {
        final String ucValue = value.toUpperCase();

        Code code = tag2code.get( ucValue );
        if( code == null ) code = tag2code.get( ucValue.substring( 0, 1 ) );
        if( code != null ){

            int count = 0;
            for( ActionType at: actionTypeMap.values() ){
                if( at.getCode() == code ) count++;
            }
            if( count >= code.getMaxCount() ){
                throw new DecisionTableParseException( "Maximum number of " +
                        code.getColHeader() + "/" + code.getColShort() + " columns is " +
                        code.getMaxCount() + ", in cell " + RuleSheetParserUtil.rc2name(row, column) );
            }
            actionTypeMap.put( new Integer( column ), new ActionType( code ) );
        } else {
            throw new DecisionTableParseException(
                    "Invalid column header: " + value + ", should be CONDITION, ACTION or attribute, " +
                    "in cell " + RuleSheetParserUtil.rc2name(row, column) );
        }
    }

    /**
     * This is where a code snippet template is added.
     */
    public void addTemplate(int row, int column, String content) {
        if( this.sourceBuilder == null ){
            throw new DecisionTableParseException(
                    "Unexpected content \"" + content + "\" in cell " +
                    RuleSheetParserUtil.rc2name(row, column) + ", leave this cell blank" );
        }
        this.sourceBuilder.addTemplate( row, column, content );
    }

    /**
     * Values are added to populate the template.
     * The source builder contained needs to be "cleared" when the resultant snippet is extracted.
     */
    public void addCellValue(int row, int column, String content, boolean _escapeQuotesFlag) {
        if (_escapeQuotesFlag){
            //Michael Neale:
            // For single standard quotes we escape them - eg they may mean "inches" 
            // as in "I want a Stonehenge replica 19" tall"
            int idx = content.indexOf("\"");
            if (idx > 0 && content.indexOf("\"", idx) > -1) {
                content = content.replace("\"", "\\\"");
            }
        }
        this.sourceBuilder.addCellValue( row, column, content );
    }

}
