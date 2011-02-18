/**
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

package org.drools.rule;

import org.drools.spi.Duration;
import org.drools.spi.Tuple;

/**
 * A fixed truthness duration.
 * 
 * @see Rule#setDuration
 * @see Rule#getDuration
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * 
 * @version $Id: FixedDuration.java,v 1.2 2005/08/14 22:34:41 mproctor Exp $
 */
public class FixedDuration
    implements
    Duration {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long serialVersionUID = 510l;
    /** Duration, in seconds. */
    private long              duration;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     */
    public FixedDuration() {
        this.duration = 0;
    }

    /**
     * Construct.
     * 
     * @param seconds
     *            Number of seconds.
     */
    public FixedDuration(final long ms) {
        this.duration = ms;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Add seconds.
     * 
     * @param seconds
     *            Number of seconds.
     */
    public void addSeconds(final long seconds) {
        this.duration += (seconds * 1000);
    }

    /**
     * Add minutes.
     * 
     * @param minutes
     *            Number of minutes.
     */
    public void addMinutes(final long minutes) {
        this.duration += ((minutes * 60) * 1000);
    }

    /**
     * Add hours.
     * 
     * @param hours
     *            Number of hours.
     */
    public void addHours(final long hours) {
        this.duration += ((hours * 60 * 60) * 1000);
    }

    /**
     * Add days.
     * 
     * @param days
     *            Number of days.
     */
    public void addDays(final long days) {
        this.duration += ((days * 60 * 60 * 24) * 1000);
    }

    /**
     * Add weeks.
     * 
     * @param weeks
     *            Number of weeks.
     */
    public void addWeeks(final long weeks) {
        this.duration += ((weeks * 60 * 60 * 24 * 7) * 1000);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @see Duration
     */
    public long getDuration(final Tuple tuple) {
        return this.duration;
    }
}
