/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.scanner;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.xml.PomModel;
import org.drools.compiler.kproject.xml.PomModelGenerator;
import org.kie.api.builder.ReleaseId;

import java.io.InputStream;

import static org.kie.scanner.embedder.MavenProjectLoader.parseMavenPom;

public class MavenPomModelGenerator implements PomModelGenerator {

    @Override
    public PomModel parse(String path, InputStream pomStream) {
        return new MavenModel(parseMavenPom(pomStream));
    }

    public static class MavenModel extends PomModel.InternalModel {

        private final MavenProject mavenProject;

        public MavenModel( MavenProject mavenProject ) {
            this.mavenProject = mavenProject;
            setReleaseId( initReleaseId( mavenProject ) );
            setParentReleaseId( initParentReleaseId( mavenProject ) );
            initDependencies( mavenProject );
        }

        public MavenProject getMavenProject() {
            return mavenProject;
        }

        private ReleaseId initReleaseId(MavenProject mavenProject) {
            return new ReleaseIdImpl(mavenProject.getGroupId(),
                                     mavenProject.getArtifactId(),
                                     mavenProject.getVersion());
        }

        private ReleaseId initParentReleaseId(MavenProject mavenProject) {
            try {
                MavenProject parentProject = mavenProject.getParent();
                if (parentProject != null) {
                    return new ReleaseIdImpl(parentProject.getGroupId(),
                                             parentProject.getArtifactId(),
                                             parentProject.getVersion());
                }
            } catch (Exception e) {
                // ignore
            }
            return null;
        }

        private void initDependencies(MavenProject mavenProject) {
            // use getArtifacts instead of getDependencies to load transitive dependencies as well
            for (Artifact dep : mavenProject.getArtifacts()) {
                addDependency(new ReleaseIdImpl( dep.getGroupId(), dep.getArtifactId(), dep.getVersion() ), dep.getScope());
            }
        }
    }
}
