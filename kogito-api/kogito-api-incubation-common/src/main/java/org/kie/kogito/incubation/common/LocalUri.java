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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * A uri of the form: kogito-local:///a/b/c...
 * <p>
 * For instance: for "/a/b/c" the {@link LocalUri} is represented as:
 *
 * <pre>
 * <code>
 *       LocalUri(
 *           LocalUri(
 *               LocalUri(Root, "a"), "b"), "c")
 * </code>
 * </pre>
 * <p>
 * And it may be constructed with:
 *
 * <pre>
 * <code>
 *     LocalUri.Root.append("a").append("b").append("c");
 * </code>
 * </pre>
 */
public abstract class LocalUri {
    public static final String SCHEME = "kogito-local";
    public static final LocalUri Root = new LocalUriRoot();

    public static LocalUri parse(String path) {
        if (path.startsWith(SCHEME)) {
            URI parsed = URI.create(path);
            path = parsed.getPath();
        }
        if (!path.startsWith("/"))
            throw new IllegalArgumentException("Path must start at root /");
        StringTokenizer tok = new StringTokenizer(path, "/");
        LocalUri hpath = Root;
        while (tok.hasMoreTokens()) {
            hpath = hpath.append(tok.nextToken());
        }
        return hpath;
    }

    // this is a closed hierarchy
    private LocalUri() {
    }

    public abstract String path();

    public abstract LocalUri parent();

    public abstract boolean startsWith(String component);

    public LocalUri append(String component) {
        return new LocalUriPathComponent(this, component);
    }

    public URI toUri() {
        try {
            return new URI(SCHEME, "", this.path(), null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return toUri().toString();
    }

    /**
     * Root of a {@link LocalUri}: "/"
     */
    public static class LocalUriRoot extends LocalUri {
        private LocalUriRoot() {
        }

        @Override
        public LocalUri parent() {
            return null;
        }

        public boolean startsWith(String component) {
            return false;
        }

        @Override
        public String path() {
            return "/";
        }

        // it is a singleton: we don't need to override equals, hashCode
    }

    /**
     * A component of a {@link LocalUri}.
     */
    public static class LocalUriPathComponent extends LocalUri {

        private final LocalUri parent;
        private final String component;

        private LocalUriPathComponent(LocalUri parent, String component) {
            this.parent = parent;
            this.component = URLEncoder.encode(component, StandardCharsets.UTF_8);
        }

        @Override
        public LocalUri parent() {
            return parent;
        }

        /**
         * Returns true when the path starts with the given argument.
         * <p>
         * e.g. when component = "a" then "/a/b/c" startsWith "a"
         */
        public boolean startsWith(String component) {
            return this.parent() == Root && this.component.equals(component)
                    || this.parent().startsWith(component);
        }

        @Override
        public String path() {
            return parent == Root ? ("/" + component) : (parent.path() + "/" + component);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            LocalUriPathComponent that = (LocalUriPathComponent) o;
            return Objects.equals(parent, that.parent) && Objects.equals(component, that.component);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parent, component);
        }
    }
}
