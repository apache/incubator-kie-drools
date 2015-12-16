/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.util.StringUtils;
import org.drools.decisiontable.parser.DefaultRuleSheetListener;
import org.drools.template.model.Condition;
import org.drools.template.model.Consequence;
import org.drools.template.model.Rule;
import org.drools.template.model.SnippetBuilder;

/**
 *         href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * Define a ruleset spreadsheet which contains a matrix style decision tables.
 * 
 * This is an example of a custom RuleSheetListener. It differs from the standard
 * decision table in the following ways:
 *    - AgendaGroup property so that all rules fall within the same agenda-group
 *    - Precondition property which specifies a condition that is always included 
 *      if a rule is being generated
 *    - Action property. Each cell within the decision table causes this action
 *      to be triggered
 *    - HorizontalCondition property. Each column header in the matrix
 *      applies this condition
 *    - VerticalCondition property. Each row header in the matrix applies this
 *      condition 
 * 
 * A table is identifed by a cell beginning with the text "RuleTable". 
 * The cells after RuleTable in the same row identify the Horizontal Conditions.
 * The cells after RuleTable in the same column identify the Vertical Conditions.
 * The cells with the matrix identify the actions.
 * Wherever an action cell exists for a Vertical/Horizontal condition intersection
 * the following rule is created:
 * rule "rule_row_col"
 *    agenda-group AgendaGroup
 *    when
 *       Precondition
 *       VerticalCondition
 *       HorizontalCondition
 *    then
 *       Action
 * end
 */
public class RuleMatrixSheetListener extends DefaultRuleSheetListener {

    //keywords
    public static final String AGENDAGROUP_TAG         = "AgendaGroup";
    public static final String PRECONDITION_TAG        = "Precondition";
    public static final String ACTION_TAG              = "Action";
    public static final String HORIZONTALCONDITION_TAG = "HorizontalCondition";
    public static final String VERTICALCONDITION_TAG   = "VerticalCondition";

    //state machine variables for this parser
    private int                ruleTableRow;
    private int                ruleTableColumn;
    private String             _currentAgendaGroup;
    private Condition          _currentPrecondition;
    private String             _action;
    private String             _horizontalCondition;
    private String             _verticalCondition;
    private List<Condition>    _horizontalConditions   = new ArrayList<Condition>();
    private Condition          _currentVerticalCondition;
    private boolean            isInRuleTable;
    private Rule               firstRule;

    public void newCell(final int row,
            final int column,
            final String value,
            final int mergedColStart) {
        // if we aren't in the rule table just use the default handling
        // (add a property)
        if ( ! isInRuleTable ) {
            super.newCell( row, column, value, mergedColStart );
            return;
        }
        // ignore empty cells
        if ( StringUtils.isEmpty( value ) ) {
            return;
        }

        //Horizontal header column
        //Create a new condition using HorizontalCondition as the template
        //and save it for later use
        if ( row == (ruleTableRow) && column > ruleTableColumn ) {
            _horizontalConditions.add( createCondition( value, _horizontalCondition ) );
        }
        //Vertical header column
        //Create a new condition using VerticalCondition as the template
        //and set it as the current condition
        else if ( row > (ruleTableRow) && column == ruleTableColumn ) {
            _currentVerticalCondition = createCondition( value, _verticalCondition );
        }
        //Intersection column
        //Create a new Consequence
        else if ( row > (ruleTableRow) && column > ruleTableColumn ) {
            createRule( row, column, value );
        }
    }

    private void createRule(final int row,
            final int column,
            final String value) {
        final Consequence consequence = createConsequence( value );

        Rule rule = firstRule;
        if ( rule == null ) {
            rule = new Rule( "rule_" + row + "_" + column,
                    null,
                    row );
            addRule( rule );
        } else {
            firstRule = null;
            rule.setName( "rule_" + row + "_" + column );
        }
        rule.setAgendaGroup( this._currentAgendaGroup );
        rule.addCondition( this._currentPrecondition );
        rule.addCondition( _currentVerticalCondition );
        rule.addCondition( (Condition) _horizontalConditions.get( column - (ruleTableColumn + 1) ) );
        rule.addConsequence( consequence );
    }

    private Consequence createConsequence(final String value) {
        final SnippetBuilder snip = new SnippetBuilder( _action );
        final String result = snip.build( value );
        final Consequence consequence = new Consequence();
        consequence.setSnippet( result );
        return consequence;
    }

    private Condition createCondition(final String value,
            final String conditionTemplate) {
        SnippetBuilder snip = new SnippetBuilder( conditionTemplate );
        String result = snip.build( value );
        Condition condition = new Condition();
        condition.setSnippet( result );
        return condition;
    }

    public void newRow(int rowNumber,
            int columns) {
        // nothing to do here
    }

    public void finishSheet() {
        // nothing to do here
    }

    protected void postInitRuleTable(int row,
            int column,
            String value) {
        this.firstRule = getCurrentRule();
    }

    /**
     * This gets called each time a "new" rule table is found.
     */
    protected void preInitRuleTable(final int row,
            final int column,
            final String value) {
        this.ruleTableColumn = column;
        this.ruleTableRow = row;
        this.isInRuleTable = true;
        this._currentAgendaGroup  = getProperties().getSingleProperty( AGENDAGROUP_TAG );
        this._action              = getProperties().getSingleProperty( ACTION_TAG );
        this._horizontalCondition = getProperties().getSingleProperty( HORIZONTALCONDITION_TAG );
        this._verticalCondition   = getProperties().getSingleProperty( VERTICALCONDITION_TAG );
        String precondition       = getProperties().getSingleProperty( PRECONDITION_TAG );
        if ( precondition != null ) {
            this._currentPrecondition = new Condition();
            this._currentPrecondition.setSnippet( precondition );
        }
    }

}
