package org.kie.efesto.compilationmanager.core.mocks;

import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class MockEfestoRedirectOutputE extends AbstractMockOutput<String> {

    public MockEfestoRedirectOutputE() {
        super(new ModelLocalUriId(LocalUri.parse("/mock/this/is/mock/friE")), "MockEfestoRedirectOutputE");
    }
}
