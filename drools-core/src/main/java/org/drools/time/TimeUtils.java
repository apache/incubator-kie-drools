/**
 * 
 */
package org.drools.time;

/**
 * A helper class with utility methods for
 * time related operations.
 * 
 * @author etirelli
 */
public class TimeUtils {

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

}
