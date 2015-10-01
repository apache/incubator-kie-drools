/*
 * Copyright 2015 JBoss Inc
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

package org.kie.scanner.embedder;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.kproject.xml.DependencyFilter;
import org.kie.scanner.DependencyDescriptor;
import org.kie.scanner.PomParser;

import java.util.ArrayList;
import java.util.List;

import static org.kie.scanner.embedder.MavenProjectLoader.loadMavenProject;

public class EmbeddedPomParser implements PomParser {

    private final MavenProject mavenProject;

    public EmbeddedPomParser() {
        this(loadMavenProject());
    }

    public EmbeddedPomParser(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    public List<DependencyDescriptor> getPomDirectDependencies(DependencyFilter filter) {
        List<DependencyDescriptor> deps = new ArrayList<DependencyDescriptor>();
        for (Dependency dep : mavenProject.getDependencies()) {
            DependencyDescriptor depDescr = new DependencyDescriptor(dep);
            if (depDescr.isValid() && filter.accept(depDescr.getReleaseId(), depDescr.getScope())) {
                deps.add(depDescr);
            }
        }
        return deps;
    }
}
