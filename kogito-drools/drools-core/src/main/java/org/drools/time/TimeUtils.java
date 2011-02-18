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

/**
 * 
 */
package org.drools.time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.RuntimeDroolsException;

/**
 * A helper class with utility methods for
 * time related operations.
 * 
 * @author etirelli
 */
public class TimeUtils {

    // Simple syntax
    private static final Pattern SIMPLE  = Pattern.compile( "([+-])?((\\d+)[Dd])?\\s*((\\d+)[Hh])?\\s*((\\d+)[Mm])?\\s*((\\d+)[Ss])?\\s*((\\d+)([Mm][Ss])?)?" );
    private static final int     SIM_SGN = 1;
    private static final int     SIM_DAY = 3;
    private static final int     SIM_HOU = 5;
    private static final int     SIM_MIN = 7;
    private static final int     SIM_SEC = 9;
    private static final int     SIM_MS  = 11;

    // ISO 8601 compliant
    //    private static final Pattern ISO8601   = Pattern.compile( "(P((\\d+)[Yy])?((\\d+)[Mm])?((\\d+)[Dd])?)?(T((\\d+)[Hh])?((\\d+)[Mm])?((\\d+)[Ss])?((\\d+)([Mm][Ss])?)?)?" );

    private static final long    SEC_MS  = 1000;
    private static final long    MIN_MS  = 60 * SEC_MS;
    private static final long    HOU_MS  = 60 * MIN_MS;
    private static final long    DAY_MS  = 24 * HOU_MS;

    //    private static final long    MON_MS = 30 * DAY_MS;
    //    private static final long    YEA_MS = 365 * DAY_MS;

    
    /**
     * This method calculates the transitive closure of the given adjacency matrix
     * in order to find the temporal distance between each event represented in the
     * adjacency matrix.
     * 
     * For more information on the calculation of the temporal distance, please refer
     * to the paper:
     * 
     * "Discarding Unused Temporal Information in a Production System", by Dan Teodosiu
     * and Gunter Pollak.
     * 
     * This method also uses an adaptation of the Floyd-Warshall algorithm to calculate 
     * the transitive closure of the interval matrix. More information can be found here:
     * 
     * http://en.wikipedia.org/wiki/Floyd-Warshall_algorithm
     * 
     * The adaptation of the algorithm follows the definition of the path addition and
     * path intersection operations as defined in the paper previously mentioned. The
     * algorithm runs in O(n^3).
     *  
     * @param constraintMatrix the starting adjacency matrix
     * 
     * @return the resulting temporal distance matrix
     */
    public static Interval[][] calculateTemporalDistance( Interval[][] constraintMatrix ) {
        Interval[][] result = new Interval[constraintMatrix.length][];
        for( int i = 0; i < result.length; i++ ) {
            result[i] = new Interval[constraintMatrix[i].length];
            for( int j = 0; j < result[i].length; j++ ) {
                result[i][j] = constraintMatrix[i][j].clone();
            }
        }
        for( int k = 0; k < result.length; k++ ) {
            for( int i = 0; i < result.length; i++ ) {
                for( int j = 0; j < result.length; j++ ) {
                    Interval interval = result[i][k].clone();
                    interval.add( result[k][j] );
                    result[i][j].intersect( interval);
                }
            }
        }
        return result;
    }
    
    /**
     * Parses the given time String and returns the corresponding time
     * in milliseconds
     * 
     * @param time
     * @return
     * 
     * @throws NullPointerException if time is null
     */
    public static long parseTimeString( String time ) {
        String trimmed = time.trim();
        long result = 0;
        if( trimmed.length() > 0 ) {
            Matcher mat = SIMPLE.matcher( trimmed );
            if ( mat.matches() ) {
                int days = (mat.group( SIM_DAY ) != null) ? Integer.parseInt( mat.group( SIM_DAY ) ) : 0;
                int hours = (mat.group( SIM_HOU ) != null) ? Integer.parseInt( mat.group( SIM_HOU ) ) : 0;
                int min = (mat.group( SIM_MIN ) != null) ? Integer.parseInt( mat.group( SIM_MIN ) ) : 0;
                int sec = (mat.group( SIM_SEC ) != null) ? Integer.parseInt( mat.group( SIM_SEC ) ) : 0;
                int ms = (mat.group( SIM_MS ) != null) ? Integer.parseInt( mat.group( SIM_MS ) ) : 0;
                long r = days * DAY_MS + hours * HOU_MS + min * MIN_MS + sec * SEC_MS + ms;
                if( mat.group(SIM_SGN) != null && mat.group( SIM_SGN ).equals( "-" ) ) {
                    r = -r;
                }
                result = r;
            } else if( "*".equals( trimmed ) || "+*".equals( trimmed ) ) {
                // positive infinity
                result = Long.MAX_VALUE;
            } else if( "-*".equals( trimmed ) ) {
                // negative infinity
                result = Long.MIN_VALUE;
            } else {
                throw new RuntimeDroolsException( "Error parsing time string: [ " + time + " ]" );
            }
        }
        return result;
    }
    

}
