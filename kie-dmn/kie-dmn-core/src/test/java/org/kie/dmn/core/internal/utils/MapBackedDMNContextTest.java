package org.kie.dmn.core.internal.utils;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;
import org.kie.dmn.core.BaseDMNContextTest;

public class MapBackedDMNContextTest extends BaseDMNContextTest {

    @Test
    public void testEmptyContext() {
        MapBackedDMNContext ctx1 = MapBackedDMNContext.of(new HashMap<>(Collections.emptyMap()));
        testCloneAndAlter(ctx1, Collections.emptyMap(), Collections.emptyMap());

        MapBackedDMNContext ctx2 = MapBackedDMNContext.of(new HashMap<>(Collections.emptyMap()));
        testPushAndPopScope(ctx2, Collections.emptyMap(), Collections.emptyMap());
    }

    @Test
    public void testContextWithEntries() {
        MapBackedDMNContext ctx1 = MapBackedDMNContext.of(new HashMap<>(DEFAULT_ENTRIES));
        testCloneAndAlter(ctx1, DEFAULT_ENTRIES, Collections.emptyMap());

        MapBackedDMNContext ctx2 = MapBackedDMNContext.of(new HashMap<>(DEFAULT_ENTRIES));
        testPushAndPopScope(ctx2, DEFAULT_ENTRIES, Collections.emptyMap());
    }

    @Test
    public void testContextWithEntriesAndMetadata() {
        MapBackedDMNContext ctx1 = MapBackedDMNContext.of(new HashMap<>(DEFAULT_ENTRIES), new HashMap<>(DEFAULT_METADATA));
        testCloneAndAlter(ctx1, DEFAULT_ENTRIES, DEFAULT_METADATA);

        MapBackedDMNContext ctx2 = MapBackedDMNContext.of(new HashMap<>(DEFAULT_ENTRIES), new HashMap<>(DEFAULT_METADATA));
        testPushAndPopScope(ctx2, DEFAULT_ENTRIES, DEFAULT_METADATA);
    }

}
