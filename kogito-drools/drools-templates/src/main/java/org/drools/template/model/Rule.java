package org.drools.template.model;

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

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 *
 * Represents a rule.
 */
public class Rule extends DRLElement
    implements
    DRLJavaEmitter {

    private static final int MAX_ROWS = 65535;

    private Integer           _salience;       // Integer as it may be null

    private String            _name;

    private String            _duration;       // RIK: New variable to the Rule class (Defines
    // a Duration tag for the rule)

    private String            _description;    // RIK: New variable to the Rule class (Set
    // the description parameter of the rule
    // tag)

    private String            _noLoop;         // RIK: New variable to the Rule class (Set the
    // no-loop parameter of the rule tag)

    private String            _activationGroup; // RIK: New variable to the Rule class (Set the
    // activation-group parameter of the rule tag)

    private String            _ruleFlowGroup;

    private String            _agendaGroup;    // SJW: New variable to the Rule class (Set the
    // agenda-group parameter of the rule tag

    private List<Condition>   _lhs;

    private List<Consequence> _rhs;

    private int               _spreadsheetRow;

    /**
     * Create a new rule. Note that the rule name should be post-fixed with the row number,
     * as one way of providing tracability for errors back to the originating spreadsheet.
     * @param name The name of the rule. This may be used to calculate DRL row error
     * to Spreadsheet row error (just need to keep track of output lines, and map spreadsheetRow to a start
     * and end range in the rendered output).
     * @param salience
     * @param spreadsheetRow The phyical row number from the spreadsheet.
     */
    public Rule(final String name,
                final Integer salience,
                final int spreadsheetRow) {
        this._name = name;
        this._salience = salience;
        this._description = "";

        this._lhs = new LinkedList<Condition>();
        this._rhs = new LinkedList<Consequence>();
        this._spreadsheetRow = spreadsheetRow;
    }

    public void addCondition(final Condition con) {
        this._lhs.add( con );
    }

    public void addConsequence(final Consequence con) {
        this._rhs.add( con );
    }

    public void renderDRL(final DRLOutput out) {
        if ( isCommented() ) {
            out.writeLine( "#" + getComment() );
        }
        out.writeLine( "rule " + "\"" + this._name + "\"" );
        if ( this._description != null ) {
            out.writeLine( "\t" + this._description );
        }
        if ( this._salience != null ) {
            out.writeLine( "\tsalience " + this._salience );
        }
        if ( this._activationGroup != null ) {
            out.writeLine( "\tactivation-group \"" + this._activationGroup + "\"" );
        }
        if ( this._agendaGroup != null ) {
            out.writeLine( "\tagenda-group " + this._agendaGroup );
        }
        if ( this._noLoop != null ) {
            out.writeLine( "\tno-loop " + this._noLoop );
        }
        if ( this._duration != null ) {
            out.writeLine( "\tduration " + this._duration );
        }

        if ( this._ruleFlowGroup != null ) {
            out.writeLine( "\truleflow-group \"" + this._ruleFlowGroup + "\"" );
        }

        out.writeLine( "\twhen" );
        renderDRL( this._lhs,
                   out );
        out.writeLine( "\tthen" );
        renderDRL( this._rhs,
                   out );

        out.writeLine( "end\n" );
    }

    private void renderDRL(final List<? extends DRLJavaEmitter> list,
                           final DRLOutput out) {
        for ( DRLJavaEmitter item : list ) {
            item.renderDRL( out );
        }
    }

    public static int calcSalience(final int rowNumber) {
        if ( rowNumber > Rule.MAX_ROWS ) {
            throw new IllegalArgumentException( "That row number is above the max: " + Rule.MAX_ROWS );
        }
        return Rule.MAX_ROWS - rowNumber;
    }

    /**
     * @param col -
     *            the column number. Start with zero.
     * @return The spreadsheet name for this col number, such as "AA" or "AB" or
     *         "A" and such and such.
     */
    public static String convertColNumToColName(final int i) {

        String result;
        final int div = i / 26;
        final int mod = i % 26;

        if ( div == 0 ) {
            final byte[] c = new byte[1];
            c[0] = (byte) (mod + 65);
            result = byteToString( c );
        } else {
            final byte[] firstChar = new byte[1];
            firstChar[0] = (byte) ((div - 1) + 65);

            final byte[] secondChar = new byte[1];
            secondChar[0] = (byte) (mod + 65);
            final String first = byteToString( firstChar );
            final String second = byteToString( secondChar );
            result = first + second;
        }
        return result;

    }

    private static String byteToString(final byte[] secondChar) {
        try {
            return new String( secondChar,
                               "UTF-8" );
        } catch ( final UnsupportedEncodingException e ) {
            throw new RuntimeException( "Unable to convert char to string.",
                                        e );
        }
    }

    public List<Condition> getConditions() {
        return this._lhs;
    }

    public List<Consequence> getConsequences() {
        return this._rhs;
    }

    public void setSalience(final Integer value) // Set the salience of the rule
    {
        this._salience = value;
    }

    public Integer getSalience() {
        return this._salience;
    }

    public void setName(final String value) // Set the name of the rule
    {
        this._name = value;
    }

    public String getName() {
        return this._name;
    }

    public void setDescription(final String value) // Set the description of the
    // rule
    {
        this._description = value;
    }

    public void appendDescription(final String value) // Set the description of the
    // rule
    {
        this._description += value;
    }

    public String getDescription() {
        return this._description;
    }

    public void setDuration(final String value) // Set the duration of the rule
    {
        this._duration = value;
    }

    public String getDuration() {
        return this._duration;
    }

    public void setActivationGroup(final String value) // Set the duration of the rule
    {
        this._activationGroup = value;
    }

    public void setRuleFlowGroup(final String value) {
        this._ruleFlowGroup = value;
    }

    public String getRuleFlowGroup() {
        return this._ruleFlowGroup;
    }

    public String getActivationGroup() {
        return this._activationGroup;
    }

    public String getAgendaGroup() {
        return _agendaGroup;
    }

    public void setAgendaGroup(String group) // Set the agenda-group of the rule
    {
        _agendaGroup = group;
    }

    public void setNoLoop(final String value) // Set the no-loop attribute of the rule
    {
        this._noLoop = value;
    }

    /**
     * @return The row in the spreadsheet this represents.
     * This can be handy when mapping a line error from Parser back to the rule row.
     * Will need to have a map of ranges of line numbers that each rule covers.
     * Then can find out the rule that cause it, and this will give the row number to report.
     */
    public int getSpreadsheetRowNumber() {
        return this._spreadsheetRow;
    }

}