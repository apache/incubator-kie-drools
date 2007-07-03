package org.drools.base;

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

import org.drools.rule.Rule;
import org.drools.spi.Duration;
import org.drools.spi.Tuple;

/**
 * Implementation of the <code>Duration</code> interface
 * for specifying truthness duration.
 */
public class BaseDuration
    implements
    Duration {
    /**
     * 
     */
    private static final long serialVersionUID = 400L;

    /** The number of seconds of the duration. */
    private long              seconds;

    private Rule              rule;

    /**
     * Constructor.
     * 
     * @param seconds the number of seconds of the duration
     */
    public BaseDuration(final Rule rule,
                        final long seconds) {
        this.rule = rule;
        this.seconds = seconds;
    }

    /**
     * Retrieves the duration for which the conditions of this
     * <code>Tuple</code> must remain true before the rule will fire.
     * 
     * @param tuple the <code>Tuple</code>
     * 
     * @return the duration in seconds
     */
    public long getDuration(final Tuple tuple) {
        return this.seconds;
    }

    public Rule getRule() {
        return this.rule;
    }
}