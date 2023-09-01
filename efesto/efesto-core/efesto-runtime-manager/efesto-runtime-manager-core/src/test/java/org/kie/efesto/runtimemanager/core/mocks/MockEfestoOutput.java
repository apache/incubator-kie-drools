package org.kie.efesto.runtimemanager.core.mocks;

import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoOutput;

public class MockEfestoOutput extends AbstractEfestoOutput<String> {

    public MockEfestoOutput() {
        super(new ModelLocalUriId(LocalUri.parse("/mock/" + MockEfestoOutput.class.getCanonicalName().replace('.',
                                                                                                              '/'))),
              "MockEfestoOutput");
    }

}
