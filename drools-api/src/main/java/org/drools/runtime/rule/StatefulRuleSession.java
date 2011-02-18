/**
 * Copyright 2010 JBoss Inc
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

package org.drools.runtime.rule;

/**
 * An interface for all <code>StatefulRuleSession</code> methods. This interface
 * adds methods available to the StatefulKnowledgeSession beyond what the WorkingMemory provides. 
 * 
 * @see org.drools.runtime.StatefulKnowledgeSession 
 */
public interface StatefulRuleSession {

    /**
     * Fire all Activations on the Agenda.
     * @return
     *     returns the number of rules fired
     */
    int fireAllRules();

    /**
     * Fire Activations on the Agenda up to the given maximum number of activations, before returning
     * the control to the application.
     * In case the application wants to continue firing the rules later, from the point where it stopped,
     * it just needs to call <code>fireAllRules()</code> again.
     * 
     * @param max
     *     the maximum number of rules that should be fired
     * @return
     *     returns the number of rules fired
     */
    int fireAllRules(int max);

    /**
     * Fire all Activations on the Agenda
     * 
     * @param agendaFilter
     *      filters the activations that may fire
     * @return
     *      returns the number of rules fired
     */
    int fireAllRules(AgendaFilter agendaFilter);

    /**
     * Keeps firing activations until a halt is called. If in a given moment,
     * there is no activation to fire, it will wait for an activation to be
     * added to an active agenda group or rule flow group. This blocks the thread
     * until halt is called.
     * 
     * @throws IllegalStateException
     *             if this method is called when running in sequential mode
     */
    public void fireUntilHalt();

    /**
     * Keeps firing activations until a halt is called. If in a given moment,
     * there is no activation to fire, it will wait for an activation to be
     * added to an active agenda group or rule flow group. This blocks the thread
     * until halt is called.
     * 
     * @param agendaFilter
     *            filters the activations that may fire
     * 
     * @throws IllegalStateException
     *             if this method is called when running in sequential mode
     */
    public void fireUntilHalt(final AgendaFilter agendaFilter);
}
