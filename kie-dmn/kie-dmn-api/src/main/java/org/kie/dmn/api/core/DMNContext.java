package org.kie.dmn.api.core;

import java.util.Map;
import java.util.Optional;

public interface DMNContext
        extends Cloneable {

    Object set(String name, Object value);

    Object get(String name);

    Map<String, Object> getAll();

    boolean isDefined(String name);

    DMNMetadata getMetadata();

    DMNContext clone();

    /**
     * Walks inside the current scope for the identifier `name`, using the supplied `namespace`, and push that as the new current scope.
     * @param name
     */
    void pushScope(String name, String namespace);

    /**
     * The current scope is pop-ed from the current scope stack.
     */
    void popScope();

    /**
     * Returns the current namespace currently at the top of the scope stack, empty if the stack is empty.
     */
    Optional<String> scopeNamespace();

}
