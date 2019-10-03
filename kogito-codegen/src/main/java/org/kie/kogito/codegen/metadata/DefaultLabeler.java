/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * Just a holder for informative or random labels that the Generators might want to add
 */
public class DefaultLabeler implements Labeler {
    
    private final Map<String, String> labels = new HashMap<>();

    public final void addLabel(final String key, final String value) {
        this.labels.put(key, value);
    }
    
    @Override
    public Map<String, String> generateLabels() {
        return labels;
    }

    
}
