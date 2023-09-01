package org.kie.efesto.compilationmanager.core.mocks;

import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class MockEfestoRedirectOutputB extends AbstractMockOutput<String> {

    public MockEfestoRedirectOutputB() {
        super(new ModelLocalUriId(LocalUri.parse("/mock/this/is/mock/friB")), "MockEfestoRedirectOutputB");
    }
}
