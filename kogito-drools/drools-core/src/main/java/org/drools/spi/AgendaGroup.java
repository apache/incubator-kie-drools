package org.drools.spi;

import java.io.Serializable;

import org.drools.common.DefaultAgenda;

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

/**
 *  The <code>Agenda</code> can be partitioned into groups, called <code>AgendaGroup</code>s. <code>Rule</code>s can be assigned to
 *  these <code>AgendaGroup</code>s. Only rules in the focus group can fire. 
 * 
 * @see DefaultAgenda
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public interface AgendaGroup extends Serializable {

    /**
     * Static reference to determine the default <code>AgendaGroup</code> name.
     */
    public static String MAIN = "MAIN";

    /**
     * @return
     *      The <code>AgendaGroup</code> name
     */
    public String getName();

    /**
     * @return An array of all the activations in the AgendaGroup
     */
    Activation[] getActivations();

    /**
     * The total number of activations in this group
     * @return
     *      int value for the total number of activations
     */
    public int size();

}
