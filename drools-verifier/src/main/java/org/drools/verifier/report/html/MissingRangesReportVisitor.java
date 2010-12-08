/**
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.report.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.base.evaluators.Operator;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.NumberRestriction;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.report.components.MissingRange;
import org.drools.verifier.report.components.VerifierRangeCheckMessage;
import org.mvel2.templates.TemplateRuntime;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

class MissingRangesReportVisitor extends ReportVisitor {

    public static Collection<String> visitRestrictionsCollection(String sourceFolder,
                                                                 Collection<Restriction> restrictions,
                                                                 Collection<MissingRange> causes) {
    	
    	Multimap<String, DataRow> dt =  TreeMultimap.create();
        Collection<String> stringRows = new ArrayList<String>();

        for ( MissingRange cause : causes ) {
            dt.put( cause.getValueAsString(),
                    new DataRow( null,
                                 null,
                                 cause.getOperator(),
                                 cause.getValueAsString() ) );
        }

        for ( Restriction r : restrictions ) {
            if ( r instanceof NumberRestriction ) {
                try {
                    NumberRestriction restriction = (NumberRestriction) r;

                    dt.put( restriction.getValue().toString(),
                            new DataRow( restriction.getRulePath(),
                                         restriction.getRuleName(),
                                         restriction.getOperator(),
                                         restriction.getValueAsString() ) );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }

        DataRow previous = null;
        for ( Iterator<DataRow> iterator = dt.values().iterator(); iterator.hasNext(); ) {
            DataRow current = iterator.next();

            if ( previous != null ) {
                // Check if previous and current are from the same rule.
                if ( previous.ruleId == null && current.ruleId == null && !previous.operator.equals( Operator.EQUAL ) && !previous.operator.equals( Operator.NOT_EQUAL ) && !current.operator.equals( Operator.EQUAL )
                     && !current.operator.equals( Operator.NOT_EQUAL ) ) {
                    // Combine these two.
                    stringRows.add( "Missing : " + previous + " .. " + current );

                    if ( iterator.hasNext() ) current = iterator.next();

                } else if ( previous.ruleId != null && previous.ruleId.equals( current.ruleId ) ) {
                    // Combine these two.
                    stringRows.add( UrlFactory.getRuleUrl( sourceFolder,
                                                           current.ruleId,
                                                           current.ruleName ) + " : " + previous.toString() + " " + current.toString() );

                    if ( iterator.hasNext() ) current = iterator.next();

                } else if ( !iterator.hasNext() ) { // If this is last row
                    // Print previous and current if they were not merged.
                    processRangeOutput( previous,
                                        stringRows,
                                        sourceFolder );
                    processRangeOutput( current,
                                        stringRows,
                                        sourceFolder );

                } else { // If they are not from the same rule
                    // Print previous.
                    processRangeOutput( previous,
                                        stringRows,
                                        sourceFolder );
                }
            } else if ( !iterator.hasNext() ) {
                processRangeOutput( current,
                                    stringRows,
                                    sourceFolder );
            }

            // Set current as previous.
            previous = current;
        }

        return stringRows;
    }

    public static String visitRanges(String sourceFolder,
                                     Collection<Restriction> restrictions,
                                     Collection<MissingRange> collection) {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put( "lines",
                 visitRestrictionsCollection( sourceFolder,
                                              restrictions,
                                              collection ) );

        String myTemplate = readFile( "ranges.htm" );

        String result = String.valueOf( TemplateRuntime.eval( myTemplate,
                                                              map ) );

        return result;
    }

    private static void processRangeOutput(DataRow dataRow,
                                           Collection<String> stringRows,
                                           String sourceFolder) {

        if ( dataRow.ruleId == null ) {
            stringRows.add( "Missing : " + dataRow.toString() );
        } else {
            stringRows.add( UrlFactory.getRuleUrl( sourceFolder,
                                                   dataRow.ruleId,
                                                   dataRow.ruleName ) + " : " + dataRow.toString() );
        }
    }

    public static String visitRangeCheckMessage(String sourceFolder,
                                                VerifierRangeCheckMessage message,
                                                VerifierData data) {
        Field field = (Field) message.getFaulty();
        Collection<Restriction> restrictions = data.getRestrictionsByFieldPath( field.getPath() );

        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "header",
                 processHeader( sourceFolder ) );
        map.put( "sourceFolder",
                 sourceFolder );
        map.put( "fieldFolder",
                 sourceFolder + "/" + UrlFactory.FIELD_FOLDER );
        map.put( "objectTypeFolder",
                 sourceFolder + "/" + UrlFactory.OBJECT_TYPE_FOLDER );
        map.put( "packageFolder",
                 sourceFolder + "/" + UrlFactory.PACKAGE_FOLDER );
        map.put( "cssStyle",
                 createStyleTag( sourceFolder + "/" + UrlFactory.CSS_FOLDER + "/" + UrlFactory.CSS_BASIC ) );

        map.put( "field",
                 field );
        map.put( "objectType",
                 data.getVerifierObject( VerifierComponentType.OBJECT_TYPE,
                                         field.getObjectTypePath() ) );
        map.put( "ranges",
                 visitRanges( UrlFactory.THIS_FOLDER,
                              restrictions,
                              message.getMissingRanges() ) );

        String myTemplate = readFile( "missingRange.htm" );

        String result = String.valueOf( TemplateRuntime.eval( myTemplate,
                                                              map ) );

        return result;
    }
}

class DataRow
    implements
    Comparable<DataRow> {
    public String      ruleName;
    protected String   ruleId;
    protected Operator operator;
    protected String   value;

    public int compareTo(DataRow o) {
        return operator.getOperatorString().compareTo( o.operator.getOperatorString() );
    }

    public DataRow(String ruleId,
                   String ruleName,
                   Operator operator,
                   String valueAsString) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.operator = operator;
        this.value = valueAsString;
    }

    public String toString() {
        return operator.getOperatorString() + " " + value;
    }
}
