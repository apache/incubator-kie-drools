package org.kie.efesto.compilationmanager.core.mocks;

import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class MockEfestoRedirectOutputC extends AbstractMockOutput<String> {

    public MockEfestoRedirectOutputC() {
        super(new ModelLocalUriId(LocalUri.parse("/mock/this/is/mock/friC")), "MockEfestoRedirectOutputC");
    }
}
