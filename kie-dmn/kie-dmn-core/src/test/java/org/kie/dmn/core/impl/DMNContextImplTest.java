package org.kie.dmn.core.impl;

import java.util.Collections;

import org.junit.Test;
import org.kie.dmn.core.BaseDMNContextTest;

public class DMNContextImplTest extends BaseDMNContextTest {

    @Test
    public void testEmptyContext() {
        DMNContextImpl ctx1 = new DMNContextImpl();
        testCloneAndAlter(ctx1, Collections.emptyMap(), Collections.emptyMap());

        DMNContextImpl ctx2 = new DMNContextImpl();
        testPushAndPopScope(ctx2, Collections.emptyMap(), Collections.emptyMap());
    }

    @Test
    public void testContextWithEntries() {
        DMNContextImpl ctx1 = new DMNContextImpl(DEFAULT_ENTRIES);
        testCloneAndAlter(ctx1, DEFAULT_ENTRIES, Collections.emptyMap());

        DMNContextImpl ctx2 = new DMNContextImpl(DEFAULT_ENTRIES);
        testPushAndPopScope(ctx2, DEFAULT_ENTRIES, Collections.emptyMap());
    }

    @Test
    public void testContextWithEntriesAndMetadata() {
        DMNContextImpl ctx1 = new DMNContextImpl(DEFAULT_ENTRIES, DEFAULT_METADATA);
        testCloneAndAlter(ctx1, DEFAULT_ENTRIES, DEFAULT_METADATA);

        DMNContextImpl ctx2 = new DMNContextImpl(DEFAULT_ENTRIES, DEFAULT_METADATA);
        testPushAndPopScope(ctx2, DEFAULT_ENTRIES, DEFAULT_METADATA);
    }

}
