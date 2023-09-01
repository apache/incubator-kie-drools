package org.kie.api.runtime.rule;

import java.util.List;

import org.kie.api.definition.rule.Rule;

public interface Match {

    /**
     * @return rule that was activated.
     */
    Rule getRule();

    /**
     *
     * @return matched FactHandles for this Match
     */
    List< ? extends FactHandle> getFactHandles();

    /**
     * @return the list of objects that make the tuple that created
     * this Match. The objects are in the proper tuple order.
     */
    List<Object> getObjects();

    /**
     * @return the list of declaration identifiers that are bound to the
     * tuple that created this Match.
     */
    List<String> getDeclarationIds();

    /**
     * @return the bound declaration value for the given declaration identifier.
     *
     * @param declarationId
     */
    Object getDeclarationValue(String declarationId);

    int getSalience();
}
