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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An Oracle for all things "operator" related
 */
public class OperatorsOracle {

    // The operators that are used at different times (based on type).
    public static final String[] STANDARD_OPERATORS = new String[]{ "==", "!=", "== null", "!= null" };

    public static final String[] STANDARD_CONNECTIVES = new String[]{ "|| ==", "|| !=", "&& !=" };

    public static final String[] COMPARABLE_OPERATORS = new String[]{ "==", "!=", "<", ">", "<=", ">=", "== null", "!= null" };

    public static final String[] COMPARABLE_CONNECTIVES = new String[]{ "|| ==", "|| !=", "&& !=", "&& >", "&& <", "|| >", "|| <", "&& >=", "&& <=", "|| <=", "|| >=" };

    public static final String[] STRING_OPERATORS = new String[]{ "==", "!=", "<", ">", "<=", ">=", "matches", "soundslike", "== null", "!= null" };

    public static final String[] STRING_CONNECTIVES = new String[]{ "|| ==", "|| !=", "&& !=", "&& >", "&& <", "|| >", "|| <", "&& >=", "&& <=", "|| <=", "|| >=", "&& matches", "|| matches" };

    public static final String[] COLLECTION_OPERATORS = new String[]{ "contains", "excludes", "==", "!=", "== null", "!= null" };

    public static final String[] COLLECTION_CONNECTIVES = new String[]{ "|| ==", "|| !=", "&& !=", "|| contains", "&& contains", "|| excludes", "&& excludes" };

    public static final String[] EXPLICIT_LIST_OPERATORS = new String[]{ "in", "not in" };

    public static final String[] CONDITIONAL_ELEMENTS = new String[]{ "not", "exists", "or" };

    public static final String[] SIMPLE_CEP_OPERATORS = new String[]{ "after", "before", "coincides" };

    public static final String[] COMPLEX_CEP_OPERATORS = new String[]{ "during", "finishes", "finishedby", "includes", "meets", "metby", "overlaps", "overlappedby", "starts", "startedby" };

    public static final String[] WINDOW_CEP_OPERATORS = new String[]{ "over window:time", "over window:length" };

    public static final String[] SIMPLE_CEP_CONNECTIVES = new String[]{ "|| after", "|| before", "|| coincides", "&& after", "&& before", "&& coincides" };

    public static final String[] COMPLEX_CEP_CONNECTIVES = new String[]{ "|| during", "|| finishes", "|| finishedby", "|| includes", "|| meets", "|| metby", "|| overlaps", "|| overlappedby", "|| starts", "|| startedby",
            "&& during", "&& finishes", "&& finishedby", "&& includes", "&& meets", "&& metby", "&& overlaps", "&& overlappedby", "&& starts", "&& startedby" };

    private static final Map<String, List<Integer>> CEP_OPERATORS_PARAMETERS = new HashMap<String, List<Integer>>();

    {
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
     * Check whether an operator requires a list of values (i.e. the operator is
     * either "in" or "not in"). Operators requiring a list of values can only
     * be compared to literal values.
     * @param operator
     * @return True if the operator requires a list values
     */
    public static boolean operatorRequiresList( final String operator ) {
        if ( operator == null || operator.equals( "" ) ) {
            return false;
        }
        for ( String explicitListOperator : EXPLICIT_LIST_OPERATORS ) {
            if ( operator.equals( explicitListOperator ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Operator "== null" and "!= null" do not need a Value. This method wraps the logic into a single method.
     * @param operator The operator to check
     * @return true is a Value is required
     */
    public static boolean isValueRequired( final String operator ) {
        if ( operator == null || operator.equals( "" ) ) {
            return false;
        }
        return !( operator.equals( "== null" ) || operator.equals( "!= null" ) );
    }

}
