package org.kie.efesto.compilationmanager.core.mocks;

import org.kie.efesto.compilationmanager.api.model.EfestoResource;

public class MockEfestoInputF implements EfestoResource<String> {

    private static final String content = "MockEfestoInputF";

    public MockEfestoInputF() {
    }

    @Override
    public String getContent() {
        return content;
    }
}
