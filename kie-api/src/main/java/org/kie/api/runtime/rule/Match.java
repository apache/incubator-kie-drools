package org.kie.api.runtime.rule;

import org.kie.api.definition.rule.Rule;

import java.util.List;

public interface Match {

    /**
     * 
     * @return
     *     The Rule that was activated.
     */
    public Rule getRule();

    /**
     * 
     * @return
     *     The matched FactHandles for this Match
     */
    public List< ? extends FactHandle> getFactHandles();

    /**
     * Returns the list of objects that make the tuple that created
     * this Match. The objects are in the proper tuple order.
     * 
     * @return
     */
    public List<Object> getObjects();

    /**
     * Returns the list of declaration identifiers that are bound to the
     * tuple that created this Match.
     * 
     * @return
     */
    public List<String> getDeclarationIds();

    /**
     * Returns the bound declaration value for the given declaration identifier.
     * 
     * @param declarationId
     * @return
     */
    public Object getDeclarationValue(String declarationId);

}
