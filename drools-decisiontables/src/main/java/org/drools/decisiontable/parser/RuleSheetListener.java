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





import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.decisiontable.model.Condition;
import org.drools.decisiontable.model.Duration;
import org.drools.decisiontable.model.Consequence;
import org.drools.decisiontable.model.Import;
import org.drools.decisiontable.model.Rule;
import org.drools.decisiontable.model.Package;
import org.drools.decisiontable.model.Global;
import org.drools.decisiontable.parser.xls.PropertiesSheetListener;

/**
 * @author <a href="mailto:shaun.addison@gmail.com"> Shaun Addison </a><a
 *         href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * Define a ruleset spreadsheet which contains one or more decision tables.
 * 
 * Stay calm, deep breaths... this is a little bit scary, its where it all
 * happens.
 * 
 * A table is identifed by a cell beginning with the text "RuleTable". The first
 * row after the table identifier defines the column type: either a condition
 * ("C") or consequence ("A" for action), and so on.
 * 
 * The second row identifies the java code block associated with the condition
 * or consequence. This code block will include a parameter marker for the
 * attribute defined by that column.
 * 
 * The third row is a label for the attribute associated with that column.
 * 
 * All subsequent rows identify rules with the set.
 */
public class RuleSheetListener
    implements
    SheetListener {

    //keywords
    public static final String      FUNCTIONS_TAG          = "Functions";
    public static final String      IMPORT_TAG             = "Import";
    public static final String      SEQUENTIAL_FLAG        = "Sequential";
    public static final String      VARIABLES_TAG          = "Variables";
    public static final String      RULE_TABLE_TAG         = "RuleTable";
    public static final String      RULESET_TAG            = "RuleSet";
    private static final int        ACTION_ROW             = 1;
    private static final int        CODE_ROW               = 2;
    private static final int        LABEL_ROW              = 3;

    //state machine variables for this parser
    private boolean                 _isInRuleTable         = false;
    private int                     _ruleRow;
    private int                     _ruleStartColumn;
    private int                     _ruleStartRow;
    private Rule                    _currentRule;
    private String                  _currentRulePrefix;
    private boolean                 _currentSequentialFlag = false;                        // indicates that we are in sequential mode

    //accumulated output
    private Map                     _actions;
    private HashMap                 _cellComments          = new HashMap();
    private List                    _ruleList              = new LinkedList();

    private PropertiesSheetListener _propertiesListner     = new PropertiesSheetListener();

    /**
     * Return the rule sheet properties
     */
    public Properties getProperties() {
        return _propertiesListner.getProperties();
    }

    /**
     * Build the final ruleset as parsed.
     */
    public Package getRuleSet() {
        if ( _ruleList.isEmpty() ) {
            throw new DecisionTableParseException( "No RuleTable's were found in spreadsheet." );
        }
        Package ruleset = buildRuleSet();
        return ruleset;
    }

    private Package buildRuleSet() {
        String rulesetName = getProperties().getProperty( RULESET_TAG, "rule_table" );
        Package ruleset = new Package( rulesetName );
        for ( Iterator it = _ruleList.iterator(); it.hasNext(); ) {
            ruleset.addRule( (Rule) it.next() );
        }
        List importList = RuleSheetParserUtil.getImportList( getProperties().getProperty( IMPORT_TAG ) );
        for ( Iterator it = importList.iterator(); it.hasNext(); ) {
            ruleset.addImport( (Import) it.next() );
        }
        List variableList = RuleSheetParserUtil.getVariableList( getProperties().getProperty( VARIABLES_TAG ) ); // Set the list of variables to
        // be added to the
        // application-data tags
        for ( Iterator it = variableList.iterator(); it.hasNext(); ) {
            ruleset.addVariable( (Global) it.next() );
        }

        String functions = getProperties().getProperty( FUNCTIONS_TAG );
        ruleset.addFunctions( functions );
        return ruleset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see my.hssf.util.SheetListener#startSheet(java.lang.String)
     */
    public void startSheet(String name) {
        // nothing to see here... move along..
    }

    /*
     * (non-Javadoc)
     * 
     * @see my.hssf.util.SheetListener#finishSheet()
     */
    public void finishSheet() {
        _propertiesListner.finishSheet();
        finishRuleTable();
    }

    /*
     * (non-Javadoc)
     * 
     * @see my.hssf.util.SheetListener#newRow()
     */
    public void newRow(int rowNumber,
                       int columns) {
        // nothing to see here... these aren't the droids your looking for..
        // move along...
    }

    /*
     * (non-Javadoc)
     * 
     * @see my.hssf.util.SheetListener#newCell(int, int, java.lang.String)
     */
    public void newCell(int row,
                        int column,
                        String value) {
        if ( isCellValueEmpty( value ) ) {
            return;
        }
        if ( _isInRuleTable ) {
            processRuleCell( row,
                             column,
                             value );
        } else {
            processNonRuleCell( row,
                                column,
                                value );
        }
    }

    /**
     * This gets called each time a "new" rule table is found.
     */
    private void initRuleTable(int row,
                               int column,
                               String value) {

        _isInRuleTable = true;
        _actions = new HashMap();
        _ruleStartColumn = column;
        _ruleStartRow = row;
        _ruleRow = row + LABEL_ROW + 1;

        // setup stuff for the rules to come.. (the order of these steps are
        // important !)
        _currentRulePrefix = RuleSheetParserUtil.getRuleName( value );
        _currentSequentialFlag = getSequentialFlag();

        _currentRule = createNewRuleForRow( _ruleRow );

        _ruleList.add( _currentRule );

    }

    private boolean getSequentialFlag() {
        String seqFlag = getProperties().getProperty( SEQUENTIAL_FLAG );
        return RuleSheetParserUtil.isStringMeaningTrue( seqFlag );
    }

    private void finishRuleTable() {
        if ( _isInRuleTable ) {
            _currentSequentialFlag = false;
            _isInRuleTable = false;
        }
    }

    private void processNonRuleCell(int row,
                                    int column,
                                    String value) {
        if ( value.startsWith( RULE_TABLE_TAG ) ) {
            initRuleTable( row,
                           column,
                           value );
        } else {
            _propertiesListner.newCell( row,
                                        column,
                                        value );
        }
    }

    private void processRuleCell(int row,
                                 int column,
                                 String value) {
        if ( value.startsWith( RULE_TABLE_TAG ) ) {
            finishRuleTable();
            initRuleTable( row,
                           column,
                           value );
            return;
        }

        // Ignore any comments cells preceeding the first rule table column
        if ( column < _ruleStartColumn ) {
            return;
        }

        // Ignore any further cells from the rule def row
        if ( row == _ruleStartRow ) {
            return;
        }

        switch ( row - _ruleStartRow ) {
            case ACTION_ROW :
                ActionType.addNewActionType( _actions,
                                             value,
                                             column,
                                             row );
                break;
            case CODE_ROW :
                codeRow( row,
                         column,
                         value );
                break;
            case LABEL_ROW :
                labelRow( row,
                          column,
                          value );
                break;
            default :
                nextRule( row,
                          column,
                          value );
                break;
        }
    }

    private void codeRow(int row,
                         int column,
                         String value) {
        ActionType actionType = getActionForColumn( row,
                                                    column );

        if ( value.trim().equals( "" ) && (actionType.type == ActionType.ACTION || actionType.type == ActionType.CONDITION) ) {
            throw new DecisionTableParseException( "Code description - row:" + (row + 1) + " cell number:" + (column + 1) + " - does not contain any code specification. It should !" );
        }

        actionType.value = value;
    }

    private void labelRow(int row,
                          int column,
                          String value) {
        ActionType actionType = getActionForColumn( row,
                                                    column );

        if ( !value.trim().equals( "" ) && (actionType.type == ActionType.ACTION || actionType.type == ActionType.CONDITION) ) {
            _cellComments.put( new Integer( column ),
                               value );
        } else {
            _cellComments.put( new Integer( column ),
                               "From column: " + Rule.convertColNumToColName( column ) );
        }
    }

    private ActionType getActionForColumn(int row,
                                          int column) {
        ActionType actionType = (ActionType) _actions.get( new Integer( column ) );

        if ( actionType == null ) {
            throw new DecisionTableParseException( "Code description - row number:" + (row + 1) + " cell number:" + (column + 1) + " - does not have an 'ACTION' or 'CONDITION' column header." );
        }

        return actionType;
    }

    private void nextRule(int row,
                          int column,
                          String value) {
        ActionType actionType = getActionForColumn( row,
                                                    column );

        if ( row - _ruleRow > 1 ) {
            // Encountered a row gap from the last rule.
            // This is not part of the ruleset.
            finishRuleTable();
            processNonRuleCell( row,
                                column,
                                value );
            return;
        }

        if ( row > _ruleRow ) {
            // In a new row/rule
            _currentRule = createNewRuleForRow( row );

            _ruleList.add( _currentRule );
            _ruleRow++;
        }

        //if the rule set is not sequential and the actionType type is PRIORITY then set the current Rule's salience paramenter with the value got from the cell
        if ( actionType.type == ActionType.PRIORITY && !_currentSequentialFlag ) {
            _currentRule.setSalience( new Integer( value ) );
        } else if ( actionType.type == ActionType.NAME ) // if the actionType
        // type is PRIORITY then
        // set the current
        // Rule's name
        // paramenter with the
        // value got from the
        // cell
        {
            _currentRule.setName( value );
        } else if ( actionType.type == ActionType.DESCRIPTION ) // if the
        // actionType
        // type is
        // DESCRIPTION
        // then set the
        // current
        // Rule's
        // description
        // paramenter
        // with the
        // value got
        // from the cell
        {
            _currentRule.setDescription( value );
        } else if ( actionType.type == ActionType.XORGROUP ) // if the actionType
        // type is NOLOOP
        // then set the
        // current Rule's
        // no-loop
        // paramenter with
        // the value got
        // from the cell
        {
            _currentRule.setXorGroup( value );
        } else if ( actionType.type == ActionType.NOLOOP ) // if the actionType
        // type is NOLOOP
        // then set the
        // current Rule's
        // no-loop
        // paramenter with
        // the value got
        // from the cell
        {
            _currentRule.setNoLoop( value );
        } else if ( actionType.type == ActionType.DURATION ) // if the actionType
        // type is DURATION
        // then creates a
        // new duration tag
        // with the value
        // got from the cell
        {
            createDuration( column,
                            value,
                            actionType );
        } else if ( actionType.type == ActionType.CONDITION ) {
            createCondition( column,
                             value,
                             actionType );
        } else if ( actionType.type == ActionType.ACTION ) {
            createConsequence( column,
                               value,
                               actionType );
        }

    }

    private Rule createNewRuleForRow(int row) {
        
        Integer salience = null;
        if ( _currentSequentialFlag ) {
            salience = new Integer( Rule.calcSalience( row ) );
        }
        int spreadsheetRow = row + 1;
        String name = _currentRulePrefix + "_" + spreadsheetRow;
        Rule rule = new Rule( name,
                              salience,
                              spreadsheetRow );
        rule.setComment( "From row number: " + (spreadsheetRow) );

        return rule;
    }

    private void createCondition(int column,
                                 String value,
                                 ActionType actionType) {

        Condition cond = new Condition();
        cond.setSnippet( actionType.getSnippet( value ) );
        cond.setComment( cellComment( column ) );
        _currentRule.addCondition( cond );
    }

    // 08 - 16 - 2005 RIK: This function creates a new DURATION TAG if apply.
    // The value in the cell must be made with the first character of the
    // parameter and the value next to it, separated by ":" character
    // Examples: w1:d3:h4 mean weeks="1" days="3" hours="4", m=1:s=45 means
    // minutes="1" seconds="45"

    private void createDuration(int column,
                                String value,
                                ActionType actionType) {

        Duration dur = new Duration();
        dur.setSnippet( value );
        dur.setComment( cellComment( column ) );
        _currentRule.setDuration( dur );
    }

    private void createConsequence(int column,
                                   String value,
                                   ActionType actionType) {

        Consequence cons = new Consequence();
        cons.setSnippet( actionType.getSnippet( value ) );
        cons.setComment( cellComment( column ) );
        _currentRule.addConsequence( cons );
    }

    private boolean isCellValueEmpty(String value) {
        return value == null || "".equals( value.trim() );
    }

    private String cellComment(int column) {
        return "From column: " + Rule.convertColNumToColName( column );
    }

}