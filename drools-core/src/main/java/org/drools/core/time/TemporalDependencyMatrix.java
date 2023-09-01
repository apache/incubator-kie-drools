package org.drools.core.time;

import java.util.List;
import java.util.stream.IntStream;

import org.drools.base.rule.Pattern;
import org.drools.base.time.Interval;

import static org.drools.base.rule.TypeDeclaration.NEVER_EXPIRES;

/**
 * A class to abstract the management of temporal
 * dependency management information
 */
public class TemporalDependencyMatrix {
    
    private Interval[][] matrix;
    private List<Pattern> events;
    
    public TemporalDependencyMatrix(Interval[][] matrix,
                                    List<Pattern> events) {
        super();
        this.matrix = matrix;
        this.events = events;
    }

    public Interval[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(Interval[][] matrix) {
        this.matrix = matrix;
    }

    public List<Pattern> getEvents() {
        return events;
    }

    public void setEvents(List<Pattern> events) {
        this.events = events;
    }

    public long getExpirationOffset(Pattern pattern) {
        int index = events.indexOf( pattern );
        Interval[] intervals = matrix[index];

        long expiration = IntStream.range( 0, intervals.length )
                                   .filter( i -> i != index ) // skip values on the diagonal
                                   .mapToLong( i -> intervals[i].getUpperBound() )
                                   .max().orElse( NEVER_EXPIRES );

        return expiration >= 0 && expiration != Long.MAX_VALUE ? expiration+1 : NEVER_EXPIRES;
    }
}
