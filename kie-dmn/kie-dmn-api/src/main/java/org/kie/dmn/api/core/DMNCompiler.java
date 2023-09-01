package org.kie.dmn.api.core;

import java.io.Reader;
import java.util.Collection;
import java.util.Collections;

import org.kie.api.io.Resource;
import org.kie.dmn.model.api.Definitions;

public interface DMNCompiler {

    default DMNModel compile(Resource resource) {
        return compile(resource, Collections.emptyList());
    }

    DMNModel compile(Resource resource, Collection<DMNModel> dmnModels);

    default DMNModel compile(Reader source) {
        return compile(source, Collections.emptyList());
    }

    DMNModel compile(Reader source, Collection<DMNModel> dmnModels);

    default DMNModel compile(Definitions dmndefs) {
        return compile(dmndefs, Collections.emptyList());
    }

    DMNModel compile(Definitions dmndefs, Collection<DMNModel> dmnModels);

    /**
     * As {@link #compile(Definitions, Collection)}, but links {@link Resource} to the manually provided {@link Definitions} while compiling the {@link DMNModel}.
     */
    DMNModel compile(Definitions dmndefs, Resource resource, Collection<DMNModel> dmnModels);

}
