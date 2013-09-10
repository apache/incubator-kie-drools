/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.oracle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An Oracle for all things "CEP" related
 */
public class CEPOracle {

    //CEP Operators
    private static final String[] WINDOW_CEP_OPERATORS = new String[]{
            "over window:time",
            "over window:length" };

    private static final String[] SIMPLE_CEP_OPERATORS = new String[]{
            "after",
            "before",
            "coincides" };

    private static final String[] COMPLEX_CEP_OPERATORS = new String[]{
            "during",
            "finishes",
            "finishedby",
            "includes",
            "meets",
            "metby",
            "overlaps",
            "overlappedby",
            "starts",
            "startedby" };

    private static final String[] SIMPLE_CEP_CONNECTIVES = new String[]{
            "|| after",
            "|| before",
            "|| coincides",
            "&& after",
            "&& before",
            "&& coincides" };

    private static final String[] COMPLEX_CEP_CONNECTIVES = new String[]{
            "|| during",
            "|| finishes",
            "|| finishedby",
            "|| includes",
            "|| meets",
            "|| metby",
            "|| overlaps",
            "|| overlappedby",
            "|| starts",
            "|| startedby",
            "&& during",
            "&& finishes",
            "&& finishedby",
            "&& includes",
            "&& meets",
            "&& metby",
            "&& overlaps",
            "&& overlappedby",
            "&& starts",
            "&& startedby" };

    private static final Map<String, List<Integer>> CEP_OPERATORS_PARAMETERS = new HashMap<String, List<Integer>>();

    static {
        CEP_OPERATORS_PARAMETERS.put( "after",
                                      Arrays.asList( new Integer[]{ 0, 1, 2 } ) );
        CEP_OPERATORS_PARAMETERS.put( "before",
                                      Arrays.asList( new Integer[]{ 0, 1, 2 } ) );
        CEP_OPERATORS_PARAMETERS.put( "coincides",
                                      Arrays.asList( new Integer[]{ 0, 1, 2 } ) );
        CEP_OPERATORS_PARAMETERS.put( "during",
                                      Arrays.asList( new Integer[]{ 0, 1, 2, 4 } ) );
        CEP_OPERATORS_PARAMETERS.put( "finishes",
                                      Arrays.asList( new Integer[]{ 0, 1 } ) );
        CEP_OPERATORS_PARAMETERS.put( "finishedby",
                                      Arrays.asList( new Integer[]{ 0, 1 } ) );
        CEP_OPERATORS_PARAMETERS.put( "includes",
                                      Arrays.asList( new Integer[]{ 0, 1, 2, 4 } ) );
        CEP_OPERATORS_PARAMETERS.put( "meets",
                                      Arrays.asList( new Integer[]{ 0, 1 } ) );
        CEP_OPERATORS_PARAMETERS.put( "metby",
                                      Arrays.asList( new Integer[]{ 0, 1 } ) );
        CEP_OPERATORS_PARAMETERS.put( "overlaps",
                                      Arrays.asList( new Integer[]{ 0, 1, 2 } ) );
        CEP_OPERATORS_PARAMETERS.put( "overlappedby",
                                      Arrays.asList( new Integer[]{ 0, 1, 2 } ) );
        CEP_OPERATORS_PARAMETERS.put( "starts",
                                      Arrays.asList( new Integer[]{ 0, 1 } ) );
        CEP_OPERATORS_PARAMETERS.put( "startedby",
                                      Arrays.asList( new Integer[]{ 0, 1 } ) );
    }

    /**
     * Check whether an operator is a CEP operator
     * @param operator
     * @return True if the operator is a CEP operator
     */
    public static boolean isCEPOperator( final String operator ) {
        if ( operator == null ) {
            return false;
        }

        String[] operators = OracleUtils.joinArrays( SIMPLE_CEP_OPERATORS,
                                                     COMPLEX_CEP_OPERATORS,
                                                     SIMPLE_CEP_CONNECTIVES,
                                                     COMPLEX_CEP_CONNECTIVES );

        for ( int i = 0; i < operators.length; i++ ) {
            if ( operator.equals( operators[ i ] ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the parameter sets for the given CEP Operator (simple, or connective)
     * e.g. CEP operator "during" requires 0, 1, 2 or 4 parameters so the
     * returned list contains 0, 1, 2 and 4.
     * @param operator
     * @return
     */
    public static List<Integer> getCEPOperatorParameterSets( String operator ) {
        List<Integer> sets = new ArrayList<Integer>();
        if ( operator == null ) {
            return sets;
        }
        if ( operator.startsWith( "|| " ) || operator.startsWith( "&& " ) ) {
            operator = operator.substring( 3 );
        }
        if ( !CEP_OPERATORS_PARAMETERS.containsKey( operator ) ) {
            return sets;
        }

        return CEP_OPERATORS_PARAMETERS.get( operator );
    }

    /**
     * Return a list of operators applicable to CEP windows
     * @return
     */
    public static List<String> getCEPWindowOperators() {
        return Arrays.asList( WINDOW_CEP_OPERATORS );
    }

    /**
     * Check whether an operator is a CEP 'window' operator
     * @param operator
     * @return True if the operator is a CEP 'window' operator
     */
    public static boolean isCEPWindowOperator( String operator ) {
        if ( operator == null ) {
            return false;
        }

        for ( String cepWindowOperator : WINDOW_CEP_OPERATORS ) {
            if ( operator.equals( cepWindowOperator ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the operator is 'window over:time'
     * @param operator
     * @return if
     */
    public static boolean isCEPWindowOperatorTime( String operator ) {
        if ( operator == null ) {
            return false;
        }
        return WINDOW_CEP_OPERATORS[ 0 ].equals( operator );
    }

    /**
     * Check if the operator is 'window over:length'
     * @param operator
     * @return if
     */
    public static boolean isCEPWindowOperatorLength( String operator ) {
        if ( operator == null ) {
            return false;
        }
        return WINDOW_CEP_OPERATORS[ 1 ].equals( operator );
    }

}
