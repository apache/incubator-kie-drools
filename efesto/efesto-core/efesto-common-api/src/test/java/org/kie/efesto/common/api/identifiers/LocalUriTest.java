/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
