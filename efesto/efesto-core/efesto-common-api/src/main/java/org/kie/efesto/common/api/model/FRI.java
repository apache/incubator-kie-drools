/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.efesto.common.api.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class representing the <b>full resource identifier</b>
 * To be replaced by programmatic API path
 */
public class FRI implements Serializable {

    public static final String SLASH = "/";

    private static final long serialVersionUID = 8729837870805955341L;
    private final String basePath;
    private final String model;
    private final String fri;

    private FRI() {
        this(null, null);
    }

    public FRI(String basePath, String model) {
        this.basePath = generateBasePath(basePath, model);
        this.model = model;
        fri = generateFri(basePath, model);
    }

    public String getBasePath() {
        return basePath;
    }

    public String getModel() {
        return model;
    }

    public String getFri() {
        return fri;
    }

    @Override
    public String toString() {
        return "FRI{" +
                "basePath='" + basePath + '\'' +
                ", model='" + model + '\'' +
                ", fri='" + fri + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FRI fri1 = (FRI) o;
        return Objects.equals(fri, fri1.fri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fri);
    }

    static String generateBasePath(String basePath, String model) {
        if (basePath == null) {
            return null;
        }
        String toReturn = basePath;
        String modelPrefix = SLASH + model + SLASH;
        if (!toReturn.startsWith(SLASH)) {
            toReturn = SLASH + toReturn;
        }
        if (toReturn.startsWith(modelPrefix)) {
            toReturn = toReturn.substring(modelPrefix.length() - 1);
        }
        return toReturn;
    }

    static String generateFri(String basePath, String model) {
        if (basePath == null) {
            return null;
        }
        if (basePath.startsWith(SLASH + model + SLASH)) {
            return basePath;
        }
        String toReturn = basePath;
        if (toReturn.startsWith(SLASH)) {
            toReturn = toReturn.substring(1);
        }
        toReturn = SLASH + model + SLASH + toReturn;
        return toReturn;
    }
}
