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

package org.drools.spi;

import java.io.Externalizable;
import java.util.Collection;

import org.drools.common.DefaultAgenda;
import org.drools.runtime.rule.Activation;

/**
 *  The <code>Agenda</code> can be partitioned into groups, called <code>AgendaGroup</code>s. <code>Rule</code>s can be assigned to
 *  these <code>AgendaGroup</code>s. Only rules in the focus group can fire.
 *
 * @see DefaultAgenda
 *
 *
 */
public interface AgendaGroup
    extends
    Externalizable,
    org.drools.runtime.rule.AgendaGroup {
    
    /**
     * Static reference to determine the default <code>AgendaGroup</code> name.
     */
    public static String MAIN = "MAIN";

    /**
     * @return 
     *     An immutable Collection of all the activations in the AgendaGroup
     */
    Activation[] getActivations();

    /** 
     * @return
     *     The int total number of activations
     */
    public int size();

    /**
     * @return
     *     boolean value indicating if this AgendaGroup is empty or not
     */
    public boolean isEmpty();

    /**
     * 
     * @return
     *     boolean value indicating if the AgendaGroup is active and thus being evaluated.
     */
    public boolean isActive();
    
}
