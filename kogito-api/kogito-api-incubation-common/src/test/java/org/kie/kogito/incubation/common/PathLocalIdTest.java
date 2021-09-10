/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.incubation.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathLocalIdTest {

    ExampleRoot exampleRoot = new ExampleRoot();

    @Test
    public void testId() {
        ExampleLocalId exampleLocalId = exampleRoot.get("some-id");
        assertEquals("/example/some-id", exampleLocalId.asLocalUri().path());
    }

    @Test
    public void testIdNested() {
        ExampleInstanceLocalId exampleLocalId = exampleRoot.get("some-id").instances().get("some-instance-id");
        assertEquals("/example/some-id/instances/some-instance-id", exampleLocalId.asLocalUri().path());
    }

    @Test
    public void testStartsWith() {
        ExampleInstanceLocalId exampleLocalId = exampleRoot.get("some-id").instances().get("some-instance-id");
        assertTrue(exampleLocalId.asLocalUri().startsWith("example"));
    }

    static class ExampleRoot implements ComponentRoot {
        public ExampleLocalId get(String identifier) {
            return new ExampleLocalId(identifier);
        }
    }

    static class ExampleLocalId extends LocalUriId {
        private static String PREFIX = "example";

        private ExampleLocalId(String identifier) {
            super(LocalUri.Root.append(PREFIX).append(identifier));
        }

        public ExampleInstanceLocalIdFactory instances() {
            return new ExampleInstanceLocalIdFactory(this);
        }

    }

    static class ExampleInstanceLocalIdFactory {
        LocalUri parent;

        public ExampleInstanceLocalIdFactory(ExampleLocalId parent) {
            this.parent = parent.asLocalUri().append("instances");
        }

        ExampleInstanceLocalId get(String identifier) {
            return new ExampleInstanceLocalId(parent, identifier);
        }
    }

    static class ExampleInstanceLocalId extends LocalUriId {

        public ExampleInstanceLocalId(LocalUri parent, String identifier) {
            super(parent.append(identifier));
        }
    }

}
