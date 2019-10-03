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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.codegen.metadata.DefaultLabeler;
import org.kie.kogito.codegen.metadata.Labeler;

public abstract class AbstractGenerator implements Generator {

    protected Path projectDirectory;
    protected GeneratorContext context;

    private final List<Labeler> labelers = new ArrayList<>();
    private final DefaultLabeler defaultLabeler = new DefaultLabeler();
    
    protected AbstractGenerator() {
        this.labelers.add(defaultLabeler);
    }

    @Override
    public void setProjectDirectory(Path projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    @Override
    public void setContext(GeneratorContext context) {
        this.context = context;
    }

    @Override
    public GeneratorContext context() {
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

}
