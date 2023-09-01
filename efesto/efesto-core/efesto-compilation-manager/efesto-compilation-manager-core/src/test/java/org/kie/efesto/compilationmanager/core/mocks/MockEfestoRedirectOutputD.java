package org.kie.efesto.compilationmanager.core.mocks;

import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class MockEfestoRedirectOutputD extends AbstractMockOutput<String> {

    public MockEfestoRedirectOutputD() {
        super(new ModelLocalUriId(LocalUri.parse("/mock/this/is/mock/friD")), "MockEfestoRedirectOutputD");
    }
}
