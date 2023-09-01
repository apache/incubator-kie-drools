package org.kie.efesto.compilationmanager.core.mocks;

import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class MockEfestoRedirectOutputA extends AbstractMockOutput<String> {

    public MockEfestoRedirectOutputA() {
        super(new ModelLocalUriId(LocalUri.parse("/mock/this/is/mock/friA")), "MockEfestoRedirectOutputA");
    }
}
