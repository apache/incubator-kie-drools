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

package org.drools.runtime.rule;

import java.util.List;

import org.drools.definition.rule.Rule;

public interface Activation {
    /**
     * 
     * @return
     *     The Rule that was activated.
     */
    Rule getRule();

    /**
     * 
     * @return 
     *     The PropagationContext that created this Activation
     */
    PropagationContext getPropagationContext();

    /**
     * 
     * @return
     *     The matched FactHandles for this activation
     */
    List< ? extends FactHandle> getFactHandles();
    
    /**
     * Returns the list of objects that make the tuple that created
     * this activation. The objects are in the proper tuple order.
     * 
     * @return
     */
    List< Object > getObjects();
    
    /**
     * Returns the list of declaration identifiers that are bound to the
     * tuple that created this activation.
     * 
     * @return
     */
    List< String > getDeclarationIDs();
    
    /**
     * Returns the bound declaration value for the given declaration identifier.
     * 
     * @param declarationId
     * @return
     */
    Object getDeclarationValue( String declarationId );
    
    /**
     * An Activation is considered active if it is on the agenda and has not yet fired.
     * Once an Activation has fired it is remove from the Agenda an considered dormant.
     * However remember that the Activation may still be considered "true", i.e. a full match. 
     * @return
     */
    boolean isActive();
    
}
