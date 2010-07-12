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
    
}
