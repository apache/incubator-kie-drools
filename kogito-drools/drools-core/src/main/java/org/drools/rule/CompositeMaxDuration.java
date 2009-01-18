package org.drools.rule;

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

import java.util.LinkedList;
import java.util.List;

import org.drools.spi.Duration;
import org.drools.spi.Tuple;

/**
 * A composite duration where the expression
 * is evaluated and the maximum duration between
 * the components is used as the rule's duration.
 * 
 * @see Rule#setDuration
 * @see Rule#getDuration
 * 
 * @author etirelli
 */
public class CompositeMaxDuration
    implements
    Duration {

    private static final long serialVersionUID = 6987782407024443255L;

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    private List<Duration> durations;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     */
    public CompositeMaxDuration() {
        this( null );
    }

    /**
     * Construct.
     * 
     * @param duration a duration to be included as a component of the composed duration
     *            Number of seconds.
     */
    public CompositeMaxDuration( final Duration duration ) {
        this.durations = new LinkedList<Duration>();
        this.durations.add( duration );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    public void addDuration( final Duration duration ) {
        this.durations.add( duration );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Returns the maximum duration among the components of this
     * composite duration.
     * 
     * @see Duration
     */
    public long getDuration(final Tuple tuple) {
        long result = 0;
        for( Duration duration : durations ) {
            result = Math.max( result, duration.getDuration( tuple ) );
        }
        return result;
    }
}