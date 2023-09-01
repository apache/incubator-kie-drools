package org.kie.api.runtime.rule;

public interface Row {
    /**
     * @param identifier the identifier of the bound object
     * @return object that is bound to the given identifier
     */
    Object get(String identifier);


    /**
     * @return FactHandle associated with the given identifier
     */
    FactHandle getFactHandle(String identifier);

}
