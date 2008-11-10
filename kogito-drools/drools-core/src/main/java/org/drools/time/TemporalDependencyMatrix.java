/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools.time;

import java.util.List;

import org.drools.rule.Pattern;

/**
 * A class to abstract the management of temporal
 * dependency management information
 * 
 * @author etirelli
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
        long expiration = 0;
        int index = events.indexOf( pattern );
        for( Interval interval : matrix[index] ) {
           expiration = Math.max( expiration, interval.getUpperBound() ); 
        }
        return expiration;
    }
    
}
