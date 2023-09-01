package org.kie.dmn.api.core.ast;

import java.util.Optional;

public interface DMNNode {

    String getId();

    String getName();

    String getModelNamespace();

    String getModelName();

    /**
     * Return the import name (short name alias) as described by this node's parent DMN Model, for the supplied namespace and model name.
     * @param ns the namespace of the imported model
     * @param iModelName the model name of the imported model
     * @return
     */
    default Optional<String> getModelImportAliasFor(String ns, String iModelName) {
        return Optional.empty();
    }
}
