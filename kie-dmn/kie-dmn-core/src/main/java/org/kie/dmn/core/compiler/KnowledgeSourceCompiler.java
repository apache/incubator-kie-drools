package org.kie.dmn.core.compiler;

import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.KnowledgeSource;

public class KnowledgeSourceCompiler implements DRGElementCompiler {
    @Override
    public boolean accept(DRGElement de) {
        return de instanceof KnowledgeSource;
    }
    @Override
    public void compileNode(DRGElement de, DMNCompilerImpl compiler, DMNModelImpl model) {
        // don't do anything as KnowledgeSource is a documentation element
        // without runtime semantics
    }
}