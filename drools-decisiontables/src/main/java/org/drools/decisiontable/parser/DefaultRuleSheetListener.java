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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.decisiontable.parser.xls.PropertiesSheetListener;
import org.drools.template.model.Condition;
import org.drools.template.model.Consequence;
import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.model.Package;
import org.drools.template.model.Rule;
import org.drools.template.parser.DecisionTableParseException;

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
 * The second row contains ObjectType declarations (optionally, or can be left blank).
 * If cells are merged, then all snippets below the merged bit will become part of
 * the same column as seperate constraints.
 *
 * The third row identifies the java code block associated with the condition
 * or consequence. This code block will include a parameter marker for the
 * attribute defined by that column.
 *
 * The third row is a label for the attribute associated with that column.
 *
 * All subsequent rows identify rules with the set.
 */
public class DefaultRuleSheetListener
    implements
    RuleSheetListener {

    //keywords
    public static final String            QUERIES_TAG          = "Queries";
    public static final String            FUNCTIONS_TAG          = "Functions";
    public static final String            IMPORT_TAG             = "Import";
    public static final String            SEQUENTIAL_FLAG        = "Sequential";
    public static final String            VARIABLES_TAG          = "Variables";
    public static final String            RULE_TABLE_TAG         = "RuleTable";
    public static final String            RULESET_TAG            = "RuleSet";
    private static final int              ACTION_ROW             = 1;
    private static final int              OBJECT_TYPE_ROW        = 2;
    private static final int              CODE_ROW               = 3;
    private static final int              LABEL_ROW              = 4;

    //state machine variables for this parser
    private boolean                       _isInRuleTable         = false;
    private int                           _ruleRow;
    private int                           _ruleStartColumn;
    private int                           _ruleStartRow;
    private Rule                          _currentRule;
    private String                        _currentRulePrefix;
    private boolean                       _currentSequentialFlag = false;                        // indicates that we are in sequential mode

    //accumulated output
    private Map<Integer, ActionType>       _actions;
    private final HashMap<Integer, String> _cellComments          = new HashMap<Integer, String>();
    private final List<Rule>               _ruleList              = new LinkedList<Rule>();

    //need to keep an ordered list of this to make conditions appear in the right order
    private List<SourceBuilder>            sourceBuilders         = new ArrayList<SourceBuilder>();

    private final PropertiesSheetListener _propertiesListner     = new PropertiesSheetListener();

	private boolean showPackage;

    public DefaultRuleSheetListener() {
        this( true );
    }

    public DefaultRuleSheetListener(boolean showPackage) {
        this.showPackage = showPackage;
    }

    /* (non-Javadoc)
     * @see org.drools.decisiontable.parser.RuleSheetListener#getProperties()
     */
    public Properties getProperties() {
        return this._propertiesListner.getProperties();
    }

    /* (non-Javadoc)
     * @see org.drools.decisiontable.parser.RuleSheetListener#getRuleSet()
     */
    public Package getRuleSet() {
        if ( this._ruleList.isEmpty() ) {
            throw new DecisionTableParseException( "No RuleTable's were found in spreadsheet." );
        }
        final Package ruleset = buildRuleSet();
        return ruleset;
    }

    /**
     * Add a new rule to the current list of rules
     * @param rule
     */
    protected void addRule(final Rule newRule) {
        this._ruleList.add( newRule );
    }

    private Package buildRuleSet() {
        final String defaultPackageName = "rule_table";
        final String rulesetName = getProperties().getProperty( RULESET_TAG,
                                                                defaultPackageName );

        final Package ruleset = new Package( (showPackage) ? rulesetName : null );
        for ( Rule rule : this._ruleList ) {
            ruleset.addRule( rule );
        }
	        final List<Import> importList = RuleSheetParserUtil.getImportList( getProperties().getProperty( IMPORT_TAG ) );
	        for ( Import import1 : importList ) {
	            ruleset.addImport( import1 );
	        }
	        final List<Global> variableList = RuleSheetParserUtil.getVariableList( getProperties().getProperty( VARIABLES_TAG ) ); // Set the list of variables to
	        // be added to the
	        // application-data tags
	        for ( Global global : variableList ) {
	            ruleset.addVariable( global );
	        }

        final String functions = getProperties().getProperty( FUNCTIONS_TAG );
        ruleset.addFunctions( functions );

        final String queries = getProperties().getProperty( QUERIES_TAG );
        ruleset.addQueries(queries);

        return ruleset;
    }

    /*
     * (non-Javadoc)
     *
     * @see my.hssf.util.SheetListener#startSheet(java.lang.String)
     */
    public void startSheet(final String name) {
        // nothing to see here... move along..
    }

    /*
     * (non-Javadoc)
     *
     * @see my.hssf.util.SheetListener#finishSheet()
     */
    public void finishSheet() {
        this._propertiesListner.finishSheet();
        finishRuleTable();
        flushRule();
    }

    /*
     * (non-Javadoc)
     *
     * @see my.hssf.util.SheetListener#newRow()
     */
    public void newRow(final int rowNumber,
                       final int columns) {
        if ( _currentRule != null ) flushRule();
        // nothing to see here... these aren't the droids your looking for..
        // move along...
    }

    /**
     * This makes sure that the rules have all their components added.
     * As when there are merged/spanned cells, they may be left out.
     */
    private void flushRule() {
        for ( Iterator<SourceBuilder> iter = sourceBuilders.iterator(); iter.hasNext(); ) {
            SourceBuilder src = iter.next();
            if ( src.hasValues() ) {
                if ( src instanceof LhsBuilder ) {
                    Condition con = new Condition();
                    con.setSnippet( src.getResult() );
                    _currentRule.addCondition( con );
                } else if ( src instanceof RhsBuilder ) {
                    Consequence con = new Consequence();
                    con.setSnippet( src.getResult() );
                    _currentRule.addConsequence( con );
                }
                src.clearValues();
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see my.hssf.util.SheetListener#newCell(int, int, java.lang.String)
     */
    public void newCell(final int row,
                        final int column,
                        final String value,
                        int mergedColStart) {
        if ( isCellValueEmpty( value ) ) {
            return;
        }
        if ( _isInRuleTable && row == this._ruleStartRow ) {
            return;
        }
        if ( this._isInRuleTable ) {
            processRuleCell( row,
                             column,
                             value,
                             mergedColStart );
        } else {
            processNonRuleCell( row,
                                column,
                                value );
        }
    }

    /**
     * This gets called each time a "new" rule table is found.
     */
    private void initRuleTable(final int row,
                               final int column,
                               final String value) {
        preInitRuleTable( row,
                          column,
                          value );
        this._isInRuleTable = true;
        this._actions = new HashMap<Integer, ActionType>();
        this.sourceBuilders = new ArrayList<SourceBuilder>();
        this._ruleStartColumn = column;
        this._ruleStartRow = row;
        this._ruleRow = row + LABEL_ROW + 1;

        // setup stuff for the rules to come.. (the order of these steps are
        // important !)
        this._currentRulePrefix = RuleSheetParserUtil.getRuleName( value );
        this._currentSequentialFlag = getSequentialFlag();

        this._currentRule = createNewRuleForRow( this._ruleRow );

        this._ruleList.add( this._currentRule );
        postInitRuleTable( row,
                           column,
                           value );

    }

    /**
     * Called before rule table initialisation. Subclasses may
     * override this method to do additional processing.
     */
    protected void preInitRuleTable(int row,
                                    int column,
                                    String value) {
    }

    protected Rule getCurrentRule() {
        return _currentRule;
    }

    /**
     * Called after rule table initialisation. Subclasses may
     * override this method to do additional processing.
     */
    protected void postInitRuleTable(int row,
                                     int column,
                                     String value) {
    }

    private boolean getSequentialFlag() {
        final String seqFlag = getProperties().getProperty( SEQUENTIAL_FLAG );
        return RuleSheetParserUtil.isStringMeaningTrue( seqFlag );
    }

    private void finishRuleTable() {
        if ( this._isInRuleTable ) {
            this._currentSequentialFlag = false;
            this._isInRuleTable = false;

        }
    }

    private void processNonRuleCell(final int row,
                                    final int column,
                                    final String value) {
        if ( value.startsWith( RULE_TABLE_TAG ) ) {
            initRuleTable( row,
                           column,
                           value );
        } else {
            this._propertiesListner.newCell( row,
                                             column,
                                             value,
                                             RuleSheetListener.NON_MERGED );
        }
    }

    private void processRuleCell(final int row,
                                 final int column,
                                 final String value,
                                 final int mergedColStart) {
        if ( value.startsWith( RULE_TABLE_TAG ) ) {
            finishRuleTable();
            initRuleTable( row,
                           column,
                           value );
            return;
        }

        // Ignore any comments cells preceeding the first rule table column
        if ( column < this._ruleStartColumn ) {
            return;
        }

        // Ignore any further cells from the rule def row
        if ( row == this._ruleStartRow ) {
            return;
        }

        switch ( row - this._ruleStartRow ) {
            case ACTION_ROW :
                ActionType.addNewActionType( this._actions,
                                             value,
                                             column,
                                             row );
                break;
            case OBJECT_TYPE_ROW :
                objectTypeRow( row,
                               column,
                               value,
                               mergedColStart );
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
                nextDataCell( row,
                              column,
                              value );
                break;
        }
    }

    /**
     * This is for handling a row where an object declaration may appear,
     * this is the row immediately above the snippets.
     * It may be blank, but there has to be a row here.
     *
     * Merged cells have "special meaning" which is why this is so freaking hard.
     * A future refactor may be to move away from an "event" based listener.
     */
    private void objectTypeRow(final int row,
                               final int column,
                               final String value,
                               final int mergedColStart) {
        if ( value.indexOf( "$param" ) > -1 || value.indexOf( "$1" ) > -1 ) {
            throw new DecisionTableParseException( "It looks like you have snippets in the row that is " + "meant for column declarations." + " Please insert an additional row before the snippets." + " Row number: " + (row + 1) );
        }
        ActionType action = getActionForColumn( row,
                                                column );
        if ( mergedColStart == RuleSheetListener.NON_MERGED ) {
            if ( action.type == ActionType.CONDITION ) {
                SourceBuilder src = new LhsBuilder( value );
                action.setSourceBuilder( src );
                this.sourceBuilders.add( src );

            } else if ( action.type == ActionType.ACTION ) {
                SourceBuilder src = new RhsBuilder( value );
                action.setSourceBuilder( src );
                this.sourceBuilders.add( src );
            }
        } else {
            if ( column == mergedColStart ) {
                if ( action.type == ActionType.CONDITION ) {
                    action.setSourceBuilder( new LhsBuilder( value ) );
                    this.sourceBuilders.add( action.getSourceBuilder() );
                } else if ( action.type == ActionType.ACTION ) {
                    action.setSourceBuilder( new RhsBuilder( value ) );
                    this.sourceBuilders.add( action.getSourceBuilder() );
                }
            } else {
                ActionType startOfMergeAction = getActionForColumn( row,
                                                                    mergedColStart );
                action.setSourceBuilder( startOfMergeAction.getSourceBuilder() );
            }

        }
    }

    private void codeRow(final int row,
                         final int column,
                         final String value) {
        final ActionType actionType = getActionForColumn( row,
                                                          column );
        if ( actionType.getSourceBuilder() == null ) {
            if ( actionType.type == ActionType.CONDITION ) {
                actionType.setSourceBuilder( new LhsBuilder( null ) );
                this.sourceBuilders.add( actionType.getSourceBuilder() );
            } else if ( actionType.type == ActionType.ACTION ) {
                actionType.setSourceBuilder( new RhsBuilder( null ) );
                this.sourceBuilders.add( actionType.getSourceBuilder() );
            } else if ( actionType.type == ActionType.PRIORITY ) {
                actionType.setSourceBuilder( new LhsBuilder( null ) );
                this.sourceBuilders.add( actionType.getSourceBuilder() );
            }
        }
        if ( value.trim().equals( "" ) && (actionType.type == ActionType.ACTION || actionType.type == ActionType.CONDITION) ) {
            throw new DecisionTableParseException( "Code description - row:" + (row + 1) + " cell number:" + (column + 1) + " - does not contain any code specification. It should !" );
        }

        actionType.addTemplate( column,
                                value );
    }

    private void labelRow(final int row,
                          final int column,
                          final String value) {
        final ActionType actionType = getActionForColumn( row,
                                                          column );

        if ( !value.trim().equals( "" ) && (actionType.type == ActionType.ACTION || actionType.type == ActionType.CONDITION) ) {
            this._cellComments.put( new Integer( column ),
                                    value );
        } else {
            this._cellComments.put( new Integer( column ),
                                    "From column: " + Rule.convertColNumToColName( column ) );
        }
    }

    private ActionType getActionForColumn(final int row,
                                          final int column) {
        final ActionType actionType = this._actions.get( new Integer( column ) );

        if ( actionType == null ) {
            throw new DecisionTableParseException( "Code description - row number:" + (row + 1) + " cell number:" + (column + 1) + " - does not have an 'ACTION' or 'CONDITION' column header." );
        }

        return actionType;
    }

    private void nextDataCell(final int row,
                          final int column,
                          final String value) {
        final ActionType actionType = getActionForColumn( row,
                                                    column );

        if ( row - this._ruleRow > 1 ) {
            // Encountered a row gap from the last rule.
            // This is not part of the ruleset.
            finishRuleTable();
            processNonRuleCell( row,
                                column,
                                value );
            return;
        }

        if ( row > this._ruleRow ) {
            // In a new row/rule
            this._currentRule = createNewRuleForRow( row );

            this._ruleList.add( this._currentRule );
            this._ruleRow++;
        }

        //if the rule set is not sequential and the actionType type is PRIORITY then set the current Rule's salience paramenter with the value got from the cell
        if ( actionType.type == ActionType.PRIORITY && !this._currentSequentialFlag ) {
            this._currentRule.setSalience( new Integer( value ) );
        } else if ( actionType.type == ActionType.NAME ) // if the actionType
        // type is PRIORITY then
        // set the current
        // Rule's name
        // paramenter with the
        // value got from the
        // cell
        {
            this._currentRule.setName( value );
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
            this._currentRule.setDescription( value );
        } else if ( actionType.type == ActionType.ACTIVATIONGROUP ) // if the actionType
        // type is NOLOOP
        // then set the
        // current Rule's
        // no-loop
        // paramenter with
        // the value got
        // from the cell
        {
            this._currentRule.setActivationGroup( value );
        } else if ( actionType.type == ActionType.NOLOOP ) // if the actionType
        // type is NOLOOP
        // then set the
        // current Rule's
        // no-loop
        // paramenter with
        // the value got
        // from the cell
        {
            this._currentRule.setNoLoop( value );
        } else if ( actionType.type == ActionType.RULEFLOWGROUP ) {
            this._currentRule.setRuleFlowGroup( value );
        }
        else if ( actionType.type == ActionType.DURATION ) // if the actionType
        // type is DURATION
        // then creates a
        // new duration tag
        // with the value
        // got from the cell
        {
            createDuration( column,
                            value,
                            actionType );
        } else if ( actionType.type == ActionType.CONDITION || actionType.type == ActionType.ACTION ) {
            actionType.addCellValue( column, value );
        }

    }

    private Rule createNewRuleForRow(final int row) {

        Integer salience = null;
        if ( this._currentSequentialFlag ) {
            salience = new Integer( Rule.calcSalience( row ) );
        }
        final int spreadsheetRow = row + 1;
        final String name = this._currentRulePrefix + "_" + spreadsheetRow;
        final Rule rule = new Rule( name,
                                    salience,
                                    spreadsheetRow );
        rule.setComment( "From row number: " + (spreadsheetRow) );

        return rule;

    }

    // 08 - 16 - 2005 RIK: This function creates a new DURATION
    private void createDuration(final int column,
                                final String value,
                                final ActionType actionType) {

        this._currentRule.setDuration( value );
    }

    private boolean isCellValueEmpty(final String value) {
        return value == null || "".equals( value.trim() );
    }

}
