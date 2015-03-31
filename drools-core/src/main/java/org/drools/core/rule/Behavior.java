/*
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

package org.drools.core.rule;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleComponent;

/**
 * An interface for all behavior implementations
 */
public interface Behavior extends RuleComponent, Cloneable {
    
    public static final Behavior[] EMPTY_BEHAVIOR_LIST = new Behavior[0];
    
    public enum BehaviorType {
        TIME_WINDOW( "time" ),
        LENGTH_WINDOW( "length" );
        
        private final String id;
        
        private BehaviorType( String id ) {
            this.id = id;
        }
        
        public boolean matches( String id ) {
            return this.id.equalsIgnoreCase( id );
        }
    }
    
    /**
     * Returns the type of the behavior
     * 
     * @return
     */
    public BehaviorType getType();

    /**
     * Creates the context object associated with this behavior.
     * The object is given as a parameter in all behavior call backs.
     * 
     * @return
     */
    public Object createContext();

    /**
     * Makes the behavior aware of the new fact entering behavior's scope
     * 
     * @param context The behavior context object
     * @param fact The new fact entering behavior's scope
     * @param workingMemory The working memory session reference
     * 
     * @return true if the propagation should continue, false otherwise. I.e., 
     *         the behaviour has veto power over the fact propagation, and prevents
     *         the propagation to continue if returns false on this method. 
     */
    public boolean assertFact(Object context,
                              InternalFactHandle fact,
                              PropagationContext pctx,
                              InternalWorkingMemory workingMemory);

    /**
     * Removes a right tuple from the behavior's scope
     * 
     * @param context The behavior context object
     * @param fact The fact leaving the behavior's scope
     * @param workingMemory The working memory session reference
     */
    public void retractFact(Object context,
                            InternalFactHandle fact,
                            PropagationContext pctx,
                            InternalWorkingMemory workingMemory);

    /**
     * A callback method that allows behaviors to expire facts
     */
    public void expireFacts(Object context,
                            PropagationContext pctx,
                            InternalWorkingMemory workingMemory);

    /**
     * Some behaviors might change the expiration offset for the 
     * associated fact type. Example: time sliding windows. 
     * 
     * For these behaviors, this method must return the expiration
     * offset associated to them.
     * 
     * @return the expiration offset for this behavior or -1 if 
     *         they don't have a time based expiration offset.
     */
    public long getExpirationOffset();
    
}
