package org.drools.reteoo;

/*
 * $Id: Scheduler.java,v 1.2 2005/08/14 22:34:41 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.ConsequenceException;

/**
 * Scheduler for rules requiring truth duration.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
final class Scheduler
{
    // ------------------------------------------------------------
    // Class members
    // ------------------------------------------------------------

    /** Singleton instance. */
    private static final Scheduler INSTANCE = new Scheduler( );

    // ------------------------------------------------------------
    // Class methods
    // ------------------------------------------------------------

    /**
     * Retrieve the singleton instance.
     * 
     * @return The singleton instance.
     */
    static Scheduler getInstance()
    {
        return INSTANCE;
    }

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Alarm manager. */
    private final Timer           scheduler;

    /** Scheduled tasks. */
    private final Map             tasks;

    private AsyncExceptionHandler exceptionHandler;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     */
    private Scheduler()
    {
        this.scheduler = new Timer( true );

        this.tasks = new HashMap( );
    }

    /**
     * Schedule an agenda item.
     * 
     * @param item
     *            The item to schedule.
     * @param workingMemory
     *            The working memory session.
     */
    void scheduleAgendaItem(AgendaItem item,
                            WorkingMemoryImpl workingMemory)
    {
        Date now = new Date( );

        Date then = new Date( now.getTime( ) + item.getRule( ).getDuration( ).getDuration( item.getTuple( ) ) );

        TimerTask task = new AgendaItemFireListener( item,
                                                     workingMemory );

        this.scheduler.schedule( task,
                                 then );

        this.tasks.put( item,
                        task );
    }

    /**
     * Cancel an agenda item.
     * 
     * @param item
     *            The item to cancle.
     */
    void cancelAgendaItem(AgendaItem item)
    {
        TimerTask task = (TimerTask) this.tasks.remove( item );

        if ( task != null )
        {
            task.cancel( );
        }
    }

    void setAsyncExceptionHandler(AsyncExceptionHandler handler)
    {
        this.exceptionHandler = handler;
    }

    AsyncExceptionHandler getAsyncExceptionHandler()
    {
        return this.exceptionHandler;
    }

    public int size()
    {
        return this.tasks.size( );
    }

    /**
     * Fire listener.
     * 
     * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
     */

    class AgendaItemFireListener extends TimerTask
    {
        // ------------------------------------------------------------
        // Instance members
        // ------------------------------------------------------------

        /** The agenda item. */
        private AgendaItem        item;

        /** The working-memory session. */
        private WorkingMemoryImpl workingMemory;

        // ------------------------------------------------------------
        // Constructors
        // ------------------------------------------------------------

        /**
         * Construct.
         * 
         * @param item
         *            The agenda item.
         * @param workingMemory
         *            The working memory session.
         */
        AgendaItemFireListener(AgendaItem item,
                               WorkingMemoryImpl workingMemory)
        {
            this.item = item;
            this.workingMemory = workingMemory;
        }

        // ------------------------------------------------------------
        // Instance methods
        // ------------------------------------------------------------

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // fr.dyade.jdring.AlarmListener
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        /**
         * Handle the firing of an alarm.
         */
        public void run()
        {
            try
            {
                this.item.fire( this.workingMemory );
                Scheduler.this.tasks.remove( item );
            }
            catch ( ConsequenceException e )
            {

                Scheduler.getInstance( ).getAsyncExceptionHandler( ).handleException( this.workingMemory,
                                                                                      e );
            }
        }
    }
}
