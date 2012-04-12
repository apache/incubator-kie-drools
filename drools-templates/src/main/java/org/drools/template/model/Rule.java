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

package org.drools.template.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Represents a rule.
 */
public class Rule extends AttributedDRLElement
    implements DRLJavaEmitter {

    private static final int MAX_ROWS = 65535;

    private String            _name;
    private String            _description;

    private List<String>      _metadata;
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
        super( salience );
        this._name = asStringLiteral( name );
        this._description = null;
        this._metadata = new LinkedList<String>();
        this._lhs = new LinkedList<Condition>();
        this._rhs = new LinkedList<Consequence>();
        this._spreadsheetRow = spreadsheetRow;
    }

    public void addMetadata(final String meta) {
        this._metadata.add( meta );
    }

    public void addCondition(final Condition con) {
        this._lhs.add( con );
    }

    public void addConsequence(final Consequence con) {
        this._rhs.add( con );
    }

    public void renderDRL(final DRLOutput out) {
        if ( isCommented() ) {
            out.writeLine( "//" + getComment() );
        }
        out.writeLine( "rule " + this._name );
        if ( this._description != null ) {
            out.writeLine( "\t// " + this._description );
        }
        
        // metadata
        for(String ms: this._metadata ){
            out.writeLine( "\t@" + ms );
        }
        
        // attributes
        super.renderDRL( out );

        out.writeLine( "\twhen" );
        renderDRL( this._lhs, out );
        out.writeLine( "\tthen" );
        renderDRL( this._rhs, out );
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

    public List<String> getMetadata() {
        return this._metadata;
    }
    
    public List<Condition> getConditions() {
        return this._lhs;
    }

    public List<Consequence> getConsequences() {
        return this._rhs;
    }
    
    public void setName(final String value){
        this._name = asStringLiteral( value );
    }

    public String getName() {
        return this._name;
    }


    public void setDescription(final String value){
        this._description = value;
    }

    public void appendDescription(final String value){
        this._description += value;
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
