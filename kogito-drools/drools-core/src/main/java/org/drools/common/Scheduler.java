package org.drools.common;

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

import java.util.Date;
import java.util.Timer;

/**
 * Scheduler for rules requiring truth duration.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
final class Scheduler {
    // ------------------------------------------------------------
    // Class members
    // ------------------------------------------------------------

    /** Singleton instance. */
    private static final Scheduler INSTANCE = new Scheduler();

    // ------------------------------------------------------------
    // Class methods
    // ------------------------------------------------------------

    /**
     * Retrieve the singleton instance.
     * 
     * @return The singleton instance.
     */
    static Scheduler getInstance() {
        return Scheduler.INSTANCE;
    }

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Alarm manager. */
    private final Timer scheduler;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     */
    private Scheduler() {
        this.scheduler = new Timer( true );
    }

    /**
     * Schedule an agenda item.
     * 
     * @param item
     *            The item to schedule.
     * @param workingMemory
     *            The working memory session.
     */
    void scheduleAgendaItem(final ScheduledAgendaItem item) {
        final Date now = new Date();

        final Date then = new Date( now.getTime() + item.getRule().getDuration().getDuration( item.getTuple() ) );

        this.scheduler.schedule( item,
                                 then );
    }
}
