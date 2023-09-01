package org.kie.efesto.compilationmanager.api.model;

import java.util.List;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * A <code>CompilationOutput</code>.
 *
 * It will be translated to a <code>GeneratedExecutableResource</code>,
 * that has a specif json-format and semantic.
 */
public interface EfestoCallableOutput extends EfestoCompilationOutput {

    /**
     * Returns the <b>full resource identifier</b> to be invoked for execution
     * @return
     */
    ModelLocalUriId getModelLocalUriId();

    /**
     * Returns the <b>full class names</b> to be instantiated for execution
     *
     * @return
     */
    List<String> getFullClassNames();
}
