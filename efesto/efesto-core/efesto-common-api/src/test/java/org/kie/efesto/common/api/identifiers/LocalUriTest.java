package org.kie.efesto.common.api.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalUriTest {
    @Test
    public void testToString() {
        LocalUri hpath = LocalUri.Root.append("example").append("some-id").append("instances").append("some-instance-id");
        assertEquals("/example/some-id/instances/some-instance-id", hpath.path());
    }

    @Test
    public void testStartsWith() {
        LocalUri hpath = LocalUri.Root.append("example").append("some-id").append("instances").append("some-instance-id");
        assertTrue(hpath.startsWith("example"));
    }

    @Test
    public void testParse() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri hpath = LocalUri.Root.append("example").append("some-id").append("instances").append("some-instance-id");
        LocalUri parsedHPath = LocalUri.parse(path);
        assertEquals(hpath, parsedHPath);

    }

    @Test
    public void testParseMalformed() {
        String path = "/example////some-id//instances/some-instance-id";
        LocalUri hpath = LocalUri.Root.append("example").append("some-id").append("instances").append("some-instance-id");
        LocalUri parsedHPath = LocalUri.parse(path);
        assertEquals(hpath, parsedHPath);
    }

    @Test
    public void testParseMalformedRelative() {
        String path = "example////some-id//instances/some-instance-id";
        assertThrows(IllegalArgumentException.class, () -> LocalUri.parse(path));
    }

    @Test
    public void testUrlEncoding() {
        LocalUri path = LocalUri.Root.append("URL unsafe").append("??").append("Compon/ents").append("are \\ encoded");
        assertEquals("/URL+unsafe/%3F%3F/Compon%2Fents/are+%5C+encoded", path.path());
    }

    @Test
    public void testUriConversion() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        assertEquals(parsed.toUri().toString(), String.format("%s://%s", LocalUri.SCHEME, parsed.path()));
    }

}
