package org.kie.efesto.compilationmanager.core.mocks;

import java.util.Collections;
import java.util.List;

import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutput;

public class MockEfestoCallableOutput implements EfestoCallableOutput {

    private ModelLocalUriId modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/mock/efesto/output/module"));

    @Override
    public ModelLocalUriId getModelLocalUriId() {
        return modelLocalUriId;
    }

    @Override
    public List<String> getFullClassNames() {
        return Collections.singletonList("mock.efesto.output.Module");
    }

}
