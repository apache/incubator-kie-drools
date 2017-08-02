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

package org.kie.scanner.adapters;

import java.util.Collection;
import java.util.List;
import org.appformer.maven.integration.ArtifactResolver;
import org.appformer.maven.integration.DependencyDescriptor;
import org.appformer.maven.support.AFReleaseId;
import org.appformer.maven.support.DependencyFilter;
import org.eclipse.aether.artifact.Artifact;

public class MavenArtifactResolver implements InternalArtifactResolver{

  private final ArtifactResolver resolver;

  public MavenArtifactResolver(ArtifactResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public Artifact resolveArtifact(AFReleaseId releaseId) {
    return resolver.resolveArtifact(releaseId);
  }

  @Override
  public Collection<DependencyDescriptor> getAllDependecies() {
    return resolver.getAllDependecies();
  }

  @Override
  public List<DependencyDescriptor> getArtifactDependecies(String artifactName) {
    return resolver.getArtifactDependecies(artifactName);
  }

  @Override
  public List<DependencyDescriptor> getPomDirectDependencies(DependencyFilter compileFilter) {
    return resolver.getPomDirectDependencies(compileFilter);
  }
}
