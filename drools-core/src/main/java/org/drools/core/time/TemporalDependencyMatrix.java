/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.time;

import java.util.List;

import org.drools.core.rule.Pattern;

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
        long expiration = -1;
        int index = events.indexOf( pattern );

        Interval[] intervals = matrix[index];
        for (int i = 0; i < intervals.length; i++) {
            if (i != index) { // skip values on the diagonal
                expiration = Math.max( expiration, intervals[i].getUpperBound() );
                if (expiration == Long.MAX_VALUE) {
                    return expiration;
                }
            }
        }

        return expiration >= 0 ? expiration+1 : Long.MAX_VALUE;
    }

}
