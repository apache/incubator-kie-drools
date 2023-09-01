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

import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringTokenizer;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

public class ModelLocalUriId extends LocalUriId implements Serializable {

    private static final long serialVersionUID = 2473381132658366922L;
    private final String model;

    private final String basePath;

    private final String fullPath;


    public ModelLocalUriId(LocalUri path) {
        super(path);
        model = getModel(path);
        basePath = getBasePath(path, model);
        fullPath = path.path();
    }

    public String model() {
        return model;
    }

    public String basePath() {
        return basePath;
    }

    public String fullPath() {
        return fullPath;
    }

    public ModelLocalUriId asModelLocalUriId() {
        return this.getClass().equals(ModelLocalUriId.class) ? this : new ModelLocalUriId(this.asLocalUri());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof ModelLocalUriId)) {
            return false;
        }
        ModelLocalUriId that = (ModelLocalUriId) o;
        return Objects.equals(model, that.model) && Objects.equals(basePath, that.basePath) && Objects.equals(fullPath, that.fullPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, basePath, fullPath);
    }
    static LocalUri.LocalUriPathComponent getFirstLocalUriPathComponent(LocalUri localUri) {
        if (localUri.parent() instanceof LocalUri.LocalUriPathComponent) {
            return getFirstLocalUriPathComponent(localUri.parent());
        } else {
            return localUri instanceof LocalUri.LocalUriPathComponent ? (LocalUri.LocalUriPathComponent) localUri : null;
        }
    }

    static String getModel(LocalUri path) {
        LocalUri.LocalUriPathComponent firstLocalUriPathComponent = getFirstLocalUriPathComponent(path);
        return firstLocalUriPathComponent != null ? firstLocalUriPathComponent.component : null;
    }

    static String getBasePath(LocalUri path, String model) {
        String uriPath = path.path();
        if (model == null || model.isEmpty()) {
            return uriPath;
        } else {
            String start = SLASH + model;
            return uriPath.substring(uriPath.indexOf(start) + start.length());
        }
    }

    protected static LocalUri appendBasePath(LocalUri parent, String basePath) {
        StringTokenizer tok = new StringTokenizer(basePath, SLASH);
        while (tok.hasMoreTokens()) {
            parent = parent.append(decodeString(tok.nextToken()));
        }
        return parent;
    }

    private static String decodeString(String toDecode) {
        return URLDecoder.decode(toDecode, StandardCharsets.UTF_8);
    }
}
