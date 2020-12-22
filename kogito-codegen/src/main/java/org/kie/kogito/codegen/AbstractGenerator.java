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

package org.kie.kogito.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.metadata.DefaultLabeler;
import org.kie.kogito.codegen.metadata.Labeler;

public abstract class AbstractGenerator implements Generator {

    private final List<Labeler> labelers = new ArrayList<>();
    private final DefaultLabeler defaultLabeler = new DefaultLabeler();
    private final KogitoBuildContext context;

    protected AbstractGenerator(KogitoBuildContext context) {
        Objects.requireNonNull(context, "context cannot be null");
        this.labelers.add(defaultLabeler);
        this.context = context;
    }

    @Override
    public KogitoBuildContext context() {
        return this.context;
    }
    
    public final void addLabeler(Labeler labeler) {
        this.labelers.add(labeler);
    }

    public final void addLabel(final String key, final String value) {
        defaultLabeler.addLabel(key, value);
    }

    @Override
    public final Map<String, String> getLabels() {
        final Map<String, String> labels = new HashMap<>();
        this.labelers.forEach(l -> labels.putAll(l.generateLabels()));
        return labels;
    }

    protected String applicationCanonicalName() {
        return context.getPackageName() + ".Application";
    }
}
