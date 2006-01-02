package org.drools.reteoo;

/*
 * $Id: AgendaItem.java,v 1.3 2005/08/14 22:44:12 mproctor Exp $
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

import java.io.Serializable;

import org.drools.FactHandle;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.ConsequenceException;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * Item entry in the <code>Agenda</code>.
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
class AgendaItem
    implements
    Activation,
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The tuple. */
    private ReteTuple          tuple;

    /** The rule. */
    private Rule               rule;

    private PropagationContext context;

    // ** The Counter */
    private static long        counter;

    private long               activationNumber;

    private boolean            retract;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param tuple
     *            The tuple.
     * @param rule
     *            The rule.
     */
    AgendaItem(ReteTuple tuple,
               PropagationContext context,
               Rule rule) {
        this.tuple = tuple;
        this.context = context;
        this.rule = rule;
        this.activationNumber = AgendaItem.counter++;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public PropagationContext getPropagationContext() {
        return this.context;
    }

    /**
     * Retrieve the rule.
     * 
     * @return The rule.
     */
    public Rule getRule() {
        return this.rule;
    }

    /**
     * Determine if this tuple depends on the values derrived from a particular
     * root object.
     * 
     * @param handle
     *            The root object handle.
     * 
     * @return <code>true<code> if this agenda item depends
     *          upon the item, otherwise <code>false</code>.
     */
    boolean dependsOn(FactHandle handle) {
        return this.tuple.dependsOn( handle );
    }

    /**
     * Set the tuple.
     * 
     * @param tuple
     *            The tuple.
     */
    void setTuple(ReteTuple tuple) {
        this.tuple = tuple;
    }

    /**
     * Retrieve the tuple.
     * 
     * @return The tuple.
     */
    public Tuple getTuple() {
        return this.tuple;
    }

    /**
     * Retrieve the <code>TupleKey</code>.
     * 
     * @return The key to the tuple in this item.
     */
    TupleKey getKey() {
        return this.tuple.getKey();
    }

    /**
     * Fire this item.
     * 
     * @param workingMemory
     *            The working memory context.
     * 
     * @throws ConsequenceException
     *             If an error occurs while attempting to fire the consequence.
     */
    void fire(WorkingMemoryImpl workingMemory) throws ConsequenceException {

        this.rule.getConsequence().invoke( null );

        workingMemory.getAgendaEventSupport().fireActivationFired( this.rule,
                                                                   this.tuple );
    }

    public long getActivationNumber() {
        return this.activationNumber;
    }

    public String toString() {
        return "[" + this.rule.getName() + " " + this.tuple + "]";
    }

    public boolean equals(Object object) {
        if ( (object == null) || !(object instanceof AgendaItem) ) {
            return false;
        }

        AgendaItem otherItem = (AgendaItem) object;

        return (this.rule.equals( otherItem.getRule() ) && this.tuple.getKey().equals( otherItem.getKey() ));
    }

    public int hashcode() {
        return this.getKey().hashCode();
    }

}
